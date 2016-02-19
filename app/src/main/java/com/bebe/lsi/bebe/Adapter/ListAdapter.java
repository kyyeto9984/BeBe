package com.bebe.lsi.bebe.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bebe.lsi.bebe.GoogleMap.MapVO;
import com.bebe.lsi.bebe.R;

import java.util.ArrayList;

/**
 * Created by LSJ on 2015-08-27.
 */
public class ListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MapVO> list;
    private LayoutInflater inflater;
    public static int height;

    public ListAdapter(Context context , ArrayList<MapVO> list , LayoutInflater inflater){
        this.context= context;
        this.list = list;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item , parent , false);
            Log.i("asdasd","height"+convertView.getHeight()+":"+convertView.getLayoutParams().height);
            height = convertView.getLayoutParams().height;
        }
        TextView placename = (TextView)convertView.findViewById(R.id.placename);
        placename.setText(list.get(position).getPlace_name());
        TextView location = (TextView)convertView.findViewById(R.id.location);
        location.setText(list.get(position).getVicinity());
        return convertView;
    }
}
