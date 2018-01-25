package com.app.fiskas.fiskas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mindorks.paracamera.Camera;

public class CameraActivity extends AppCompatActivity {

    private final Camera _camera;

    public CameraActivity(Camera camera){
        _camera = camera;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _camera.deleteImage();
    }
}
