package com.app.fiskas.fiskas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fiskas.fiskas.API.AuthManager;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.studioidan.httpagent.HttpAgent;
import com.studioidan.httpagent.JsonCallback;

import org.json.JSONObject;

import java.util.regex.Pattern;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A login screen that offers login via email/password.
 */
public class RegistrationActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

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
    private TextInputEditText mPasswordView_repeat;
    private TextInputEditText mName;
    private TextInputEditText sName;
    private TextInputEditText mPhone;
    private TextInputEditText mAdres;
    private TextInputEditText mNip;
    private TextInputEditText mRegon;
    private TextInputEditText mCompanyPhone;
    private TextInputEditText mCompanyEmail;
    private MaterialSpinner spinner_tax_form;
    private FancyButton registerButton;
    private TextView call_to_us;
    private TextView mCompanyName;
    private TextView have_account;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        // Set up the login form.
        have_account = (TextView)findViewById(R.id.txt_have_account);
        mEmailView = (TextInputEditText) findViewById(R.id.mail_txt_box);
        registerButton = (FancyButton) findViewById(R.id.btn_login);
        mPasswordView = (TextInputEditText) findViewById(R.id.pass_txt_box);
        mPasswordView_repeat = (TextInputEditText) findViewById(R.id.pass_repeat_txt_box);
        mName = (TextInputEditText) findViewById(R.id.txt_register_name);
        sName = (TextInputEditText) findViewById(R.id.txt_register_sname);
        mPhone = (TextInputEditText) findViewById(R.id.tb_register_phone);
        mCompanyName = (TextInputEditText) findViewById(R.id.tb_registration_company_name);
        mAdres = (TextInputEditText) findViewById(R.id.tb_registration_company_address);
        mNip = (TextInputEditText) findViewById(R.id.tb_registration_nip);
        mRegon = (TextInputEditText) findViewById(R.id.tb_registration_regon);
        mCompanyEmail = (TextInputEditText) findViewById(R.id.tb_registration_company_email);
        mCompanyPhone = (TextInputEditText) findViewById(R.id.tb_registration_company_phone);
        spinner_tax_form = (MaterialSpinner) findViewById(R.id.spinner_items_tax);
        spinner_tax_form.setItems("VAT", "PIT", "Ryczałt", "Karta", "CIT");
        have_account.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivityForResult(intent, 1);
            }
        });

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validEmail(mEmailView.getText().toString())){
                    Snackbar.make(view, R.string.email_is_not_valid, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if((!(mPasswordView.getText().toString().equals(mPasswordView_repeat.getText().toString()))& (mPasswordView.getText().toString().length()>5)) | mPasswordView.getText().toString().equals("")){
                    Snackbar.make(view, R.string.password_current_is_not_match, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(mName.getText().toString().equals("")){
                    Snackbar.make(view, R.string.first_name_is_not_valid, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(sName.getText().toString().equals("")){
                    Snackbar.make(view, R.string.second_name_is_not_valid, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if((mPhone.getText().toString().equals(""))){
                    Snackbar.make(view, R.string.phone_is_not_valid, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if((mCompanyName.getText().toString().equals(""))){
                    Snackbar.make(view, R.string.company_name_is_not_valid, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if((mCompanyPhone.getText().toString().equals(""))){
                    Snackbar.make(view, R.string.company_phone_is_not_valid, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if((mCompanyEmail.getText().toString().equals(""))){
                    Snackbar.make(view, R.string.company_email_is_not_valid, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if((mAdres.getText().toString().equals(""))){
                    Snackbar.make(view, R.string.company_address_should_not_be_empty, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if((mNip.getText().toString().equals(""))){
                    Snackbar.make(view, R.string.nip_should_not_be_empty, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if((mRegon.getText().toString().equals(""))){
                    Snackbar.make(view, R.string.regon_should_not_be_empty, Snackbar.LENGTH_LONG).show();
                    return;
                }

                ProgressDialog pd = new ProgressDialog(RegistrationActivity.this);
                pd.setCancelable(false);
                pd.setTitle("Wait...");
                pd.setMessage("Loading...");
                pd.show();
                if (LoginActivity.authManager == null)
                    LoginActivity.authManager = new AuthManager();
                LoginActivity.authManager.onRegistered(new AuthManager.Registered() {
                    @Override
                    public void registered() {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
                LoginActivity.authManager.registerUser(mName.getText().toString(),
                        sName.getText().toString(),
                        mEmailView.getText().toString(),
                        mPhone.getText().toString(),
                        mPasswordView.getText().toString(),
                        mCompanyName.getText().toString(),
                        mCompanyPhone.getText().toString(),
                        mCompanyEmail.getText().toString(),
                        mAdres.getText().toString(),
                        mNip.getText().toString(),
                        mRegon.getText().toString(),
                        spinner_tax_form.getText().toString());
            }
        });

        call_to_us = (TextView) findViewById(R.id.txt_calltous);
        call_to_us.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+48 535 555 549 ")));
            }
        });
        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(validEmail(mEmailView.getText().toString())){
                    mEmailView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_mail_black_24dp, 0, R.drawable.success, 0 );
                }
                else{
                    mEmailView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_mail_black_24dp, 0, R.drawable.error, 0 );
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((mPasswordView.getText().toString().equals(mPasswordView_repeat.getText().toString()))& (mPasswordView.getText().toString().length()>5)){
                    mPasswordView_repeat.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_https_black_24dp, 0, R.drawable.success, 0 );
                }
                else{
                    mPasswordView_repeat.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_https_black_24dp, 0, R.drawable.error, 0 );
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mPasswordView_repeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if((mPasswordView.getText().toString().equals(mPasswordView_repeat.getText().toString()))& (mPasswordView.getText().toString().length()>5)){
                    mPasswordView_repeat.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_https_black_24dp, 0, R.drawable.success, 0 );
                }
                else{
                    mPasswordView_repeat.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_https_black_24dp, 0, R.drawable.error, 0 );
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

        public static boolean validEmail(String email) {
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            return pattern.matcher(email).matches();
        }
    }


