package io.a_ware.a_ware;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomArrayListAdapterForPerm extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;
    private final ArrayList<String> itemdetail;

    public CustomArrayListAdapterForPerm(Activity context, ArrayList<String> itemname, ArrayList<String> itemdetail) {
        super(context, R.layout.tc_container, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.itemdetail=itemdetail;

    }



    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.tc_container, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        TextView extratxt = (TextView) rowView.findViewById(R.id.Itemdetail);
        ProgressBar progressBar = (ProgressBar) rowView.findViewById(R.id.progressBar);

        txtTitle.setText(itemname.get(position));
        extratxt.setText(itemdetail.get(position));
        return rowView;

    };
}