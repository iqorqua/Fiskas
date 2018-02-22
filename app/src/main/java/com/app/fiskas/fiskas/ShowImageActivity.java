package com.app.fiskas.fiskas;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bluehomestudio.progresswindow.ProgressWindow;
import com.bluehomestudio.progresswindow.ProgressWindowConfiguration;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thanosfisherman.mayi.Mayi;
import com.thanosfisherman.mayi.PermissionBean;
import com.thanosfisherman.mayi.PermissionToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import in.myinnos.awesomeimagepicker.activities.HelperActivity;
import in.myinnos.savebitmapandsharelib.SaveAndShare;


public class ShowImageActivity extends HelperActivity {

    public final static String APP_PATH_SD_CARD = "/DesiredSubfolderName/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";
    Bitmap bitmap = null;
    private ProgressWindow progressWindow;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        progressWindow.hideProgress();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        Mayi.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
                .onRationale(this::permissionRationaleMulti)
                .onResult(this::permissionResultMulti)
                .check();

        ImageView previewImage = (ImageView) findViewById(R.id.image_view_preview);

        Bundle args = getIntent().getExtras();
        String previewFilePath = args.get("image_link").toString();
        String previewFileDate = args.get("image_date").toString();
        String previewFileName = args.get("image_name").toString();
        progressConfigurations();
        progressWindow.showProgress();
        try {
            Picasso.with(this).load(previewFilePath).fit().centerInside().into(previewImage, new Callback() {
                @Override
                public void onSuccess() {
                    progressWindow.hideProgress();
                }

                @Override
                public void onError() {
                    previewImage.setImageResource(R.drawable.error);
                    progressWindow.hideProgress();
                    Snackbar.make((ImageView) findViewById(R.id.image_view_preview), R.string.error_loadind, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
            finish();
        }
            ;
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
                    MediaStore.Images.Media.insertImage(getContentResolver(), ((BitmapDrawable) previewImage.getDrawable()).getBitmap(), previewFileName+".jpeg", previewFileDate);
                    SaveAndShare.save(ShowImageActivity.this, ((BitmapDrawable) previewImage.getDrawable()).getBitmap(), previewFileName, previewFileDate, previewFilePath);
                    bitmap = ((BitmapDrawable) previewImage.getDrawable()).getBitmap();
                    if (bitmap != null){
                        saveToInternalStorage(bitmap);
                        finish();
                    }
                    else {
                        Snackbar.make(view, getResources().getText(R.string.photo_send_alert_image_name), Snackbar.LENGTH_LONG)
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

    private void permissionResultMulti(PermissionBean[] permissionBeans) {
    }

    private void permissionRationaleMulti(PermissionBean[] permissionBeans, PermissionToken permissionToken) {
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


    private void progressConfigurations(){
        progressWindow = ProgressWindow.getInstance(this);
        ProgressWindowConfiguration progressWindowConfiguration = new ProgressWindowConfiguration();
        progressWindowConfiguration.backgroundColor = Color.parseColor("#32000000") ;
        progressWindowConfiguration.progressColor = Color.WHITE ;
        progressWindow.setConfiguration(progressWindowConfiguration);
    }
}
