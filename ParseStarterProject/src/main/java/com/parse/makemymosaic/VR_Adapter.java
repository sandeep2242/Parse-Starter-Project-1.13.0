package com.parse.makemymosaic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

    public VR_Adapter(ViewRequests context, List<Rqst_G_S> list) {
        this.context = context;
        this.rqstGSList = list;

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
        holder.distance.setText(item.getDistance());
        holder.duration.setText(item.getDuration());
        holder.username.setText(item.getUsernames());

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
    }

    @Override
    public int getItemCount() {
        return rqstGSList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username, distance, duration;
        LinearLayout vrLinear;
        public MyViewHolder(View v) {
            super(v);

            username = (TextView) v.findViewById(R.id.username);
            distance = (TextView) v.findViewById(R.id.distance);
            duration = (TextView) v.findViewById(R.id.duration);
            vrLinear = (LinearLayout) v.findViewById(R.id.vr_linear);
        }
    }
}
