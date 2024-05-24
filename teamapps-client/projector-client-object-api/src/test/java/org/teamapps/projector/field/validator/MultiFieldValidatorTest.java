/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.projector.field.validator;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.teamapps.commons.event.Event;
import org.teamapps.projector.clientobject.ClientObjectChannel;
import org.teamapps.projector.clientobject.component.ComponentConfig;
import org.teamapps.projector.field.AbstractField;
import org.teamapps.projector.field.FieldMessage;
import org.teamapps.projector.session.CurrentSessionContext;
import org.teamapps.projector.session.SessionContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class MultiFieldValidatorTest {

    public static final String ERROR_MESSAGE = "some error";

    @Test
    public void testManualTriggeringDoesNotTriggerWhenFieldValueChanges() throws Exception {
        doWithFakeSessionContext(() -> {
            DummyField field1 = new DummyField();
            DummyField field2 = new DummyField();

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
            DummyField field1 = new DummyField();
            DummyField field2 = new DummyField();

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
            DummyField field1 = new DummyField();
            DummyField field2 = new DummyField();

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
            DummyField field1 = new DummyField();
            DummyField field2 = new DummyField();

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
            DummyField field1 = new DummyField();
            DummyField field2 = new DummyField();

            CustomValidator customValidatorMock = Mockito.mock(CustomValidator.class);
            Mockito.when(customValidatorMock.validate()).thenReturn(List.of(new FieldMessage(FieldMessage.Severity.ERROR, ERROR_MESSAGE)));

            MultiFieldValidator validator = new MultiFieldValidator(customValidatorMock, MultiFieldValidator.TriggeringPolicy.MANUALLY_WITH_AUTOCLEAR, field1, field2);

            field1.onValueChanged.fire("asdf");

            List<FieldMessage> messages = validator.validate();
            verifyFieldMessages(messages, ERROR_MESSAGE);
            verifyFieldMessages(field1.getFieldMessages(), ERROR_MESSAGE);
            verifyFieldMessages(field2.getFieldMessages(), ERROR_MESSAGE);

            field2.onValueChanged.fire("qwerty");

            Assertions.assertThat(field1.getFieldMessages().isEmpty());
            Assertions.assertThat(field2.getFieldMessages().isEmpty());
        });
    }

    @Test
    public void testManualWithAutoClearReAddsMessageOnSecondValidation() throws Exception {
        doWithFakeSessionContext(() -> {
            DummyField field1 = new DummyField();
            DummyField field2 = new DummyField();

            CustomValidator customValidatorMock = Mockito.mock(CustomValidator.class);
            Mockito.when(customValidatorMock.validate()).thenReturn(List.of(new FieldMessage(FieldMessage.Severity.ERROR, ERROR_MESSAGE)));

            MultiFieldValidator validator = new MultiFieldValidator(customValidatorMock, MultiFieldValidator.TriggeringPolicy.MANUALLY_WITH_AUTOCLEAR, field1, field2);

            field1.onValueChanged.fire("asdf");

            List<FieldMessage> messages = validator.validate();
            verifyFieldMessages(messages, ERROR_MESSAGE);
            verifyFieldMessages(field1.getFieldMessages(), ERROR_MESSAGE);
            verifyFieldMessages(field2.getFieldMessages(), ERROR_MESSAGE);

            field2.onValueChanged.fire("qwerty");

            Assertions.assertThat(field1.getFieldMessages().isEmpty());
            Assertions.assertThat(field2.getFieldMessages().isEmpty());

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
        Mockito.when(sessionContextMock.getClientObjectChannel(any())).thenReturn(Mockito.mock(ClientObjectChannel.class));
        Mockito.when(sessionContextMock.onDestroyed()).thenReturn(new Event<>());
        Mockito.when(sessionContextMock.runWithContext(any(Runnable.class))).then(invocation -> {
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

    public static class DummyField extends AbstractField<String> {
        @Override
        public ComponentConfig createConfig() {
            return null;
        }
    }

}
