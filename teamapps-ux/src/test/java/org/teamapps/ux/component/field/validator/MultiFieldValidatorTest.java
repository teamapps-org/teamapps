package org.teamapps.ux.component.field.validator;

import org.junit.Test;
import org.mockito.Mockito;
import org.teamapps.event.Event;
import org.teamapps.ux.component.field.FieldMessage;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

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
        Mockito.when(sessionContextMock.onDestroyed()).thenReturn(new Event<>());
        Mockito.when(sessionContextMock.runWithContext(Mockito.any())).then(invocation -> {
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

}