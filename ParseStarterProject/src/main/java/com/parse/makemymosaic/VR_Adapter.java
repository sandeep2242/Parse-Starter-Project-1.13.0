package com.parse.makemymosaic;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lenovo on 27-10-2017.
 */

class VR_Adapter extends RecyclerView.Adapter<VR_Adapter.MyViewHolder> {
    private Context context;
    private List<Rqst_G_S> rqstGSList = null;
    private Location location;

    public VR_Adapter(ViewRequests context, List<Rqst_G_S> list, Location location) {
        this.context = context;
        this.rqstGSList = list;
        this.location = location;

    }

    @Override
    public VR_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vr_single_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VR_Adapter.MyViewHolder holder, int position) {

        int pos = position;
        final Rqst_G_S item = rqstGSList.get(pos);
        holder.distance.setText(item.getListViewContent());
        holder.username.setText(item.getUsernames());
        Log.i("dhamiji",item.getLocation().getLatitude()+" "+item.getLocation().getLongitude());

        holder.vrLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewRiderLocation.class);
                i.putExtra("userNames", item.getUsernames());
                i.putExtra("lat", item.getLat());
                i.putExtra("lng", item.getLng());
                i.putExtra("userLat", item.getLocation().getLatitude());
                i.putExtra("userLng", item.getLocation().getLongitude());
                context.startActivity(i);
            }
        });
        Log.i("sandeep", item.getUsernames());

    }

    @Override
    public int getItemCount() {
        return rqstGSList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username,distance;
        LinearLayout vrLinear;
        public MyViewHolder(View v) {
            super(v);

            username = (TextView) v.findViewById(R.id.username);
            distance = (TextView) v.findViewById(R.id.distance);
            vrLinear = (LinearLayout) v.findViewById(R.id.vr_linear);
        }
    }
}
