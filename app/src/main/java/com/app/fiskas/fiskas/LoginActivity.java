package com.app.fiskas.fiskas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.app.fiskas.fiskas.API.AuthManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import mehdi.sakout.fancybuttons.FancyButton;

import static io.reactivex.annotations.SchedulerSupport.IO;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {//implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static AuthManager authManager = null;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;
    private FancyButton loginButton;
    private TextView call_to_us;
    private TextView forgot_pass;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        mEmailView = (TextInputEditText) findViewById(R.id.mail_txt_box);
        mPasswordView = (TextInputEditText) findViewById(R.id.pass_txt_box);
        loginButton = (FancyButton) findViewById(R.id.btn_login);
        forgot_pass = (TextView)findViewById(R.id.txt_fogot_pass);
        register = (TextView)findViewById(R.id.txt_register);
        call_to_us = (TextView) findViewById(R.id.txt_calltous);
        forgot_pass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEmailView.getText().toString().equals("") | !RegistrationActivity.validEmail(mEmailView.getText().toString())){

                    Snackbar.make(view, getText(R.string.email_is_not_valid), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                authManager = new AuthManager();

                authManager.restorePass(mEmailView.getText().toString());
                authManager.onRecovered(new AuthManager.Recovered() {
                    @Override
                    public void recovered(String messages) {
                        Snackbar.make(view, messages, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                authManager = new AuthManager();
                String login = null;
                String pass = null;
                try {
                    login = mEmailView.getText().toString();
                    pass = mPasswordView.getText().toString();
                }
                catch (Exception ex){
                    Snackbar.make(view, "Error!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setCancelable(false);
                pd.setTitle("Wait...");
                pd.setMessage("Loading...");
                pd.show();
                String finalLogin = login +"#"+pass;
                authManager.onLogined(new AuthManager.Logined() {
                   @Override
                   public void logined() {
                       pd.dismiss();
                       FileOutputStream stream = null;
                       try {
                       File file = new File(getApplicationInfo().dataDir + "/file");
                       stream = new FileOutputStream(file);
                           stream.write((finalLogin).getBytes());
                           stream.close();
                       }
                       catch (Exception ex){
                           ex.printStackTrace();
                       }
                       Intent intent = new Intent(getBaseContext(), MainActivity.class);
                       startActivityForResult(intent, 1);
                   }
               });

               authManager.onError(new AuthManager.Error() {
                   @Override
                   public void error() {
                       pd.dismiss();
                       Snackbar.make(loginButton, "Error!", Snackbar.LENGTH_LONG)
                               .setAction("Action", null).show();
                   }
               });
                authManager.logIn(login, pass, false);//("qwerty@gmail.com", "qqqqqq", false);///


            }
        });
        call_to_us.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+48 535 555 549 ")));
            }
        });

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        /*mPasswordView = (EditText) findViewById(R.id.password);
Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);*/
    }

    @Override
    protected void onResume(){
        super.onResume();
        File file = new File(getApplicationInfo().dataDir + "/file");
        if(file.exists()){
            String text = new String();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text= line;
                }
                br.close();
                String auth_data[] = text.split("#");
                if (auth_data.length==2){
                    mEmailView.setText(auth_data[0]);
                    mPasswordView.setText(auth_data[1]);
                    loginButton.performClick();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//Do something
        else{

        }
// Do something else.
    }
}

