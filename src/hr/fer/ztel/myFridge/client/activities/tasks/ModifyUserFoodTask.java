package hr.fer.ztel.myFridge.client.activities.tasks;

import java.text.SimpleDateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

import hr.fer.ztel.myFridge.client.activities.AsyncResultHandler;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.FileUtil;
import hr.fer.ztel.myFridge.client.util.HttpUtils;
import hr.fer.ztel.myFridge.data.FoodList;
import hr.fer.ztel.myFridge.data.UserFood;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask class that modifies a single user food item in db via HTTP POST request.
 * @author suncana
 *
 */
public class ModifyUserFoodTask extends AsyncTask<UserFood, Void, Boolean> {

	private final Context context;
	private final AsyncResultHandler resultHandler;

	private ProgressDialog progressDialog;
	private Exception taskException;

	public ModifyUserFoodTask(Context context, AsyncResultHandler resultHandler) {
		this.context = context;
		this.resultHandler = resultHandler;
	}

	@Override
	protected void onPreExecute() {

		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Setting date opened...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected Boolean doInBackground(UserFood... params) {
		try {
			//formatting opened date to a string so it can be sent as a path parameter
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			String dateOpened = sdf.format(params[0].getDateOpened());

			// constructing valid HTTP POST request
			HttpPost request = new HttpPost(Constants.MODIFY_USER_FOOD_URI + params[0].getId() + "/"
					+ dateOpened);
			HttpResponse response = HttpUtils.sendRequest(request);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("HTTP error " + response.getStatusLine().getStatusCode());
			}

			// save updated user food to file (set the opened date to null
			//so equals method in UserFood class can find item's index)
			UserFood tempUserFood = params[0];
			tempUserFood.setDateOpened(null);
			
			FoodList foodList = FileUtil.retrieveFoodList(context);
			
			int index = foodList.getUserFoods().indexOf(tempUserFood);
			foodList.getUserFoods().set(index, params[0]);
			
			FileUtil.storeFoodList(foodList, context);

			//returning true if request was successful (response is HTTP 200 OK)
			return true;

		} catch (Exception e) {
			//returning null if request was unsuccessful
			Log.e(Constants.LOG_TAG, "Exception: ModifyUserFoodTask", e);
			this.taskException = e;
			return null;
		}
	}

	@Override
	protected void onPostExecute(Boolean response) {
		progressDialog.dismiss();

		if (response == null) {
			Log.e(Constants.LOG_TAG, "ModifyUserFoodTask onPostExecute - calling handleFailure",
					taskException);
			//calling parent method for handling failure
			this.resultHandler.handleAsyncTaskFailure(this, "Failure", this.taskException.getMessage()
					+ ".", this.taskException);

		} else {
			// response can only be null or true(statusCode == 200)
			//calling parent method for handling success
			this.resultHandler.handleAsyncTaskSuccess(this, "Success", "Date opened set.");
		}
		Log.i(Constants.LOG_TAG, "ModifyUserFoodTask onPostExecute - ended");
	}
}
