package hr.fer.ztel.myFridge.client.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class containing static methods used for user input validation.
 * @author suncana
 *
 */
public class InputValidation {

	//Regex patterns for input validation
	private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,15}$";
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PASSWORD_PATTERN = "^.{6,20}$";

	/**
	 * Method that validates username input.
	 * @param username username to be validated
	 * @return boolean
	 */
	public static boolean isValidUserName(String username) {

		if (username == null || username.isEmpty()) {
			return false;
		}
		Pattern pattern = Pattern.compile(USERNAME_PATTERN);
		Matcher matcher = pattern.matcher(username);
		return matcher.matches();
	}

	/**
	 * Method that validates password input.
	 * @param password password to be validated
	 * @return boolean
	 */
	public static boolean isValidPassword(String password) {
		if (password == null || password.isEmpty()) {
			return false;
		}
		Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
	
	/**
	 * Method that validates retyped password input.
	 * @param password retyped password to be validated
	 * @return boolean
	 */
	public static boolean areEqualPasswords(String password, String password2) {
		if (password2 == null || password2.isEmpty() || !password.equals(password2)) {
			return false;
		}
		return true;
	}

	/**
	 * Method that validates email input.
	 * @param email email to be validated
	 * @return boolean
	 */
	public static boolean isValidEmail(String email) {
		if (email == null || email.isEmpty()) {
			return false;
		}
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
