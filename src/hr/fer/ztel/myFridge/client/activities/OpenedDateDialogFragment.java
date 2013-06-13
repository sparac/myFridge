package hr.fer.ztel.myFridge.client.activities;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * Dialog fragment class that is used when user is setting an opening date for a user
 * food item in their food list. Prompts the user to enter food item opening date
 * Calls the appropriate parent activity method to handle result and passes set date parameters.
 * @author suncana
 *
 */
public class OpenedDateDialogFragment extends DialogFragment implements OnDateSetListener {

	private boolean allowClose;

	public OpenedDateDialogFragment() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it

		// extending base class and overriding
		final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month,
				day) {

			@Override
			public void onBackPressed() {
				//on back press allow dialog dismissal
				allowClose = true;
				super.onBackPressed();
			}

			@Override
			public void dismiss() {
				//only allowing dialog dismiss if the set date is within allowed range
				//[current date - 1 year, current date]
				if (allowClose) {
					super.dismiss();
				}
			}
		};

		datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//if user cancels the opened date dialog, allow dismiss
						if (which == DialogInterface.BUTTON_NEGATIVE) {
							allowClose = true;
						}
					}
				});

		return datePickerDialog;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {

		// get selected date
		Calendar dateOpened = Calendar.getInstance();
		clearTimeComponent(dateOpened);
		dateOpened.set(year, month, day);

		// get max date opened (now)
		Calendar maxDateOpened = Calendar.getInstance();
		clearTimeComponent(maxDateOpened);

		// get min date opened
		Calendar minDateOpened = Calendar.getInstance();
		clearTimeComponent(minDateOpened);
		minDateOpened.add(Calendar.YEAR, -1);

		// validate date opened
		if (dateOpened.after(maxDateOpened)) {
			// invalid input, cannot set opened date to future date
			view.updateDate(maxDateOpened.get(Calendar.YEAR), maxDateOpened.get(Calendar.MONTH),
					maxDateOpened.get(Calendar.DAY_OF_MONTH));
			Toast.makeText(getActivity(), "Invalid date, reset to" + "\n" + "maximum value (today)",
					Toast.LENGTH_LONG).show();

			allowClose = false;
			return;
		} else if (dateOpened.before(minDateOpened)) {
			// invalid input, cannot set opened date more than 1 year in the
			// past
			view.updateDate(minDateOpened.get(Calendar.YEAR), minDateOpened.get(Calendar.MONTH),
					minDateOpened.get(Calendar.DAY_OF_MONTH));
			Toast.makeText(getActivity(),
					"Invalid date, reset to" + "\n" + "minimum value (1 year ago)", Toast.LENGTH_LONG)
					.show();

			allowClose = false;
			return;
		} else {
			// valid input
			allowClose = true;
			((UserFoodDetailsActivity) getActivity()).setDateOpenedFromDialog(day, month, year);
		}
	}

	/**
	 * Method for clearing time component of a passed Calendar object (both expiry
	 * date and opened date use only day, month and year components).
	 * @param calendar Calendar to be cleared of its time component
	 * @return calendar Calendar with cleared time component
	 */
	private Calendar clearTimeComponent(Calendar calendar) {

		calendar.clear(Calendar.HOUR_OF_DAY);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		return calendar;
	}
}
