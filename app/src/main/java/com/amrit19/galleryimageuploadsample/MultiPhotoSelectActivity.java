package com.amrit19.galleryimageuploadsample;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class MultiPhotoSelectActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MultiPhotoSelectActivity.class.getSimpleName();
    private ArrayList<String> imageUrlsList;
    private GridView gridView;
    private Button btnSelectPhotos;
    private ImageAdapter imageAdapter;
    // private ImageRecyclerAdapter imageRecyclerAdapter;
    private Context mContext;
    private Cursor imageCursor;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    //long totalSize = 0;
    private ArrayList<String> selectedItems = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_photo_select);

        mContext = MultiPhotoSelectActivity.this;
        imageUrlsList = new ArrayList<String>();

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.stub_image)
                .showImageForEmptyUri(R.drawable.image_for_empty_url)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();

        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        try {
            imageCursor = mContext.getContentResolver().query//managedQuery
                    (MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            columns, null, null, orderBy + " DESC");
        } catch (Exception ec) {
            ec.printStackTrace();
        }

        if (imageCursor != null) {
            for (int i = 0; i < imageCursor.getCount(); i++) {
                imageCursor.moveToPosition(i);
                int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                imageUrlsList.add(imageCursor.getString(dataColumnIndex));

                //  Log.i("ImagePath [ " + i + "] = ", imageUrlsList.get(i));
            }

        } else {
            // Log.e(TAG, "cursor is null");
        }

        btnSelectPhotos = (Button) findViewById(R.id.btnSelectPhotos);
        btnSelectPhotos.setOnClickListener(this);

        imageAdapter = new ImageAdapter(this, imageUrlsList, imageLoader, options);
        // imageRecyclerAdapter = new ImageRecyclerAdapter(this, imageUrlsList, imageLoader, options);
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(imageAdapter);
        // gridView.setAdapter(imageRecyclerAdapter);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSelectPhotos:
                selectPhotos();
                break;
        }
    }

    private void selectPhotos() {

        selectedItems = imageAdapter.getCheckedItems();
        Toast.makeText(MultiPhotoSelectActivity.this, "Total photos selected: " + selectedItems.size(), Toast.LENGTH_SHORT).show();
        Log.d(MultiPhotoSelectActivity.class.getSimpleName(), "Selected Items: " + selectedItems.toString());

        if (selectedItems != null) {
            if (selectedItems.size() > 0) {
                 /* using async task */
                // new UploadImageToServer(selectedItems).execute();
                /* using volley */
                String response = new UploadOperations(mContext).
                        uploadMultipleFilesUsingVolley(selectedItems, Config.FILE_UPLOAD_URL);
                if (response != null) {
                    // showing the server response in an alert dialog
                   // String title = "Response from Server: ";
                   // showAlert(response, title);

                    //uncheck all checkboxes after upload
                    uncheckAllCheckBoxes();
                    selectedItems.clear();

                } else {
                    Log.v(TAG, "response is null");
                }


            } else {
                Log.d(TAG, "selected item size is 0");
                Toast.makeText(mContext, "Please select images to upload!!", Toast.LENGTH_LONG).show();
            }
        }

    }


    private class UploadImageToServer extends AsyncTask<String, Integer, String> {

        private ArrayList<String> selectedItems;
        private ProgressDialog progressDialog;

        public UploadImageToServer(ArrayList<String> selectedItems) {
            this.selectedItems = selectedItems;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.v("UploadImageToServer: ", "onPreExecute()");

            progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Uploading Image...");
            progressDialog.setMessage("Upload in progress ...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //return new UploadOperations(mContext).uploadFile(selectedItems.get(0));
            return new UploadOperations(mContext).uploadFileNew(selectedItems.get(0));
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            String title = "Response from Server: ";
            showAlert(result, title);

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            //uncheck all checkboxes after upload
            uncheckAllCheckBoxes();

            super.onPostExecute(result);
        }

    }//end of async class

    private void uncheckAllCheckBoxes() {
        imageAdapter.updateAdapter();
    }

    private void showAlert(String message, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}