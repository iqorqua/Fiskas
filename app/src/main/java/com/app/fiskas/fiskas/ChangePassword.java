package com.app.fiskas.fiskas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import eu.amirs.JSON;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.app.fiskas.fiskas.LoginActivity.authManager;

public class ChangePassword extends AppCompatActivity {


    private TextInputEditText pass;
    private TextInputEditText new_pass;
    private TextInputEditText new_pass1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        pass = (TextInputEditText) findViewById(R.id.change_pass_txt_box);
        new_pass = (TextInputEditText) findViewById(R.id.change_pass_new_txt_box);
        new_pass1 = (TextInputEditText) findViewById(R.id.change_pass_new_txt_box1);

        new_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((new_pass.getText().toString().equals(new_pass1.getText().toString()))& (new_pass.getText().toString().length()>5)){
                    new_pass1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_https_black_24dp, 0, R.drawable.success, 0 );
                }
                else{
                    new_pass1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_https_black_24dp, 0, R.drawable.error, 0 );
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        new_pass1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((new_pass.getText().toString().equals(new_pass1.getText().toString()))& (new_pass.getText().toString().length()>5)){
                    new_pass1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_https_black_24dp, 0, R.drawable.success, 0 );
                }
                else{
                    new_pass1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_https_black_24dp, 0, R.drawable.error, 0 );
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findViewById(R.id.btn_change_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((new_pass.getText().toString().equals(new_pass1.getText().toString()))& (new_pass.getText().toString().length()>5))){

                    Snackbar.make(view, R.string.password_current_is_not_match, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                final ProgressDialog pd = new ProgressDialog(ChangePassword.this);
                pd.setCancelable(false);
                pd.setTitle("Wait...");
                pd.setMessage("Loading...");
                pd.show();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {

                        OkHttpClient client = new OkHttpClient();
                        try {
                            Request request;
                            RequestBody requestBody = new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("email", authManager.get_login())
                                    .addFormDataPart("pass", pass.getText().toString())
                                    .addFormDataPart("new_pass", new_pass.getText().toString())
                                    .build();
                            request = new Request.Builder().url("http://fiskasapp.unixstorm.org/admin/api/set_pass").post(requestBody).build();
                            Response response = client.newCall(request).execute();
                            JSON json = new JSON(response.body().string());
                            if(json.key("res").intValue() == 0){
                                finish();
                                pd.dismiss();
                                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivityForResult(intent, 1);
                                Snackbar.make(getWindow().getDecorView(), R.string.restore_password_alert_message, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                            else {
                                pd.dismiss();
                                Snackbar.make(getWindow().getDecorView(), json.key("message").stringValue(), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute();
            }
        });
    }
}
