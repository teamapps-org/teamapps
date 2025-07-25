

package testapp.test.table;

import org.teamapps.projector.component.treecomponents.money.value.CurrencyValue;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TableTestRecord {

	private int id;
	private String textField;
	private Number numberField;
	private String multiLineTextField;
	private String passwordField;
	private String richTextEditor;
	private String displayField;
	private CurrencyValue currencyField;
	private List<BaseTemplateRecord> fileField;
	private boolean checkBox;
	private BaseTemplateRecord comboBox;
	private BaseTemplateRecord templateField;
	private List<BaseTemplateRecord> tagComboBox;
	private LocalDate localDateField;
	private LocalTime localTimeField;
	private LocalDateTime localDateTimeField;
	private Instant instantDateField;
	private Instant instantTimeField;
	private Instant instantDateTimeField;
	private String toggledColumn;

	public TableTestRecord() {
	}

	public TableTestRecord(
			int id,
			String textField,
			Number numberField,
			String multiLineTextField,
			String passwordField,
			String richTextEditor,
			String displayField,
			CurrencyValue currencyField,
			List<BaseTemplateRecord> fileField,
			boolean checkBox,
			BaseTemplateRecord comboBox,
			BaseTemplateRecord templateField,
			List<BaseTemplateRecord> tagComboBox,
			LocalDate localDateField,
			LocalTime localTimeField,
			LocalDateTime localDateTimeField,
			Instant instantDateField,
			Instant instantTimeField,
			Instant instantDateTimeField,
			String toggledColumn
	) {
		this.id = id;
		this.textField = textField;
		this.numberField = numberField;
		this.multiLineTextField = multiLineTextField;
		this.passwordField = passwordField;
		this.richTextEditor = richTextEditor;
		this.displayField = displayField;
		this.currencyField = currencyField;
		this.fileField = fileField;
		this.checkBox = checkBox;
		this.comboBox = comboBox;
		this.templateField = templateField;
		this.tagComboBox = tagComboBox;
		this.localDateField = localDateField;
		this.localTimeField = localTimeField;
		this.localDateTimeField = localDateTimeField;
		this.instantDateField = instantDateField;
		this.instantTimeField = instantTimeField;
		this.instantDateTimeField = instantDateTimeField;
		this.toggledColumn = toggledColumn;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTextField() {
		return textField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}

	public Number getNumberField() {
		return numberField;
	}

	public void setNumberField(Number numberField) {
		this.numberField = numberField;
	}

	public String getMultiLineTextField() {
		return multiLineTextField;
	}

	public void setMultiLineTextField(String multiLineTextField) {
		this.multiLineTextField = multiLineTextField;
	}

	public String getPasswordField() {
		return passwordField;
	}

	public void setPasswordField(String passwordField) {
		this.passwordField = passwordField;
	}

	public String getRichTextEditor() {
		return richTextEditor;
	}

	public void setRichTextEditor(String richTextEditor) {
		this.richTextEditor = richTextEditor;
	}

	public String getDisplayField() {
		return displayField;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	public CurrencyValue getCurrencyField() {
		return currencyField;
	}

	public void setCurrencyField(CurrencyValue currencyField) {
		this.currencyField = currencyField;
	}

	public List<BaseTemplateRecord> getFileField() {
		return fileField;
	}

	public void setFileField(List<BaseTemplateRecord> fileField) {
		this.fileField = fileField;
	}

	public boolean isCheckBox() {
		return checkBox;
	}

	public void setCheckBox(boolean checkBox) {
		this.checkBox = checkBox;
	}

	public BaseTemplateRecord getComboBox() {
		return comboBox;
	}

	public void setComboBox(BaseTemplateRecord comboBox) {
		this.comboBox = comboBox;
	}

	public List<BaseTemplateRecord> getTagComboBox() {
		return tagComboBox;
	}

	public void setTagComboBox(List<BaseTemplateRecord> tagComboBox) {
		this.tagComboBox = tagComboBox;
	}

	public LocalDate getLocalDateField() {
		return localDateField;
	}

	public void setLocalDateField(LocalDate localDateField) {
		this.localDateField = localDateField;
	}

	public LocalTime getLocalTimeField() {
		return localTimeField;
	}

	public void setLocalTimeField(LocalTime localTimeField) {
		this.localTimeField = localTimeField;
	}

	public LocalDateTime getLocalDateTimeField() {
		return localDateTimeField;
	}

	public void setLocalDateTimeField(LocalDateTime localDateTimeField) {
		this.localDateTimeField = localDateTimeField;
	}

	public Instant getInstantDateField() {
		return instantDateField;
	}

	public Instant getInstantTimeField() {
		return instantTimeField;
	}

	public Instant getInstantDateTimeField() {
		return instantDateTimeField;
	}

	public String getToggledColumn() {
		return toggledColumn;
	}

	public void setToggledColumn(String toggledColumn) {
		this.toggledColumn = toggledColumn;
	}

	public BaseTemplateRecord getTemplateField() {
		return templateField;
	}

	public void setTemplateField(BaseTemplateRecord templateField) {
		this.templateField = templateField;
	}

	@Override
	public String toString() {
		return "TableTestRecord{id=" + id + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TableTestRecord that = (TableTestRecord) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
