package hr.fer.ztel.myFridge.client.activities.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import hr.fer.ztel.myFridge.client.activities.MainMenuActivity;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.FileUtil;
import hr.fer.ztel.myFridge.client.util.HttpUtils;
import hr.fer.ztel.myFridge.data.FoodList;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask class that retrieves all user food items from db via HTTP GET request.
 * @author suncana
 *
 */
public class RetrieveAllUserFoodTask extends AsyncTask<Void, Void, FoodList> {

	private final MainMenuActivity parent;
	private ProgressDialog progressDialog;
	private Exception taskException;

	public RetrieveAllUserFoodTask(MainMenuActivity parent) {
		this.parent = parent;
	}

	@Override
	protected void onPreExecute() {

		progressDialog = new ProgressDialog(this.parent);
		progressDialog.setTitle("Retrieving all user food...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	@Override
	protected FoodList doInBackground(Void... params) {
		FoodList foodList = null;

		try {
			//constructing a valid HTTP GET request
			HttpGet request = new HttpGet(Constants.RETRIEVE_ALL_USER_FOOD_URI);

			HttpResponse response = HttpUtils.sendRequest(request);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("HTTP error " + response.getStatusLine().getStatusCode());
			}

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));

			foodList = HttpUtils.deserializeUserFoods(bufferedReader);

			//storing downloaded user food items to file
			FileUtil.storeFoodList(foodList, parent);

		} catch (Exception e) {
			//returning null if request was unsuccessful
			Log.e(Constants.LOG_TAG, "Exception: RetrieveAllUserFoodTask", e);
			foodList = null;
			this.taskException = e;
		}

		//returning FoodList object if request was successful (response is HTTP 200 OK)
		return foodList;
	}

	@Override
	protected void onPostExecute(FoodList foodList) {
		progressDialog.dismiss();

		if (foodList == null) {

			Log.e(Constants.LOG_TAG, "User food retrieval failed", this.taskException);
			//calling parent method for handling failure
			this.parent.handleFailure("Failure", this.taskException.getMessage() + ".");

		} else {
			// response can only be null or a deserialized food list
			//calling parent method for handling success
			this.parent.handleListDownloadSuccess("Success", "User food downloaded," + " view list?");
		}

	}

}
