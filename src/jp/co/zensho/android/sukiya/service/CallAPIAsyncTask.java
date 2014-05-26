package jp.co.zensho.android.sukiya.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import jp.co.zensho.android.sukiya.bean.ErrorInfo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class CallAPIAsyncTask extends AsyncTask<String, Void, JSONObject> {
	private CallAPIListener listener;
	private String tag;

	private ErrorInfo error;
	private final HttpClient client = new DefaultHttpClient();

	public CallAPIAsyncTask(CallAPIListener l, String tag) {
		this.listener = l;
		this.tag = tag;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		JSONObject json = null;
		try {
			Log.d("CALL API", "Call API " + params[0]
					+ ((this.tag == null) ? "" : " " + this.tag) + "  - START");

			HttpResponse response = client.execute(new HttpGet(params[0]));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();

				String responseData = out.toString();
				System.out.println(responseData);
				json = new JSONObject(responseData);
				System.out.println(json.toString());
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			if (error == null) {
				error = new ErrorInfo();
				error.setMessage(MessageFormat.format("System error: {0}",
						e.getMessage()));
			}
			e.printStackTrace();
		} catch (IOException e) {
			if (error == null) {
				error = new ErrorInfo();
				error.setMessage(MessageFormat.format("System error: {0}",
						e.getMessage()));
			}
			e.printStackTrace();
		} catch (JSONException e) {
			if (error == null) {
				error = new ErrorInfo();
				error.setMessage(MessageFormat.format("System error: {0}",
						e.getMessage()));
			}
			e.printStackTrace();
		}
		System.out.println(json.toString());
		return json;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		Log.d("CALL API", "Call API"
				+ ((this.tag == null) ? "" : " " + this.tag) + " - FINISH");

		if (this.listener != null) {
			this.listener.callAPIFinish(this.tag, result, this.error);
		}
		super.onPostExecute(result);
	}

}
