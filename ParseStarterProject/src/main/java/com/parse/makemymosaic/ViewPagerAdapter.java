package com.parse.makemymosaic;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Lenovo on 21-07-2017.
 */

class ViewPagerAdapter extends PagerAdapter {
    Context context;
    ImageView imgs;
    LayoutInflater inflater;
    private List<AutoImages> imagesList = null;

    public ViewPagerAdapter(Context context, List<AutoImages> list) {
        this.context = context;
        this.imagesList = list;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        AutoImages ai = imagesList.get(position);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,false);



        imgs = (ImageView) itemView.findViewById(R.id.flag);
        Picasso.with(context).load(imagesList.get(position).getImages()).placeholder(R.drawable.ic_action_name).into(imgs);

        Log.i("sand", String.valueOf(imagesList.get(position).getImages()));


        container.addView(itemView);
        return itemView;
    }


    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((LinearLayout) arg1);
    }


    @Override
    public Parcelable saveState() {
        return null;
    }


}
