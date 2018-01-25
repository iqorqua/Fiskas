package com.app.fiskas.fiskas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import in.myinnos.awesomeimagepicker.activities.HelperActivity;


public class ShowImageActivity extends HelperActivity {

    public final static String APP_PATH_SD_CARD = "/DesiredSubfolderName/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        ImageView previewImage = (ImageView) findViewById(R.id.image_view_preview);

        Bundle args = getIntent().getExtras();
        String previewFilePath = args.get("image_link").toString();
        String previewFileDate = args.get("image_date").toString();
        String previewFileName = args.get("image_name").toString();
        Picasso.with(this).load(previewFilePath).into(previewImage);
        /*ImageViewFuture ion = Ion.with(previewImage)
                .centerCrop()
                .error(R.drawable.error)
                .load(previewFilePath);*/

        findViewById(R.id.btn_preview_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btn_preview_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(ShowImageActivity.this);
                pd.setCancelable(false);
                pd.setTitle("Wait...");
                pd.setMessage("Loading...");
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(), ((BitmapDrawable) previewImage.getDrawable()).getBitmap(), previewFileName, previewFileDate);
                    bitmap = ((BitmapDrawable) previewImage.getDrawable()).getBitmap();
                    if (bitmap != null){
                        saveToInternalStorage(bitmap);
                        finish();
                    }
                    else {
                        Snackbar.make(view, getResources().getText(R.string.picture_not_ready), Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
                catch (Exception ex){
                    Snackbar.make(view, ex.getMessage(), Snackbar.LENGTH_LONG)
                            .show();

                    pd.dismiss();
                }
            }
        });
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("FiskasPhoto", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, UUID.randomUUID().toString() + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
