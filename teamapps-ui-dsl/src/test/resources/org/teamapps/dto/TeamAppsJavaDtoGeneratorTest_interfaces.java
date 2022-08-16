package org.teamapps.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_type")
@JsonTypeIdResolver(TeamAppsJacksonTypeIdResolver.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface A extends UiObject {

	public String getA();
	public String getB();
	public A setB(String b);

	public static class YEvent implements UiEvent {

		protected String componentId;
		protected String y;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public YEvent() {
			// default constructor for Jackson
		}

		public YEvent(String componentId, String y) {
			this.componentId = componentId;
			this.y = y;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("componentId=" + componentId).append(", ")
					.append("y=" + y)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("componentId")
		public String getComponentId() {
			return componentId;
		}

		@com.fasterxml.jackson.annotation.JsonGetter("y")
		public String getY() {
			return y;
		}

	}

	public static class QQuery implements UiQuery {

		protected String componentId;
		protected String y;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public QQuery() {
			// default constructor for Jackson
		}

		public QQuery(String componentId, String y) {
			this.componentId = componentId;
			this.y = y;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("componentId=" + componentId).append(", ")
					.append("y=" + y)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("componentId")
		public String getComponentId() {
			return componentId;
		}

		@com.fasterxml.jackson.annotation.JsonGetter("y")
		public String getY() {
			return y;
		}

	}

	public static class XCommand implements UiCommand<Void> {

		protected String componentId;
		protected String x;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public XCommand() {
			// default constructor for Jackson
		}

		public XCommand(String componentId, String x) {
			this.componentId = componentId;
			this.x = x;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("componentId=" + componentId).append(", ")
					.append("x=" + x)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("componentId")
		public String getComponentId() {
			return componentId;
		}

		@com.fasterxml.jackson.annotation.JsonGetter("x")
		public String getX() {
			return x;
		}

	}

	public static class X2Command implements UiCommand<Boolean> {

		protected String componentId;
		protected String x2;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public X2Command() {
			// default constructor for Jackson
		}

		public X2Command(String componentId, String x2) {
			this.componentId = componentId;
			this.x2 = x2;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("componentId=" + componentId).append(", ")
					.append("x2=" + x2)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("componentId")
		public String getComponentId() {
			return componentId;
		}

		@com.fasterxml.jackson.annotation.JsonGetter("x2")
		public String getX2() {
			return x2;
		}

	}

}