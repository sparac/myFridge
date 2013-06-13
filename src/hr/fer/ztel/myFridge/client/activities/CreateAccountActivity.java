package hr.fer.ztel.myFridge.client.activities;

import hr.fer.ztel.myFridge.R;
import hr.fer.ztel.myFridge.client.activities.tasks.CreateAccountTask;
import hr.fer.ztel.myFridge.client.util.InputValidation;
import hr.fer.ztel.myFridge.data.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Activity class that handles account creation.
 * @author suncana
 *
 */
public class CreateAccountActivity extends Activity {

	public static final String RESULT_USERNAME_KEY = "username";
	public static final String RESULT_PASSWORD_KEY = "password";

	private EditText txtUsername;
	private EditText txtPassword;
	private EditText txtPassword2;
	private EditText txtEMail;
	private CheckBox chkBxNotifications;

	private CreateAccountTask createUserTask;
	private User newUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account_activity);

		this.txtUsername = (EditText) findViewById(R.id.username);
		this.txtPassword = (EditText) findViewById(R.id.password);
		this.txtPassword2 = (EditText) findViewById(R.id.password2);
		this.txtEMail = (EditText) findViewById(R.id.email);
		this.chkBxNotifications = (CheckBox) findViewById(R.id.chkBxNotifications);

		Button btnCreateAcc = (Button) findViewById(R.id.btnCreateAcc);

		btnCreateAcc.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = txtUsername.getText().toString();
				String password = txtPassword.getText().toString();
				String password2 = txtPassword2.getText().toString();
				String email = txtEMail.getText().toString();
				boolean notifications = chkBxNotifications.isChecked();

				// checking if username is valid
				if (!InputValidation.isValidUserName(username)) {
					txtUsername.setError("Invalid username, can contain only"
							+ " alphanumeric, underscore and hyphen characters and be"
							+ " between 3 and 15 characters long.");
				}

				// checking if email is valid
				else if (!InputValidation.isValidEmail(email)) {
					txtEMail.setError("Invalid email format");
				}

				// checking if password is valid
				else if (!InputValidation.isValidPassword(password)) {
					txtPassword.setError("Invalid password, must be " +
							"between 6 and 20 characters long.");
				}
				else if(!InputValidation.areEqualPasswords(password, password2)) {
					txtPassword2.setError("Password must be retyped correctly.");
				}

				//if all validation is passed, execute the CreateAccountTask that
				//inserts the new user into db
				if (InputValidation.isValidUserName(username) && InputValidation.isValidEmail(email)
						&& InputValidation.isValidPassword(password)
						&& InputValidation.areEqualPasswords(password, password2)) {
					newUser = new User(username, password, email, notifications);
					createUserTask = new CreateAccountTask(CreateAccountActivity.this);
					createUserTask.execute(newUser);
				}
			}
		});
	}

	/**
	 * Method that is called on succesful execution of the CreateUserTask, displays
	 * an alert dialog to inform user of successful user account creation.
	 * Sets the username and password to be returned to the calling activity (LoginActivity)
	 * so it can display credentials required for login. 
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	public void handleRegistrationSuccess(String title, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.success_icon_resized_v2);
		builder.setNeutralButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Intent resultIntent = new Intent();
				resultIntent.putExtra(RESULT_USERNAME_KEY, newUser.getUsername());
				resultIntent.putExtra(RESULT_PASSWORD_KEY, newUser.getPassword());
				setResult(Activity.RESULT_OK, resultIntent);

				dialog.dismiss();

				finish();
			}
		});

		builder.show();
	}

	/**
	 * Method that is called on unsuccesful execution of the CreateUserTask, displays
	 * an alert dialog to inform user of unsuccessful user account creation.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	public void handleRegistrationFailure(String title, String message) {
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
}
