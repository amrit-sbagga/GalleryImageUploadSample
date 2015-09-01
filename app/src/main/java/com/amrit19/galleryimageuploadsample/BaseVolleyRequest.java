package com.amrit19.galleryimageuploadsample;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by amritsingh on 8/28/2015.
 */
public class BaseVolleyRequest extends Request<NetworkResponse> {

    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private Response.Listener<NetworkResponse> mListener;
    private Response.ErrorListener mErrorListener;
    private File mImageFile;
    private static final String FILE_PART_NAME = "file";

    public BaseVolleyRequest(int method, String fileUploadUrl, Response.Listener<NetworkResponse> listener,
                             Response.ErrorListener errorListener, File mImageFile) {
        super(method, fileUploadUrl, errorListener);

        mListener = listener;
        this.mImageFile = mImageFile;
        this.mErrorListener = errorListener;
        buildMultipartEntity(mImageFile);
    }


    private void buildMultipartEntity(File mImageFile) {

        FileBody fileBody = new FileBody(mImageFile);
        mBuilder.addPart("image", fileBody);

        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    @Override
    public String getBodyContentType() {
        String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return contentTypeHeader;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mBuilder.build().writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }

        return bos.toByteArray();
    }

}
