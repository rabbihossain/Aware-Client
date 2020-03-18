package io.a_ware.a_ware;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CustomArrayListAdapterForPermGraph extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> itemname;
    private ArrayList<String> itemdetail;
    private ArrayList<Integer> itemcount;
    private ArrayList<Integer> itemcountsystem;


    public CustomArrayListAdapterForPermGraph(Activity context, ArrayList<String> itemname, ArrayList<String> itemdetail, ArrayList<Integer> itemcount, ArrayList<Integer> itemcountsystem) {
        super(context, R.layout.tc_container_graph, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.itemdetail=itemdetail;
        this.itemcount = itemcount;
        this.itemcountsystem = itemcountsystem;
    }
    public void setData(ArrayList<String> itemname, ArrayList<String> itemdetail, ArrayList<Integer> itemcount, ArrayList<Integer> itemcountsystem){
        this.itemname=itemname;
        this.itemdetail=itemdetail;
        this.itemcount = itemcount;
        this.itemcountsystem = itemcountsystem;
    }


    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.tc_container_graph, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        TextView extratxt = (TextView) rowView.findViewById(R.id.Itemdetail);
        ProgressBar progressBar = (ProgressBar) rowView.findViewById(R.id.progressBar);
        TextView graphtext  = (TextView) rowView.findViewById(R.id.Itemgraph);


        txtTitle.setText(itemname.get(position));
        extratxt.setText(itemdetail.get(position));
        graphtext.setText("App's Permission Usage - "+itemcount.get(position)+" / System Permission Usage - "+itemcountsystem.get(position));
        progressBar.setProgress(itemcount.get(position));
        progressBar.setMax(itemcountsystem.get(position));
        
        
        
        return rowView;

    };
}