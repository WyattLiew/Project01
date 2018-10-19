package com.step.id.project01.RecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.step.id.project01.R;
import com.step.id.project01.firebase.User;

import java.util.ArrayList;

public class MonitorUserAdapter extends RecyclerView.Adapter <MonitorUserAdapter.monitorUserViewHolder> {

    Context context;
    ArrayList<User> mData;

    public MonitorUserAdapter(Context context, ArrayList<User> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public monitorUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v =LayoutInflater.from(context).inflate(R.layout.list_monitor_user,parent,false);
       monitorUserViewHolder ViewHolder = new monitorUserViewHolder(v);


        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull monitorUserViewHolder holder, int i) {


        holder.tv_Name.setText(mData.get(i).getName());
        holder.tv_Email.setText(mData.get(i).getEmail());
        /**
        if(mData.get(i).getImgURL()!=null) {
            Picasso.get().load(mData.get(i).getImgURL())
                    .fit()
                    .centerCrop()
                    .into(holder.tv_image);
        }
         **/

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class monitorUserViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_Name,tv_Email;
       // private ImageView tv_image;

        public monitorUserViewHolder(View itemView){
            super(itemView);

            //tv_image = (ImageView) itemView.findViewById(R.id.monitorUserImage);
            tv_Name = (TextView) itemView.findViewById(R.id.monitorUserName);
            tv_Email = (TextView) itemView.findViewById(R.id.monitorUserEmail);
        }
    }
}
