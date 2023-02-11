package com.example.test333;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.fimgViewHolder>{

    List<String> uri = new ArrayList<String>();
    List <String> fimg_images;
    LayoutInflater fimg_inflater;
    ImageView dialog_iv;
    Button dialog_but;

    public ImageAdapter(Context ctx, List <String> fimg_images){
        this.fimg_images = fimg_images;
        this.fimg_inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public fimgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View fimg_view = fimg_inflater.inflate(R.layout.fimg_item, null, false);
        return new fimgViewHolder(fimg_view);
    }

    @Override
    public void onBindViewHolder(@NonNull fimgViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext()).load(fimg_images.get(position)).into(holder.fimg_iv);
        uri.add(fimg_images.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(fimg_inflater.getContext());
                dialog.setContentView(R.layout.dialog_vendimg);
                dialog_iv = dialog.findViewById(R.id.dialog_iv);
                dialog_but = dialog.findViewById(R.id.dialog_delimg);
                Glide.with(holder.itemView.getContext()).load(fimg_images.get(holder.getAdapterPosition())).into(dialog_iv);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(dialog.getContext(), R.drawable.prof_dialog_background));
                }
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
                dialog.show();
                dialog_but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(dialog.getContext(), "Image will be removed shortly", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
//                        Toast.makeText(dialog.getOwnerActivity(), "Image will be removed shortly", Toast.LENGTH_SHORT).show();
                        //code to remove  image from recycler view
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return fimg_images.size();
    }

    public class fimgViewHolder extends RecyclerView.ViewHolder{

        ImageView fimg_iv;

        public fimgViewHolder(@NonNull View itemView) {
            super(itemView);
            fimg_iv = itemView.findViewById(R.id.fimg_iv);

        }
    }
}
