package com.app.fiskas.fiskas.CustomElements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.app.fiskas.fiskas.R;
import com.lucasurbas.listitemview.ListItemView;

import java.util.ArrayList;

/**
 * Created by igorqua on 15.10.2017.
 */

public class PaymentAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<PaymentItem> objects;

    public PaymentAdapter(Context context, ArrayList<PaymentItem> payments){
        ctx = context;
        objects = payments;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.payment_item, parent, false);
        }

        PaymentItem p = getPaymentItem(position);
        ListItemView listItem = (ListItemView) view.findViewById(R.id.list_item_view);
        listItem.setTitle(p.paymentNameTitle);
        listItem.setSubtitle(p.price + " zł");
        listItem.setTag(p.m_tag);
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), view.getTag().toString(), Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }


    PaymentItem getPaymentItem(int position) {
        return ((PaymentItem) getItem(position));
    }
}

