package com.app.fiskas.fiskas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gohn.nativedialog.ButtonClickListener;
import com.gohn.nativedialog.ButtonType;
import com.gohn.nativedialog.NDialog;
import com.google.gson.annotations.SerializedName;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.thanosfisherman.mayi.Mayi;
import com.thanosfisherman.mayi.PermissionBean;
import com.thanosfisherman.mayi.PermissionToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;
import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.app.fiskas.fiskas.LoginActivity.authManager;
import static com.app.fiskas.fiskas.PhotoResultSend.FILE_PATH_ARG;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int START_CAMERA_FRAGMENT = 777;
    View cameraControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mayi.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MEDIA_CONTENT_CONTROL, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
                .onRationale(this::permissionRationaleMulti)
                .onResult(this::permissionResultMulti)
                .check();

        setContentView(R.layout.activity_main);
        cameraControls = findViewById(R.id.camera_controls);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AdvanceDrawerLayout drawer = (AdvanceDrawerLayout) findViewById(R.id.drawer_layout);
        ;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer.useCustomBehavior(Gravity.START); //assign custom behavior for "Left" drawer
        drawer.useCustomBehavior(Gravity.END); //assign custom behavior for "Right" drawer
        drawer.setViewScale(Gravity.START, 0.9f); //set height scale for main view (0f to 1f)
        drawer.setViewElevation(Gravity.START, 20);//set main view elevation when drawer open (dimension)
        drawer.setViewScrimColor(Gravity.START, Color.TRANSPARENT);//set drawer overlay coloe (color)
        drawer.setDrawerElevation(Gravity.START, 20);//set drawer elevation (dimension)

        drawer.setRadius(Gravity.START, 25);//set end container's corner radius (dimension)
        if (authManager != null)
        Snackbar.make(drawer, "Hello, "+ authManager.get_name(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        navigationView.getMenu().performIdentifierAction(R.id.nav_balans, 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void permissionResultMulti(PermissionBean[] permissions)
    {
        //Toast.makeText(MainActivity.this, "MULTI PERMISSION RESULT " + Arrays.deepToString(permissions), Toast.LENGTH_LONG).show();
    }

    private void permissionRationaleMulti(PermissionBean[] permissions, PermissionToken token)
    {
        //Toast.makeText(MainActivity.this, "Rationales for Multiple Permissions " + Arrays.deepToString(permissions), Toast.LENGTH_LONG).show();
        token.continuePermissionRequest();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        int id = item.getItemId();
        switch (id) {
            case R.id.nav_take_a_picture: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    break;

                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    break;

                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    break;
                }

                Intent intent = new Intent(getBaseContext(), CameraActivity.class);
                startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
/*
                cameraControls.setVisibility(View.VISIBLE);
                findViewById(R.id.btn_select_photo_concamera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), AlbumSelectActivity.class);
                        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 50); // set limit for image selection
                        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
                    }
                });
                findViewById(R.id.record_button).setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            try {
                                                                                camera.takePicture();
                                                                            } catch (IllegalAccessException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    });*/

// Build the camera
                /*camera = new CameraExtended.Builder()
                        .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                        .setTakePhotoRequestCode(CameraExtended.REQUEST_TAKE_PHOTO)
                        .setDirectory("pics")
                        .setName("ali_" + System.currentTimeMillis())
                        .setImageFormat(Camera.IMAGE_JPEG)
                        .setCompression(0)
                        .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                        .build(this);
                try {
                    camera.takePicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);*/

                title = getString(R.string.capture_photo);
                break;
            }
            case R.id.nav_contact: {
                fragment = new Contacts();
                title = getString(R.string.contacts);
                break;
            }
           /* case R.id.nav_send: {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            }*/
            case R.id.nav_my_profile: {
                fragment = new Profile();
                title = getString(R.string.profile);
                break;
            }
            case R.id.nav_balans: {
                fragment = new PaymentList();
                title = getString(R.string.balance);
                break;
            }
            case R.id.nav_my_pictures: {
                fragment = new FacturesFragment();
                title = getString(R.string.factures);
                break;
            }
            case R.id.nav_logout:{
                NDialog nDialog = new NDialog(MainActivity.this, ButtonType.TWO_BUTTON);
                nDialog.setIcon(R.drawable.ic_logout);
                nDialog.setTitle(R.string.log_out);
                nDialog.setMessage(R.string.r_u_sure_quit);
                ButtonClickListener buttonClickListener = new ButtonClickListener() {
                    @Override
                    public void onClick(int button) {
                        switch (button) {
                            case NDialog.BUTTON_POSITIVE:
                                finish();
                                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivityForResult(intent, 1);
                                break;
                            case NDialog.BUTTON_NEGATIVE:
                                //finish();
                                break;
                        }
                    }
                };

// Positive Button
                nDialog.setPositiveButtonText("YES");
                nDialog.setPositiveButtonTextColor(Color.BLUE);
                nDialog.setPositiveButtonOnClickDismiss(false); // default : true
                nDialog.setPositiveButtonClickListener(buttonClickListener);

// Negative Button
                nDialog.setNegativeButtonText("NO");
                nDialog.setNegativeButtonTextColor(Color.parseColor("#FF0000"));
                nDialog.setNegativeButtonOnClickDismiss(true); // default : true
                nDialog.setNegativeButtonClickListener(buttonClickListener);
                nDialog.show();
            }

        }
        if (fragment != null) {
            if (title != getString(R.string.capture_photo)) {
                cameraControls.setVisibility(View.GONE);
                //findViewById(R.id.fab).setVisibility(View.VISIBLE);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commitAllowingStateLoss();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        if (false ) {
            Bitmap bitmap = null;
            if (bitmap == null)
                return;
            OutputStream os;

            try {
                File filesDir = getApplicationContext().getFilesDir();
                File imageFile = new File(filesDir, Calendar.getInstance().getTime() + ".jpg");
                os = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
                        Intent intent = new Intent(getBaseContext(), PhotoResultSend.class);
                        intent.putExtra(FILE_PATH_ARG, imageFile.getPath());
                        startActivityForResult(intent, 1);

                } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

}