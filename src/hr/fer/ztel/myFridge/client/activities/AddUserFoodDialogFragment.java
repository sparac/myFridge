package hr.fer.ztel.myFridge.client.activities;

import java.util.Calendar;
import java.util.Date;

import hr.fer.ztel.myFridge.R;
import hr.fer.ztel.myFridge.client.activities.tasks.AddUserFoodTask;
import hr.fer.ztel.myFridge.data.Food;
import hr.fer.ztel.myFridge.data.UserFood;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Dialog fragment class that is used when user is trying to add a food item
 * to their food list. Prompts the user to enter food item expiry date and validity
 * after opening (in days). Executes the async task AddUserFoodTask to insert the new
 * user food item into db.
 * @author suncana
 *
 */
public class AddUserFoodDialogFragment extends DialogFragment {

	private static int MAX_YEAR_OF_EXPIRY;
	private static int MIN_VALIDITY_AFTER_OPENING = 0;
	private static int MAX_VALIDITY_AFTER_OPENING = 9999;

	private Context context;

	private Food food;
	private EditText txtValidAfterDays;
	private DatePicker pckExpiryDatePicker;

	public static AddUserFoodDialogFragment newInstance() {
		AddUserFoodDialogFragment fragment = new AddUserFoodDialogFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;

	}

	@Override
	public void onStart() {
		super.onStart();

		AlertDialog dialog = (AlertDialog) getDialog();
		if (dialog != null) {
			Button btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			btnPositive.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					
					Calendar calendar = Calendar.getInstance();
					MAX_YEAR_OF_EXPIRY = calendar.get(Calendar.YEAR) + 20;

					Integer validAfterDays = Integer.parseInt(txtValidAfterDays.getText().toString());

					// validating the expiry after opening value against
					//[0, 9999] range (in days)
					if (validAfterDays < MIN_VALIDITY_AFTER_OPENING
							|| validAfterDays > MAX_VALIDITY_AFTER_OPENING) {
						txtValidAfterDays.setError("Invalid input, expected value"
								+ " between 0 and 9999 days.");
						return;
					}

					//validating the max expiry date for food item as current date plus 20 years
					if (pckExpiryDatePicker.getYear() > MAX_YEAR_OF_EXPIRY) {
						pckExpiryDatePicker.updateDate(MAX_YEAR_OF_EXPIRY, 11, 31);
						Toast.makeText(getActivity(), "Invalid date, reset to maximum value.",
								Toast.LENGTH_LONG).show();
						return;
					}

					// getting the date and validity set in dialog
					// and setting it to the user food item
					calendar = Calendar.getInstance();
					calendar.clear();
					calendar.set(pckExpiryDatePicker.getYear(), pckExpiryDatePicker.getMonth(),
							pckExpiryDatePicker.getDayOfMonth());
					Date dateExpiry = calendar.getTime();

					UserFood userFood = new UserFood();
					userFood.setFood(food);
					userFood.setDateExpiry(dateExpiry);
					userFood.setValidAfterOpening(validAfterDays);

					//executing the async task that inserts the user food item into db
					AddUserFoodTask addUserFoodTask = new AddUserFoodTask(context,
							(AsyncResultHandler) context);
					addUserFoodTask.execute(userFood);
					dismiss();
				}
			});
		}

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		if (this.context == null) {
			this.context = getActivity();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = getActivity().getLayoutInflater().inflate(R.layout.add_user_food_dialog, null);
		builder.setView(view);

		food = (Food) this.getArguments().getSerializable("food");
		txtValidAfterDays = (EditText) view.findViewById(R.id.txtValidAfterDays);
		pckExpiryDatePicker = (DatePicker) view.findViewById(R.id.pckExpiryDatePicker);

		//default value for expiry after opening is 3 days
		txtValidAfterDays.setText("3");
		builder.setTitle("Expiry date");

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				// override in onStart()
			}

		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}

		}).create();
		return builder.create();
	}
}