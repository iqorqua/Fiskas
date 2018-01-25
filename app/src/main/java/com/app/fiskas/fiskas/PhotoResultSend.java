package com.app.fiskas.fiskas;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.florent37.camerafragment.internal.utils.ImageLoader;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

import static com.app.fiskas.fiskas.LoginActivity.authManager;

public class PhotoResultSend extends AppCompatActivity {

    public final static String FILE_PATH_ARG = "file_path_arg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_result_send);

        final ProgressDialog pd = new ProgressDialog(PhotoResultSend.this);
        pd.setCancelable(false);
        pd.setTitle("Wait...");
        pd.setMessage("Uploading...");
        Bundle args = getIntent().getExtras();

        if(args.getInt("start_camera")==MainActivity.START_CAMERA_FRAGMENT )
            setResult(MainActivity.START_CAMERA_FRAGMENT);

        String previewFilePath = args.get(FILE_PATH_ARG).toString();
            ImageLoader.Builder builder = new ImageLoader.Builder(this);
            builder.load(previewFilePath).build().into((ImageView) findViewById(R.id.imagePreviewer));
            //Picasso.with(this).load(previewFilePath).into((ImageView) findViewById(R.id.imagePreviewer));
        Ion.with(this).load(previewFilePath).intoImageView(findViewById(R.id.imagePreviewer));
        findViewById(R.id.btn_select_photo_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText name_paynemt = (TextInputEditText)findViewById(R.id.txt_box_name_photo_send);
                if (name_paynemt.getText().toString().equals("")){
                    Snackbar.make(view, R.string.please_input_payment_name, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
          new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                try {
                    File file = new File(previewFilePath);
                    Request request;

//post the wrapped request body
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("email", authManager.get_login())
                            .addFormDataPart("pass", authManager.get_pass())
                            .addFormDataPart("image", UUID.randomUUID().toString() + ".jpeg",
                                    RequestBody.create(MediaType.parse("image/jpeg"), file))
                            .addFormDataPart("name", name_paynemt.getText().toString())
                            .build();
                    RequestBody progressRequestBody = ProgressHelper.withProgress(requestBody, new ProgressUIListener() {

                        //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                        @Override
                        public void onUIProgressStart(long totalBytes) {
                            super.onUIProgressStart(totalBytes);
                            pd.show();
                        }

                        @Override
                        public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                            Log.e("TAG", "percent:" + (int) (100 * percent));
                            //uploadProgress.setProgress((int) (100 * percent));
                            //uploadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + "  MB/秒");
                        }

                        //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                        @Override
                        public void onUIProgressFinish() {
                            super.onUIProgressFinish();
                            pd.dismiss();
                            findViewById(R.id.send_image_panel).setVisibility(View.GONE);
                            Snackbar.make(view, "Done!", Snackbar.LENGTH_LONG).show();
                            new Thread(){
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                                        PhotoResultSend.this.finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }

                    });
                    request = new Request.Builder().url("https://serwer1651270.home.pl/admin/api/upload").post(progressRequestBody).build();
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
            }
        });
        findViewById(R.id.btn_select_photo_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

