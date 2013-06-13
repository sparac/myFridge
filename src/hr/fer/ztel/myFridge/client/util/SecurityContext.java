package hr.fer.ztel.myFridge.client.util;

import android.util.Log;
import hr.fer.ztel.myFridge.data.User;

/**
 * Singleton class used for setting and getting User object associated with the user account.
 * @author suncana
 *
 */
public class SecurityContext {

	//a single instance of Security context
	private static SecurityContext instance;

	private User currentUser;

	//private constructor called only from getInstance() method
	private SecurityContext() {
		Log.i(Constants.LOG_TAG, "Security context initialized");
	}

	/**
	 * Method that returns a singleton SecurityContext object.
	 * @return
	 */
	public static SecurityContext getInstance() {

		//instantiating SecurityContext only once
		if (instance == null) {
			instance = new SecurityContext();
		}

		return instance;
	}
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;

		//clearing Security context
		if (currentUser == null) {
			Log.i(Constants.LOG_TAG, "Current user cleared");
			
		} else {
			Log.i(Constants.LOG_TAG, "Current user set");
		}
	}
}
