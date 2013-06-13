package hr.fer.ztel.myFridge.client.activities.tasks;

import hr.fer.ztel.myFridge.client.activities.MainMenuActivity;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.FileUtil;
import hr.fer.ztel.myFridge.client.util.HttpUtils;
import hr.fer.ztel.myFridge.data.FoodList;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask class that deletes all user food items from db via HTTP DELETE request.
 * @author suncana
 *
 */
public class DeleteAllUserFoodTask extends AsyncTask<Void, Void, Boolean> {
	private ProgressDialog progressDialog;
	private MainMenuActivity parent;
	private Exception taskException;

	public DeleteAllUserFoodTask(MainMenuActivity parent) {
		this.parent = parent;
	}

	@Override
	protected void onPreExecute() {

		Log.i("DeleteAllUserFoodTask onPreExecute", "started");
		progressDialog = new ProgressDialog(parent);
		progressDialog.setTitle("Deleting user food...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		Log.e("DeleteAllUserFoodTask onPreExecute", "ended");
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {

			// constructing valid HTTP DELETE request
			HttpDelete request = new HttpDelete(Constants.DELETE_ALL_USER_FOOD_URI);
			HttpResponse response = HttpUtils.sendRequest(request);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("HTTP error " + response.getStatusLine().getStatusCode());
			}

			// clearing local file containing user food list
			FoodList foodList = FileUtil.retrieveFoodList(parent);
			foodList.getUserFoods().clear();
			FileUtil.storeFoodList(foodList, parent);

			Log.i("DeleteAllUserFoodTask doInBackground", "success");
			//returning true if request was successful (response is HTTP 200 OK)
			return true;

		} catch (Exception e) {
			//returning null if request was unsuccessful
			Log.e("Exception: DeleteAllUserFoodTask", e.getMessage());
			this.taskException = e;
			return null;
		}
	}

	@Override
	protected void onPostExecute(Boolean response) {
		progressDialog.dismiss();

		if (response == null) {
			Log.e("DeleteAllUserFoodTask onPostExecute", "calling handleFailure");
			//calling parent method for handling failure
			this.parent.handleFailure("Failure", this.taskException.getMessage() + ".");

		} else {
			Log.e("DeleteAllUserFoodTask onPostExecute", "calling handleDeleteFoodSuccess");
			// response can only be null or true (statusCode == 200)
			//calling parent method for handling success
			this.parent.handleDeleteAllFoodSuccess("Success", "All user food deleted.");
		}
		Log.i("DeleteAllUserFoodTask onPostExecute", "ended");
	}
}
