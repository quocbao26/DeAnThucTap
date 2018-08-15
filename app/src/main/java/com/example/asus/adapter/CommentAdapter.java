package com.example.asus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.asus.deanthuctap.R;
import com.example.asus.model.Comment;
import com.example.asus.model.DiaDiemModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    Context context;
    int layout;
    ArrayList<Comment> commentList;

    public CommentAdapter(Context context, int layout, ArrayList<Comment> commentList) {
        this.context = context;
        this.layout = layout;
        this.commentList = commentList;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int i) {
        return commentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return commentList.indexOf(getItem(i));
    }



    class ViewHolder{
        TextView txtTenCmt,txtNgayCmt,txtCmtItem;
        RatingBar ratingBarItem;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            holder = new ViewHolder();
            holder.txtTenCmt = view.findViewById(R.id.txtTenCmt);
            holder.txtNgayCmt = view.findViewById(R.id.txtNgayCmt);
            holder.txtCmtItem = view.findViewById(R.id.txtCmtItem);
            holder.ratingBarItem = view.findViewById(R.id.ratingBarItem);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        Comment comment = commentList.get(i);
        holder.txtTenCmt.setText(comment.getTen());
        holder.txtNgayCmt.setText(comment.getNgay());
        holder.txtCmtItem.setText(comment.getComment());
        holder.ratingBarItem.setNumStars(Integer.parseInt(comment.getSao()));

        return view;
    }
}
