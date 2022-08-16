package org.teamapps.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
		EVENT event2 = new EVENT("asdf", 1, new UiTable.SortingChangedEvent("alsdkjf", "ljdf", UiSortDirection.ASC));

		String event1String = objectMapper.writeValueAsString(event1);
		System.out.println(event1String);
		String event2String = objectMapper.writeValueAsString(event2);
		System.out.println(event2String);

		int x = 0;
		for (int i = 0; i < 20; i++) {
			x += measureFullDeserialization("runFullDeserialization", event1String, event2String, this::runFullDeserialization);
			x += measureFullDeserialization("runWrapping", event1String, event2String, this::runWrapping);
		}
		System.out.println(x);
	}

	interface TestRun {
		int run(String e1, String e2) throws Exception;
	}

	private int measureFullDeserialization(String testName, String event1String, String event2String, TestRun testRun) throws Exception {
		int x = 0;
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			var e1 = event1String.substring(0, 42) + i + event1String.substring(42);
			var e2 = event2String.substring(0, 42) + i + event2String.substring(42);
			x += testRun.run(e1, e2);
		}
		System.out.println(testName + ": " + (System.currentTimeMillis() - startTime));
		return x;
	}

	private int runFullDeserialization(String e1, String e2) throws JsonProcessingException {
		int x = 0;
		AbstractClientMessage m = objectMapper.readValue(e1, AbstractClientMessage.class);
		EVENT event = (EVENT) m;
		x += event.getSessionId().hashCode();
		x += event.getId();
		UiTable.CellValueChangedEvent cellValueChangedEvent = (UiTable.CellValueChangedEvent) event.getUiEvent();
		x += cellValueChangedEvent.getComponentId().hashCode();
		x += cellValueChangedEvent.getRecordId();
		x += cellValueChangedEvent.getColumnPropertyName().hashCode();
		UiCurrencyValue currencyValue = (UiCurrencyValue) cellValueChangedEvent.getValue();
		UiCurrencyUnit currencyUnit = currencyValue.getCurrencyUnit();
		x += currencyUnit.getCode().hashCode();
		x += currencyUnit.getFractionDigits();
		x += currencyUnit.getName().hashCode();
		x += currencyUnit.getSymbol().hashCode();

		m = objectMapper.readValue(e2, AbstractClientMessage.class);
		event = (EVENT) m;
		x += event.getSessionId().hashCode();
		x += event.getId();
		UiTable.SortingChangedEvent sortingChangedEvent = (UiTable.SortingChangedEvent) event.getUiEvent();
		x += sortingChangedEvent.getComponentId().hashCode();
		x += sortingChangedEvent.getSortField().hashCode();
		x += sortingChangedEvent.getSortDirection().hashCode();

		return x;
	}

	private int runWrapping(String e1, String e2) throws JsonProcessingException {
		int x = 0;
		JsonNode jsonNode = objectMapper.readTree(e1);
		EVENTWrapper eventWrapper = new EVENTWrapper(jsonNode);
		x += eventWrapper.getSessionId().hashCode();
		x += eventWrapper.getId();
		UiEventWrapper uiEventWrapper = eventWrapper.getUiEvent();
		CellValueChangedEventWrapper cellValueChangedEventWrapper = uiEventWrapper.as(CellValueChangedEventWrapper.class);
		x += cellValueChangedEventWrapper.getComponentId().hashCode();
		x += cellValueChangedEventWrapper.getRecordId();
		x += cellValueChangedEventWrapper.getColumnPropertyName().hashCode();
		UiCurrencyValueWrapper currencyValue = cellValueChangedEventWrapper.getValue().as(UiCurrencyValueWrapper.class);
		UiCurrencyUnitWrapper currencyUnit = currencyValue.getCurrencyUnit();
		x += currencyUnit.getCode().hashCode();
		x += currencyUnit.getFractionDigits();
		x += currencyUnit.getName().hashCode();
		x += currencyUnit.getSymbol().hashCode();

		jsonNode = objectMapper.readTree(e2);
		eventWrapper = new EVENTWrapper(jsonNode);
		x += eventWrapper.getSessionId().hashCode();
		x += eventWrapper.getId();
		uiEventWrapper = eventWrapper.getUiEvent();
		SortingChangedEventWrapper sortingChangedEventWrapper = uiEventWrapper.as(SortingChangedEventWrapper.class);
		x += sortingChangedEventWrapper.getComponentId().hashCode();
		x += sortingChangedEventWrapper.getSortField().hashCode();
		x += sortingChangedEventWrapper.getSortDirection().hashCode();

		return x;
	}


