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
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "_type", defaultImpl = A.class)
public abstract class A implements UiObject {



	public A() {
	}


	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.toString();
	}





	public static class ACommand implements UiCommand<Void> {

		protected String b;

		/**
		 * @deprecated Only for Jackson deserialization. Use the other constructor instead.
		 */
		@Deprecated
		public ACommand() {
			// default constructor for Jackson
		}

		public ACommand(String b) {
			this.b = b;
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

		public String getComponentId() {
			return null;
		}

	}

}