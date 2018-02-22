package com.app.fiskas.fiskas;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fiskas.fiskas.CustomElements.PaymentAdapter;
import com.app.fiskas.fiskas.CustomElements.PaymentItem;
import com.app.fiskas.fiskas.views.LevelBeamView;
import com.ericliu.asyncexpandablelist.CollectionView;
import com.ericliu.asyncexpandablelist.CollectionViewCallbacks;
import com.gohn.nativedialog.ButtonType;
import com.gohn.nativedialog.NDialog;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.amirs.JSON;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.app.fiskas.fiskas.LoginActivity.authManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentList extends Fragment {


    private CollectionView mCollectionView;
    ProgressDialog pd = null;
    //private  TextView dateTxt;
    private  String dateTxtStart;
    private  String dateTxtEnd;
    private FancyButton date_range;
    private  TextView t_ost_txt;
    private  TextView t_nal_txt;
    private  TextView t_dolg_txt;
    private  TextView t_tax_txt;
    private  TextView acctual_time;
    private static JSON payment_list_json = null;
    private static double total_nal = 0;
    private static double total_ost = 0;
    private static double total_dolg = 0;
    private static double total_tax = 0;
    private String month[];

    public PaymentList(){
    }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    final View result = inflater.inflate(R.layout.fragment_payment_list, container, false);
    month= new String[]{
            getResources().getString(R.string.balance_table_month_1),
            getResources().getString(R.string.balance_table_month_2),
            getResources().getString(R.string.balance_table_month_3),
            getResources().getString(R.string.balance_table_month_4),
            getResources().getString(R.string.balance_table_month_5),
            getResources().getString(R.string.balance_table_month_6),
            getResources().getString(R.string.balance_table_month_7),
            getResources().getString(R.string.balance_table_month_8),
            getResources().getString(R.string.balance_table_month_9),
            getResources().getString(R.string.balance_table_month_10),
            getResources().getString(R.string.balance_table_month_11),
            getResources().getString(R.string.balance_table_month_12)};
    ((TextInputEditText)result.findViewById(R.id.txt_box_name_company_balance)).setText(authManager.get_company_name());
    ((TextInputEditText)result.findViewById(R.id.txt_box_NIP_balance)).setText(authManager.get_tax_code());
    ((TextInputEditText)result.findViewById(R.id.txt_box_forma_opodatk)).setText(authManager.get_tax_type());
    date_range = (FancyButton)result.findViewById(R.id.btn_balance_change_attr);

    //dateTxt = result.findViewById(R.id.txt_edit_date2);
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    dateTxtStart = dateTxtEnd = sdf.format(Calendar.getInstance().getTime());


    CheckActivation();


    result.findViewById(R.id.btn_payment_refresh).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
                                .addFormDataPart("pass", authManager.get_pass())
                                .addFormDataPart("startDate", dateTxtStart)
                                .addFormDataPart("endDate", dateTxtEnd)
                                .build();
                        request = new Request.Builder().url("http://fiskasapp.unixstorm.org/admin/api/report").post(requestBody).build();
                        Response response = client.newCall(request).execute();
                        payment_list_json = new JSON(response.body().string());
                        while (payment_list_json == null)
                            Thread.sleep(100);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                acctual_time.setText(getResources().getText(R.string.balance_date_time) + ": " + payment_list_json.key("report").key("date").stringValue());
                                RecyclerView recyclerView1 = (RecyclerView) result.findViewById(R.id.recycler_view_1);
                                recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView1.setAdapter(new SimpleAdapter(recyclerView1, new String[]{"Kvartal 1", "Kvartal 2", "Kvartal 3", "Kvartal 4"}));

                                RecyclerView recyclerView2 = (RecyclerView) result.findViewById(R.id.recycler_view_2);
                                recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView2.setAdapter(new SimpleAdapter(recyclerView2, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

                                result.findViewById(R.id.btn_category2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal2);
                                        if (q.isCollapsed())
                                            q.expand();
                                        else
                                            q.collapse();
                                    }
                                });

                                RecyclerView recyclerView3 = (RecyclerView) result.findViewById(R.id.recycler_view_3);
                                recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView3.setAdapter(new SimpleAdapter(recyclerView3, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

                                result.findViewById(R.id.btn_category3).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal3);
                                        if (q.isCollapsed())
                                            q.expand();
                                        else
                                            q.collapse();
                                    }
                                });

                                RecyclerView recyclerView4 = (RecyclerView) result.findViewById(R.id.recycler_view_4);
                                recyclerView4.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView4.setAdapter(new SimpleAdapter(recyclerView4, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

                                result.findViewById(R.id.btn_category4).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal4);
                                        if (q.isCollapsed())
                                            q.expand();
                                        else
                                            q.collapse();
                                    }
                                });

                                ((TextView)result.findViewById(R.id.tw_balance_buy)).setText(payment_list_json.key("report").key("balance").key("buy").stringValue() + "zł");
                                ((TextView)result.findViewById(R.id.tw_balance_sell)).setText(payment_list_json.key("report").key("balance").key("sale").stringValue() + "zł");
                                ((TextView)result.findViewById(R.id.tw_balance_income)).setText(payment_list_json.key("report").key("balance").key("income").stringValue() + "zł");
                                //pd.dismiss();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute();
        }
    });

    t_nal_txt = (TextView) result.findViewById(R.id.txt_balance_total_nal);
    t_ost_txt = (TextView) result.findViewById(R.id.txt_balance_total_ost);
    t_dolg_txt = (TextView) result.findViewById(R.id.txt_balance_total_dolg);
    t_tax_txt = (TextView) result.findViewById(R.id.txt_balance_total_tax);
    acctual_time = (TextView) result.findViewById(R.id.txt_payment_acctual_time);
    pd = new ProgressDialog(result.getContext());
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
                        .addFormDataPart("pass", authManager.get_pass())
                        .addFormDataPart("startDate", dateTxtStart)
                        .addFormDataPart("endDate", dateTxtEnd)
                        .build();
                request = new Request.Builder().url("http://fiskasapp.unixstorm.org/admin/api/report").post(requestBody).build();
                Response response = client.newCall(request).execute();
                //"{\"res\":0,\"report\":{\"date\":\"27.01.2018 23:04\",\"table\":{\"nal\":{\"1\":123,\"2\":0,\"3\":0,\"4\":0,\"5\":0,\"6\":0,\"7\":1,\"8\":0,\"9\":0,\"10\":0,\"11\":0,\"12\":0},\"ost\":{\"1\":31,\"2\":0,\"3\":0,\"4\":0,\"5\":0,\"6\":0,\"7\":0,\"8\":0,\"9\":0,\"10\":0,\"11\":0,\"12\":0},\"dolg\":{\"1\":34,\"2\":0,\"3\":0,\"4\":0,\"5\":0,\"6\":0,\"7\":0,\"8\":0,\"9\":0,\"10\":0,\"11\":0,\"12\":0},\"tax\":{\"1\":34,\"2\":0,\"3\":0,\"4\":0,\"5\":0,\"6\":0,\"7\":0,\"8\":0,\"9\":0,\"10\":0,\"11\":0,\"12\":0}},\"balance\":{\"buy\":1,\"sale\":123,\"income\":123}}}"
                payment_list_json = new JSON(response.body().string());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RecyclerView recyclerView1 = (RecyclerView) result.findViewById(R.id.recycler_view_1);
                        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView1.setAdapter(new SimpleAdapter(recyclerView1, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));
                        RecyclerView recyclerView2 = (RecyclerView) result.findViewById(R.id.recycler_view_2);
                        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView2.setAdapter(new SimpleAdapter(recyclerView2, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

                        result.findViewById(R.id.btn_category2).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal2);
                                if (q.isCollapsed())
                                    q.expand();
                                else
                                    q.collapse();
                            }
                        });

                        RecyclerView recyclerView3 = (RecyclerView) result.findViewById(R.id.recycler_view_3);
                        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView3.setAdapter(new SimpleAdapter(recyclerView3, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

                        result.findViewById(R.id.btn_category3).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal3);
                                if (q.isCollapsed())
                                    q.expand();
                                else
                                    q.collapse();
                            }
                        });

                        RecyclerView recyclerView4 = (RecyclerView) result.findViewById(R.id.recycler_view_4);
                        recyclerView4.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView4.setAdapter(new SimpleAdapter(recyclerView4, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

                        result.findViewById(R.id.btn_category4).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal4);
                                if (q.isCollapsed())
                                    q.expand();
                                else
                                    q.collapse();
                            }
                        });

                        ((TextView)result.findViewById(R.id.tw_balance_buy)).setText(payment_list_json.key("report").key("balance").key("buy").stringValue() + "zł");
                        ((TextView)result.findViewById(R.id.tw_balance_sell)).setText(payment_list_json.key("report").key("balance").key("sale").stringValue() + "zł");
                        ((TextView)result.findViewById(R.id.tw_balance_income)).setText(payment_list_json.key("report").key("balance").key("income").stringValue() + "zł");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }.execute();
    result.findViewById(R.id.btn_category1).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal1);
            if (q.isCollapsed())
                q.expand();
            else
                q.collapse();
        }
    });
    RecyclerView recyclerView2 = (RecyclerView) result.findViewById(R.id.recycler_view_2);
    recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView2.setAdapter(new SimpleAdapter(recyclerView2, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

    result.findViewById(R.id.btn_category2).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal2);
            if (q.isCollapsed())
                q.expand();
            else
                q.collapse();
        }
    });

    RecyclerView recyclerView3 = (RecyclerView) result.findViewById(R.id.recycler_view_3);
    recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView3.setAdapter(new SimpleAdapter(recyclerView3, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

    result.findViewById(R.id.btn_category3).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal3);
            if (q.isCollapsed())
                q.expand();
            else
                q.collapse();
        }
    });

    RecyclerView recyclerView4 = (RecyclerView) result.findViewById(R.id.recycler_view_4);
    recyclerView4.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView4.setAdapter(new SimpleAdapter(recyclerView4, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

    result.findViewById(R.id.btn_category4).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal4);
            if (q.isCollapsed())
                q.expand();
            else
                q.collapse();
        }
    });
    result.findViewById(R.id.btn_balance_make_photo).setOnClickListener(new View.OnClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void onClick(View view) {
            ((NavigationView) getActivity().findViewById(R.id.nav_view)).getMenu().getItem(2).setChecked(true);
            ((NavigationView) getActivity().findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_take_a_picture);
            ((NavigationView) getActivity().findViewById(R.id.nav_view)).getMenu().performIdentifierAction(R.id.nav_take_a_picture, 0);
        }
    });
    date_range.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            pd = new ProgressDialog(result.getContext());
            pd.setCancelable(false);
            pd.setTitle("Wait...");
            pd.setMessage("Loading...");
            SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                    new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                        @Override
                        public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                                   int yearStart, int monthStart,
                                                   int dayStart, int yearEnd,
                                                   int monthEnd, int dayEnd) {
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                          pd.show();
                                        }});
                                    OkHttpClient client = new OkHttpClient();
                                    try {
                                        Request request;
                                        dateTxtStart = dayStart + "." + monthStart + "." + yearStart;
                                        dateTxtEnd = dayEnd + "." + monthEnd + "." + yearEnd;
                                        RequestBody requestBody = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("email", authManager.get_login())
                                                .addFormDataPart("pass", authManager.get_pass())
                                                .addFormDataPart("startDate", dateTxtStart)
                                                .addFormDataPart("endDate",  dateTxtEnd)
                                                .build();
                                        request = new Request.Builder().url("http://fiskasapp.unixstorm.org/admin/api/report").post(requestBody).build();
                                        Response response = client.newCall(request).execute();
                                        payment_list_json = new JSON(response.body().string());
                                        while (payment_list_json == null)
                                            Thread.sleep(100);

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                RecyclerView recyclerView1 = (RecyclerView) result.findViewById(R.id.recycler_view_1);
                                                recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerView1.setAdapter(new SimpleAdapter(recyclerView1, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

                                                RecyclerView recyclerView2 = (RecyclerView) result.findViewById(R.id.recycler_view_2);
                                                recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerView2.setAdapter(new SimpleAdapter(recyclerView2, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

                                                result.findViewById(R.id.btn_category2).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal2);
                                                        if (q.isCollapsed())
                                                            q.expand();
                                                        else
                                                            q.collapse();
                                                    }
                                                });

                                                RecyclerView recyclerView3 = (RecyclerView) result.findViewById(R.id.recycler_view_3);
                                                recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerView3.setAdapter(new SimpleAdapter(recyclerView3, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

                                                result.findViewById(R.id.btn_category3).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal3);
                                                        if (q.isCollapsed())
                                                            q.expand();
                                                        else
                                                            q.collapse();
                                                    }
                                                });

                                                RecyclerView recyclerView4 = (RecyclerView) result.findViewById(R.id.recycler_view_4);
                                                recyclerView4.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerView4.setAdapter(new SimpleAdapter(recyclerView4, new String[]{getString(R.string.kvartal_1), getString(R.string.kvartal_2), getString(R.string.kvartal_3), getString(R.string.kvartal_4)}));

                                                result.findViewById(R.id.btn_category4).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        com.github.chuross.library.ExpandableLayout q = (com.github.chuross.library.ExpandableLayout)result.findViewById(R.id.quartal4);
                                                        if (q.isCollapsed())
                                                            q.expand();
                                                        else
                                                            q.collapse();
                                                    }
                                                });

                                                ((TextView)result.findViewById(R.id.tw_balance_buy)).setText(payment_list_json.key("report").key("balance").key("buy").stringValue() + "zł");
                                                ((TextView)result.findViewById(R.id.tw_balance_sell)).setText(payment_list_json.key("report").key("balance").key("sale").stringValue() + "zł");
                                                ((TextView)result.findViewById(R.id.tw_balance_income)).setText(payment_list_json.key("report").key("balance").key("income").stringValue() + "zł");

                                                date_range.setText(dateTxtStart + " - " + dateTxtEnd);
                                                pd.dismiss();
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    return null;
                                }
                            }.execute();
                        }
                    });


            smoothDateRangePickerFragment.show(getActivity().getFragmentManager(), "smoothDateRangePicker");

        }
    });
        return result;
    }

    private void CheckActivation() {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                try {
                    Request request;
                    request = new Request.Builder().url("https://raw.githubusercontent.com/iqorqua/Fiskas/master/README.txt").get().build();
                    Response response = client.newCall(request).execute();
                    String activation_status[] = response.body().string().split(":");
                    if(activation_status[0].equals("1")){
                        NDialog nDialog = new NDialog(getContext(), ButtonType.ONE_BUTTON);
                        nDialog.setIcon(R.drawable.error);
                        nDialog.setTitle("Application is deactivated by developer.");
                        nDialog.setMessage(activation_status[1]);
                        nDialog.setNeutralButtonText(R.string.ok);
                        nDialog.isCancelable(false);
                        nDialog.setPositiveButtonClickListener(i -> {
                            getActivity().finish();
                        });
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nDialog.show();
                            }
                        });
                    }
                }
                catch (Exception e){

                }
                return null;
            }
        }.execute();


    }

    private class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {
        private static final int UNSELECTED = -1;

        String [] elemets;

        private RecyclerView recyclerView;
        private int selectedItem = UNSELECTED;

        public SimpleAdapter(RecyclerView recyclerView, String [] items) {
            this.recyclerView = recyclerView;
            elemets = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.expandable_payment_item, parent, false);
            ListView  listView = (ListView) itemView.findViewById(R.id.elenentsInPaymentList);
            ArrayList<PaymentItem> elem_array = new ArrayList();
            int quart = 1;
            String parent_name = "";
            String tab = getResources().getResourceEntryName(parent.getId()).split("_")[2];
            try{
                while (payment_list_json == null)
                    Thread.sleep(50);
                total_dolg = total_nal = total_ost = total_tax = 0;

                for (int i=1; i <= 12; i++){
                    total_nal += payment_list_json.key("report").key("table").key("nal").key(i+"").doubleValue();
                    total_dolg += payment_list_json.key("report").key("table").key("dolg").key(i+"").doubleValue();
                    total_tax+= payment_list_json.key("report").key("table").key("tax").key(i+"").doubleValue();
                    total_ost += payment_list_json.key("report").key("table").key("ost").key(i+"").doubleValue();
                }
                t_nal_txt.setText(total_nal + "zł");
                t_ost_txt.setText(total_ost + "zł");
                t_dolg_txt.setText(total_dolg + "zł");
                t_tax_txt.setText(total_tax + "zł");

                acctual_time.setText(getResources().getText(R.string.balance_date_time) + ": " + payment_list_json.key("report").key("date").stringValue());
                int counter = parent.getChildCount()*3;
                switch (tab){
                    case "1":{//nal
                        elem_array = new ArrayList();
                        parent_name = getResources().getResourceName(((View)recyclerView.getParent()).getId());
                        elem_array.add(new PaymentItem(month[ counter ], "description", payment_list_json.key("report").key("table").key("nal").key(""+(parent.getChildCount()*3+1)).doubleValue(), parent_name));
                        elem_array.add(new PaymentItem(month[counter+1], "description", payment_list_json.key("report").key("table").key("nal").key(""+(parent.getChildCount()*3+2)).doubleValue(), parent_name));
                        elem_array.add(new PaymentItem(month[counter+2], "description", payment_list_json.key("report").key("table").key("nal").key(""+(parent.getChildCount()*3+3)).doubleValue(), parent_name));
                        break;
                    }
                    case "2":{//ost
                        elem_array = new ArrayList();
                        parent_name = getResources().getResourceName(((View)recyclerView.getParent()).getId());
                        elem_array.add(new PaymentItem(month[ counter ], "description", payment_list_json.key("report").key("table").key("ost").key(""+(parent.getChildCount()*3+1)).doubleValue(), parent_name));
                        elem_array.add(new PaymentItem(month[counter+1], "description", payment_list_json.key("report").key("table").key("ost").key(""+(parent.getChildCount()*3+2)).doubleValue(), parent_name));
                        elem_array.add(new PaymentItem(month[counter+2], "description", payment_list_json.key("report").key("table").key("ost").key(""+(parent.getChildCount()*3+3)).doubleValue(), parent_name));
                        break;
                    }
                    case "3":{//dolg
                        elem_array = new ArrayList();
                        parent_name = getResources().getResourceName(((View)recyclerView.getParent()).getId());
                        elem_array.add(new PaymentItem(month[ counter ], "description", payment_list_json.key("report").key("table").key("dolg").key(""+(parent.getChildCount()*3+1)).doubleValue(), parent_name));
                        elem_array.add(new PaymentItem(month[counter+1], "description", payment_list_json.key("report").key("table").key("dolg").key(""+(parent.getChildCount()*3+2)).doubleValue(), parent_name));
                        elem_array.add(new PaymentItem(month[counter+2], "description", payment_list_json.key("report").key("table").key("dolg").key(""+(parent.getChildCount()*3+3)).doubleValue(), parent_name));
                        break;
                    }
                    case "4":{//tax
                        elem_array = new ArrayList();
                        parent_name = getResources().getResourceName(((View)recyclerView.getParent()).getId());
                        elem_array.add(new PaymentItem(month[ counter ], "description", payment_list_json.key("report").key("table").key("tax").key(""+(parent.getChildCount()*3+1)).doubleValue(), parent_name));
                        elem_array.add(new PaymentItem(month[counter+1], "description", payment_list_json.key("report").key("table").key("tax").key(""+(parent.getChildCount()*3+2)).doubleValue(), parent_name));
                        elem_array.add(new PaymentItem(month[counter+2], "description", payment_list_json.key("report").key("table").key("tax").key(""+(parent.getChildCount()*3+3)).doubleValue(), parent_name));
                        break;
                    }
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }

            /*String parent_name = getResources().getResourceEntryName(parent.getId())+"/"+getResources().getResourceName(((View)recyclerView.getParent()).getId());
            elem_array.add(new PaymentItem("Month 1", "description", 50.5, parent_name));
            elem_array.add(new PaymentItem("Month 2", "description", 25.4, parent_name));
            elem_array.add(new PaymentItem("Month 3", "description", 110, parent_name));*/
            double total_price = 0;
            for (PaymentItem p:elem_array ) {
                total_price+=p.price;
            }
            ((TextView)itemView.findViewById(R.id.total_price_tw)).setText(getString(R.string.balance_total) +": " + total_price + " zł");
            PaymentAdapter listAdapter = new PaymentAdapter(itemView.getContext(), elem_array);
            listView.setAdapter(listAdapter);

            listView.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            setListViewHeightBasedOnChildren(listView);
            pd.dismiss();
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return elemets.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
            private ExpandableLayout expandableLayout;
            private TextView expandButton;
            private int position;

            public ViewHolder(View itemView) {
                super(itemView);

                expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout);
                expandableLayout.setInterpolator(new OvershootInterpolator());
                expandableLayout.setOnExpansionUpdateListener(this);
                expandButton = (TextView) itemView.findViewById(R.id.expand_button);

                expandButton.setOnClickListener(this);
            }

            public void bind(int position) {
                this.position = position;

                expandButton.setText(elemets[position]);

                expandButton.setSelected(false);
                expandableLayout.collapse(false);
            }

            @Override
            public void onClick(View view) {
                ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
                if (holder != null) {
                    holder.expandButton.setSelected(false);
                    holder.expandableLayout.collapse();
                }

                if (position == selectedItem) {
                    selectedItem = UNSELECTED;
                } else {
                    expandButton.setSelected(true);
                    expandableLayout.expand();
                    selectedItem = position;
                }
            }

            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d("ExpandableLayout", "State: " + state);
                recyclerView.smoothScrollToPosition(getAdapterPosition());
            }
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RecyclerView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onDestroy() {
        pd.dismiss();
        super.onDestroy();
    }
}
