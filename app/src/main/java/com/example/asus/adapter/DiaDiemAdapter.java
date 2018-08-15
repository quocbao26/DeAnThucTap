package com.example.asus.adapter;

import android.content.Context;
import android.util.Log;
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
    ArrayList<DiaDiemModel> diaDiemModelList,filterList;
    CustomFilter filter;


    public DiaDiemAdapter(Context context, int layout, ArrayList<DiaDiemModel> diaDiemModelList) {
        this.context = context;
        this.layout = layout;
        this.diaDiemModelList = diaDiemModelList;
        this.filterList = diaDiemModelList;

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



    class ViewHolder{
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
        Picasso.get().load(diaDiemModel.getHinhanhdiadiem()).resize(350,250).into(holder.imgItemHinh);


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

            if(constraint != null && constraint.length() > 0)
            {
                constraint = constraint.toString().toUpperCase();
                Log.e("DiaDiemAdapter", "constraint:" + constraint);

            ArrayList<DiaDiemModel>  filters = new ArrayList<>();

            for(int i = 0; i < filterList.size(); i++)
            {
                if(filterList.get(i).getTendiadiem().toUpperCase().contains(constraint))
                {
                    DiaDiemModel diaDiemModel = new DiaDiemModel(filterList.get(i).getMadiadiem(),
                            filterList.get(i).getTendiadiem(),filterList.get(i).getDiachi(),
                            filterList.get(i).getGioithieu(),filterList.get(i).getLatitude(),
                            filterList.get(i).getLongitude(),filterList.get(i).getHinhanhdiadiem());
                    filters.add(diaDiemModel);

                }
            }
            // trả về số lượng địa điểm
                results.count = filters.size();
                Log.e("DiaDiemAdapter", "results.count:" + results.count);
            // trả về giá trị địa điểm
                results.values = filters;
                Log.e("DiaDiemAdapter", "results.values:" + results.values);

            }
            else{
                results.count = filterList.size();
                Log.e("DiaDiemAdapter", "results.count1:" + results.count);
                results.values = filterList;
                Log.e("DiaDiemAdapter", "results.values1:" + results.values);
            }



            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            diaDiemModelList  = (ArrayList<DiaDiemModel>) results.values;
            Log.e("DiaDiemAdapter", "diaDiemModelList:" + diaDiemModelList.toString());
            notifyDataSetChanged();
        }
    }
}
