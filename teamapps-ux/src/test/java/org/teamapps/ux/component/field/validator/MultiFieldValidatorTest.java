/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.ux.component.field.validator;

import org.junit.Test;
import org.mockito.Mockito;
import org.teamapps.event.Event;
import org.teamapps.ux.component.field.FieldMessage;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiFieldValidatorTest {

    public static final String ERROR_MESSAGE = "some error";

    @Test
    public void testManualTriggeringDoesNotTriggerWhenFieldValueChanges() throws Exception {
        doWithFakeSessionContext(() -> {
            TextField field1 = new TextField();
            TextField field2 = new TextField();

            CustomValidator customValidatorMock = Mockito.mock(CustomValidator.class);
            Mockito.when(customValidatorMock.validate()).thenReturn(List.of(new FieldMessage(FieldMessage.Severity.ERROR, ERROR_MESSAGE)));

            MultiFieldValidator validator = new MultiFieldValidator(customValidatorMock, field1, field2);

            field1.onValueChanged.fire("asdf");

            verifyFieldMessages(field1.getFieldMessages());
            verifyFieldMessages(field2.getFieldMessages());
        });
    }

    @Test
    public void testManualTriggeringDoesNotPileUpMessages() throws Exception {
        doWithFakeSessionContext(() -> {
            TextField field1 = new TextField();
            TextField field2 = new TextField();

            CustomValidator customValidatorMock = Mockito.mock(CustomValidator.class);
            Mockito.when(customValidatorMock.validate()).thenReturn(List.of(new FieldMessage(FieldMessage.Severity.ERROR, ERROR_MESSAGE)));

            MultiFieldValidator validator = new MultiFieldValidator(customValidatorMock, field1, field2);

            List<FieldMessage> messages = validator.validate();
            verifyFieldMessages(messages, ERROR_MESSAGE);
            verifyFieldMessages(field1.getFieldMessages(), ERROR_MESSAGE);
            verifyFieldMessages(field2.getFieldMessages(), ERROR_MESSAGE);

            messages = validator.validate();
            verifyFieldMessages(messages, ERROR_MESSAGE);
            verifyFieldMessages(field1.getFieldMessages(), ERROR_MESSAGE);
            verifyFieldMessages(field2.getFieldMessages(), ERROR_MESSAGE);
        });
    }

    @Test
    public void testManualTriggeringClearsMessages() throws Exception {
        doWithFakeSessionContext(() -> {
            TextField field1 = new TextField();
            TextField field2 = new TextField();

            CustomValidator customValidatorMock = Mockito.mock(CustomValidator.class);
            Mockito.when(customValidatorMock.validate()).thenReturn(List.of(new FieldMessage(FieldMessage.Severity.ERROR, ERROR_MESSAGE)));

            MultiFieldValidator validator = new MultiFieldValidator(customValidatorMock, field1, field2);

            List<FieldMessage> messages = validator.validate();
            verifyFieldMessages(messages, ERROR_MESSAGE);
            verifyFieldMessages(field1.getFieldMessages(), ERROR_MESSAGE);
            verifyFieldMessages(field2.getFieldMessages(), ERROR_MESSAGE);

            Mockito.when(customValidatorMock.validate()).thenReturn(List.of());

            messages = validator.validate();
            verifyFieldMessages(messages);
            verifyFieldMessages(field1.getFieldMessages());
            verifyFieldMessages(field2.getFieldMessages());
        });
    }

    @Test
    public void testTriggeringOnChange() throws Exception {
        doWithFakeSessionContext(() -> {
            TextField field1 = new TextField();
            TextField field2 = new TextField();

            CustomValidator customValidatorMock = Mockito.mock(CustomValidator.class);
            Mockito.when(customValidatorMock.validate()).thenReturn(List.of(new FieldMessage(FieldMessage.Severity.ERROR, ERROR_MESSAGE)));

            MultiFieldValidator validator = new MultiFieldValidator(customValidatorMock, MultiFieldValidator.TriggeringPolicy.ON_FIELD_CHANGE, field1, field2);

            field1.onValueChanged.fire("asdf");

            verifyFieldMessages(field1.getFieldMessages(), ERROR_MESSAGE);
            verifyFieldMessages(field2.getFieldMessages(), ERROR_MESSAGE);
        });
    }

    @Test
    public void testManualWithAutoClearRmovesMessages() throws Exception {
        doWithFakeSessionContext(() -> {
            TextField field1 = new TextField();
            TextField field2 = new TextField();

            CustomValidator customValidatorMock = Mockito.mock(CustomValidator.class);
            Mockito.when(customValidatorMock.validate()).thenReturn(List.of(new FieldMessage(FieldMessage.Severity.ERROR, ERROR_MESSAGE)));

            MultiFieldValidator validator = new MultiFieldValidator(customValidatorMock, MultiFieldValidator.TriggeringPolicy.MANUALLY_WITH_AUTOCLEAR, field1, field2);

            field1.onValueChanged.fire("asdf");

            List<FieldMessage> messages = validator.validate();
            verifyFieldMessages(messages, ERROR_MESSAGE);
            verifyFieldMessages(field1.getFieldMessages(), ERROR_MESSAGE);
            verifyFieldMessages(field2.getFieldMessages(), ERROR_MESSAGE);

            field2.onValueChanged.fire("qwerty");

            assertThat(field1.getFieldMessages().isEmpty());
            assertThat(field2.getFieldMessages().isEmpty());
        });
    }

    @Test
    public void testManualWithAutoClearReAddsMessageOnSecondValidation() throws Exception {
        doWithFakeSessionContext(() -> {
            TextField field1 = new TextField();
            TextField field2 = new TextField();

            CustomValidator customValidatorMock = Mockito.mock(CustomValidator.class);
            Mockito.when(customValidatorMock.validate()).thenReturn(List.of(new FieldMessage(FieldMessage.Severity.ERROR, ERROR_MESSAGE)));

            MultiFieldValidator validator = new MultiFieldValidator(customValidatorMock, MultiFieldValidator.TriggeringPolicy.MANUALLY_WITH_AUTOCLEAR, field1, field2);

            field1.onValueChanged.fire("asdf");

            List<FieldMessage> messages = validator.validate();
            verifyFieldMessages(messages, ERROR_MESSAGE);
            verifyFieldMessages(field1.getFieldMessages(), ERROR_MESSAGE);
            verifyFieldMessages(field2.getFieldMessages(), ERROR_MESSAGE);

            field2.onValueChanged.fire("qwerty");

            assertThat(field1.getFieldMessages().isEmpty());
            assertThat(field2.getFieldMessages().isEmpty());

            List<FieldMessage> validation2 = validator.validate();
            verifyFieldMessages(field1.getFieldMessages(), ERROR_MESSAGE);
            verifyFieldMessages(field2.getFieldMessages(), ERROR_MESSAGE);
        });
    }

    private void verifyFieldMessages(List<FieldMessage> fieldMessages, String... errorMessages) {
        assertThat(fieldMessages)
                .extracting(FieldMessage::getMessage)
                .containsExactly(errorMessages);
    }

    private void doWithFakeSessionContext(RunnableWithException r) throws Exception {
        Method set = CurrentSessionContext.class.getDeclaredMethod("set", SessionContext.class);
        set.setAccessible(true);
        SessionContext sessionContextMock = Mockito.mock(SessionContext.class);
        unsafelySetOnDestroyed(sessionContextMock, new Event<>());
        Mockito.when(sessionContextMock.runWithContext(Mockito.any(Runnable.class))).then(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return CompletableFuture.completedFuture(null);
        });
        set.invoke(null, sessionContextMock);
        try {
            r.run();
        } finally {
            set.invoke(null, new Object[]{null});
        }
    }

    public interface RunnableWithException {
        void run() throws Exception;
    }

    private static void unsafelySetOnDestroyed(Object object, Object value) throws NoSuchFieldException, IllegalAccessException {
        final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        final Unsafe unsafe = (Unsafe) unsafeField.get(null);
        Field f = object.getClass().getField("onDestroyed");
        unsafe.putObject(object, unsafe.objectFieldOffset(f), value);
    }

}
