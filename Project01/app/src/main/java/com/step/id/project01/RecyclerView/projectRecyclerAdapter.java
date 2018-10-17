package com.step.id.project01.RecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.step.id.project01.R;
import com.step.id.project01.sqlitedata.newProjectProvider;

import java.util.ArrayList;

public class projectRecyclerAdapter extends RecyclerView.Adapter<projectRecyclerAdapter.projectViewHolder> {

    Context context;
    ArrayList<newProjectProvider> mData ;

    public projectRecyclerAdapter(Context context,ArrayList<newProjectProvider> mData){
        this.mData =mData;
        this.context =context;
    }


    @NonNull
    @Override
    public projectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v =LayoutInflater.from(context).inflate(R.layout.list_group,parent,false);
        projectViewHolder ViewHolder = new projectViewHolder(v);


        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull projectViewHolder holder, int position) {

        holder.tv_title.setText(mData.get(position).getTitle());
        holder.tv_description.setText(mData.get(position).getDescription());
        holder.tv_conName.setText(mData.get(position).getName());
        holder.tv_conNum.setText(mData.get(position).getNumber());
        holder.tv_location.setText(mData.get(position).getLocation());
        holder.tv_date.setText(mData.get(position).getDate());
        holder.tv_notes.setText(mData.get(position).getNotes());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class projectViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_title, tv_description, tv_conName, tv_conNum,tv_location,tv_date,tv_notes;

        public projectViewHolder(View itemView){
            super(itemView);

            tv_title = (TextView) itemView.findViewById(R.id.lg_projTitle_textView);
            tv_description = (TextView) itemView.findViewById(R.id.lg_projDescription_textView);
            tv_conName = (TextView) itemView.findViewById(R.id.lg_projConName_textView);
            tv_conNum = (TextView) itemView.findViewById(R.id.lg_projNumber_textView);
            tv_location = (TextView) itemView.findViewById(R.id.lg_projLocation_textView);
            tv_date = (TextView) itemView.findViewById(R.id.lg_projDate_textView);
            tv_notes = (TextView) itemView.findViewById(R.id.lg_projNotes_textView);

        }
    }
}
