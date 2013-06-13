package hr.fer.ztel.myFridge.client.activities;

/**
 * Modify user data activity class.
 */
import java.io.File;

import hr.fer.ztel.myFridge.R;
import hr.fer.ztel.myFridge.client.activities.tasks.DeleteUserTask;
import hr.fer.ztel.myFridge.client.activities.tasks.ModifyUserTask;
import hr.fer.ztel.myFridge.client.util.SecurityContext;
import hr.fer.ztel.myFridge.client.util.InputValidation;
import hr.fer.ztel.myFridge.data.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class ModifyUserActivity extends Activity {

	private SharedPreferences settings = null;

	private EditText txtUsername;
	private EditText txtPassword;
	private EditText txtPassword2;
	private EditText txtEMail;
	private CheckBox chkBxNotifications;

	private ModifyUserTask modifyUserTask;
	private DeleteUserTask deleteUserTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_user_data_activity);

		this.settings = getSharedPreferences("MyPreferences", 0);

		User currentUser = SecurityContext.getInstance().getCurrentUser();

		txtUsername = (EditText) findViewById(R.id.usernameEdit);
		txtUsername.setText(currentUser.getUsername());
		txtUsername.setBackgroundColor(Color.LTGRAY);
		txtUsername.setFocusable(false);

		txtEMail = (EditText) findViewById(R.id.emailEdit);
		txtEMail.setText(currentUser.geteMail());

		txtPassword = (EditText) findViewById(R.id.passwordEdit);
		txtPassword.setText(currentUser.getPassword());

		txtPassword2 = (EditText) findViewById(R.id.password2Edit);
		txtPassword2.setText(currentUser.getPassword());

		chkBxNotifications = (CheckBox) findViewById(R.id.chkBxNotificationsEdit);
		chkBxNotifications.setChecked(currentUser.isNotifications());

		//Button used for executing account deletion task
		Button btnDeleteUser = (Button) findViewById(R.id.btnDeleteUser);
		btnDeleteUser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showConfirmDeleteUserDialog("Delete user", "This action cannot be reversed.");
			}
		});

		//Button used for executing user data modification task
		Button btnModifyData = (Button) findViewById(R.id.btnModifyUserDataShort);
		btnModifyData.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = txtUsername.getText().toString();
				String password = txtPassword.getText().toString();
				String password2 = txtPassword2.getText().toString();
				String email = txtEMail.getText().toString();
				boolean notifications = chkBxNotifications.isChecked();

				// checking if email is valid
				if (!InputValidation.isValidEmail(email)) {
					txtEMail.setError("Invalid email format");
				}

				// checking if password is valid
				else if (!InputValidation.isValidPassword(password)) {
					txtPassword.setError("Invalid password, must be " +
							"between 6 and 20 characters long.");
				}
				//checking if retyped password matches the password
				else if(!InputValidation.areEqualPasswords(password, password2)) {
					txtPassword2.setError("Password must be retyped correctly.");
				}
				
				//if all validation is passed, execute the ModifyUserTask that
				//updates user data in db
				if (InputValidation.isValidEmail(email)
						&& InputValidation.isValidPassword(password)
						&& InputValidation.areEqualPasswords(password, password2)) {
					User modifiedUser = new User(username, password, email, notifications);
					modifyUserTask = new ModifyUserTask(ModifyUserActivity.this);
					modifyUserTask.execute(modifiedUser);
				}
			}
		});
	}

	/**
	 * Method that is called on unsuccesful execution of any task, displays
	 * an alert dialog to inform user of failure.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	public void handleFailure(String title, String message) {

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

	/**
	 * Method that is called on succesful execution of the ModifyUserTask.
	 * Informs the user of successful retrieval via an alert dialog and sets the
	 * appropriate activity result code.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	public void handleModificationSuccess(String title, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.success_icon_resized_v2);
		builder.setNeutralButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				setResult(RESULT_OK);
				finish();
			}
		});

		builder.show();
	}

	/**
	 * Method that is called on succesful execution of the DeleteUserTask.
	 * Clears all cached credentials, user preferences and User singleton used
	 * for authorization header creation. Informs the user of successful deletion
	 * via an alert dialog, clears the activity stack and start a new LoginActivity.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	public void handleDeleteSuccess(String title, String message) {

		// get current user in order to access the food list file named after their username
		User user = SecurityContext.getInstance().getCurrentUser();

		// clear form of user data
		txtUsername.setText("");
		txtPassword.setText("");
		txtEMail.setText("");
		chkBxNotifications.setChecked(false);

		// delete file that contains current user's food list
		File file = new File(getFilesDir(), user.getUsername());
		file.delete();

		// clear all user cached credentials and preferences
		settings.edit().clear().commit();

		// delete current user object (previously used to display user data in
		//ModifyUserActivity and set authorization header to outgoing HTTP requests)
		SecurityContext.getInstance().setCurrentUser(null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.success_icon_resized_v2);
		builder.setNeutralButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent loginMenuIntent = new Intent(ModifyUserActivity.this, LoginActivity.class);
				//clear activity stack and start the login activity
				loginMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(loginMenuIntent);

				dialog.dismiss();
			}
		});

		builder.show();
	}
	
	/**
	 * Method that prompts the user to confirm user account deletion and on positive input
	 * executes the DeleteUserTask that clears user account data from db.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	private void showConfirmDeleteUserDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.question_icon_resized_v2);
		builder.setPositiveButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteUserTask = new DeleteUserTask(ModifyUserActivity.this);
				deleteUserTask.execute();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}
}
