package hr.fer.ztel.myFridge.data;

import java.io.Serializable;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Class representing a user food item.
 * @author suncana
 *
 */
@Root(name = "userFood")
public class UserFood implements Serializable {

	private static final long serialVersionUID = 4090084358907982326L;

	@Element(name = "id")
	private Integer id;

	@Element(name = "dateExpiry")
	private Date dateExpiry;

	@Element(name = "dateOpened", required = false)
	private Date dateOpened;

	@Element(name = "food")
	private Food food;

	@Element(name = "validAfterOpening")
	private Integer validAfterOpening;

	public UserFood() {

	}

	public UserFood(Integer id, Date dateAdded, Date dateExpiry, Date dateOpened, Food food,
			Integer validAfterOpening) {

		this.id = id;
		this.dateExpiry = dateExpiry;
		this.dateOpened = dateOpened;
		this.food = food;
		this.validAfterOpening = validAfterOpening;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDateExpiry() {
		return dateExpiry;
	}

	public void setDateExpiry(Date dateExpiry) {
		this.dateExpiry = dateExpiry;
	}

	public Date getDateOpened() {
		return dateOpened;
	}

	public void setDateOpened(Date dateOpened) {
		this.dateOpened = dateOpened;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	public Integer getValidAfterOpening() {
		return validAfterOpening;
	}

	public void setValidAfterOpening(Integer validAfterOpening) {
		this.validAfterOpening = validAfterOpening;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateExpiry == null) ? 0 : dateExpiry.hashCode());
		result = prime * result + ((dateOpened == null) ? 0 : dateOpened.hashCode());
		result = prime * result + ((food == null) ? 0 : food.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((validAfterOpening == null) ? 0 : validAfterOpening.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserFood other = (UserFood) obj;
		if (dateExpiry == null) {
			if (other.dateExpiry != null)
				return false;
		} else if (!dateExpiry.equals(other.dateExpiry))
			return false;
		if (dateOpened == null) {
			if (other.dateOpened != null)
				return false;
		} else if (!dateOpened.equals(other.dateOpened))
			return false;
		if (food == null) {
			if (other.food != null)
				return false;
		} else if (!food.equals(other.food))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (validAfterOpening == null) {
			if (other.validAfterOpening != null)
				return false;
		} else if (!validAfterOpening.equals(other.validAfterOpening))
			return false;
		return true;
	}
}
