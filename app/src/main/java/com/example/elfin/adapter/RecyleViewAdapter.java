package com.example.elfin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elfin.Activities.AboutCharger;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RecyleViewAdapter extends RecyclerView.Adapter<RecyleViewAdapter.MyViewHolder> {

    Context context;
    List<ChargerItem> chargerItems;
    Intent intent;


    public RecyleViewAdapter(Context context, List<ChargerItem> chargerItems) {
        this.context = context;
        this.chargerItems=chargerItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.charge_stations_list, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);
        myViewHolder.chargeStationslistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latlng = chargerItems.get(myViewHolder.getAdapterPosition()).getLatLng();
                //ChargerItem chargerItem = chargerItems.get(myViewHolder.getAdapterPosition());
                String longLatString = latlng.latitude +"," +latlng.longitude;
                intent = new Intent(context, AboutCharger.class);
                /*

                System.out.println(chargerItem.getOwnedBy());
                intent.putExtra("charger", chargerItem);

                 */

                String owned = chargerItems.get(myViewHolder.getAdapterPosition()).getOwnedBy();
                intent.putExtra("owned", owned);
                intent.putExtra("latlng", longLatString);

                context.startActivity(intent);
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        holder.stationName.setText(chargerItems.get(position).getOwnedBy());

        //holder.stationName.setText(chargerItems.get(position).getStreet() + " " + chargerItems.get(position).getHouseNumber());
        holder.description.setText(chargerItems.get(position).getStreet() + " " + chargerItems.get(position).getHouseNumber()
                + " " +chargerItems.get(position).getCity());
        holder.chargeTimeFast.setText(chargerItems.get(position).getFastTime());
        holder.avaiableFast.setText(chargerItems.get(position).getNumberOfChademo());
        holder.chargeTimeSlow.setText(chargerItems.get(position).getLightningTime());
        holder.avaiableSlow.setText(chargerItems.get(position).getNumberOfCcs());
        holder.imageViewFast.setImageResource(chargerItems.get(position).getImageFast());
        holder.imageViewSlow.setImageResource(chargerItems.get(position).getImageSlow());
        holder.textViewFast.setText(chargerItems.get(position).getFast());
        holder.textViewLigtning.setText(chargerItems.get(position).getLigtning());


/*
        if(chargerItems.get(position).getChademo().equals("")){
            holder.imageViewFast.setVisibility(View.GONE);
        }
        if(chargerItems.get(position).getCcs().equals("")){
            holder.imageViewSlow.setVisibility(View.GONE);
        }

 */




        /*
        holder.chargeTimeFast.setText(chargerItems.get(position).getChargeTimeFast());
        holder.avaiableFast.setText(chargerItems.get(position).getAvailableFast());
        holder.chargeTimeSlow.setText(chargerItems.get(position).getChargeTimeSlow());
        holder.avaiableSlow.setText(chargerItems.get(position).getAvailableSlow());
        holder.distanceKm.setText(chargerItems.get(position).getDistanceKm());
        holder.imageViewFast.setImageResource(chargerItems.get(position).getImageFast());
        holder.imageViewSlow.setImageResource(chargerItems.get(position).getImageSlow());
         */
    }



    @Override
    public int getItemCount() {
        return chargerItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView stationName;
        private TextView description;
        private TextView chargeTimeFast;
        private TextView avaiableFast;
        private TextView chargeTimeSlow;
        private TextView chargeTimeSlow2;
        private TextView avaiableSlow;
        private TextView avaiableSlow2;
        private TextView distanceKm;
        private TextView textViewFast;
        private TextView textViewLigtning;


        private ImageView imageViewFast;
        private ImageView imageViewSlow;
        private ImageView imageViewSlow2;


        private ConstraintLayout chargeStationslistLayout;



        public MyViewHolder(View itemView) {
            super(itemView);
            chargeStationslistLayout = itemView.findViewById(R.id.charge_stations_list_id);
            stationName = itemView.findViewById(R.id.station_name);
            description = itemView.findViewById(R.id.description);
            chargeTimeFast = itemView.findViewById(R.id.charge_time_fast);
            avaiableFast = itemView.findViewById(R.id.available_fast);
            chargeTimeSlow = itemView.findViewById(R.id.charge_time_slow);
            avaiableSlow = itemView.findViewById(R.id.available_slow);
            imageViewFast = itemView.findViewById(R.id.imageViewFast);
            imageViewSlow = itemView.findViewById(R.id.imageViewSlow);
            textViewFast = itemView.findViewById(R.id.textViewFast);
            textViewLigtning = itemView.findViewById(R.id.textViewLigtning);






            /*
            chargeTimeFast = itemView.findViewById(R.id.charge_time_fast);
            avaiableFast = itemView.findViewById(R.id.available_fast);
            chargeTimeSlow = itemView.findViewById(R.id.charge_time_slow);
            avaiableSlow = itemView.findViewById(R.id.available_slow);
            distanceKm = itemView.findViewById(R.id.distance_km);
            imageViewFast = itemView.findViewById(R.id.imageViewFast);
            imageViewSlow = itemView.findViewById(R.id.imageViewSlow);


             */

        }
    }
}
