

package testapp.test.formfield;

import org.teamapps.commons.formatter.FileSizeFormatter;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.field.CheckBox;
import org.teamapps.projector.component.filefield.FileField;
import org.teamapps.projector.component.filefield.FileFieldDisplayType;
import org.teamapps.projector.component.filefield.UploadStartedEventData;
import org.teamapps.projector.component.filefield.UploadedFile;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.composite.CompositeIcon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.resource.InputStreamResource;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.common.TemplateComboBoxEntry;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FileFieldTest extends AbstractFieldTest<FileField<BaseTemplateRecord<?>>> {

	private static final List<TemplateComboBoxEntry> TEMPLATE_COMBO_BOX_ENTRIES = Arrays.asList(
			new TemplateComboBoxEntry("List style file item", BaseTemplates.FILE_ITEM_LIST),
			new TemplateComboBoxEntry("Floating style file item)", BaseTemplates.FILE_ITEM_FLOATING)
	);

	private static final List<TemplateComboBoxEntry> UPLOAD_BUTTON_TEMPLATE_COMBO_BOX_ENTRIES = Arrays.asList(
			new TemplateComboBoxEntry("Form Button", BaseTemplates.BUTTON),
			new TemplateComboBoxEntry("Icon two lines", BaseTemplates.LIST_ITEM_MEDIUM_ICON_TWO_LINES)
	);

	private FileField<BaseTemplateRecord<?>> field;

	private TemplateComboBoxEntry itemTemplate = TEMPLATE_COMBO_BOX_ENTRIES.get(1);
	private boolean showEntriesAsButtonsOnHover = false;
	private FileFieldDisplayType displayType = FileFieldDisplayType.FLOATING;
	private final int maxFiles = 100;
	private TemplateComboBoxEntry uploadButtonTemplate = UPLOAD_BUTTON_TEMPLATE_COMBO_BOX_ENTRIES.get(0);
	private UploadStartedEventData lastUploadStartedEvent;

	public FileFieldTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addConfigurationFields(ResponsiveFormLayout responsiveFormLayout) {
		ConfigurationFieldGenerator<FileField<BaseTemplateRecord<?>>> fieldGenerator = new ConfigurationFieldGenerator<>(getComponent(), getTestContext());

		ComboBox<FileFieldDisplayType> displayTypeComboBox = ComboBox.createForEnum(FileFieldDisplayType.class);
		displayTypeComboBox.onValueChanged.addListener(displayType -> {
			this.displayType = displayType;
			printInvocationToConsole("setDisplayType", displayType);
			field.setDisplayType(displayType);
		});
		displayTypeComboBox.setValue(this.displayType);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Display type", displayTypeComboBox);

		ComboBox<TemplateComboBoxEntry> itemTemplateComboBox = ComboBox.createForList(TEMPLATE_COMBO_BOX_ENTRIES);
		itemTemplateComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		itemTemplateComboBox.onValueChanged.addListener(itemTemplate -> {
			this.itemTemplate = itemTemplate;
			printInvocationToConsole("setItemTemplate", itemTemplate.getTemplate());
			field.setFileItemTemplate(itemTemplate.getTemplate());
		});
		itemTemplateComboBox.setValue(itemTemplate);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Item template", itemTemplateComboBox);

		CheckBox showEntriesAsButtonsOnHoverCheckBox = new CheckBox("Show entries as buttons on hover");
		showEntriesAsButtonsOnHoverCheckBox.onValueChanged.addListener(showEntriesAsButtonsOnHover -> {
			this.showEntriesAsButtonsOnHover = showEntriesAsButtonsOnHover;
			printInvocationToConsole("setShowEntriesAsButtonsOnHover", showEntriesAsButtonsOnHover);
			field.setShowEntriesAsButtonsOnHover(showEntriesAsButtonsOnHover);
		});
		showEntriesAsButtonsOnHoverCheckBox.setValue(showEntriesAsButtonsOnHover);
		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Show entries as buttons on hover", showEntriesAsButtonsOnHoverCheckBox);

		ComboBox<TemplateComboBoxEntry> uploadButtonTemplateComboBox = ComboBox.createForList(UPLOAD_BUTTON_TEMPLATE_COMBO_BOX_ENTRIES);
		uploadButtonTemplateComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		uploadButtonTemplateComboBox.onValueChanged.addListener(template -> {
			this.uploadButtonTemplate = template;
			field.setUploadButtonTemplate(template.getTemplate());
		});
		uploadButtonTemplateComboBox.setValue(uploadButtonTemplate);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Upload button template", uploadButtonTemplateComboBox);

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Max file size", fieldGenerator.createNumberField("maxBytesPerFile", 0, 100, 10_000_000_000L, false));

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Max number of files", fieldGenerator.createNumberField("maxFiles", 0, 1, 100, false));

		Button cancelUploadsButton = Button.create("cancelUploads()");
		cancelUploadsButton.onClick.addListener(() -> getComponent().cancelUploads());
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Cancel uploads", cancelUploadsButton);

		Button setFieldValue = Button.create("setValue(cancelUploads=true)");
		setFieldValue.onClick.addListener(() -> resetFieldValue(true));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Reset field value", setFieldValue);

		Button setFieldValueNoCancel = Button.create("setValue(cancelUploads=false)");
		setFieldValueNoCancel.onClick.addListener(() -> resetFieldValue(false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Reset field value", setFieldValueNoCancel);

		Button cancelLastUploadButton = Button.create("Cancel last upload");
		cancelLastUploadButton.onClick.addListener(() -> lastUploadStartedEvent.cancelUpload());
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Cancel last upload", cancelLastUploadButton);

//		TextField fileTooLargeMessageTextField = new TextField("fileTooLargeMessage");
//		fileTooLargeMessageTextField.onValueChanged.addListener(fileTooLargeMessage -> {
//			this.fileTooLargeMessage = fileTooLargeMessage;
//			field.setFileTooLargeMessage(Caption.staticCaption(fileTooLargeMessage));
//		});
//		fileTooLargeMessageTextField.setValue(fileTooLargeMessage);
//		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, new StaticCaption("\"File too large\" message"), fileTooLargeMessageTextField);
//
//		TextField uploadErrorMessageTextField = new TextField("uploadErrorMessage");
//		uploadErrorMessageTextField.onValueChanged.addListener(uploadErrorMessage -> {
//			this.uploadErrorMessage = uploadErrorMessage;
//			field.setUploadErrorMessage(Caption.staticCaption(uploadErrorMessage));
//		});
//		uploadErrorMessageTextField.setValue(uploadErrorMessage);
//		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, new StaticCaption("\"Upload error\" message"), uploadErrorMessageTextField);
	}

	@Override
	protected FileField<BaseTemplateRecord<?>> createField() {
		field = new FileField<>(file -> new BaseTemplateRecord<>(MaterialIcon.HELP, file.getName(), FileSizeFormatter.humanReadableByteCount(file.getSizeInBytes(), true, 1), file));
		field.setFileItemTemplate(itemTemplate.getTemplate());
		field.setMaxBytesPerFile(5_000_000);
		field.setUploadButtonTemplate(uploadButtonTemplate.getTemplate());
		field.setUploadButtonData(new BaseTemplateRecord(MaterialIcon.HELP, "Upload"));
//		field.setFileTooLargeMessage(new StaticCaption(fileTooLargeMessage));
//		field.setUploadErrorMessage(new StaticCaption(uploadErrorMessage));
		field.setShowEntriesAsButtonsOnHover(showEntriesAsButtonsOnHover);
		field.setDisplayType(displayType);
		field.setMaxFiles(maxFiles);

		field.onFileItemClicked.addListener(fileRecord -> {
			if (fileRecord.getPayload() instanceof UploadedFile) {
				UploadedFile uploadedFile = (UploadedFile) fileRecord.getPayload();
				getSessionContext().download(getSessionContext().createFileLink(uploadedFile.getAsFile()), fileRecord.getCaption());
			} else {
				try {
					String resourceName = "/static-resources/" + fileRecord.getCaption();
					int size = FileFieldTest.class.getResource(resourceName).openConnection().getContentLength();
					getSessionContext().download(getSessionContext().createResourceLink(new InputStreamResource(() -> FileFieldTest.class.getResourceAsStream(resourceName), size)), fileRecord.getCaption());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		resetFieldValue(false);

		field.onUploadStarted.addListener(uploadStartedEventData -> this.lastUploadStartedEvent = uploadStartedEventData);

		return field;
	}

	private void resetFieldValue(boolean cancelUploads) {
		field.setValue(Arrays.asList(
				new BaseTemplateRecord<>(MaterialIcon.HELP, "sample-document.docx", FileSizeFormatter.humanReadableByteCount(73_833, true, 1)),
				new BaseTemplateRecord<>(MaterialIcon.HELP, "test.mv.binary", FileSizeFormatter.humanReadableByteCount(16_384, true, 1))
		), cancelUploads);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/FileField.html";
	}

	public static class FileRecord {

		private String name;
		private String mimeType;
		private int sizeInBytes;
		private String uploadUuid;

		public FileRecord() {
		}

		public FileRecord(String name, String mimeType, int sizeInBytes, String uploadUuid) {
			this.name = name;
			this.mimeType = mimeType;
			this.sizeInBytes = sizeInBytes;
			this.uploadUuid = uploadUuid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMimeType() {
			return mimeType;
		}

		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}

		public int getSizeInBytes() {
			return sizeInBytes;
		}

		public void setSizeInBytes(int sizeInBytes) {
			this.sizeInBytes = sizeInBytes;
		}

		public String getUploadUuid() {
			return uploadUuid;
		}

		public void setUploadUuid(String uploadUuid) {
			this.uploadUuid = uploadUuid;
		}

		public Icon getIcon() {
			return MaterialIcon.HELP;
		}

		public String getSizeString() {
			return FileSizeFormatter.humanReadableByteCount(sizeInBytes, true, 1);
		}
	}

}
