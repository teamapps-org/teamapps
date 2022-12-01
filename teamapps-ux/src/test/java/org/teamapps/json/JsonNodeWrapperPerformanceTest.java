package org.teamapps.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.teamapps.dto.*;
import org.teamapps.dto.protocol.DtoEVT;
import org.teamapps.dto.protocol.DtoEVTWrapper;
import org.teamapps.dto.protocol.DtoEventWrapper;

public class JsonNodeWrapperPerformanceTest {

	public static final int NUMBER_OF_RUNS = 100_000;
	private static ObjectMapper objectMapper;

	@Test
	public void doIt() throws Exception {
		objectMapper = new ObjectMapper();

		DtoCurrencyUnit uiCurrencyUnit = new DtoCurrencyUnit();
		uiCurrencyUnit.setCode("EUR");
		uiCurrencyUnit.setFractionDigits(2);
		uiCurrencyUnit.setName("asdf");
		uiCurrencyUnit.setSymbol("asldjf");
		DtoEVT event1 = new DtoEVT("asdf", 1, new DtoTable.CellValueChangedEvent("asdf", 123, "columnPropName", new DtoCurrencyValue(uiCurrencyUnit, "123897")));

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
			x += testRun.run(event1String);
		}
		System.out.println(testName + ": " + (System.currentTimeMillis() - startTime));
		return x;
	}

	private int runWrapping(String e1) throws JsonProcessingException {
		int x = 0;
		JsonNode jsonNode = objectMapper.readTree(e1);
		DtoEVTWrapper eventWrapper = new DtoEVTWrapper(jsonNode);
		x += eventWrapper.getId();
		DtoEventWrapper uiEventWrapper = eventWrapper.getUiEvent();
		DtoTable.CellValueChangedEventWrapper cellValueChangedEventWrapper = uiEventWrapper.as(DtoTable.CellValueChangedEventWrapper.class);
		x += cellValueChangedEventWrapper.getComponentId().hashCode();
		x += cellValueChangedEventWrapper.getRecordId();
		x += cellValueChangedEventWrapper.getColumnPropertyName().hashCode();
		DtoCurrencyValueWrapper currencyValue = cellValueChangedEventWrapper.getValue().as(DtoCurrencyValueWrapper.class);
		DtoCurrencyUnitWrapper currencyUnit = currencyValue.getCurrencyUnit();
		x += currencyUnit.getCode().hashCode();
		x += currencyUnit.getFractionDigits();
		x += currencyUnit.getName().hashCode();
		x += currencyUnit.getSymbol().hashCode();

		return x;
	}
}
