package com.app.fiskas.fiskas.CustomElements;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.fiskas.fiskas.LoginActivity;
import com.app.fiskas.fiskas.R;
import com.app.fiskas.fiskas.ShowImageActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by igorqua on 08.01.2018.
 */

public class FacturesAdapter extends BaseAdapter {

    ArrayList<FactureItem> objects;
    Context context;

    public void setObjects(Context context, ArrayList<FactureItem> objects) {
        this.objects = objects;
        this.context = context;
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
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.facture_item, parent, false);
        }

        FactureItem item = objects.get(position);
        ((TextView)view.findViewById(R.id.txt_facute_item_name)).setText(item.factureName);
        ((TextView)view.findViewById(R.id.txt_facute_item_date)).setText(item.factureDate);
        ((LinearLayout)view.findViewById(R.id.facture_item_lo)).setTag(item.factureLink);
        Picasso.with(context).load(item.factureLink)
                .resize(100, 100)
                .error(R.drawable.error)
                .onlyScaleDown()
                .into((ImageView)view.findViewById(R.id.image_facture_item));
        /*Ion.with((ImageView)view.findViewById(R.id.image_facture_item))
                .centerCrop()
                .resize(50,50)
                .error(R.drawable.error)
                .load(item.factureLink);*/
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ShowImageActivity.class);
                intent.putExtra("image_link", v.findViewById(R.id.facture_item_lo).getTag().toString());
                intent.putExtra("image_date", ((TextView) v.findViewById(R.id.txt_facute_item_date)).getText().toString());
                intent.putExtra("image_name", ((TextView) v.findViewById(R.id.txt_facute_item_name)).getText().toString());
                context.startActivity(intent);
            }
        });
        return view;
    }
}
