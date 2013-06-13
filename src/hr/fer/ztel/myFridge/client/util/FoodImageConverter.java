package hr.fer.ztel.myFridge.client.util;

import hr.fer.ztel.myFridge.data.FoodImage;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import android.util.Base64;

/**
 * Class used as a converter when intercepting deserialization of Food object. Used to
 * intercept FoodImage deserialization. Necessary because of JAXB -> SimpleXml incompatibility. 
 * @author suncana
 *
 */
public class FoodImageConverter implements Converter<FoodImage> {

	@Override
	public FoodImage read(InputNode node) throws Exception {

		//reading the Base64 encoded image from xml, decoding it to byte array
		//and setting it to a FoodImage wrapper class
		String imageBase64 = node.getValue();
		byte[] imageBinary = Base64.decode(imageBase64, Base64.DEFAULT);

		FoodImage foodImage = new FoodImage();
		foodImage.setImageContent(imageBinary);

		return foodImage;
	}

	@Override
	public void write(OutputNode node, FoodImage image) throws Exception {

		//reading byte array from FoodImage wrapper class
		//and encoding it to a Base64 String
		byte[] imageBinary = image.getImageContent();
		String imageBase64 = Base64.encodeToString(imageBinary, Base64.DEFAULT);

		node.setValue(imageBase64);
	}
}
