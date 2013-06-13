package hr.fer.ztel.myFridge.client.activities;

import hr.fer.ztel.myFridge.R;
import hr.fer.ztel.myFridge.client.activities.tasks.DeleteAllUserFoodTask;
import hr.fer.ztel.myFridge.client.activities.tasks.RetrieveAllUserFoodTask;
import hr.fer.ztel.myFridge.client.activities.tasks.SearchBarcodeTask;
import hr.fer.ztel.myFridge.client.util.Constants;
import hr.fer.ztel.myFridge.client.util.SecurityContext;
import hr.fer.ztel.myFridge.data.Food;
import hr.fer.ztel.myFridge.data.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.zxing.integration.IntentIntegrator;
import com.google.zxing.integration.IntentResult;

/**
 * Main menu activity class.
 * @author suncana
 *
 */
public class MainMenuActivity extends Activity {

	private static final int MODIFY_USER_REQUEST = 0;

	//class used to open/create file for reading/writing user login credentials
	private SharedPreferences settings = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu_activity);

		this.settings = getSharedPreferences("MyPreferences", 0);

		//Button used for starting the scan method
		Button btnScan = (Button) findViewById(R.id.btnScan);
		btnScan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				IntentIntegrator integrator = new IntentIntegrator(MainMenuActivity.this);
				integrator.initiateScan();
			}
		});

		//Button used for starting the UserFoodListActivity
		Button btnViewItemList = (Button) findViewById(R.id.btnViewItemList);
		btnViewItemList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent viewItemListIntent = new Intent(MainMenuActivity.this, UserFoodListActivity.class);
				startActivity(viewItemListIntent);
			}
		});

		//Button used for starting the DeleteAllUserFoodTask, promts the user
		//to confirm list deletion
		Button btnDeleteFoodData = (Button) findViewById(R.id.btnDeleteFoodData);
		btnDeleteFoodData.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showConfirmDeleteListDialog("Delete", "Delete all user food data? This action" +
						" cannot be reversed.");
			}
		});

		//Button used for starting the RetrieveAllUserFoodTask
		Button btnSyncFoodData = (Button) findViewById(R.id.btnSyncFoodData);
		btnSyncFoodData.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				RetrieveAllUserFoodTask retrieveAll = new RetrieveAllUserFoodTask(MainMenuActivity.this);
				retrieveAll.execute();
			}
		});

		//Button used for starting the ModifyUserActivity
		Button btnEditUserData = (Button) findViewById(R.id.btnModifyUserData);
		btnEditUserData.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent modifyUserDataIntent = new Intent(MainMenuActivity.this, ModifyUserActivity.class);
				startActivityForResult(modifyUserDataIntent, MODIFY_USER_REQUEST);
			}
		});

		//Button used for starting the LogoutActivity, promts the user
		//to confirm logout
		Button btnLogout = (Button) findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showLogoutUserAlertDialog("Logout", "If you do not wish to clear all"
						+ " user preferences select BACK instead.");
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		//if activity result is from the scan acitivity (IntentIntegrator)
		//check if scan result is set and execute the SearchBarcodeTask
		if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
					intent);

			String barcode = scanResult.getContents();
			if (barcode != null && !barcode.isEmpty()) {
				SearchBarcodeTask searchBarcodeTask = new SearchBarcodeTask(this);
				searchBarcodeTask.execute(barcode);
			}
			//if activity result is from the ModifyUserActivity and
			//user has checked the cache credentials checkbox update the cached credentials
		} else if (requestCode == MODIFY_USER_REQUEST && resultCode == RESULT_OK) {
			
			if (settings.getBoolean(Constants.CACHED_CREDENTIALS_ENABLED_KEY, false)) {

				// update cached credentials
				User currentUser = SecurityContext.getInstance().getCurrentUser();

				settings.edit().putString(Constants.CACHED_USERNAME_KEY, currentUser.getUsername())
						.commit();
				settings.edit().putString(Constants.CACHED_PASSWORD_KEY, currentUser.getPassword())
						.commit();
			}
		}
	}

	/**
	 * Method that is called on succesful execution of the SearchBarcodeTask.
	 * Starts the ScannedFoodDetailsActivity and passes the food item object
	 * to be displayed in the activity.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 * @param food Food object to be displayed in the ScannedFoodDetailsActivity
	 */
	public void handleBarcodeSearchSuccess(String title, String message, final Food food) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.success_icon_resized_v2);
		builder.setPositiveButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// next activity is ScannedFoodDetailsActivity
				Intent detailsIntent = new Intent(MainMenuActivity.this,
						ScannedFoodDetailsActivity.class);
				detailsIntent.putExtra("food", food);
				startActivity(detailsIntent);
			}
		});

		builder.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}

	/**
	 * Method that is called on succesful execution of the DeleteAllFoodTask.
	 * Informs the user of successful delete via an alert dialog.
	  * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	public void handleDeleteAllFoodSuccess(String title, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.success_icon_resized_v2);
		builder.setNeutralButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}

	/**
	 * Method that is called on succesful execution of the RetrieveAllUserFoodTask.
	 * Informs the user of successful retrieval via an alert dialog and prompts them
	 * to view the updated food list. On positive user input starts the UserFoodListActivity.
	  * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	public void handleListDownloadSuccess(String title, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.success_icon_resized_v2);
		builder.setPositiveButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent detailsIntent = new Intent(MainMenuActivity.this, UserFoodListActivity.class);
				startActivity(detailsIntent);
			}
		});

		builder.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}

	/**
	 * Method that is called on unsuccesful execution of any task, displays
	 * an alert dialog to inform user of failure.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	public void handleFailure(String title, String message) {

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
	 * Method that prompts the user to confirm logout and on positive input
	 * clears user credentials, clears the activity stack and starts a new LoginActivity.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	private void showLogoutUserAlertDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.question_icon_resized_v2);
		builder.setPositiveButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// remove cached credentials (if any exist)
				settings.edit().clear().commit();

				Intent loginMenuIntent = new Intent(MainMenuActivity.this, LoginActivity.class);
				loginMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(loginMenuIntent);

				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}

	/**
	 * Method that prompts the user to confirm food list deletion and on positive input
	 * executes the DeleteAllUserFoodTask that clears user food data from db.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 */
	private void showConfirmDeleteListDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		builder.setMessage(message);
		builder.setIcon(R.drawable.question_icon_resized_v2);
		builder.setPositiveButton("OK", new AlertDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				DeleteAllUserFoodTask deleteAll = new DeleteAllUserFoodTask(MainMenuActivity.this);
				deleteAll.execute();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}
}
