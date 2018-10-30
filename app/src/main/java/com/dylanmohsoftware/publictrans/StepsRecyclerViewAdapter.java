package com.dylanmohsoftware.publictrans;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dylanmohsoftware.publictrans.Modules.RouteStep;

import java.text.NumberFormat;
import java.util.List;

public class StepsRecyclerViewAdapter extends RecyclerView.Adapter<StepsRecyclerViewAdapter.ViewHolder> {

    private List<RouteStep> mData;
    private LayoutInflater mInflater;
    private StepClickListener mClickListener;


    // data is passed into the constructor
    StepsRecyclerViewAdapter(Context context, List<RouteStep> steps) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = steps;
    }

    // data is passed into the constructor
    public void updateData(List<RouteStep> steps) {
        this.mData = steps;
        notifyDataSetChanged();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.step_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RouteStep tempStep = mData.get(position);
        String title = tempStep.name;
        String address = tempStep.distance.text;
        String duration = tempStep.duration.text;
        holder.stepTitle.setText(title);
        holder.stepAddress.setText(address);
        duration = duration.replace("hours", "hrs").replace("hour", "hr");;
        holder.stepDuration.setText(duration);
        if (tempStep.vehicle.price > 0) {
            holder.stepPrice.setText(NumberFormat.getCurrencyInstance().format(tempStep.vehicle.price));
        }
        else {
            holder.stepPrice.setText("");
        }
        if (tempStep.vehicle.type.equals("BUS")) {
            holder.stepIcon.setImageResource(R.drawable.bus2);
        }
        else if (tempStep.vehicle.type.equals("HEAVY_RAIL")) {
            holder.stepIcon.setImageResource(R.drawable.rail2);
        }
        else if (tempStep.vehicle.type.equals("TRAM")) {
            holder.stepIcon.setImageResource(R.drawable.tram2);
        }
        else {
            holder.stepIcon.setImageResource(R.drawable.walk2);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView stepTitle;
        TextView stepAddress;
        TextView stepDuration;
        TextView stepPrice;
        ImageView stepIcon;


        ViewHolder(View itemView) {
            super(itemView);
            stepTitle = itemView.findViewById(R.id.step_title);
            stepAddress = itemView.findViewById(R.id.step_address);
            stepDuration = itemView.findViewById(R.id.step_duration);
            stepPrice = itemView.findViewById(R.id.step_price);
            stepIcon = itemView.findViewById(R.id.icon_step);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onStepClick(view, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    RouteStep getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setStepClickListener(StepClickListener stepClickListener) {
        this.mClickListener = stepClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface StepClickListener {
        void onStepClick(View view, int position);
    }
}