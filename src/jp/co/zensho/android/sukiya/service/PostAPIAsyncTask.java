package jp.co.zensho.android.sukiya.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import jp.co.zensho.android.sukiya.bean.ErrorInfo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class PostAPIAsyncTask extends AsyncTask<String, Void, JSONObject> {
    private CallAPIListener listener;
    private List<NameValuePair> data;
    private String tag;
    
    private ErrorInfo error;
    private final HttpClient client = new DefaultHttpClient();
    
    public PostAPIAsyncTask(CallAPIListener l, List<NameValuePair> data, String tag) {
        this.listener = l;
        this.data = data;
        this.tag = tag;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpPost request;
        HttpResponse response;
        String responseString = null;
        try {
            Log.d("CALL API", "Call API" + ((this.tag == null) ? "" : " " + this.tag) + "  - START");
            Log.d("CALL API", "URL: " + params[0] + " / Value: " + data.toString());
            
            request = new HttpPost(params[0]);
            request.setEntity(new UrlEncodedFormEntity(this.data, HTTP.UTF_8));
            
            // Send request to WCF service
            response = client.execute(request);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
                Log.e("responstring", responseString);

                JSONObject json = new JSONObject(responseString);
                return json;
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            if (error == null) {
                error = new ErrorInfo();
                error.setMessage(MessageFormat.format("System error: {0}", e.getMessage()));
            }
            e.printStackTrace();
        } catch (IOException e) {
            if (error == null) {
                error = new ErrorInfo();
                error.setMessage(MessageFormat.format("System error: {0}", e.getMessage()));
            }
            e.printStackTrace();
        } catch (JSONException e) {
            if (error == null) {
                error = new ErrorInfo();
                error.setMessage(MessageFormat.format("System error: {0}", e.getMessage()));
            }
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        Log.d("CALL API", "Call API" + ((this.tag == null) ? "" : " " + this.tag) + " - FINISH");
        
        if (this.listener != null) {
            this.listener.callAPIFinish(this.tag, result, this.error);
        }
        super.onPostExecute(result);
    }

}
