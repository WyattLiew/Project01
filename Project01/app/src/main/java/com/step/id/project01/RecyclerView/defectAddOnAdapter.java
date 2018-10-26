package com.step.id.project01.RecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.step.id.project01.R;
import com.step.id.project01.model.defect;

import java.util.ArrayList;

public class defectAddOnAdapter extends RecyclerView.Adapter<defectAddOnAdapter.defectViewHolder> {

    Context context;
    ArrayList<defect> mData = new ArrayList<>();

    public defectAddOnAdapter(Context context, ArrayList<defect> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public defectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_defectaddon,viewGroup,false);
        defectAddOnAdapter.defectViewHolder ViewHolder = new defectAddOnAdapter.defectViewHolder(v);


        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull defectViewHolder holder, int position) {

        holder.tv_date.setText(mData.get(position).getDate());
        holder.tv_defect.setText(mData.get(position).getDefect());
        holder.tv_comment.setText(mData.get(position).getComments());
        Picasso.get().load(mData.get(position).getImgURL())
                .fit()
                .centerCrop()
                .into(holder.tv_image);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class defectViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_date,tv_defect,tv_comment;
        private ImageView tv_image;

        public defectViewHolder(View itemView){
            super(itemView);

            tv_defect = (TextView) itemView.findViewById(R.id.defectList_defect);
            tv_date = (TextView) itemView.findViewById(R.id.defectList_Date);
            tv_comment = (TextView) itemView.findViewById(R.id.defectList_comment);
            tv_image = (ImageView)itemView.findViewById(R.id.defectList_Image);


        }
    }


}
