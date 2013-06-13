package hr.fer.ztel.myFridge.data;

import java.io.Serializable;

import hr.fer.ztel.myFridge.client.util.FoodImageConverter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;
/**
 * Class representing a food item.
 * @author suncana
 *
 */
@Root(name = "foodItem")
public class Food implements Serializable {

	private static final long serialVersionUID = 5053222412278835581L;

	@Element(name = "id")
	private Integer id;

	@Element(name = "barcode")
	private String barcode;

	@Element(name = "name")
	private String name;

	@Element(name = "manufacturer", required = false)
	private String manufacturer;

	@Element(name = "imageSmall")
	@Convert(FoodImageConverter.class)
	private FoodImage imageSmall;

	@Element(name = "imageLarge")
	@Convert(FoodImageConverter.class)
	private FoodImage imageLarge;

	@Element(name = "description", required = false)
	private String description;

	public Food() {
	}

	public Food(Integer id, String barcode, String name, String manufacturer, FoodImage imageSmall,
			FoodImage imageLarge, String description) {
		super();
		this.id = id;
		this.barcode = barcode;
		this.name = name;
		this.manufacturer = manufacturer;
		this.imageSmall = imageSmall;
		this.imageLarge = imageLarge;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public FoodImage getImageSmall() {
		return imageSmall;
	}

	public void setImageSmall(FoodImage imageSmall) {
		this.imageSmall = imageSmall;
	}

	public FoodImage getImageLarge() {
		return imageLarge;
	}

	public void setImageLarge(FoodImage imageLarge) {
		this.imageLarge = imageLarge;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((barcode == null) ? 0 : barcode.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imageLarge == null) ? 0 : imageLarge.hashCode());
		result = prime * result + ((imageSmall == null) ? 0 : imageSmall.hashCode());
		result = prime * result + ((manufacturer == null) ? 0 : manufacturer.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Food other = (Food) obj;
		if (barcode == null) {
			if (other.barcode != null)
				return false;
		} else if (!barcode.equals(other.barcode))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (imageLarge == null) {
			if (other.imageLarge != null)
				return false;
		} else if (!imageLarge.equals(other.imageLarge))
			return false;
		if (imageSmall == null) {
			if (other.imageSmall != null)
				return false;
		} else if (!imageSmall.equals(other.imageSmall))
			return false;
		if (manufacturer == null) {
			if (other.manufacturer != null)
				return false;
		} else if (!manufacturer.equals(other.manufacturer))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
