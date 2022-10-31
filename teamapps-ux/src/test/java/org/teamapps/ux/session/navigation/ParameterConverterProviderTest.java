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