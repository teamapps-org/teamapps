/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
package org.teamapps.data.extract;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PropertyProvider<RECORD> implements PropertyExtractor<RECORD> {

	private Map<String, EntityProperty<RECORD>> propertyByName = new HashMap<>();
	private Map<String, EntityProperty<RECORD>> propertyByCaption = new HashMap<>();

	private Map<String, ValueExtractor<RECORD>> valueExtractorByPropertyName = new HashMap<>();
	private Map<String, Function<RECORD, Instant>> instantExtractorProvider = new HashMap<>();


	private BeanPropertyExtractor beanPropertyExtractor;

	public PropertyProvider() {
		this(true);
	}

	public PropertyProvider(boolean useBeanPropertyExtractorFallback) {
		if (useBeanPropertyExtractorFallback) {
			beanPropertyExtractor = new BeanPropertyExtractor();
		}
	}

	public void addProperty(String propertyName, String caption, ValueExtractor<RECORD> valueExtractor) {
		EntityProperty property = new EntityProperty(propertyName, caption, valueExtractor);
		propertyByName.put(propertyName, property);
		propertyByCaption.put(caption, property);
	}

	@Override
	public Object getValue(RECORD record, String propertyName) {
		ValueExtractor<RECORD> extractor = getExtractor(record, propertyName);
		if (extractor != null) {
			return extractor.extract(record);
		}
		return null;
	}
	public Instant getInstantValue(RECORD record, String propertyName) {
		Function<RECORD, Instant> instantExtractor = getInstantExtractor(record, propertyName);
		if (instantExtractor != null) {
			return instantExtractor.apply(record);
		}
		return null;
	}

	private ValueExtractor<RECORD> getExtractor(RECORD record, String propertyName) {
		ValueExtractor<RECORD> valueExtractor = valueExtractorByPropertyName.get(propertyName);
		if (valueExtractor != null) {
			return valueExtractor;
		}
		EntityProperty<RECORD> property = propertyByName.get(propertyName);
		if (property != null) {
			valueExtractor = property.getValueExtractor();
		} else if (beanPropertyExtractor != null) {
			valueExtractor = beanPropertyExtractor.getValueExtractor(record.getClass(), propertyName);
		}
		if (valueExtractor != null) {
			valueExtractorByPropertyName.put(propertyName, valueExtractor);
		}
		return valueExtractor;
	}

	private Function<RECORD, Instant> getInstantExtractor(RECORD record, String propertyName) {
		Function<RECORD, Instant> instantExtractorFunction = instantExtractorProvider.get(propertyName);
		if (instantExtractorFunction != null) {
			return instantExtractorFunction;
		}
		ValueExtractor<RECORD> extractor = getExtractor(record, propertyName);
		if (extractor == null) {
			return null;
		}
		if (record.getClass().isAssignableFrom(Instant.class)) {
			instantExtractorFunction = rec -> (Instant) extractor.extract(rec);
		} else if (record.getClass().isAssignableFrom(Long.class)) {
			instantExtractorFunction = rec -> {
				Long value = (Long) extractor.extract(rec);
				return value == null ? null : Instant.ofEpochMilli(value);
			};
		} else if (record.getClass().isAssignableFrom(Integer.class)) {
			instantExtractorFunction = rec -> {
				Integer value = (Integer) extractor.extract(rec);
				return value == null ? null : Instant.ofEpochSecond(value);
			};
		} else if (record.getClass().isAssignableFrom(LocalDateTime.class)) {
			instantExtractorFunction = rec -> {
				LocalDateTime value = (LocalDateTime) extractor.extract(rec);
				return value == null ? null : value.toInstant(ZoneOffset.UTC);
			};
		}
		if (instantExtractorFunction != null) {
			instantExtractorProvider.put(propertyName,instantExtractorFunction);
		}
		return instantExtractorFunction;
	}

	public List<String> getPropertyNamesByCaptionQuery(String query) {
		if (StringUtils.isBlank(query)) {
			return propertyByName.values().stream()
					.map(property -> property.getName())
					.collect(Collectors.toList());
		}
		return propertyByName.values().stream()
				.filter(property -> property.getCaption() != null && property.getCaption().toLowerCase().startsWith(query.toLowerCase()))
				.map(property -> property.getName())
				.collect(Collectors.toList());
	}

	public static class EntityProperty<RECORD> {
		private String name;
		private String caption;
		private ValueExtractor<RECORD> valueExtractor;

		public EntityProperty(String name, String caption, ValueExtractor<RECORD> valueExtractor) {
			this.name = name;
			this.caption = caption;
			this.valueExtractor = valueExtractor;
		}

		public String getName() {
			return name;
		}

		public String getCaption() {
			return caption;
		}

		public ValueExtractor<RECORD> getValueExtractor() {
			return valueExtractor;
		}
	}
}
