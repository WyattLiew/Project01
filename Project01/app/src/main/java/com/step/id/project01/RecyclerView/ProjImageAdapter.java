package com.step.id.project01.RecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.step.id.project01.R;
import com.step.id.project01.model.projectImageAddon;

import java.util.ArrayList;

public class ProjImageAdapter extends RecyclerView.Adapter<ProjImageAdapter.ViewHolder> {

   Context context;
   ArrayList<projectImageAddon> projectImages;

    public ProjImageAdapter(Context context, ArrayList<projectImageAddon> projectImages) {
        this.context = context;
        this.projectImages = projectImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
       View v = LayoutInflater.from(context).inflate(R.layout.list_upload_image,parent,false);
       return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

       projectImageAddon image = projectImages.get(i);
        Picasso.get().load(image.getImgURL())
                .fit()
                .centerCrop()
                .into(viewHolder.fileImageView);
    }

    @Override
    public int getItemCount() {
        return projectImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

         private ImageView fileImageView;

        public ViewHolder(View itemView){
            super(itemView);

            mView = itemView;

            fileImageView = (ImageView)mView.findViewById(R.id.def_uploadImage);
        }
    }
}
