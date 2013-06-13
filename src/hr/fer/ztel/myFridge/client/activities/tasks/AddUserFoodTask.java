package hr.fer.ztel.myFridge.client.activities.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import hr.fer.ztel.myFridge.client.activities.AsyncResultHandler;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.FileUtil;
import hr.fer.ztel.myFridge.client.util.HttpUtils;
import hr.fer.ztel.myFridge.data.FoodList;
import hr.fer.ztel.myFridge.data.UserFood;

/**
 * AsyncTask class that adds user food to db via HTTP PUT request.
 * @author suncana
 *
 */
public class AddUserFoodTask extends AsyncTask<UserFood, Void, UserFood> {

	private final Context context;
	private final AsyncResultHandler resultHandler;

	private ProgressDialog progressDialog;
	private Exception taskException;

	public AddUserFoodTask(Context context, AsyncResultHandler resultHandler) {
		this.context = context;
		this.resultHandler = resultHandler;
	}

	@Override
	protected void onPreExecute() {

		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Adding user food...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected UserFood doInBackground(UserFood... params) {
		try {
			//formatting expiry date to a string so it can be sent as a path parameter
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			String dateExpiry = sdf.format(params[0].getDateExpiry());

			// constructing valid HTTP PUT request
			HttpPut request = new HttpPut(Constants.ADD_USER_FOOD_URI + params[0].getFood().getBarcode()
					+ "/" + dateExpiry + "/" + params[0].getValidAfterOpening());

			HttpResponse response = HttpUtils.sendRequest(request);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("HTTP error " + response.getStatusLine().getStatusCode());
			}

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));

			// get user food and save it to file
			UserFood userFood = HttpUtils.deserializeUserFood(bufferedReader);
			FoodList foodList = FileUtil.retrieveFoodList(context);
			foodList.getUserFoods().add(userFood);
			FileUtil.storeFoodList(foodList, context);

//			return user food if request was successful (response is HTTP 200 OK)
			return userFood;

		} catch (Exception e) {

			//returning null if request was unsuccessful
			Log.e(Constants.LOG_TAG, "Exception: AddUserFoodTask", e);
			this.taskException = e;
			return null;
		}
	}

	@Override
	protected void onPostExecute(UserFood userFood) {
		progressDialog.dismiss();

		if (userFood == null) {
			Log.e(Constants.LOG_TAG, "AddUserFoodTask onPostExecute - calling handleFailure",
					taskException);
			//calling parent method for handling failure
			this.resultHandler.handleAsyncTaskFailure(this, "Failure", this.taskException.getMessage()
					+ ".", this.taskException);

		} else {
			// response can only be null or true (statusCode == 200)
			//calling parent method for handling success
			this.resultHandler.handleAsyncTaskSuccess(this, "Success",
					"Food successfully added to list.", userFood);
		}
		Log.i(Constants.LOG_TAG, "AddUserFoodTask onPostExecute - ended");
	}
}