//	private static class JsonWrapper {
//		JsonNode jsonNode;
//
//		public JsonWrapper(JsonNode jsonNode) {
//			this.jsonNode = jsonNode;
//		}
//
//		public JsonNode getJsonNode() {
//			return jsonNode;
//		}
//
//		public <W extends JsonWrapper> W as(Class<W> clazz) {
//			try {
//				return clazz.getConstructor(JsonNode.class).newInstance(jsonNode);
//			} catch (Exception e) {
//				throw new RuntimeException(e);
//			}
//		}
//	}
//
//	private static class EVENTWrapper extends JsonWrapper {
//
//		public EVENTWrapper(JsonNode jsonNode) {
//			super(jsonNode);
//		}
//
//		public String getSessionId() {
//			return jsonNode.get("sessionId").asText();
//		}
//
//		public int getId() {
//			return jsonNode.get("id").asInt();
//		}
//
//		public UiEventWrapper getUiEvent() {
//			JsonNode event = jsonNode.get("uiEvent");
//			return event != null ? new UiEventWrapper(event) : null;
//		}
//	}
//
//	public static class UiEventWrapper extends JsonWrapper {
//		public UiEventWrapper(JsonNode jsonNode) {
//			super(jsonNode);
//		}
//	}
//
	private static class CellValueChangedEventWrapper extends JsonWrapper {

		public CellValueChangedEventWrapper(JsonNode jsonNode) {
			super(jsonNode);
		}

		public String getComponentId() {
			return jsonNode.get("componentId").asText();
		}

		public int getRecordId() {
			return jsonNode.get("recordId").asInt();
		}

		public String getColumnPropertyName() {
			return jsonNode.get("columnPropertyName").asText();
		}

		public JsonWrapper getValue() {
			return new JsonWrapper(jsonNode.get("value"));
		}
	}
//
//	private static class CurrencyValueWrapper extends JsonWrapper {
//		public CurrencyValueWrapper(JsonNode jsonNode) {
//			super(jsonNode);
//		}
//
//		public UiCurrencyUnitWrapper getCurrencyUnit() {
//			JsonNode currencyUnit = jsonNode.get("currencyUnit");
//			return currencyUnit != null ? new UiCurrencyUnitWrapper(currencyUnit) : null;
//		}
//
//		public String getAmount() {
//			return jsonNode.get("amount").asText();
//		}
//	}
//
//	public static class UiCurrencyUnitWrapper extends JsonWrapper {
//
//		public UiCurrencyUnitWrapper(JsonNode jsonNode) {
//			super(jsonNode);
//		}
//
//		public String getCode() {
//			return jsonNode.get("code").asText();
//		}
//
//		public int getFractionDigits() {
//			return jsonNode.get("fractionDigits").asInt();
//		}
//
//		public String getName() {
//			return jsonNode.get("name").asText();
//		}
//
//		public String getSymbol() {
//			return jsonNode.get("symbol").asText();
//		}
//	}
//
	public static class SortingChangedEventWrapper extends JsonWrapper {

		public SortingChangedEventWrapper(JsonNode jsonNode) {
			super(jsonNode);
		}

		public String getComponentId() {
			return jsonNode.get("componentId").asText();
		}

		public String getSortField() {
			return jsonNode.get("sortField").asText();
		}

		public UiSortDirection getSortDirection() {
			JsonNode x = jsonNode.get("sortDirection");
			if (x.isInt()) {
				return UiSortDirection.values()[x.intValue()];
			} else if (x.isTextual()) {
				return UiSortDirection.valueOf(x.textValue());
			} else {
				return null;
			}
		}

		public List<EVENTWrapper> getX() {
			ArrayNode a = ((ArrayNode) jsonNode.get("x"));
			return a != null ? StreamSupport.stream(Spliterators.spliterator(a.elements(), a.size(), Spliterator.ORDERED), false)
					.map(e -> new EVENTWrapper(e))
					.collect(Collectors.toList()) : null;
		}

		public Map<String, EVENTWrapper> getY() {
			ObjectNode a = ((ObjectNode) jsonNode.get("x"));
			return a != null ? StreamSupport.stream(Spliterators.spliterator(a.fields(), a.size(), Spliterator.ORDERED), false)
					.collect(Collectors.toMap(e -> e.getKey(), e -> new EVENTWrapper(e.getValue()))) : null;
		}

	}
}
