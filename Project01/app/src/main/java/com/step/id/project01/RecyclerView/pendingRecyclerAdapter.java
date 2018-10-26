package com.step.id.project01.RecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.step.id.project01.R;
import com.step.id.project01.model.Pending;

import java.util.ArrayList;

public class pendingRecyclerAdapter extends RecyclerView.Adapter<pendingRecyclerAdapter.pendingViewHolder> {

    Context context;
    ArrayList<Pending> mData;

    public pendingRecyclerAdapter(Context context, ArrayList<Pending> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public pendingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v =LayoutInflater.from(context).inflate(R.layout.item_pending,viewGroup,false);
        pendingViewHolder ViewHolder = new pendingViewHolder(v);


        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull pendingViewHolder holder, int position) {
        holder.tv_title.setText(mData.get(position).getTitle());
        holder.tv_description.setText(mData.get(position).getDescription());
        holder.tv_conName.setText(mData.get(position).getName());
        holder.tv_conNum.setText(mData.get(position).getNumber());
        holder.tv_conEmail.setText(mData.get(position).getEmail());
        holder.tv_location.setText(mData.get(position).getLocation());
        holder.tv_date.setText(mData.get(position).getDate());
        holder.tv_notes.setText(mData.get(position).getNotes());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class pendingViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_title, tv_description, tv_conName, tv_conNum,tv_conEmail,tv_location,tv_date,tv_notes;
        public pendingViewHolder(View itemView){
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.lg_pendTitle_textView);
            tv_description = (TextView) itemView.findViewById(R.id.lg_pendDescription_textView);
            tv_conName = (TextView) itemView.findViewById(R.id.lg_pendConName_textView);
            tv_conNum = (TextView) itemView.findViewById(R.id.lg_pendNumber_textView);
            tv_conEmail = (TextView)itemView.findViewById(R.id.lg_pendConEmail_textView);
            tv_location = (TextView) itemView.findViewById(R.id.lg_pendLocation_textView);
            tv_date = (TextView) itemView.findViewById(R.id.lg_pendDate_textView);
            tv_notes = (TextView) itemView.findViewById(R.id.lg_pendNotes_textView);
        }
    }
}
