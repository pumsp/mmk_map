package com.example.pumsper.mmk;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class getData {

    //    JSONObject jObject;
    JSONArray jsonArray;
    public getData(String url){

        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpPost httppost = new HttpPost(url);
        // Depends on your web service
        httppost.setHeader("Content-type", "application/json");

        InputStream inputStream = null;
        String result = null;
        try {

            Log.w("jsTest", "aa_");
            HttpResponse response = httpclient.execute(httppost);
            Log.w("jsTest", "aa_");
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();


            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
            StringBuilder sb = new StringBuilder();

            Log.w("jsTest", "aa_");
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");


            }
            is.close();


            result = sb.toString();
            Log.w("jsTest","result_"+result);

            jsonArray = new JSONArray(result);
            Log.i("js", "jsArray_"+jsonArray);
//                JSONElement jsonElem = new JsonParser().parse(result);
//                if(jsonElem.isJsonArray()) {
//                    // Normal data
//                } else {
//                    // 'Error' data'
//                }

            //jObject = jsArray.getJSONObject(0);

            //Log.i("jsTest", "jObject_"+jObject);



        } catch (Exception e) {
            // Oops
            Log.w("error", e.getMessage());
        }
        finally {
            try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
        }

    }



}
