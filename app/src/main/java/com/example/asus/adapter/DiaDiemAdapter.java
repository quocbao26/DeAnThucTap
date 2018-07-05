package com.example.asus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.deanthuctap.R;
import com.example.asus.model.DiaDiemModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DiaDiemAdapter extends BaseAdapter {

    Context context;
    int layout;
    List<DiaDiemModel> diaDiemModelList;

    public DiaDiemAdapter(Context context, int layout, List<DiaDiemModel> diaDiemModelList) {
        this.context = context;
        this.layout = layout;
        this.diaDiemModelList = diaDiemModelList;
    }

    @Override
    public int getCount() {
        return diaDiemModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    class  ViewHolder{
        ImageView imgItemHinh;
        TextView txtItemName;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            holder = new ViewHolder();
            holder.imgItemHinh = view.findViewById(R.id.imgItemHinh);
            holder.txtItemName = view.findViewById(R.id.txtItemName);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        DiaDiemModel diaDiemModel = diaDiemModelList.get(i);
        holder.txtItemName.setText(diaDiemModel.getTendiadiem());
        Picasso.get().load(diaDiemModel.getHinhanhdiadiem()).resize(450,350).into(holder.imgItemHinh);


        return view;
    }
}
