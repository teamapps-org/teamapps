package org.teamapps.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_type")
@JsonTypeIdResolver(TeamAppsJacksonTypeIdResolver.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class A implements UiObject {



	public A() {
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public UiObjectType getUiObjectType() {
		return UiObjectType.A;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.toString();
	}



	public static class AEvent implements UiEvent {

		@UiComponentId protected String componentId;
		protected String a;
		protected X.XSubEvent sub;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public AEvent() {
			// default constructor for Jackson
		}

		public AEvent(String componentId, String a, X.XSubEvent sub) {
			this.componentId = componentId;
			this.a = a;
			this.sub = sub;
		}

		public UiEventType getUiEventType() {
			return UiEventType.A_A;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("componentId=" + componentId).append(", ")
					.append("a=" + a).append(", ")
					.append(sub != null ? "sub={" + sub.toString() + "}" : "")
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("componentId")
		public String getComponentId() {
			return componentId;
		}

		@com.fasterxml.jackson.annotation.JsonGetter("a")
		public String getA() {
			return a;
		}

		@com.fasterxml.jackson.annotation.JsonGetter("sub")
		public X.XSubEvent getSub() {
			return sub;
		}

	}





}