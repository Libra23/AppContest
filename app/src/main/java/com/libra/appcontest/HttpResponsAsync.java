package com.libra.appcontest;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hiroki on 2017/10/17.
 */

public class HttpResponsAsync extends AsyncTask <JSONObject, Void, Integer> {

    private final static String TAG = "HttpResponsAsync";
    private static final String POST_URL = "hello";
    private static final String GET_URL = "world";

    public HttpResponsAsync() {
        //super();
    }

    @Override
    protected Integer doInBackground(JSONObject... params) {
        //super.doInBackground(params);
        OkHttpClient client = new OkHttpClient();
        final String url = POST_URL;

        final MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, params[0].toString());
        Request request = new Request
                .Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d(TAG, response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        //super.onPostExecute(result);
        Log.d(TAG, "onPostExcute");
    }

    public String readInputStream(InputStream in) throws IOException, UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        String st = "";

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while((st = br.readLine()) != null)
        {
            sb.append(st);
        }
        try
        {
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
