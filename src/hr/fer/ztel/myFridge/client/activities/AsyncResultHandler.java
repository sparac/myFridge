package hr.fer.ztel.myFridge.client.activities;

import android.os.AsyncTask;
/**
 * Interface that enforces implementation of methods that handle async task result.
 * @author suncana
 *
 */
public interface AsyncResultHandler {

	public void handleAsyncTaskSuccess(AsyncTask<?, ?, ?> source, Object... results);

	public void handleAsyncTaskFailure(AsyncTask<?, ?, ?> source, String title, String message,
			Throwable cause);
}
