package com.app.fiskas.fiskas.API;

import android.os.AsyncTask;

import java.io.IOException;

import eu.amirs.JSON;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.app.fiskas.fiskas.LoginActivity.authManager;

/**
 * Created by igorqua on 16.12.2017.
 */

public class AuthManager {

    private String token = null;
    private String _email = null;
    private String _name = "";
    private String _sname = "";
    private String _phoneNumber = "";
    private String _address = "";
    private String _tax_code = "";
    private String _company_name = "";
    private String _company_email = "";
    private String _company_phone = "";
    private String _company_tax_code = "";
    private String _tax_type = "";
    private String _login = "";
    private String _pass = "";

    public String get_login() {
        return _login;
    }
    public String get_pass() {
        return _pass;
    }
    public String get_email() {
        return _email;
    }
    public String get_name() { return _name;  }
    public String get_sname() { return _sname;  }
    public String get_phoneNumber() { return _phoneNumber;  }
    public String get_address() { return _address;  }
    public String get_tax_code() { return _tax_code;  }
    public String get_tax_type() { return _tax_type;  }
    public String get_company_name() { return _company_name;  }
    public String get_company_tax_code() { return _company_tax_code;  }
    public String get_token() {
        return token;
    }
    public String get_company_email() { return _company_email; }
    public String get_company_phone() { return _company_phone; }


    public interface Recovered{
        void recovered(String messages);
    }
    static Recovered recovered_callback;
    public void onRecovered (Recovered callback){
        this.recovered_callback = callback;
    }
    public static void restorePass(String email) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                OkHttpClient client = new OkHttpClient();
                try {
                    Request request;
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("email", email)
                            .build();
                    request = new Request.Builder().url("https://serwer1651270.home.pl/admin/api/recovery").post(requestBody).build();
                    Response response = client.newCall(request).execute();

                    recovered_callback.recovered(new JSON(response.body().string()).key("message").stringValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    public interface Logined{
        void logined();
    }
    Logined login_calback;

    public void onLogined(Logined callback){
        this.login_calback = callback;
    }

    public interface Registered{
        void registered();
    }
    Registered register_calback;
    public void onRegistered(Registered callback){
        this.register_calback = callback;
    }
    public interface Error{
        void error();
    }
    Error error_calback;
    public void onError(Error callback){
        this.error_calback = callback;
    }

    public boolean logIn(String arg_email, String arg_password, boolean justUpdate){
        try {
            _login = arg_email;
            _pass = arg_password;
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    OkHttpClient client = new OkHttpClient();
                    Request request;
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("email", arg_email)
                            .addFormDataPart("pass", arg_password)
                            .build();
                    request = new Request.Builder().url("https://serwer1651270.home.pl/admin/api/login").post(requestBody).build();

                    try {
                        Response response = client.newCall(request).execute();
                        JSON json = new JSON(response.body().string());
                        if ((json== null) | (json.key("res").intValue() == 1)){
                            error_calback.error();
                        }
                        else {
                            _name = json.key("user").key("name").toString();
                            _sname = json.key("user").key("surname").toString();
                            _phoneNumber = json.key("user").key("phone").toString();
                            _company_name  = json.key("user").key("company_name").toString();
                            _company_phone  = json.key("user").key("company_phone").toString();
                            _company_email  = json.key("user").key("company_email").toString();
                            _email = json.key("user").key("email").toString();
                            _address = json.key("user").key("address").toString();
                            _tax_code = json.key("user").key("tax_code").toString();
                            _tax_type = json.key("user").key("tax_type").toString();
                            _company_tax_code = json.key("user").key("company_tax_code").toString();
                            if (!justUpdate) login_calback.logined();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        error_calback.error();
                    }
                    return null;
                }
            }.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(String name, String sname, String email, String phoneNumber, String pass, String company_name, String company_phone, String company_email, String address, String taxcode, String companyTaxCode, String tax_type)
    {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request;
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("name", name)
                            .addFormDataPart("surname", sname)
                            .addFormDataPart("email", email)
                            .addFormDataPart("phone", phoneNumber)
                            .addFormDataPart("pass", pass)
                            .addFormDataPart("company_name", company_name)
                            .addFormDataPart("company_phone", company_phone)
                            .addFormDataPart("company_email", company_email)
                            .addFormDataPart("address", address)
                            .addFormDataPart("taxcode", taxcode)
                            .addFormDataPart("company_tax_code", companyTaxCode)
                            .addFormDataPart("tax_type", tax_type)
                            .build();
                    request = new Request.Builder().url("https://serwer1651270.home.pl/admin/api/register").post(requestBody).build();

                    Response response = client.newCall(request).execute();
                    register_calback.registered();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
        return true;
    }
}
