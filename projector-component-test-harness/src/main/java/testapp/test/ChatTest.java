

package testapp.test;

import net.coobird.thumbnailator.Thumbnails;
import org.teamapps.commons.util.ExceptionUtil;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.chat.*;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.flexcontainer.FlexContainer;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.treecomponents.itemview.SimpleItemGroup;
import org.teamapps.projector.component.treecomponents.itemview.SimpleItemView;
import org.teamapps.projector.format.FlexDirection;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.resource.FileResource;
import org.teamapps.projector.resource.Resource;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.util.DemoDataGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class ChatTest extends AbstractComponentTest {

	private static final InMemoryChatDisplayModel model = createModel();

	private final Set<String> imageMimeTypes = new HashSet<>(Arrays.asList(
			"image/bmp",
			"image/gif",
			"image/heic",
			"image/heic-sequence",
			"image/heif",
			"image/heif-sequence",
			"image/ief",
			"image/jls",
			"image/jp2",
			"image/jpeg",
			"image/jpm",
			"image/jpx",
			"image/ktx",
			"image/png",
			"image/sgi",
			"image/svg+xml",
			"image/tiff",
			"image/webp",
			"image/wmf"
	));

	private final String userNickName = "TestUser";
	private final Resource ownUserImage = createUserImageResource();

	private static Resource createUserImageResource() {
		return ExceptionUtil.runWithSoftenedExceptions(() -> {
			BufferedImage bufferedImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = bufferedImage.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setColor(new Color(ThreadLocalRandom.current().nextInt(256), ThreadLocalRandom.current().nextInt(256), ThreadLocalRandom.current().nextInt(256)));
			graphics.fillRect(0, 0, 64, 64);
			graphics.setColor(Color.WHITE);
			graphics.setFont(new Font("Sans", Font.PLAIN, 40));
			graphics.drawString("A", 18, 45);
			File file = File.createTempFile("avatar", ".png");
			ImageIO.write(bufferedImage, "png", file);
			return new FileResource(file);
		});
	}

	public ChatTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);

		Button deleteButton = Button.create("Delete and refill with some new messages...");
		deleteButton.onClick.addListener(() -> {
			model.addMessage(sessionContext -> sessionContext.createResourceLink(createUserImageResource()), "Some User", DemoDataGenerator.randomWords(30, true) + ".");
			getSessionContext().runWithContext(() -> {
				throw new IllegalArgumentException();
			});
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Reset messages", deleteButton);
	}

	@Override
	protected Component createComponent() {
		ChatDisplay chatDisplay = new ChatDisplay(model);
		chatDisplay.setCssStyle("flex", "1 1 1px");
		chatDisplay.setContextMenuProvider(chatMessage -> {
			SimpleItemView<Void> itemView = new SimpleItemView<>();
			SimpleItemGroup<Void> group = itemView.addSingleColumnGroup(MaterialIcon.DELETE, "Delete");
			group.addItem(MaterialIcon.DELETE, "Delete", "Delete this message")
					.onClick.addListener(() -> {
						model.deleteMessage(chatMessage.getId());
						chatDisplay.closeContextMenu();
					});
			group.addItem(MaterialIcon.DELETE, "Delete 2", "Update the message...")
					.onClick.addListener(() -> {
						SimpleChatMessage changedMessage = new SimpleChatMessage(chatMessage.getId(), chatMessage.getUserImage(), chatMessage.getUserNickname(), null, null, null, true);
						model.updateMessage(changedMessage);
						chatDisplay.closeContextMenu();
					});
			return itemView;
		});
		ChatInput chatInput = new ChatInput();
		chatInput.setCssStyle("flex", "0 0 auto");
		chatInput.onMessageSent.addListener(newChatMessageData -> {
			ArrayList<ChatPhoto> photos = new ArrayList<>();
			ArrayList<ChatFile> files = new ArrayList<>();
			newChatMessageData.getFiles().forEach(uploadedFile -> {
				File file = getSessionContext().getUploadedFileByUuid(uploadedFile.getUploadedFileUuid());
				boolean isImage = Stream.of(".jpg", ".jpeg", ".bmp", ".png", ".gif").anyMatch(extension -> uploadedFile.getFileName().endsWith(extension));
				if (isImage) {
					photos.add(new SimpleChatPhoto(uploadedFile.getFileName(), sessionContext -> sessionContext.createFileLink(createThumbnail(file)), sessionContext -> sessionContext.createFileLink(file)));
				} else {
					files.add(new SimpleChatFile(uploadedFile.getFileName(), MaterialIcon.HELP, null, sessionContext -> sessionContext.createFileLink(file), file.length()));
				}
			});
			model.addMessage(
					sessionContext -> sessionContext.createResourceLink(ownUserImage),
					userNickName,
					newChatMessageData.getText(),
					photos,
					files,
					false
			);
		});

		FlexContainer flexContainer = new FlexContainer();
		flexContainer.setFlexDirection(FlexDirection.COLUMN);
		flexContainer.addComponent(chatDisplay);
		flexContainer.addComponent(chatInput);

		return flexContainer;
	}

	private static InMemoryChatDisplayModel createModel() {
		InMemoryChatDisplayModel model = new InMemoryChatDisplayModel();
		for (int i = 0; i < 1000; i++) {
			Resource userImageResource = createUserImageResource();
			model.addMessage(sessionContext -> sessionContext.createResourceLink(userImageResource), "Some User " + i, DemoDataGenerator.randomWords(30, true) + "." + i);
		}
		Resource userImageResource = createUserImageResource();
		model.addMessage(sessionContext -> sessionContext.createResourceLink(userImageResource), "Another User", DemoDataGenerator.randomWords(30, true) + ".");
		return model;
	}

	private File createThumbnail(File file) {
		File tempFile = createTempFile();
		try {
			Thumbnails.of(file)
					.size(100, 100)
					.toFile(tempFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return tempFile;
	}

	private File createTempFile() {
		try {
			return File.createTempFile("chat-thumbnail", "");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getDocsHtmlResourceName() {
		return null;
	}
}
