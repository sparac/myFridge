package hr.fer.ztel.myFridge.data;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

/**
 * Wrapper class for image content stored as a byte array.
 * Used as an attribute in Food and UserFood classes.
 * @author suncana
 *
 */
public class FoodImage implements Serializable {

	private static final long serialVersionUID = 4172404535659664079L;

	private byte[] imageContent;

	public byte[] getImageContent() {
		return imageContent;
	}

	public void setImageContent(byte[] imageContent) {
		this.imageContent = imageContent;
	}

	public Bitmap getBitmap() {

		if (this.imageContent == null || this.imageContent.length == 0) {
			return null;
		}

		return BitmapFactory.decodeByteArray(this.imageContent, 0, this.imageContent.length);
	}

	public void setBitmap(Bitmap bitmap) {

		if (bitmap == null) {
			this.imageContent = null;
			return;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, baos);

		this.imageContent = baos.toByteArray();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof FoodImage))
			return false;

		if (other == this) {
			return true;
		}

		return Arrays.equals(imageContent, ((FoodImage) other).imageContent);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(imageContent);
	}
}
