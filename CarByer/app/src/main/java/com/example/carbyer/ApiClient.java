package com.example.carbyer;

import android.os.Looper;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ApiClient {

    public static final String BASE_URL = "http://10.0.2.2:8088/";

    public interface Callback{
        void onSuccess(JSONObject body);
        void onError(int httpCode, String message);
    }

    private static final ExecutorService EXEC = Executors.newCachedThreadPool();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    public static void post(String path, JSONObject body, String token, Callback cb){
        request("POST", path, body, token, cb);
    }

    public static void get(String path, String token, Callback cb){
        request("GET", path, null, token, cb);
    }

    public static void get(String path, Map<String, String> query, String token, Callback cb){
        request("GET", path + buildQuery(query), null, token, cb);
    }

    private static String buildQuery(Map<String, String> query) {

        if(query == null || query.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for(Map.Entry<String, String> e : query.entrySet()){
            if(e.getValue() == null)
                continue;

            if(!first)
                sb.append("&");

            sb.append(e.getKey());
            sb.append("=");
            sb.append(e.getValue());

            first = false;
        }

        return sb.toString();
    }

    private static void request(String method, String path, JSONObject body, String token, Callback cb) {
        EXEC.execute(
                () -> {
                    HttpURLConnection con = null;

                    try{
                        URL url = new URL(BASE_URL + path);
                        con = (HttpURLConnection) url.openConnection();

                        con.setRequestMethod(method);
                        con.setConnectTimeout(60000);
                        con.setReadTimeout(60000);

                        con.setRequestProperty("Accept", "application/json");

                        if(token != null){
                            con.setRequestProperty("Authorization", "Bearer " + token);
                        }

                        if(body != null){
                            con.setDoOutput(true);
                            con.setRequestProperty("Content-Type", "application/json");
                            byte[] bodyArray = body.toString().getBytes();

                            try(OutputStream os = con.getOutputStream()){
                                os.write(bodyArray);
                            }
                        }

                        int code = con.getResponseCode();
                        InputStream stream;

                        if(code >= 200 && code <= 300){
                            stream = con.getInputStream();
                        }else{
                            stream = con.getErrorStream();
                        }


                        String text = readAll(stream);

                        if (text == null || text.trim().isEmpty()) {
                            MAIN.post(() -> cb.onError(code, "Empty response from server"));
                            return;
                        }

                        try {
                            JSONObject json = new JSONObject(text);


                            if(code >= 200 && code <= 300){
                                MAIN.post( () -> cb.onSuccess(json));
                            }else{
                                MAIN.post(() -> cb.onError(code, json.optString("message", "Request failed")));
                            }
                        } catch (JSONException e) {
                            MAIN.post(() -> cb.onError(code, "Invalid JSON: " + text));
                        }

                    } catch (IOException e) {
                        MAIN.post(() ->
                                cb.onError(-1, e.getMessage())
                        );
                    }finally {
                        if(con != null)
                            con.disconnect();
                    }
                });
    }

    private static String readAll(InputStream stream) throws IOException {
        if(stream == null) return "";

        StringBuilder sb = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
            String line;

            while( (line = br.readLine()) != null){
                sb.append(line);
            }
        }

        return sb.toString();
    }
}
