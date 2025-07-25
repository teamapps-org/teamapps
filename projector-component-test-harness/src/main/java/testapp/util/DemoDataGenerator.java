

package testapp.util;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.projector.common.format.RgbaColor;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.material.MaterialIcon;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DemoDataGenerator {

	public static final Random random = new Random();

	private static final List<String> FIRST_NAMES = readLines(DemoDataGenerator.class.getResourceAsStream("/org/teamapps/ux/testapp/demodata/firstNames.txt"));
	private static final List<String> LAST_NAMES = readLines(DemoDataGenerator.class.getResourceAsStream("/org/teamapps/ux/testapp/demodata/lastNames.txt"));
	private static final List<String> WORDS = readLines(DemoDataGenerator.class.getResourceAsStream("/org/teamapps/ux/testapp/demodata/test-text.txt"));
	public static final List<RgbaColor> FOREGROUND_COLORS = Arrays.asList(
			RgbaColor.MATERIAL_GREEN_500,
			RgbaColor.MATERIAL_RED_700,
			RgbaColor.MATERIAL_BLUE_600,
			RgbaColor.MATERIAL_YELLOW_600,
			RgbaColor.MATERIAL_PURPLE_500,
			RgbaColor.MATERIAL_BROWN_500,
			RgbaColor.MATERIAL_PINK_500,
			RgbaColor.MATERIAL_DEEP_ORANGE_500
	);
	public static final List<Icon> FOOD_ICONS = Arrays.asList(MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP,
			MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP);
	public static final List<Icon> TOOL_ICONS = Arrays.asList(MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP,
			MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP,
			MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP, MaterialIcon.HELP);

	private static List<String> readLines(InputStream in) {
		Scanner s = new Scanner(in);
		List<String> list = new ArrayList<>();
		while (s.hasNext()) {
			list.add(s.next());
		}
		s.close();
		return list;
	}

	public static String randomFirstName() {
		return randomOf(FIRST_NAMES);
	}

	public static String randomLastName() {
		return randomOf(LAST_NAMES);
	}

	public static String randomFullName() {
		return randomFirstName() + " " + randomLastName();
	}

	public static String randomWord(boolean capitalize) {
		String word = randomOf(WORDS);
		if (capitalize) {
			word = StringUtils.capitalize(word);
		}
		return word;
	}

	public static <T> T randomOf(Collection<T> collection) {
		List<T> list = new ArrayList<>(collection);
		return list.get(random.nextInt(list.size()));
	}

	public static <T extends Enum> T randomOf(Class<T> enumClass) {
		return randomOf(Arrays.asList(enumClass.getEnumConstants()));
	}

	public static String randomWords(int amount, boolean capitalizeFirstWord) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < amount; i++) {
			sb.append(randomWord(false));
			if (i < amount - 1) {
				sb.append(" ");
			}
		}
		return capitalizeFirstWord ? StringUtils.capitalize(sb.toString()) : sb.toString();
	}

	public static String randomDateTimeString(String dateTimeFormat) {
		LocalDateTime now = LocalDateTime.now();
		now.plusMinutes(random.nextInt(100000) - 50000);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
		return formatter.format(now);
	}

	public static boolean randomBoolean() {
		return random.nextBoolean();
	}

	public static boolean randomBoolean(double trueLikelihood) {
		return random.nextDouble() <= trueLikelihood;
	}

	public static int randomInt() {
		return random.nextInt();
	}

	public static float randomLongitude() {
		return (random.nextFloat() * 360) - 180;
	}

	public static float randomLatitude() {
		return (random.nextFloat() * 180) - 90;
	}

	public static float randomLongitude(float arroundLongitude) {
		return arroundLongitude + (random.nextFloat() * 20) - 10;
	}

	public static float randomLatitude(float arroundLatitude) {
		return arroundLatitude + (random.nextFloat() * 10) - 5;
	}

	public static int randomInt(int bound) {
		return random.nextInt(bound);
	}

	public static RgbaColor randomColor() {
		return randomOf(FOREGROUND_COLORS);
	}

	public static Icon randomIcon() {
		return randomOf(Arrays.asList(
				MaterialIcon.ADD,
				MaterialIcon.SAVE,
				MaterialIcon.DELETE,
				MaterialIcon.FILTER,
				MaterialIcon.MESSAGE,
				MaterialIcon.SORT,
				MaterialIcon.CLOSE,
				MaterialIcon.CLOUD_DOWNLOAD,
				MaterialIcon.VOLUME_DOWN,
				MaterialIcon.SETTINGS,
				MaterialIcon.ATTACHMENT,
				MaterialIcon.DATE_RANGE,
				MaterialIcon.CALL
		));
	}

	public static Icon randomToolIcon() {
		return randomOf(TOOL_ICONS);
	}

	public static Icon randomFoodIcon() {
		return randomOf(FOOD_ICONS);
	}

	public static String randomUserImageUrl() {
		return "https://avatars1.githubusercontent.com/u/" + (10000 + randomInt(1000)) + "?v=3&s=30";
	}
}
