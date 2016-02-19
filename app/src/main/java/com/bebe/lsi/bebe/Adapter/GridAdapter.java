package com.bebe.lsi.bebe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bebe.lsi.bebe.R;
import com.bebe.lsi.bebe.VO.GridItemVO;

import java.util.ArrayList;

/**
 * Created by LSJ on 2015-08-18.
 */
public class GridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GridItemVO> list = new ArrayList<>();
    private LayoutInflater inflater;

    public GridAdapter(Context context , ArrayList<GridItemVO> list ,  LayoutInflater inflater){
        this.context = context;
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
            convertView = inflater.inflate(R.layout.grid_item , null);
        }
        TextView month = (TextView)convertView.findViewById(R.id.month);
        month.setText(list.get(position).getMonth()+" 개월");
        TextView height = (TextView)convertView.findViewById(R.id.height);
        height.setText(list.get(position).getHeight());
        return convertView;
    }

    public void updateReceiptsList(ArrayList<GridItemVO> newlist) {
        list.clear();
        list.addAll(newlist);
        this.notifyDataSetChanged();
    }
}
