package org.teamapps.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.teamapps.dto.*;

public class JsonNodeWrapperPerformanceTest {

	public static final int NUMBER_OF_RUNS = 100_000;
	private static ObjectMapper objectMapper;

	@Test
	public void doIt() throws Exception {
		objectMapper = new ObjectMapper();

		UiCurrencyUnit uiCurrencyUnit = new UiCurrencyUnit();
		uiCurrencyUnit.setCode("EUR");
		uiCurrencyUnit.setFractionDigits(2);
		uiCurrencyUnit.setName("asdf");
		uiCurrencyUnit.setSymbol("asldjf");
		EVENT event1 = new EVENT("asdf", 1, new UiTable.CellValueChangedEvent("asdf", 123, "columnPropName", new UiCurrencyValue(uiCurrencyUnit, "123897")));

		String event1String = objectMapper.writeValueAsString(event1);
		System.out.println(event1String);

		int x = 0;
		for (int i = 0; i < 5; i++) {
			x += measureFullDeserialization("runWrapping", event1String, this::runWrapping);
		}
		System.out.println(x);
	}

	interface TestRun {
		int run(String e1) throws Exception;
	}

	private int measureFullDeserialization(String testName, String event1String, TestRun testRun) throws Exception {
		int x = 0;
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			var e1 = event1String.substring(0, 42) + i + event1String.substring(42);
			x += testRun.run(e1);
		}
		System.out.println(testName + ": " + (System.currentTimeMillis() - startTime));
		return x;
	}

	private int runWrapping(String e1) throws JsonProcessingException {
		int x = 0;
		JsonNode jsonNode = objectMapper.readTree(e1);
		EVENTWrapper eventWrapper = new EVENTWrapper(jsonNode);
		x += eventWrapper.getSessionId().hashCode();
		x += eventWrapper.getId();
		UiEventWrapper uiEventWrapper = eventWrapper.getUiEvent();
		UiTable.CellValueChangedEventWrapper cellValueChangedEventWrapper = uiEventWrapper.as(UiTable.CellValueChangedEventWrapper.class);
		x += cellValueChangedEventWrapper.getComponentId().hashCode();
		x += cellValueChangedEventWrapper.getRecordId();
		x += cellValueChangedEventWrapper.getColumnPropertyName().hashCode();
		UiCurrencyValueWrapper currencyValue = cellValueChangedEventWrapper.getValue().as(UiCurrencyValueWrapper.class);
		UiCurrencyUnitWrapper currencyUnit = currencyValue.getCurrencyUnit();
		x += currencyUnit.getCode().hashCode();
		x += currencyUnit.getFractionDigits();
		x += currencyUnit.getName().hashCode();
		x += currencyUnit.getSymbol().hashCode();

		return x;
	}
}
