package hr.fer.ztel.myFridge.client.activities.tasks;

import hr.fer.ztel.myFridge.client.activities.LoginActivity;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.HttpUtils;
import hr.fer.ztel.myFridge.client.util.SecurityContext;
import hr.fer.ztel.myFridge.data.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask class that handles user login via HTTP GET request.
 * @author suncana
 *
 */
public class LoginTask extends AsyncTask<String, Void, Boolean> {

	private final LoginActivity parent;

	private ProgressDialog progressDialog;
	private Exception taskException;

	private String username;
	private String password;

	public LoginTask(LoginActivity parent) {
		this.parent = parent;
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(this.parent);
		progressDialog.setTitle("Logging in...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	@Override
	protected Boolean doInBackground(String... params) {

		//parameters passed from edittexts on the login screen
		this.username = params[0];
		this.password = params[1];

		Boolean result = null;

		try {
			Log.i(Constants.LOG_TAG, "Sending validation request");

			//server responds to a valid request with a user object later
			//used for setting authorization header on all requests
			HttpGet request = new HttpGet(Constants.CHECK_CREDENTIALS_URI);
			Credentials credentials = new UsernamePasswordCredentials(username, password);

			HttpResponse response = HttpUtils.sendRequest(request, credentials);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("HTTP error " + response.getStatusLine().getStatusCode());
			}

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));

			//deserializing user from xml to object
			User user = HttpUtils.deSerializeUser(bufferedReader);

			Log.i(Constants.LOG_TAG, "Validation success");

			//setting the received object to the SecurityContext singleton
			SecurityContext.getInstance().setCurrentUser(user);

			//returning true if request was successful (response is HTTP 200 OK)
			result = true;

		} catch (Exception e) {
			//returning null if request was unsuccessful
			Log.i(Constants.LOG_TAG, "Validation failed");
			result = null;
			this.taskException = e;
		}

		return result;
	}

	@Override
	protected void onPostExecute(Boolean loginResult) {

		progressDialog.dismiss();

		if (loginResult != null) {

			if (loginResult == true) {
				//calling parent method for handling success
				parent.handleLoginSuccess();

			} else {
				//calling parent method for handling failure
				parent.handleLoginFailure("Failure", "Invalid credentials.");
			}

		} else {
			// login error
			String errorMessage = null;

			if (this.taskException != null) {
				errorMessage = "Error: " + this.taskException.getMessage();
			} else {
				errorMessage = "Unknown error";
			}

			//calling parent method for handling failure, passing caught exception message
			parent.handleLoginFailure("Failure", errorMessage + ".");
		}
	}
}