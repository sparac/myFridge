package hr.fer.ztel.myFridge.client.activities.tasks;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import hr.fer.ztel.myFridge.client.activities.ModifyUserActivity;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.HttpUtils;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask class that deletes user account from db via HTTP DELETE request.
 * @author suncana
 *
 */
public class DeleteUserTask extends AsyncTask<Void, Void, Boolean> {

	private ProgressDialog progressDialog;
	private ModifyUserActivity parent;
	private Exception taskException;

	public DeleteUserTask(ModifyUserActivity parent) {
		this.parent = parent;
	}

	@Override
	protected void onPreExecute() {

		progressDialog = new ProgressDialog(parent);
		progressDialog.setTitle("Deleting user account...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			// constructing valid HTTP DELETE request
			HttpDelete request = new HttpDelete(Constants.DELETE_USER_URI);
			HttpResponse response = HttpUtils.sendRequest(request);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("HTTP error " + response.getStatusLine().getStatusCode());
			}

			//returning true if request was successful (response is HTTP 200 OK)
			return true;

		} catch (Exception e) {
			//returning null if request was unsuccessful
			Log.e("Exception: DeleteUserTask", e.getMessage());
			this.taskException = e;
			return null;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		progressDialog.dismiss();

		if (result != null) {

			if (result == true) {
				//calling parent method for handling success
				parent.handleDeleteSuccess("Success", "User deleted.");

			} else {
				//calling parent method for handling failure
				parent.handleFailure("Failure", "Unknown cause.");
			}

		} else {
			//calling parent method for handling failure, passing exception message
			String errorMessage = null;
			if (this.taskException != null) {
				errorMessage = "Error: " + this.taskException.getMessage();
			} else {
				errorMessage = "Unknown error";
			}

			parent.handleFailure("Failure", errorMessage + ".");
		}
	}
}
