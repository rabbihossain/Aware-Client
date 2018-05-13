package io.a_ware.a_ware;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAppDetailView extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;

    public CustomAppDetailView(Activity context, ArrayList<String> itemname) {
        super(context, R.layout.tc_container, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.tc_detail_container, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        txtTitle.setText(itemname.get(position));
        return rowView;

    };
}