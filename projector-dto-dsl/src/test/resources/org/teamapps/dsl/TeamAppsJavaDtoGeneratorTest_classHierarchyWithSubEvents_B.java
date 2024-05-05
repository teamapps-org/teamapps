package org.teamapps.dsl;

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
public class B extends A implements X, UiObject {



	public B() {
		super();
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public UiObjectType getUiObjectType() {
		return UiObjectType.B;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.toString();
	}






	public static interface BSubEvent extends ASubEvent, XSubEvent {

	}

	public static class BSubEvent implements BSubEvent {

		protected String b;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public BSubEvent() {
			// default constructor for Jackson
		}

		public BSubEvent(String b) {
			this.b = b;
		}

		public UiSubEventType getUiSubEventType() {
			return UiSubEventType.B_B;
		}

		@SuppressWarnings("unchecked")
		public String toString() {
			return new StringBuilder(getClass().getSimpleName()).append(": ")
					.append("b=" + b)
					.toString();
		}

		@com.fasterxml.jackson.annotation.JsonGetter("b")
		public String getB() {
			return b;
		}

	}

}