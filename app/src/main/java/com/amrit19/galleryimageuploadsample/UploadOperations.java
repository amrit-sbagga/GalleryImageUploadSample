package com.amrit19.galleryimageuploadsample;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by amritsingh on 8/28/2015.
 */
public class UploadOperations {

    private Context mContext;
    String responseString = null;
    StringBuilder responseBuilder = new StringBuilder();

    public UploadOperations(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Uploading File using AndroidMultiPartEntity
     * @param imagePath1
     * @return responseString
     */
    @Deprecated
    public String uploadFile(String imagePath1) {
        long totalSize = 0;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            // publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            File sourceFile = new File(imagePath1);//filePath);

            // Adding file data to http body
            entity.addPart("image", new FileBody(sourceFile));
            totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

        } catch (ClientProtocolException e) {
            Log.e("uploadFile: ", "ClientProtocolException");
            responseString = e.toString();
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("uploadFile: ", "IOException");
            responseString = e.toString();
            e.printStackTrace();
        }

        return responseString;

    }

    /**
     *
     * Uploading file using MultipartEntityBuilder
     * @param imagePath
     * @return responseString
     */
    @Deprecated
    public String uploadFileNew(String imagePath) {
        String responseString = null;

        try {
            HttpClient client = new DefaultHttpClient();
            File file = new File(imagePath);
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                 /* example for adding an image part */
            FileBody fileBody = new FileBody(new File(imagePath)); //image should be a String
            entityBuilder.addPart("image", fileBody);

            HttpEntity entity = entityBuilder.build();
            long totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = client.execute(httppost);
            HttpEntity httpEntity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(httpEntity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

            Log.v("result", responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseString;
    }

    /**
     * Uploading multiple files using Volley
     * @param selectedItems
     * @param fileUploadUrl
     * @return responseBuilder.toString()
     */
    public String uploadMultipleFilesUsingVolley(ArrayList<String> selectedItems, String fileUploadUrl) {

        try {

            for (int item = 0; item < selectedItems.size(); item++) {
                File imageFile = new File(selectedItems.get(item));

                BaseVolleyRequest myRequest = new BaseVolleyRequest(Request.Method.POST, fileUploadUrl,
                        new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                try {
                                    String jsonString = new String(response.data,
                                            HttpHeaderParser.parseCharset(response.headers));
                                    Toast.makeText(mContext, jsonString, Toast.LENGTH_SHORT).show();
                                    responseString = jsonString;
                                    responseBuilder.append(responseString);
                                    Log.v("-----SUCCESS-----", "-----SUCCESS-----");
                                    Log.d("ResponseString: ", responseString);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                        responseString = error.toString();
                        responseBuilder.append(responseString);
                        Log.v("-----ERROR-----", "-----ERROR-----");
                        Log.d("ResponseString: " , responseString);
                    }
                }, imageFile);
                AppController.getInstance().addToRequestQueue(myRequest);
            }

        } catch (Exception ec) {
            ec.printStackTrace();
        }


        /*try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return responseBuilder.toString();
    }

    /**
     * Uploading File using Volley
     * @param imagePath
     * @param fileUploadUrl
     * @return responseString
     */
    public String uploadFileUsingVolley(String imagePath, String fileUploadUrl) {

        try {
            File imageFile = new File(imagePath);

            BaseVolleyRequest myRequest = new BaseVolleyRequest(Request.Method.POST, fileUploadUrl,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                String jsonString = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers));
                                Toast.makeText(mContext, jsonString, Toast.LENGTH_SHORT).show();
                                responseString = jsonString;
                                responseBuilder.append(responseString);
                                Log.v("-----SUCCESS-----", "-----SUCCESS-----");
                                Log.d("ResponseString: " , responseString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                    responseString = error.toString();
                    responseBuilder.append(responseString);
                    Log.v("-----ERROR-----", "-----ERROR-----");
                    Log.d("ResponseString: " , responseString);
                }
            }, imageFile);
            AppController.getInstance().addToRequestQueue(myRequest);

        } catch (Exception ec) {
            ec.printStackTrace();
        }

        return responseBuilder.toString();
    }


    private StringBuffer request(String urlString) {

        StringBuffer chain = new StringBuffer("");
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            // Allow Outputs
            connection.setDoOutput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chain.append(line);
            }

        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
        }

        return chain;
    }

}
