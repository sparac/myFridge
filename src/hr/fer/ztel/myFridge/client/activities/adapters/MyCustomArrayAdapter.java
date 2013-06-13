package hr.fer.ztel.myFridge.client.activities.adapters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hr.fer.ztel.myFridge.R;
import hr.fer.ztel.myFridge.data.UserFood;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom ArrayAdapter class used for displaying user food items in a list.
 * @author suncana
 *
 */
public class MyCustomArrayAdapter extends ArrayAdapter<UserFood> {

	//constants used for setting single list item background color (green, yellow and orange respectively)
	private static final int FRESH = 2;
	private static final int NEUTRAL_FRESH = 1;
	private static final int NOT_FRESH = 0;

	private final Context context;
	private List<UserFood> foodlist;

	public MyCustomArrayAdapter(Context context, List<UserFood> foodlist) {
		super(context, R.layout.row_layout, foodlist);
		this.context = context;
		this.foodlist = foodlist;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.row_layout, parent, false);

		// get freshness category from user food item from its expiry date, opened date
		//and validity after opening)
		int freshnessCategory = checkFoodFreshness(foodlist.get(position).getDateExpiry(), foodlist.get(position)
				.getDateOpened(), foodlist.get(position).getValidAfterOpening());

		switch (freshnessCategory) {
		case NOT_FRESH:
			rowView.setBackgroundResource(R.drawable.list_selector_orange_gradient);
			break;
		case NEUTRAL_FRESH:
			rowView.setBackgroundResource(R.drawable.list_selector_yellow_gradient);
			break;
		case FRESH:
			rowView.setBackgroundResource(R.drawable.list_selector_light_green_gradient);
			break;
		default:
			rowView.setBackgroundResource(R.drawable.list_selector_gray_gradient);
			break;
		}

		//setting item image, name and manufacturer to a row
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imgFoodImageDetail);
		TextView txtNameDetail = (TextView) rowView.findViewById(R.id.txtNameDetail);
		TextView txtManufacturerDetail = (TextView) rowView.findViewById(R.id.txtManufacturerDetail);

		txtNameDetail.setText(foodlist.get(position).getFood().getName());
		txtManufacturerDetail.setText(foodlist.get(position).getFood().getManufacturer());
		imageView.setImageBitmap(foodlist.get(position).getFood().getImageLarge().getBitmap());

		return rowView;
	}

	private int checkFoodFreshness(Date expiryDate, Date openedDate, Integer validAfterOpening) {

		Calendar expiryBoundary = Calendar.getInstance();
		expiryBoundary.clear();
		expiryBoundary.setTime(expiryDate);

		// get effective product expiry as
		// min(expiryDate, openedDate+validAfterOpening)
		if (openedDate != null) {
			Calendar expiryAfterOpen = Calendar.getInstance();
			expiryAfterOpen.clear();
			expiryAfterOpen.setTime(openedDate);
			expiryAfterOpen.add(Calendar.DAY_OF_MONTH, validAfterOpening);

			if (expiryAfterOpen.before(expiryBoundary)) {
				expiryBoundary = expiryAfterOpen;
			}
		}

		// get freshness boundary by substracting 3 days from expiry date
		//item freshness categories are
		//[-inf, 0] (not fresh)
		//[0, 3] (neutral)
		//[3, +inf] (fresh)
		Calendar freshnessBoundary = Calendar.getInstance();
		freshnessBoundary.clear();
		freshnessBoundary.setTime(expiryBoundary.getTime());
		freshnessBoundary.add(Calendar.DAY_OF_MONTH, -3);

		// get current time
		Calendar now = Calendar.getInstance();
		clearTimeComponent(now);

		if (now.before(freshnessBoundary)) {
			// before (effectiveExpiry - 3days)
			return FRESH;
		} else if (now.before(expiryBoundary)) {
			// between (effectiveExpiry - 3days) and (effectiveExpiry)
			return NEUTRAL_FRESH;
		} else {
			// after (effectiveExpiry)
			return NOT_FRESH;
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
