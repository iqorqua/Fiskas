package com.app.fiskas.fiskas;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dewarder.camerabutton.CameraButton;
import com.google.android.cameraview.CameraView;
import com.thanosfisherman.mayi.Mayi;
import com.thanosfisherman.mayi.PermissionBean;
import com.thanosfisherman.mayi.PermissionToken;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

import static com.app.fiskas.fiskas.PhotoResultSend.FILE_PATH_ARG;


public class CameraActivity extends AppCompatActivity {
    private String TAG = "Fiskas camera";
    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d(TAG, "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d(TAG, "onPictureTaken " + data.length);
            getBackgroundHandler().post(new Runnable() {
                @Override
                public void run() {
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            UUID.randomUUID().toString() + ".jpg");
                    file.deleteOnExit();
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(data);
                        os.close();
                        Intent intent = new Intent(getBaseContext(), PhotoResultSend.class);
                        intent.putExtra(FILE_PATH_ARG, file.getPath());
                        startActivityForResult(intent, 1);
                    } catch (IOException e) {
                        Log.w(TAG, "Cannot write to " + file, e);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }
                }
            });
        }

    };
    private CameraView mCameraView;
    private Handler mBackgroundHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Mayi.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
                .onRationale(this::permissionRationaleMulti)
                .onResult(this::permissionResultMulti) 
                .check();
        findViewById(R.id.record_panel_activity).bringToFront();
        findViewById(R.id.record_panel_activity).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_select_photo_concamera).setVisibility(View.VISIBLE);
        findViewById(R.id.txt_close_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCameraView = (CameraView) findViewById(R.id.camera);

        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }

        ((CameraButton)findViewById(R.id.record_button)).setOnTapEventListener(new CameraButton.OnTapEventListener() {
            @Override public void onTap() {
                mCameraView.takePicture();
            }
        });

        findViewById(R.id.btn_select_photo_concamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AlbumSelectActivity.class);
                intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 50); // set limit for image selection
                startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
            }
        });
    }

    private void permissionResultMulti(PermissionBean[] permissionBeans) {
    }

    private void permissionRationaleMulti(PermissionBean[] permissionBeans, PermissionToken permissionToken) {
    }


    @Override
    protected void onStart() {
        super.onStart();
        mCameraView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);

            for (int i = 0; i < images.size(); i++) {
                Intent intent = new Intent(getBaseContext(), PhotoResultSend.class);
                intent.putExtra(FILE_PATH_ARG, Uri.fromFile(new File(images.get(i).path)).getPath());
                startActivityForResult(intent, 1);
            }

        }
        else {
        }
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
        finish();
    }

}