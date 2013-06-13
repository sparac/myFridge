package hr.fer.ztel.myFridge.client.activities;

import java.io.IOException;

import hr.fer.ztel.myFridge.R;
import hr.fer.ztel.myFridge.client.activities.adapters.MyCustomArrayAdapter;
import hr.fer.ztel.myFridge.client.activities.tasks.DeleteUserFoodTask;
import hr.fer.ztel.myFridge.client.util.FileUtil;
import hr.fer.ztel.myFridge.data.FoodList;
import hr.fer.ztel.myFridge.data.UserFood;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * ListActivity class that displays the user food items in a list.
 * @author suncana
 *
 */
public class UserFoodListActivity extends ListActivity {

	public static final int MODIFY_USER_FOOD_REQUEST = 0;
	public static final int CLONE_USER_FOOD_REQUEST = 1;
	private static FoodList foodList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			//retrieving the food list from file
			foodList = FileUtil.retrieveFoodList(UserFoodListActivity.this);
		} catch (IOException e) {
			// TODO create alert dialog
			e.printStackTrace();
		}
		if (foodList.getUserFoods() != null) {
			//instantiating a custom array adapter and passing the food list
			ArrayAdapter<UserFood> adapter = new MyCustomArrayAdapter(this, foodList.getUserFoods());
			setListAdapter(adapter);
			ListView listView=getListView();
			//removing the list divider line
			listView.setDivider(this.getResources().getDrawable(R.drawable.transparent_color));
		}
		registerForContextMenu(getListView());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//checking result code
		if (intent != null && resultCode == MODIFY_USER_FOOD_REQUEST) {
			UserFood userFood = (UserFood) intent.getSerializableExtra("userFood");
			
			//setting the modified user food item to its original index in the list
			foodList.getUserFoods().set(requestCode, userFood);
			((MyCustomArrayAdapter) getListAdapter()).notifyDataSetChanged();
			
		} else if (intent != null && resultCode == CLONE_USER_FOOD_REQUEST) {
			UserFood userFood = (UserFood) intent.getSerializableExtra("userFood");

			// adding new user food item to list
			foodList.getUserFoods().add(userFood);
			((MyCustomArrayAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//on item click start user food details activity and pass user food item
		//to be displayed
		Intent userItemDetailsIntent = new Intent(UserFoodListActivity.this,
				UserFoodDetailsActivity.class);
		userItemDetailsIntent.putExtra("userFood", foodList.getUserFoods().get(position));
		startActivityForResult(userItemDetailsIntent, position);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.item_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		//on long click and selection of the delete item option, execute DeleteUserFoodTask
		switch (item.getItemId()) {
		case R.id.delete_item:
			DeleteUserFoodTask deleteUserFoodTask = new DeleteUserFoodTask(UserFoodListActivity.this);
			deleteUserFoodTask.execute(foodList.getUserFoods().get(info.position));

		}
		return false;
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
	 * Method that is called on succesful execution of any task, displays
	 * an alert dialog to inform user of failure and removes user food item from list.
	 * @param title Alert dialog title
	 * @param message Alert dialog message
	 * @param deletedItem deleted user food item
	 */
	public void handleDeleteFoodSuccess(String title, String message, final UserFood deletedItem) {
		
		//on successful deletion from db remove item from list
		foodList.getUserFoods().remove(deletedItem);
		((MyCustomArrayAdapter) getListAdapter()).notifyDataSetChanged();
		
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

}
