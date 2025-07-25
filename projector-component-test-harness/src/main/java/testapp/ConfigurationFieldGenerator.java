

package testapp;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.component.core.NumberFieldSliderMode;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.core.field.ColorPicker;
import org.teamapps.projector.component.core.field.NumberField;
import org.teamapps.projector.component.core.field.TextField;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.treecomponents.combobox.TagComboBox;
import org.teamapps.projector.component.treecomponents.datetime.LocalDateField;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.common.IconComboBoxEntry;
import testapp.util.Coercor;
import testapp.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationFieldGenerator<C> {

	public static final List<IconComboBoxEntry> FOOD_ICON_ENTRIES = Stream.of(
					MaterialIcon.ROTATION_3D,
					MaterialIcon.AC_UNIT,
					MaterialIcon.ACCESS_ALARM,
					MaterialIcon.ACCESS_ALARMS,
					MaterialIcon.ACCESS_TIME,
					MaterialIcon.ACCESSIBILITY,
					MaterialIcon.ACCESSIBLE,
					MaterialIcon.ACCOUNT_BALANCE,
					MaterialIcon.ACCOUNT_BALANCE_WALLET,
					MaterialIcon.ACCOUNT_BOX,
					MaterialIcon.ACCOUNT_CIRCLE
			)
			.map(icon -> new IconComboBoxEntry(icon, StringUtils.capitalize(icon.getIconName().toUpperCase())))
			.collect(Collectors.toList());
	public static final List<IconComboBoxEntry> TOOL_ICON_ENTRIES = Stream.of(
					MaterialIcon.ADJUST,
					MaterialIcon.ALARM_ADD,
					MaterialIcon.APPS,
					MaterialIcon.ARROW_FORWARD,
					MaterialIcon.ASSIGNMENT_RETURN,
					MaterialIcon.BATTERY_UNKNOWN,
					MaterialIcon.BORDER_LEFT,
					MaterialIcon.BRIGHTNESS_6,
					MaterialIcon.CALL_MADE,
					MaterialIcon.CAMERA_FRONT
			)
			.map(icon -> new IconComboBoxEntry(icon, StringUtils.capitalize(icon.getIconName().toUpperCase())))
			.collect(Collectors.toList());
	private final ComponentTestContext testContext;
	private C component;

	public ConfigurationFieldGenerator(C component, ComponentTestContext testContext) {
		this.component = component;
		this.testContext = testContext;
	}

	public CheckBox createCheckBox(String propertyName) {
		Class<?> type = ReflectionUtil.findGetter(component.getClass(), propertyName).getReturnType();
		CheckBox checkBox = new CheckBox(null);
		checkBox.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value);
			ReflectionUtil.setProperty(component, propertyName, Coercor.coerce(value, type));
		});
		checkBox.setValue(ReflectionUtil.getPropertyValue(component, propertyName));
		return checkBox;
	}

	public NumberField createNumberField(String propertyName, int precision, double minValue, double maxValue, boolean clearable) {
		Class<?> type = getPropertyType(propertyName);
		NumberField numberField = new NumberField(precision)
				.setSliderMode(NumberFieldSliderMode.VISIBLE)
				.setMinValue(minValue)
				.setMaxValue(maxValue)
				.setSliderStep(Math.pow(10, -precision));
		numberField.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value);
			ReflectionUtil.setProperty(component, propertyName, Coercor.coerce(value, type));
		});
		if (ReflectionUtil.findGetter(component.getClass(), propertyName) != null) {
			numberField.setValue(ReflectionUtil.getPropertyValue(component, propertyName));
		}
		numberField.setClearButtonEnabled(clearable);
		return numberField;
	}

	public NumberField createNumberField(String printedSetterName, Supplier<Number> getter, Consumer<Number> setter, int precision, double minValue, double maxValue, boolean clearable) {
		NumberField numberField = new NumberField(precision)
				.setSliderMode(NumberFieldSliderMode.VISIBLE)
				.setMinValue(minValue)
				.setMaxValue(maxValue);
		numberField.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(printedSetterName, value);
			setter.accept(value);
		});
		if (getter != null) {
			numberField.setValue(getter.get());
		}
		numberField.setClearButtonEnabled(clearable);
		return numberField;
	}

	public TextField createTextField(String propertyName) {
		return createTextField(propertyName, s -> s, o -> o != null ? o.toString() : null);
	}

	public TextField createTextField(String propertyName, Function<String, Object> stringToObject, Function<Object, String> objectToString) {
		TextField textField = new TextField();
		textField.setClearButtonEnabled(true);
		textField.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value);
			ReflectionUtil.setProperty(component, propertyName, stringToObject.apply(value));
		});
		textField.setValue(objectToString.apply(ReflectionUtil.getPropertyValue(component, propertyName)));
		return textField;
	}

	public <E> ComboBox<E> createComboBoxForEnum(String propertyName) {
		Class<?> type = ReflectionUtil.findGetter(component.getClass(), propertyName).getReturnType();
		return (ComboBox<E>) createComboBoxForEnum(propertyName, (Class<? extends Enum>) type);
	}

	public <E extends Enum<E>> ComboBox<E> createComboBoxForEnum(String propertyName, Class<E> enumClass) {
		ComboBox<E> comboBox = ComboBox.createForEnum(enumClass);
		comboBox.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value);
			ReflectionUtil.setProperty(component, propertyName, Coercor.coerce(value, enumClass));
		});
		comboBox.setValue(ReflectionUtil.getPropertyValue(component, propertyName));
		return comboBox;
	}

	public <E> ComboBox<E> createComboBoxForList(String propertyName, List<E> values) {
		return createComboBoxForList(propertyName, values, false);
	}

	public <E> ComboBox<E> createComboBoxForList(String propertyName, List<E> values, boolean clearable) {
		return createComboBoxForList(propertyName, values,
				(component) -> ReflectionUtil.getPropertyValue(component, propertyName),
				(component, value) -> ReflectionUtil.setProperty(component, propertyName, value),
				clearable
		);
	}

	public <E> ComboBox<E> createComboBoxForList(String propertyName, List<E> values, Function<C, E> getterImpl, BiConsumer<C, E> setterImpl) {
		return createComboBoxForList(propertyName, values, getterImpl, setterImpl, false);
	}

	public <E> ComboBox<E> createComboBoxForList(String propertyName, List<E> values, Function<C, E> getterImpl, BiConsumer<C, E> setterImpl, boolean clearable) {
		ComboBox<E> comboBox = ComboBox.createForList(values);
		comboBox.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value);
			setterImpl.accept(component, value);
		});
		comboBox.setValue(getterImpl.apply(component));
		comboBox.setClearButtonEnabled(clearable);
		return comboBox;
	}

	public <E extends Enum<E>> TagComboBox<E> createTagComboBoxForEnum(String propertyName, Class<E> type) {
		TagComboBox<E> tagComboBox = TagComboBox.createForEnum(type);
		tagComboBox.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value);
			ReflectionUtil.setProperty(component, propertyName, Coercor.coerce(value, type));
		});
		tagComboBox.setValue(ReflectionUtil.getPropertyValue(component, propertyName));
		return tagComboBox;
	}

	public <E> TagComboBox<?> createTagComboBoxForList(String propertyName, List<E> values) {
		TagComboBox<E> tagComboBox = TagComboBox.createForList(values);
		tagComboBox.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value);
			ReflectionUtil.setProperty(component, propertyName, value);
		});
		tagComboBox.setValue(ReflectionUtil.getPropertyValue(component, propertyName));
		return tagComboBox;
	}

	public AbstractField createColorPicker(String propertyName) {
		Class<?> type = getPropertyType(propertyName);
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value);
			ReflectionUtil.setProperty(component, propertyName, Coercor.coerce(value, type));
		});
		if (ReflectionUtil.findGetter(component.getClass(), propertyName) != null) {
			colorPicker.setValue(ReflectionUtil.getPropertyValue(component, propertyName));
		} else {
			colorPicker.setValue(RgbaColor.TRANSPARENT);
		}
		return colorPicker;
	}

	private Class<?> getPropertyType(String propertyName) {
		Method getter = ReflectionUtil.findGetter(component.getClass(), propertyName);
		Method setter = ReflectionUtil.findSetter(component.getClass(), propertyName);
		Class<?> type;
		if (getter != null) {
			type = getter.getReturnType();
		} else if (setter != null) {
			type = setter.getParameterTypes()[0];
		} else {
			throw new IllegalArgumentException("Cannot find getter nor setter for property " + propertyName + " in " + component.getClass());
		}
		return type;
	}

	public ComboBox<IconComboBoxEntry> createComboBoxForIcon(String propertyName) {
		ComboBox<IconComboBoxEntry> comboBox = ComboBox.createForList(FOOD_ICON_ENTRIES);
		comboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		comboBox.setClearButtonEnabled(true);
		comboBox.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value != null ? value.getIcon() : null);
			ReflectionUtil.setProperty(component, propertyName, value != null ? value.getIcon() : null);
		});
		Icon initialPropertyValue = ReflectionUtil.getPropertyValue(component, propertyName);
		comboBox.setValue(initialPropertyValue != null ? new IconComboBoxEntry(initialPropertyValue, "" + initialPropertyValue) : null);
		return comboBox;
	}

	public TagComboBox<IconComboBoxEntry> createTagComboBoxForIcons(String propertyName) {
		TagComboBox<IconComboBoxEntry> comboBox = TagComboBox.createForList(FOOD_ICON_ENTRIES);
		comboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		comboBox.setClearButtonEnabled(true);
		comboBox.onValueChanged.addListener(values -> {
			List<Icon> icons = values != null ? values.stream()
					.map(IconComboBoxEntry::getIcon)
					.collect(Collectors.toList())
					: Collections.emptyList();
			testContext.printInvocationToConsole(setterName(propertyName), icons);
			ReflectionUtil.setProperty(component, propertyName, icons);
		});
		List<Icon> initialPropertyValue = ReflectionUtil.getPropertyValue(component, propertyName);
		comboBox.setValue(initialPropertyValue != null ? initialPropertyValue.stream()
				.map(icon -> new IconComboBoxEntry(icon, "" + icon))
				.collect(Collectors.toList()) : null);
		return comboBox;
	}

	public ComboBox<String> createFreeTextComboBox(String propertyName, String... staticData) {
		ComboBox<String> comboBox = ComboBox.createForList(Arrays.asList(staticData));
		comboBox.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value);
			ReflectionUtil.setProperty(component, propertyName, value);
		});
		String initialPropertyValue = ReflectionUtil.getPropertyValue(component, propertyName);
		comboBox.setValue(initialPropertyValue);
		return comboBox;
	}

	public LocalDateField createLocalDateField(String propertyName, boolean clearable) {
		LocalDateField dateField = new LocalDateField();
		dateField.setClearButtonEnabled(clearable);
		dateField.onValueChanged.addListener(value -> {
			testContext.printInvocationToConsole(setterName(propertyName), value);
			ReflectionUtil.setProperty(component, propertyName, value);
		});
		dateField.setValue(ReflectionUtil.getPropertyValue(component, propertyName));
		return dateField;
	}

	private String setterName(String propertyName) {
		return "set" + StringUtils.capitalize(propertyName);
	}

	public C getComponent() {
		return component;
	}

	public void setComponent(C component) {
		this.component = component;
	}
}
