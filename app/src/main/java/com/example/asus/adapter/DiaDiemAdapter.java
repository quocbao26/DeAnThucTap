package com.example.asus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.deanthuctap.R;
import com.example.asus.model.DiaDiemModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DiaDiemAdapter extends BaseAdapter implements Filterable {

    Context context;
    int layout;
    List<DiaDiemModel> diaDiemModelList;
    CustomFilter filter;
    ArrayList<DiaDiemModel> filterList;

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
        return diaDiemModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return diaDiemModelList.indexOf(getItem(i));
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

    @Override
    public Filter getFilter() {

        if(filter == null)
        {
            filter = new CustomFilter();
        }

        return filter;
    }

    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length() > 0){
                constraint = constraint.toString().toUpperCase();

            ArrayList<DiaDiemModel>  filters = new ArrayList<>();

            for(int i = 0; i < diaDiemModelList.size(); i++)
            {
                if(diaDiemModelList.get(i).getTendiadiem().toUpperCase().equals(constraint))
                {
                    DiaDiemModel diaDiemModel = new DiaDiemModel(diaDiemModelList.get(i).getTendiadiem(),
                            diaDiemModelList.get(i).getMadiadiem(),diaDiemModelList.get(i).getDiachi(),
                            diaDiemModelList.get(i).getGioithieu(),diaDiemModelList.get(i).getLongitude(),
                            diaDiemModelList.get(i).getLatitude(),diaDiemModelList.get(i).getHinhanhdiadiem());
                    filters.add(diaDiemModel);
                }
            }
                results.count = filters.size();
                results.values = filters;
            }
            else{
                results.count = diaDiemModelList.size();
                results.values = diaDiemModelList;
            }



            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            diaDiemModelList = (ArrayList<DiaDiemModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
