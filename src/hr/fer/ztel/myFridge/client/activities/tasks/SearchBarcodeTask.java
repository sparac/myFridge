package hr.fer.ztel.myFridge.client.activities.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import hr.fer.ztel.myFridge.client.activities.MainMenuActivity;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.HttpUtils;
import hr.fer.ztel.myFridge.data.Food;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * AsyncTask class that executes a barcode search against db via HTTP GET request.
 * @author suncana
 *
 */
public class SearchBarcodeTask extends AsyncTask<String, Void, Food> {

	private ProgressDialog progressDialog;
	private MainMenuActivity parent;
	private Exception taskException;

	public SearchBarcodeTask(MainMenuActivity parent) {
		this.parent = parent;
	}

	@Override
	protected void onPreExecute() {

		progressDialog = new ProgressDialog(parent);
		progressDialog.setTitle("Searching barcode...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	@Override
	protected Food doInBackground(String... params) {
		Food food = null;

		try {
			//constructing a valid HTTP GET request
			HttpGet request = new HttpGet(Constants.BARCODE_SEARCH_URI + params[0]);

			HttpResponse response = HttpUtils.sendRequest(request);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("HTTP error " + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			//deserializing xml to food object
			food = HttpUtils.deSerializeFood(br);

		} catch (Exception e) {
			//returning null if request was unsuccessful
			Log.e(Constants.LOG_TAG, "Exception: SearchBarcodeTask", e);
			food = null;
			this.taskException = e;
		}

		//returning food object if request was successful (response is HTTP 200 OK)
		return food;
	}

	@Override
	protected void onPostExecute(Food food) {

		progressDialog.dismiss();

		if (food == null) {

			Log.e(Constants.LOG_TAG, "Barcode search failed", this.taskException);
			//calling parent method for handling failure
			this.parent.handleFailure("Failure", this.taskException.getMessage() + ".");

		} else {
			// response can only be null or a deserialized food object
			//calling parent method for handling success
			this.parent.handleBarcodeSearchSuccess("Success, view food details?", food.getName(), food);
		}
	}
}
