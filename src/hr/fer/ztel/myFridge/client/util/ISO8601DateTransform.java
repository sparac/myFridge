package hr.fer.ztel.myFridge.client.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.simpleframework.xml.transform.Transform;

public class ISO8601DateTransform implements Transform<Date> {

	private static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	private static final String TIMEZONE_SEPARATOR_REMOVE_PATTERN = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}):(\\d{2})$";
	private static final String TIMEZONE_SEPARATOR_ADD_PATTERN = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{2})(\\d{2})$";

	@Override
	public Date read(String input) throws Exception {

		// SimpleDateFormat expects timezone in HHmm format - ISO8601 has HH:mm
		input = input.replaceAll(TIMEZONE_SEPARATOR_REMOVE_PATTERN, "$1$2");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO8601_FORMAT, Locale.ENGLISH);
		Date output = simpleDateFormat.parse(input);

		return output;
	}

	@Override
	public String write(Date input) throws Exception {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO8601_FORMAT, Locale.ENGLISH);
		String output = simpleDateFormat.format(input);

		// SimpleDateFormat produces timezone in HHmm format - ISO8601 has HH:mm
		output = output.replaceAll(TIMEZONE_SEPARATOR_ADD_PATTERN, "$1$2");

		return output;
	}
}
