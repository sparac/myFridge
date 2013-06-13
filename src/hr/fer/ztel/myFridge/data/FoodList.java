package hr.fer.ztel.myFridge.data;

import java.io.Serializable;
import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Wrapper class for UserFood objects stored in a list. Used for deserializing XML containing
 * a user food list and for reading/writing list to internal storage file.
 * @author suncana
 *
 */
@Root(name = "userFoods")
public class FoodList implements Serializable {

	private static final long serialVersionUID = -1402792975484000116L;

	@ElementList(inline = true, required = false)
	private ArrayList<UserFood> userfoods;

	public FoodList() {

	}

	public FoodList(ArrayList<UserFood> userfoods) {
		this.userfoods = userfoods;
	}

	public ArrayList<UserFood> getUserFoods() {
		if (userfoods != null) {
			return userfoods;
		}
		userfoods = new ArrayList<UserFood>();
		return userfoods;

	}

	public void setUserFoods(ArrayList<UserFood> userfoods) {
		this.userfoods = userfoods;
	}

}
