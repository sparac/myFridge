package hr.fer.ztel.myFridge.client.util;

/**
 * Class that contains most commonly used constants.
 * @author suncana
 *
 */
public class Constants {

	//various request URIs
	public static final String CREATE_USER_URI = "http://solaris.bot.nu:8081/MyFridgeServer/user/register";
	public static final String CHECK_CREDENTIALS_URI = "http://solaris.bot.nu:8081/MyFridgeServer/user";
	public static final String BARCODE_SEARCH_URI = "http://solaris.bot.nu:8081/MyFridgeServer/food/";
	public static final String RETRIEVE_ALL_USER_FOOD_URI = "http://solaris.bot.nu:8081/MyFridgeServer/userFood/all";
	public static final String MODIFY_USER_URI = "http://solaris.bot.nu:8081/MyFridgeServer/user";
	public static final String DELETE_USER_URI = "http://solaris.bot.nu:8081/MyFridgeServer/user";
	public static final String ADD_USER_FOOD_URI = "http://solaris.bot.nu:8081/MyFridgeServer/userFood/";
	public static final String MODIFY_USER_FOOD_URI = "http://solaris.bot.nu:8081/MyFridgeServer/userFood/";
	public static final String DELETE_USER_FOOD_URI = "http://solaris.bot.nu:8081/MyFridgeServer/userFood/";
	public static final String DELETE_ALL_USER_FOOD_URI = "http://solaris.bot.nu:8081/MyFridgeServer/userFood/all";

	public static final String LOG_TAG = "MyFridgeClient";
	public static final String FOOD_LIST_FILENAME = "food_list";

	//keys for accessing user preferences from file
	public static final String CACHED_CREDENTIALS_ENABLED_KEY = "cacheCredentials";
	public static final String CACHED_USERNAME_KEY = "username";
	public static final String CACHED_PASSWORD_KEY = "password";
}
