package com.parse.makemymosaic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

class HoriAdapter extends RecyclerView.Adapter<HoriAdapter.MyViewHolder> {

    private Context context;
    private List<AutoImages> imagesList = null;
    private ArrayList<AutoImages> arrayList;

    HoriAdapter(Context context, List<AutoImages> list) {
        this.context = context;
        this.imagesList = list;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(imagesList);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hori_single,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        AutoImages ai = imagesList.get(position);
        holder.txt.setText(ai.getImagesName());

        Glide.with(context).load(ai.getImages()).dontAnimate().into(holder.imgs);
    }



    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgs ;
        TextView txt;
        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView =itemView;
            imgs = (ImageView) itemView.findViewById(R.id.single_image);
            txt = (TextView) itemView.findViewById(R.id.single_text);

        }
    }
}
