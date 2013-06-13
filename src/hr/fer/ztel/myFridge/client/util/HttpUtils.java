package hr.fer.ztel.myFridge.client.util;

import hr.fer.ztel.myFridge.data.Food;
import hr.fer.ztel.myFridge.data.FoodList;
import hr.fer.ztel.myFridge.data.User;
import hr.fer.ztel.myFridge.data.UserFood;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.transform.RegistryMatcher;

import android.util.Log;

/**
 * Class that provides methods for sending HTTP requests. Sets authorization header to
 * outgoing requests if necessary.
 * @author suncana
 *
 */
public class HttpUtils {

	//method overloading so user can choose whether to pass credentials directly
	//or have them set within sendRequest method via SecurityContext singleton.
	/**
	 * Method for sending HTTP requests. Overloading the sendRequest(HttpUriRequest request, Credentials credentials)
	 * method so user can choose whether to pass credentials directly
	 * or have them set within sendRequest method via SecurityContext singleton.
	 * @param request HTTP request to be sent
	 * @return calls sendRequest(HttpUriRequest request, Credentials credentials) and returns
	 * its result
	 * @throws IOException
	 */
	public static HttpResponse sendRequest(HttpUriRequest request) throws IOException {

		Credentials credentials = null;

		User currentUser = SecurityContext.getInstance().getCurrentUser();
		if (currentUser != null) {

			//constructing credentials to be passed to the request
			credentials = new UsernamePasswordCredentials(currentUser.getUsername(),
					currentUser.getPassword());
		}

		return sendRequest(request, credentials);
	}

	/**
	 * Method for sending HTTP requests.
	 * @param request HTTP request to be sent
	 * @param credentials Credentials to be set to the request
	 * @return HttpResponse
	 * @throws IOException
	 */
	public static HttpResponse sendRequest(HttpUriRequest request, Credentials credentials)
			throws IOException {

		
		final HttpParams httpParams = new BasicHttpParams();
		
		// set request timeouts at 10 seconds
		// HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		// HttpConnectionParams.setSoTimeout(httpParams, 10000);
		
		DefaultHttpClient client = new DefaultHttpClient(httpParams);

		// enable preemptive authentication
		client.addRequestInterceptor(new PreemptiveAuthenticationInterceptor(), 0);

		if (credentials != null) {

			client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
		}

		HttpResponse response = client.execute(request);
		Log.i(Constants.LOG_TAG, "Request executed, response code "
				+ response.getStatusLine().getStatusCode());
		return response;

	}

	/**
	 * Method that serializes User object to XML.
	 * @param user User object to be serialized
	 * @return userXML containing serialized user
	 * @throws Exception
	 */
	public static String serializeUser(User user) throws Exception {

		Strategy strategy = new AnnotationStrategy();
		Persister persister = new Persister(strategy);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		persister.write(user, bos, "UTF-8");

		return bos.toString("UTF-8");
	}

	/**
	 * Method that deserializes user from XML to User object.
	 * @param br BufferedReader retrieved from HTTP response
	 * @return User object
	 * @throws Exception
	 */
	public static User deSerializeUser(BufferedReader br) throws Exception {

		Strategy strategy = new AnnotationStrategy();
		Serializer serializer = new Persister(strategy);

		User user = serializer.read(User.class, br);

		br.close();
		return user;
	}

	/**
	 * Method that deserializes foor from XML to Food object.
	 * @param br BufferedReader retrieved from HTTP response
	 * @return Food object
	 * @throws Exception
	 */
	public static Food deSerializeFood(BufferedReader br) throws Exception {

		Strategy strategy = new AnnotationStrategy();
		Serializer serializer = new Persister(strategy);

		Food food = serializer.read(Food.class, br);

		br.close();
		return food;
	}

	/**
	 * Method that deserializes user food items from XML to FoodList object.
	 * @param br BufferedReader retrieved from HTTP response
	 * @return FoodList object used as a wrapper for a list of UserFood objects
	 * @throws Exception
	 */
	public static FoodList deserializeUserFoods(BufferedReader br) throws Exception {

		Strategy strategy = new AnnotationStrategy();

		RegistryMatcher matcher = new RegistryMatcher();
		//intercepting Date conversion with a custom converter, necessary because of
		//JAXB->SimpleXml incompatibility
		matcher.bind(Date.class, new ISO8601DateTransform());

		Serializer serializer = new Persister(strategy, matcher);
		FoodList userFoods = serializer.read(FoodList.class, br);

		br.close();
		return userFoods;
	}

	/**
	 * Method that deserializes user food item from XML to UserFood object.
	 * @param br BufferedReader retrieved from HTTP response
	 * @return UserFood object
	 * @throws Exception
	 */
	public static UserFood deserializeUserFood(BufferedReader br) throws Exception {

		Strategy strategy = new AnnotationStrategy();

		RegistryMatcher matcher = new RegistryMatcher();
		//intercepting Date conversion with a custom converter, necessary because of
		//JAXB->SimpleXml incompatibility
		matcher.bind(Date.class, new ISO8601DateTransform());

		Serializer serializer = new Persister(strategy, matcher);
		UserFood userFood = serializer.read(UserFood.class, br);

		br.close();
		return userFood;
	}
}
