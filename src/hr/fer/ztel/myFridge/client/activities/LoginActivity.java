package hr.fer.ztel.myFridge.client.activities;

import hr.fer.ztel.myFridge.R;
import hr.fer.ztel.myFridge.client.activities.tasks.LoginTask;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.InputValidation;
import hr.fer.ztel.myFridge.client.util.SecurityContext;
import hr.fer.ztel.myFridge.data.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Activity class that handles user login.
 * 
 * @author suncana
 */
public class LoginActivity extends Activity {

	private static final int CREATE_ACCOUNT_REQUEST = 0;
	private static final int MAIN_MENU_REQUEST = 1;

	private SharedPreferences settings = null;
	private EditText txtUsername;
	private EditText txtPassword;
	private CheckBox chkBoxCacheCredentials;

	// flag used for displaying user credentials after account creation
	private boolean keepDisplayedCredentials;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(Constants.LOG_TAG, "Created");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		chkBoxCacheCredentials = (CheckBox) findViewById(R.id.chkBxCredentialsLogin);

		this.settings = getSharedPreferences("MyPreferences", 0);
		this.keepDisplayedCredentials = false;

		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(Constants.LOG_TAG, "Login button click");
				loginAsync();
			}
		});

		Button btnCreateAccount = (Button) findViewById(R.id.btnRegister);
		btnCreateAccount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createAccountAsync();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (keepDisplayedCredentials) {
			// turn off flag
			keepDisplayedCredentials = false;
		} else {
			// if the account was not created by the returning activity and the
			// cache credentials checkbox was set previously
			// fill login form with cached data (if any exist)
			chkBoxCacheCredentials.setChecked(settings.getBoolean(Constants.CACHED_CREDENTIALS_ENABLED_KEY, false));

			txtUsername.setText(settings.getString(Constants.CACHED_USERNAME_KEY, ""));
			txtPassword.setText(settings.getString(Constants.CACHED_PASSWORD_KEY, ""));

			// auto-login (currenty disabled as it makes debugging difficult)
			// if (hasValue(txtUsername) && hasValue(txtPassword)) {
			// loginAsync();
			// }
		}
	}

	private void loginAsync() {

		String username = txtUsername.getText().toString();
		String password = txtPassword.getText().toString();

		// checking if username is valid
		if (!InputValidation.isValidUserName(username)) {
			txtUsername.setError("Invalid username, can contain only"
					+ " alphanumeric, underscore and hyphen characters and be" + " between 3 and 15 characters long.");
		}

		// checking if password is valid
		else if (!InputValidation.isValidPassword(password)) {
			txtPassword.setError("Invalid password, must be " + "between 6 and 20 characters long.");
		}

		// if all validation is passed, execute the LoginTask that
		// authenticates the user against db
		if (InputValidation.isValidUserName(username) && InputValidation.isValidPassword(password)) {
			Log.i(Constants.LOG_TAG, "Executing credentials validation task");
			LoginTask loginTask = new LoginTask(this);
			loginTask.execute(username, password);
		}

	}
	/**
	 * Method that calls the CreateAccountTask.
	 */
	private void createAccountAsync() {
		Intent createAccountIntent = new Intent(LoginActivity.this, CreateAccountActivity.class);
		startActivityForResult(createAccountIntent, CREATE_ACCOUNT_REQUEST);
	}

	/**
	 * Method that is called on succesful execution of the LoginTask. If the
	 * cache credentials checkbox is checked, stores the user credentials to a
	 * SharedPreferences file that is used for displaying credentials in the
	 * LoginActivity. Starts the MainMenuActivity.
	 */
	public void handleLoginSuccess() {

		if (chkBoxCacheCredentials.isChecked()) {
			// store credentials
			User currentUser = SecurityContext.getInstance().getCurrentUser();

			settings.edit().putString(Constants.CACHED_USERNAME_KEY, currentUser.getUsername()).commit();
			settings.edit().putString(Constants.CACHED_PASSWORD_KEY, currentUser.getPassword()).commit();
			settings.edit().putBoolean(Constants.CACHED_CREDENTIALS_ENABLED_KEY, true).commit();
		} else {
			// clear any cached credentials
			settings.edit().clear().commit();
		}

		// next activity
		Intent mainMenuIntent = new Intent(LoginActivity.this, MainMenuActivity.class);
		startActivityForResult(mainMenuIntent, MAIN_MENU_REQUEST);
	}

	/**
	 * Method that is called on unsuccesful execution of the LoginTask, displays
	 * an alert dialog to inform user of unsuccessful login.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	public void handleLoginFailure(String title, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.error_icon_resized_v2);
		builder.setNeutralButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CREATE_ACCOUNT_REQUEST && resultCode == RESULT_OK) {

			//getting the user credentials passed to activity on successful account creation
			String username = data.getStringExtra(CreateAccountActivity.RESULT_USERNAME_KEY);
			String password = data.getStringExtra(CreateAccountActivity.RESULT_PASSWORD_KEY);

			//displaying the user credentials 
			txtUsername.setText(username);
			txtPassword.setText(password);

			// used to prevent override in onResume() when an account is created
			keepDisplayedCredentials = true;

		} else if (requestCode == MAIN_MENU_REQUEST) {

			// clear user session
			SecurityContext.getInstance().setCurrentUser(null);
		}
	}

	//currently not used
//	private boolean hasValue(EditText textbox) {
//
//		String textValue = textbox.getText().toString();
//
//		if (textValue == null || textValue.isEmpty()) {
//			return false;
//		} else {
//			return true;
//		}
//	}
}
