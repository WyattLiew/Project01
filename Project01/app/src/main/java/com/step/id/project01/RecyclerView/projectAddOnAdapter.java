package com.step.id.project01.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.step.id.project01.R;
import com.step.id.project01.model.ProjectAddOnProvider;

import java.util.ArrayList;

public class projectAddOnAdapter extends RecyclerView.Adapter<projectAddOnAdapter.projectViewHolder> {

    Context context;
    ArrayList<ProjectAddOnProvider> mData = new ArrayList<>();

    public projectAddOnAdapter(Context context,ArrayList<ProjectAddOnProvider> mData) {
        this.mData = mData;
        this.context = context;
    }

    @NonNull
    @Override
    public projectAddOnAdapter.projectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.list_projectaddon,parent,false);
        projectAddOnAdapter.projectViewHolder ViewHolder = new projectAddOnAdapter.projectViewHolder(v);


        return ViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull projectAddOnAdapter.projectViewHolder holder, int position) {
        ProjectAddOnProvider retrieveCurrent = mData.get(position);
        String Status = mData.get(position).getStatus();
        if(Status.equals("Completed")){
            holder.tv_status.setText("Completed");
            holder.tv_status.setTextColor(Color.GREEN);
        }else if(Status.equals("In Progress")){
            holder.tv_status.setText("In Progress");
            holder.tv_status.setTextColor(Color.rgb(225, 237, 59));
        }else{
            holder.tv_status.setText("Deferred");
            holder.tv_status.setTextColor(Color.RED);
        }

        //holder.tv_status.setText(mData.get(position).getStatus());
        holder.tv_date.setText(mData.get(position).getDate());
        holder.tv_notes.setText(mData.get(position).getNotes());
        Picasso.get().load(retrieveCurrent.getImgURL())
                .fit()
                .centerCrop()
                .into(holder.tv_image);
        //holder.tv_image.setImageBitmap(imageEfficiently.decodeSampledBitmapFromResource(mData.get(position).getImg(),100,100));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public static class projectViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_date,tv_notes,tv_status;
        private ImageView tv_image;

        public projectViewHolder(View itemView){
            super(itemView);

            tv_status = (TextView) itemView.findViewById(R.id.projectList_Status);
            tv_date = (TextView) itemView.findViewById(R.id.projectList_Date);
            tv_notes = (TextView) itemView.findViewById(R.id.projectList_Notes);
            tv_image = (ImageView)itemView.findViewById(R.id.projectList_Image);


        }
    }
}
