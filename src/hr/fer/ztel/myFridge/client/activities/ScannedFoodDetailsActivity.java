package hr.fer.ztel.myFridge.client.activities;

import java.util.Date;

import hr.fer.ztel.myFridge.R;
import hr.fer.ztel.myFridge.data.Food;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Scanned item details activity.
 * @author suncana
 *
 */
public class ScannedFoodDetailsActivity extends FragmentActivity implements AsyncResultHandler {

	public static Food food;
	public static Date dateExpiry;
	public static Integer validAfterDays;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanned_food_details_activity);

		Intent intent = getIntent();
		food = (Food) intent.getSerializableExtra("food");

		ImageView image = (ImageView) findViewById(R.id.imgFoodLarge);
		image.setImageBitmap(food.getImageLarge().getBitmap());

		TextView txtName = (TextView) findViewById(R.id.txtFoodName);
		txtName.setText(food.getName());

		TextView txtManufacturer = (TextView) findViewById(R.id.txtManufacturer);
		txtManufacturer.setText(food.getManufacturer());

		TextView txtDescription = (TextView) findViewById(R.id.txtDescription);

		// set description if there is one, otherwise textView uses string
		// resource to display N/A
		if ((food.getDescription() != null) && !food.getDescription().isEmpty()) {
			txtDescription.setText(food.getDescription());
		}

		//Button used for adding food to user food 
		Button btnAddUserFood = (Button) findViewById(R.id.btnAddUserFood);
		btnAddUserFood.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}
		});

		//Cancel button for returning to main menu
		Button btnCancelAddUserFood = (Button) findViewById(R.id.btnCancelUserFood);
		btnCancelAddUserFood.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * Method that starts the AddUserFoodDialogFragment and passes the food item
	 * object to the dialog fragment.
	 */
	private void showDialog() {
		DialogFragment newFragment = AddUserFoodDialogFragment.newInstance();
		Bundle bundle = new Bundle();
		bundle.putSerializable("food", food);
		// set Fragmentclass Arguments
		newFragment.setArguments(bundle);
		newFragment.show(getSupportFragmentManager(), "dialog");
	}

	/**
	 * Method that handles any async task success and notifies the user of success
	 * via an alert dialog.
	 * @param source Async task that called the method
	 * @param results Result array passed from the async task
	 */
	@Override
	public void handleAsyncTaskSuccess(AsyncTask<?, ?, ?> source, final Object... results) {

		String title = (String) results[0];
		String message = (String) results[1];

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.success_icon_resized_v2);
		builder.setNeutralButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});

		builder.show();
	}

	/**
	 * Method that handles any async task failure and notifies the user of failure
	 * via an alert dialog.
	 * @param source Async task that called the method
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	@Override
	public void handleAsyncTaskFailure(AsyncTask<?, ?, ?> source, String title, String message,
			Throwable cause) {
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
}
