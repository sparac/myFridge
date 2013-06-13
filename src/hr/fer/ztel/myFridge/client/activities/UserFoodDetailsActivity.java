package hr.fer.ztel.myFridge.client.activities;

import hr.fer.ztel.myFridge.R;
import hr.fer.ztel.myFridge.client.activities.tasks.AddUserFoodTask;
import hr.fer.ztel.myFridge.client.activities.tasks.ModifyUserFoodTask;
import hr.fer.ztel.myFridge.data.UserFood;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
 * Activity class used for displaying a detailed view of a single user food
 * item.
 * 
 * @author suncana
 * 
 */
public class UserFoodDetailsActivity extends FragmentActivity implements AsyncResultHandler {

	public static UserFood userFood;
	public static Date dateExpiry;
	public static Date dateOpened;
	public static Integer validAfterDays;
	public static TextView txtOpenedDate;
	private ModifyUserFoodTask modifyUserFoodTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_food_details_activity);

		Intent intent = getIntent();
		userFood = (UserFood) intent.getSerializableExtra("userFood");

		ImageView image = (ImageView) findViewById(R.id.imgFoodLarge);
		image.setImageBitmap(userFood.getFood().getImageLarge().getBitmap());

		TextView txtName = (TextView) findViewById(R.id.txtFoodName);
		txtName.setText(userFood.getFood().getName());

		TextView txtManufacturer = (TextView) findViewById(R.id.txtManufacturer);
		txtManufacturer.setText(userFood.getFood().getManufacturer());

		TextView txtDescription = (TextView) findViewById(R.id.txtDescription);

		// set description if there is one, otherwise textView uses string
		// resource to display N/A
		if ((userFood.getFood().getDescription() != null) && !userFood.getFood().getDescription().isEmpty()) {
			txtDescription.setText(userFood.getFood().getDescription());
		}

		TextView txtExpiryDate = (TextView) findViewById(R.id.txtExpiryDate);
		txtExpiryDate.setText(formatDate(userFood.getDateExpiry()));

		TextView txtValidAfterDays = (TextView) findViewById(R.id.txtValidAfterDays);
		txtValidAfterDays.setText(userFood.getValidAfterOpening().toString());

		txtOpenedDate = (TextView) findViewById(R.id.txtOpenedDate);
		if (userFood.getDateOpened() != null) {
			txtOpenedDate.setText(formatDate(userFood.getDateOpened()));
		}

		// Button used for setting an opening date to a user food item
		Button btnModifyUserFood = (Button) findViewById(R.id.btnModifyUserFood);
		btnModifyUserFood.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// if the date opened is already set display an appropriate
				// alerd dialog
				if (userFood.getDateOpened() != null) {
					handleAsyncTaskFailure(null, "Failure", "Date opened already set.", null);
				} else {
					showDateOpenedDialog();
				}

			}
		});

		// Button used for cloning an existing user food item (only immutable
		// item attributes such as name and manufacturer are cloned)
		Button btnCloneUserFood = (Button) findViewById(R.id.btnCloneUserFood);
		btnCloneUserFood.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showAddFoodDialog();
			}
		});

	}

	/**
	 * Method that displays a date picker dialog fragment used for setting the
	 * date opened item attribute.
	 */
	private void showDateOpenedDialog() {

		DialogFragment newFragment = new OpenedDateDialogFragment();
		newFragment.show(getSupportFragmentManager(), "dateOpenedDialog");
	}

	/**
	 * Method that displays a custom dialog fragment used for setting the expiry
	 * date and item validity after opening. Used when an item is being cloned.
	 */
	private void showAddFoodDialog() {
		DialogFragment newFragment = AddUserFoodDialogFragment.newInstance();
		Bundle bundle = new Bundle();
		bundle.putSerializable("food", userFood.getFood());
		// set Fragmentclass Arguments
		newFragment.setArguments(bundle);
		newFragment.show(getSupportFragmentManager(), "dialog");
	}

	/**
	 * Method called from the OpenedDateDialogFragment that passes the date
	 * opened to the ModifyUserFoodTask and executes the task.
	 * 
	 * @param day Day parameter passed from the date picker
	 * @param month Month parameter passed from the date picker
	 * @param year Year parameter passed from the date picker
	 */
	public void setDateOpenedFromDialog(int day, int month, int year) {
		// creating a Calendar object from passed parameters
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month, day);
		dateOpened = cal.getTime();

		UserFood tempUserFood = userFood;

		// call async modifyUserData task, pass user food with date opened
		tempUserFood.setDateOpened(dateOpened);
		modifyUserFoodTask = new ModifyUserFoodTask(UserFoodDetailsActivity.this, UserFoodDetailsActivity.this);
		modifyUserFoodTask.execute(tempUserFood);
	}

	/**
	 * Method that handles any async task success and notifies the user of
	 * success via an alert dialog.
	 * @param source Async task that called the method
	 * @param results Result array passed from the async task
	 */
	@Override
	public void handleAsyncTaskSuccess(AsyncTask<?, ?, ?> source, final Object... results) {

		String title = (String) results[0];
		String message = (String) results[1];

		// if the calling async task was instance of ModifyUserFoodTask set food
		// item's opened date, pass the object to the parent activity
		// (UserFoodListActivity) and finish current activity
		if (source != null && source instanceof ModifyUserFoodTask) {
			userFood.setDateOpened(dateOpened);
			txtOpenedDate.setText(formatDate(dateOpened));

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(title);
			builder.setMessage(message);
			builder.setIcon(R.drawable.success_icon_resized_v2);
			builder.setNeutralButton("OK", new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					Intent resultIntent = new Intent();
					resultIntent.putExtra("userFood", userFood);
					setResult(UserFoodListActivity.MODIFY_USER_FOOD_REQUEST, resultIntent);
					finish();
				}
			});

			builder.show();

		// if the calling async task was instance of AddUserFoodTask 
		// pass the added food item object to the parent activity
		// (UserFoodListActivity) and finish current activity
		} else if (source != null && source instanceof AddUserFoodTask) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle(title);
			builder.setMessage(message);
			builder.setIcon(R.drawable.success_icon_resized_v2);
			builder.setPositiveButton("OK", new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent resultIntent = new Intent();
					resultIntent.putExtra("userFood", (UserFood) results[2]);
					setResult(UserFoodListActivity.CLONE_USER_FOOD_REQUEST, resultIntent);
					finish();
				}
			});

			builder.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});

			builder.show();
		}

	}

	/**
	 * Method that handles any async task failure and notifies the user of failure
	 * via an alert dialog.
	 * @param source Async task that called the method
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 * @param e Exception thrown while executing async task 
	 */
	@Override
	public void handleAsyncTaskFailure(AsyncTask<?, ?, ?> source, String title, String message, Throwable cause) {
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

	/**
	 * Method used for formatting date objects for display (dd-MM-yyyy).
	 * @param date Date to be formatted
	 * @return String containing the formatted date
	 */
	private String formatDate(Date date) {
		String formattedDate = "";
		if (date != null) {
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
			formattedDate = df.format(date);
		}
		return formattedDate;

	}
}
