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
import com.step.id.project01.model.defectImage;

import java.util.ArrayList;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {

   Context context;
   ArrayList<defectImage> defectImages;

    public UploadListAdapter(Context context, ArrayList<defectImage> defectImages) {
        this.context = context;
        this.defectImages = defectImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
       View v = LayoutInflater.from(context).inflate(R.layout.list_upload_image,parent,false);
       return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

       defectImage image = defectImages.get(i);
       viewHolder.fileNameView.setText(image.getName());
        Picasso.get().load(image.getUri())
                .fit()
                .centerCrop()
                .into(viewHolder.fileImageView);
    }

    @Override
    public int getItemCount() {
        return defectImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

         private TextView fileNameView;
         private ImageView fileImageView;

        public ViewHolder(View itemView){
            super(itemView);

            mView = itemView;

            fileNameView = (TextView)mView.findViewById(R.id.def_uploadText);
            fileImageView = (ImageView)mView.findViewById(R.id.def_uploadImage);
        }
    }
}
