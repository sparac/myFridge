package hr.fer.ztel.myFridge.client.util;

import hr.fer.ztel.myFridge.data.FoodList;
import hr.fer.ztel.myFridge.data.UserFood;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import android.content.Context;

/**
 * Class that provides methods for storing and retrieving user food list from internal storage.
 * @author suncana
 *
 */
public class FileUtil {

	/**
	 * Method that stores user food list to internal storage.
	 * @param foodList FoodList object to be saved to storage
	 * @param context Context used for opening file output
	 * @throws IOException
	 */
	public static void storeFoodList(FoodList foodList, Context context) throws IOException {

		//getting username
		String username = SecurityContext.getInstance().getCurrentUser().getUsername();

		ObjectOutput out = null;

		try {
			//opening user food list file named after username
			FileOutputStream fos = context.getApplicationContext().openFileOutput(username,
					Context.MODE_PRIVATE);

			//writing foodList to file
			out = new ObjectOutputStream(fos);
			out.writeObject(foodList);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Method that retrievs user food list from internal storage.
	 * @param context Context used for opening file input
	 * @return foodList FoodList object wrapping a list of user food items
	 * @throws IOException
	 */
	public static FoodList retrieveFoodList(Context context) throws IOException {

		//getting username
		String username = SecurityContext.getInstance().getCurrentUser().getUsername();

		FoodList foodList = null;
		ObjectInputStream in = null;

		//if no file exist create one and store an empty list
		if (!context.getFileStreamPath(username).exists()) {
			ArrayList<UserFood> list = new ArrayList<UserFood>();
			foodList = new FoodList(list);
			storeFoodList(foodList, context);
		} else {
			try {
				//open file input and read FoodList object from file
				FileInputStream fis = context.getApplicationContext().openFileInput(username);
				in = new ObjectInputStream(fis);

				foodList = (FoodList) in.readObject();
				in.close();

			} catch (ClassNotFoundException e) {
				throw new IOException("Deserialization failed", e);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}

		//returning either an empty FoodList object or a FoodList object read from file
		return foodList;
	}
}
