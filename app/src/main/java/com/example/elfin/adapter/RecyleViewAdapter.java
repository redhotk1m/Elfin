package com.example.elfin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elfin.Activities.AboutCharger;
import com.example.elfin.Activities.Station.StationList.ChargerItem;
import com.example.elfin.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
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
                ChargerItem chargerItem = chargerItems.get(myViewHolder.getAdapterPosition());
                String longLatString = latlng.latitude +"," +latlng.longitude;
                intent = new Intent(context, AboutCharger.class);


                System.out.println(chargerItem.getOwnedBy());
                //intent.putExtra("charger", chargerItem);
                /*
                intent.getParcelableArrayExtra("charger", chargerItems);
                intent.getParcelableArrayListExtra("charger", chargerItems);

                 */

                ArrayList<String> infoFromList = new ArrayList<>();
                infoFromList.add(chargerItem.getOwnedBy());
                infoFromList.add(chargerItem.getStreet() + " " + chargerItem.getHouseNumber());
                infoFromList.add(chargerItem.getChademo());
                infoFromList.add(chargerItem.getNumberOfChademo());
                infoFromList.add(chargerItem.getCcs());
                infoFromList.add(chargerItem.getNumberOfCcs());
                infoFromList.add(chargerItem.getLightningCCS());
                infoFromList.add(chargerItem.getNumberOflightningCCS());
                infoFromList.add(chargerItem.getDescriptionOfLocation());
                infoFromList.add(chargerItem.getContactInfo());


                intent.putExtra("infoFromList", infoFromList);

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
        holder.imageViewFast.setImageResource(chargerItems.get(position).getImageFast());
        holder.imageViewSlow.setImageResource(chargerItems.get(position).getImageSlow());
        //holder.chargeTimeFast.setText(chargerItems.get(position).getFastTime());


        holder.avaiableFast.setText(chargerItems.get(position).getNumberOfChademo());
        holder.chargeTimeFast.setText(chargerItems.get(position).getChademoTime());
        holder.chargeTimeLightning.setText(chargerItems.get(position).getLightningTime());
        holder.avaiableLightning.setText(chargerItems.get(position).getNumberOflightningCCS());
        holder.textViewFast.setText(chargerItems.get(position).getFastText());
        holder.textViewLigtning.setText(chargerItems.get(position).getLightningText());

        if("CCS/Combo".equals(chargerItems.get(position).getCcs())){
            holder.avaiableFast.setText(chargerItems.get(position).getNumberOfCcs());
            holder.chargeTimeFast.setText(chargerItems.get(position).getCcsTime());
            holder.chargeTimeLightning.setText(chargerItems.get(position).getLightningTime());
            holder.avaiableLightning.setText(chargerItems.get(position).getNumberOflightningCCS());
            holder.textViewFast.setText(chargerItems.get(position).getFastText());
            holder.textViewLigtning.setText(chargerItems.get(position).getLightningText());
        }



        //TODO: Vise med decimaltall, dersom vi har mindre enn 100KM i lengde, bare teksthåndtering
        long KM = Math.round(Double.parseDouble(chargerItems.get(position).getMFromStartLocation()) / 1000);
        holder.distanceKm.setText(String.valueOf(KM) + " KM");

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
        private TextView chargeTimeLightning;
        private TextView chargeTimeSlow2;
        private TextView avaiableLightning;
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
            chargeTimeLightning = itemView.findViewById(R.id.charge_time_lightning);
            avaiableLightning = itemView.findViewById(R.id.available_lightning);
            imageViewFast = itemView.findViewById(R.id.imageViewFast);
            imageViewSlow = itemView.findViewById(R.id.imageViewSlow);
            textViewFast = itemView.findViewById(R.id.textViewFast);
            textViewLigtning = itemView.findViewById(R.id.textViewLigtning);
            distanceKm = itemView.findViewById(R.id.distance_km);

        }
    }
}
