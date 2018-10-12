package com.dylanmohsoftware.publictrans;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dylanmohsoftware.publictrans.Modules.Route;
import com.dylanmohsoftware.publictrans.Modules.RouteStep;

import java.text.NumberFormat;
import java.util.List;

public class StepsRecyclerViewAdapter extends RecyclerView.Adapter<StepsRecyclerViewAdapter.ViewHolder> {

    private List<RouteStep> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    StepsRecyclerViewAdapter(Context context, List<RouteStep> steps) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = steps;
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
        holder.stepDuration.setText(duration);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView stepTitle;
        TextView stepAddress;
        TextView stepDuration;


        ViewHolder(View itemView) {
            super(itemView);
            stepTitle = itemView.findViewById(R.id.step_title);
            stepAddress = itemView.findViewById(R.id.step_address);
            stepDuration = itemView.findViewById(R.id.step_duration);
        }

    }

    // convenience method for getting data at click position
    RouteStep getItem(int id) {
        return mData.get(id);
    }

}