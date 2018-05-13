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

public class CustomArrayListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;
    private final ArrayList<String> itemdetail;
    private final ArrayList<Drawable> imgid;

    public CustomArrayListAdapter(Activity context, ArrayList<String> itemname, ArrayList<String> itemdetail, ArrayList<Drawable> imgid) {
        super(context, R.layout.tc_container, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.itemdetail=itemdetail;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.tc_container, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.Itemdetail);

        txtTitle.setText(itemname.get(position));
        imageView.setImageDrawable(imgid.get(position));
        extratxt.setText(itemdetail.get(position));
        return rowView;

    };
}