package com.agendadigital.core.shared.infrastructure;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class AsyncHttpRest {
    private static final String BASE_URL = "http://192.168.1.7:3000";
//    private static final String BASE_URL = "http://192.168.100.65:3000/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void post(Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
        StringEntity stringEntity = new StringEntity(params.toString(), "UTF-8");
        stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8"));
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json;charset=utf-8");

        client.post(context, getAbsoluteUrl(url), stringEntity,"application/json;charset=utf-8", responseHandler);
        //client.post(context, getAbsoluteUrl(url), stringEntity,"application/json;charset=utf-8", responseHandler);

    }

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(context, getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
