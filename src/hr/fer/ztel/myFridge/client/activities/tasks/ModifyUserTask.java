package hr.fer.ztel.myFridge.client.activities.tasks;

import hr.fer.ztel.myFridge.client.activities.ModifyUserActivity;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.HttpUtils;
import hr.fer.ztel.myFridge.client.util.SecurityContext;
import hr.fer.ztel.myFridge.data.User;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask class that modifies user account data in db via HTTP POST request.
 * @author suncana
 *
 */
public class ModifyUserTask extends AsyncTask<User, Void, Boolean> {

	private ProgressDialog progressDialog;
	private ModifyUserActivity parent;
	private Exception taskException;

	public ModifyUserTask(ModifyUserActivity parent) {
		this.parent = parent;
	}

	@Override
	protected void onPreExecute() {

		progressDialog = new ProgressDialog(parent);
		progressDialog.setTitle("Modifying user data...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	@Override
	protected Boolean doInBackground(User... modifiedUser) {
		try {
			// serializing modified user object so it can be updated in db
			String userXml = HttpUtils.serializeUser(modifiedUser[0]);

			StringEntity entity = new StringEntity(userXml, HTTP.UTF_8);
			entity.setContentType("application/xml");

			HttpPost request = new HttpPost(Constants.MODIFY_USER_URI);
			request.setEntity(entity);

			HttpResponse response = HttpUtils.sendRequest(request);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("HTTP error " + response.getStatusLine().getStatusCode());
			}

			SecurityContext.getInstance().setCurrentUser(modifiedUser[0]);

			//returning true if request was successful (response is HTTP 200 OK)
			return true;

		} catch (Exception e) {
			//returning null if request was unsuccessful
			Log.e(Constants.LOG_TAG, "Exception: ModifyUserTask", e);
			this.taskException = e;
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		progressDialog.dismiss();

		if (result == true) {
			// response can only be null or true(if statusCode != 200) exception
			// is thrown
			//calling parent method for handling success
			this.parent.handleModificationSuccess("Success", "User data modified.");

		} else {

			//calling parent method for handling failure
			this.parent.handleFailure("Failure", this.taskException.getMessage() + ".");
		}
	}

}
