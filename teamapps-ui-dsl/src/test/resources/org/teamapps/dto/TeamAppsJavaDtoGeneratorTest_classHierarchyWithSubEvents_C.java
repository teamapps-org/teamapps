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
public class C extends B implements UiObject {



	public C() {
		super();
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public UiObjectType getUiObjectType() {
		return UiObjectType.C;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.toString();
	}






	public static interface CSubEvent extends BSubEvent {

	}

	public static class CSubEvent implements CSubEvent {

		protected String c;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public CSubEvent() {
			// default constructor for Jackson
		}

		public CSubEvent(String c) {
			this.c = c;
		}

		public UiSubEventType getUiSubEventType() {
			return UiSubEventType.C_C;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("c=" + c)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("c")
		public String getC() {
			return c;
		}

	}

}