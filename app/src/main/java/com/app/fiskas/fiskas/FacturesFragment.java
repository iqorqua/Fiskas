package com.app.fiskas.fiskas;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.fiskas.fiskas.CustomElements.FactureItem;
import com.app.fiskas.fiskas.CustomElements.FacturesAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import eu.amirs.JSON;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.app.fiskas.fiskas.LoginActivity.authManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class FacturesFragment extends Fragment {

    static JSON jsonItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_factures, container, false);
        final ListView listView = (ListView)result.findViewById(R.id.list_factures);
        final ProgressDialog pd = new ProgressDialog(getContext());

        pd.setCancelable(false);
        pd.setTitle("Wait...");
        pd.setMessage("Loading...");
        pd.show();
        OkHttpClient client = new OkHttpClient();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    Request request;
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("email", authManager.get_login())
                            .addFormDataPart("pass", authManager.get_pass())
                            .build();
                    request = new Request.Builder().url("http://fiskasapp.unixstorm.org/admin/api/factures").post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    JSON json = new JSON(response.body().string());
                    if(json.key("res").intValue() != 0){
                        Snackbar.make(result, json.key("message").stringValue(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        pd.dismiss();
                    }
                    else {
                        pd.dismiss();
                        jsonItems = json;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();

        pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                FacturesAdapter adapter = new FacturesAdapter();
                ArrayList<FactureItem> images = new ArrayList();
                for (int i=0; i<jsonItems.key("factures").count(); i++){
                    try {
                        String name = jsonItems.key("factures").getJsonArray().getJSONObject(i).get("name").toString();
                        String image_link = "http://fiskasapp.unixstorm.org" + jsonItems.key("factures").getJsonArray().getJSONObject(i).get("image").toString();
                        String data = jsonItems.key("factures").getJsonArray().getJSONObject(i).get("date").toString();
                        String id = jsonItems.key("factures").getJsonArray().getJSONObject(i).get("id").toString();
                        FactureItem fi = new FactureItem(name, id, image_link, data);
                        images.add(fi);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.setObjects(getContext(), images);
                listView.setAdapter(adapter);
            }
        });
        return result;
    }

}
