package com.bebe.lsi.bebe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bebe.lsi.bebe.CircleImage.CircleImageView;
import com.bebe.lsi.bebe.R;
import com.bebe.lsi.bebe.VO.DiaryVO;

import java.util.ArrayList;

/**
 * Created by LSJ on 2015-08-18.
 */
public class DiaryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<DiaryVO> list;
    private LayoutInflater inflater;

    public DiaryAdapter(Context context , ArrayList<DiaryVO> list , LayoutInflater inflater){
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
        ViewHolder viewHolder;
        if(convertView == null){
            convertView  = inflater.inflate(R.layout.list_item_diary , parent , false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)convertView.getTag();
        viewHolder.title.setText(list.get(position).getTitle());
        viewHolder.content.setText(list.get(position).getContent());
        viewHolder.date.setText(list.get(position).getDate());
        viewHolder.boardImage.setImageBitmap(list.get(position).getBitmap());

        return convertView;
    }
}

class ViewHolder{

    public CircleImageView profile;
    public TextView title;
    public TextView date;
    public TextView content;
    public ImageView boardImage;

    public ViewHolder(View convertView){
        profile  = (CircleImageView)convertView.findViewById(R.id.profile);
        title = (TextView)convertView.findViewById(R.id.title);
        date = (TextView)convertView.findViewById(R.id.date);
        content = (TextView)convertView.findViewById(R.id.content);
        boardImage = (ImageView)convertView.findViewById(R.id.boardImage);
    }

}
