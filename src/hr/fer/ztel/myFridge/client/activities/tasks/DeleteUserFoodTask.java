package hr.fer.ztel.myFridge.client.activities.tasks;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;

import hr.fer.ztel.myFridge.client.activities.UserFoodListActivity;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.FileUtil;
import hr.fer.ztel.myFridge.client.util.HttpUtils;
import hr.fer.ztel.myFridge.data.FoodList;
import hr.fer.ztel.myFridge.data.UserFood;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask class that deletes a single user food item from db via HTTP DELETE request.
 * @author suncana
 *
 */
public class DeleteUserFoodTask extends AsyncTask<UserFood, Void, UserFood> {

	private ProgressDialog progressDialog;
	private UserFoodListActivity parent;
	private Exception taskException;

	public DeleteUserFoodTask(UserFoodListActivity parent) {
		this.parent = parent;
	}

	@Override
	protected void onPreExecute() {

		Log.i("DeleteUserFoodTask onPreExecute", "started");
		progressDialog = new ProgressDialog(parent);
		progressDialog.setTitle("Deleting user food...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		Log.e("DeleteUserFoodTask onPreExecute", "ended");
	}

	@Override
	protected UserFood doInBackground(UserFood... params) {
		try {

			// constructing valid HTTP DELETE request
			HttpDelete request = new HttpDelete(Constants.DELETE_USER_FOOD_URI + params[0].getId());
			HttpResponse response = HttpUtils.sendRequest(request);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("HTTP error " + response.getStatusLine().getStatusCode());
			}

			//removing user food item from local file containing user food list
			FoodList foodList = FileUtil.retrieveFoodList(parent);
			foodList.getUserFoods().remove(params[0]);
			FileUtil.storeFoodList(foodList, parent);

//			return user food if request was successful (response is HTTP 200 OK)
			return params[0];

		} catch (Exception e) {
			Log.e("Exception: DeleteUserFoodTask", e.getMessage());
			this.taskException = e;
			return null;
		}
	}

	@Override
	protected void onPostExecute(UserFood response) {
		progressDialog.dismiss();

		if (response == null) {
			Log.e("DeleteUserFoodTask onPostExecute", "calling handleFailure");
			//calling parent method for handling failure
			this.parent.handleFailure("Failure", this.taskException.getMessage() + ".");

		} else {
			Log.e("DeleteUserFoodTask onPostExecute", "calling handleDeleteFoodSuccess");
			// response can only be null or true(statusCode == 200)
			//calling parent method for handling success
			this.parent.handleDeleteFoodSuccess("Success", "User food deleted.", response);
		}
		Log.i("DeleteUserFoodTask onPostExecute", "ended");
	}
}
