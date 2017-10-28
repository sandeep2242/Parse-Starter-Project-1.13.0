package com.parse.makemymosaic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;


public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.MyViewHolder> {

    private Context context;
    private List<Cabs_G_S> jobs_g_sList = null;

    public JobsAdapter(Context context, List<Cabs_G_S> grid_list) {

        this.context = context;
        this.jobs_g_sList = grid_list;


    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, fare, seats, bookSingle, bookWholeCab, ifBooked;
        ImageView cabImage;
        LinearLayout linear3;
        View mview;

        MyViewHolder(View v) {
            super(v);
            mview = v;
            name = (TextView) v.findViewById(R.id.cabName);
            fare = (TextView) v.findViewById(R.id.cabFare);
            seats = (TextView) v.findViewById(R.id.cabSeats);
            bookWholeCab = (TextView) v.findViewById(R.id.bookWhole);
            bookSingle = (TextView) v.findViewById(R.id.bookSingle);
            ifBooked = (TextView) v.findViewById(R.id.ifBooked);
            linear3 = (LinearLayout) v.findViewById(R.id.linear3);
            cabImage = (ImageView) v.findViewById(R.id.cabImage);


        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jobs_1_single_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Cabs_G_S item = jobs_g_sList.get(position);
        holder.name.setText(item.getCabName());
        holder.fare.setText(item.getPrice());
        holder.seats.setText(String.valueOf(item.getTotalSeats() - item.getBookedSeats()));

        if (item.getBookedSeats() == 0) {
            holder.bookWholeCab.setVisibility(View.VISIBLE);
        } else {
            holder.bookWholeCab.setVisibility(View.GONE);
        }

        if (item.getDiscount() != 0) {
            holder.bookWholeCab.setText("Book all seats with " + String.valueOf(item.getDiscount()) + " % discount");
        } else {
            holder.bookWholeCab.setText("Book all seats");
        }
        Picasso.with(context).load(item.getCabImages()).into(holder.cabImage);

        if (ParseUser.getCurrentUser().get("requested").equals(true) && !ParseUser.getCurrentUser().get("cabId").equals(item.getObjId())) {
            holder.ifBooked.setVisibility(View.VISIBLE);
            holder.bookWholeCab.setOnClickListener(null);
            holder.bookSingle.setOnClickListener(null);

            holder.linear3.setAlpha(0.2f);
        } else {
            holder.bookSingle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, UserLocation.class);
                    i.putExtra("cabId", item.getObjId());
                    i.putExtra("driverName",item.getDriverName());
                    context.startActivity(i);
                }
            });

            holder.bookWholeCab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, UserLocation.class);
                    i.putExtra("type", true);         //if true whole cab is booked
                    i.putExtra("cabId", item.getObjId());
                    i.putExtra("driverName",item.getDriverName());
                    context.startActivity(i);
                }
            });
        }


    }

    @Override
    public int getItemCount() {

        return jobs_g_sList.size();


    }


}
