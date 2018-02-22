package com.app.fiskas.fiskas;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    TextInputEditText name;
    TextInputEditText sname;
    TextInputEditText phone;
    TextInputEditText mail;
    TextInputEditText company_mail;
    TextInputEditText company_phone;
    TextInputEditText company_name;
    TextInputEditText company_address;
    TextInputEditText tax_code;
    TextInputEditText tax_type;
    TextInputEditText company_tax;
    MaterialSpinner spinner_tax_form;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_profile, container, false);
        name = ((TextInputEditText)result.findViewById(R.id.txt_box_profile_firstname));
        name.setText(LoginActivity.authManager.get_name());
        sname = ((TextInputEditText)result.findViewById(R.id.txt_box_profile_secondname));
        sname.setText(LoginActivity.authManager.get_sname());
        phone = ((TextInputEditText)result.findViewById(R.id.txt_box_profile_telephone));
        phone.setText(LoginActivity.authManager.get_phoneNumber());
        company_name = ((TextInputEditText)result.findViewById(R.id.txt_box_profile_company_name));
        company_name.setText(LoginActivity.authManager.get_company_name());
        company_address = ((TextInputEditText)result.findViewById(R.id.txt_box_profile_company_adress));
        company_address.setText(LoginActivity.authManager.get_address());
        tax_code = ((TextInputEditText)result.findViewById(R.id.txt_box_profile_nip));
        tax_code.setText(LoginActivity.authManager.get_tax_code());
        company_tax = ((TextInputEditText)result.findViewById(R.id.txt_profile_box_region));
        company_tax.setText(LoginActivity.authManager.get_company_tax_code());
        company_mail = ((TextInputEditText)result.findViewById(R.id.txt_box_profile_company_mail));
        company_mail.setText(LoginActivity.authManager.get_company_email());
        company_phone = ((TextInputEditText)result.findViewById(R.id.txt_box_profile_company_telephone));
        company_phone.setText(LoginActivity.authManager.get_company_phone());
        ((TextInputEditText)result.findViewById(R.id.txt_box_profile_tax_service)).setText(LoginActivity.authManager.get_tax_type());
        mail = ((TextInputEditText)result.findViewById(R.id.txt_box_profile_mail));
        mail.setText(LoginActivity.authManager.get_email());


        spinner_tax_form = (MaterialSpinner) result.findViewById(R.id.profile_spinner_items_tax);
        spinner_tax_form.setItems("VAT", "PIT", "Ryczałt", "Karta", "CIT");
        spinner_tax_form.setText(LoginActivity.authManager.get_tax_type());

        result.findViewById(R.id.btn_politics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(result.getContext(), PrivatePolicy.class);
                startActivityForResult(intent, 1);
            }
        });
        result.findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(result.getContext(), ChangePassword.class);
                startActivityForResult(intent, 1);
            }
        });

        result.findViewById(R.id.btn_save_changes).setOnClickListener(view ->{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request;
                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("name", name.getText().toString())
                                .addFormDataPart("surname", sname.getText().toString())
                                .addFormDataPart("email", mail.getText().toString())
                                .addFormDataPart("phone", phone.getText().toString())
                                .addFormDataPart("pass", LoginActivity.authManager.get_pass())
                                .addFormDataPart("company_name", company_name.getText().toString())
                                .addFormDataPart("company_phone", company_phone.getText().toString())
                                .addFormDataPart("company_email", company_mail.getText().toString())
                                .addFormDataPart("address", company_address.getText().toString())
                                .addFormDataPart("taxcode", tax_code.getText().toString())
                                .addFormDataPart("company_tax_code", company_tax.getText().toString())
                                .addFormDataPart("tax_type", spinner_tax_form.getText().toString())
                                .build();
                        request = new Request.Builder().url("http://fiskasapp.unixstorm.org/admin/api/update").post(requestBody).build();

                        Response response = client.newCall(request).execute();
                        LoginActivity.authManager.logIn(LoginActivity.authManager.get_login(), LoginActivity.authManager.get_pass(), true);
                        Snackbar.make(view, getResources().getText(R.string.profile_changed), Snackbar.LENGTH_LONG)
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        });
        return result;
    }

}
