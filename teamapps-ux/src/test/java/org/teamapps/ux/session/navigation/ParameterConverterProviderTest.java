/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.session.navigation;

import jakarta.ws.rs.ext.ParamConverter;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ParameterConverterProviderTest {

	public enum TestEnum {
		FOO, BAR;
	}

	public static List<TestEnum> listProperty;

	@Test
	public void name() throws NoSuchFieldException {
		Field field = this.getClass().getField("listProperty");
		ParameterConverterProvider provider = new ParameterConverterProvider();
		ParamConverter<List<TestEnum>> converter = (ParamConverter) provider.getConverter(field.getType(), field.getGenericType(), null);
		assertThat(converter.toString(List.of(TestEnum.FOO, TestEnum.BAR))).isEqualTo("FOO,BAR");
		assertThat(converter.fromString("FOO,BAR")).containsExactly(TestEnum.FOO, TestEnum.BAR);
	}
}
