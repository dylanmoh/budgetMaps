package com.dylanmohsoftware.publictrans;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dylanmohsoftware.publictrans.Modules.Route;

import java.text.NumberFormat;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Route> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Route> routes) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = routes;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.result_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Route tempRoute = mData.get(position);
        String title = "Option " + Integer.toString(tempRoute.index + 1);
        String schedule = tempRoute.startTime.text + " - " + tempRoute.endTime.text;
        String duration = tempRoute.duration.text;
        String price = NumberFormat.getCurrencyInstance().format(tempRoute.price);
        holder.resultTitle.setText(title);
        holder.resultSchedule.setText(schedule);
        holder.resultDuration.setText(duration);
        holder.resultPrice.setText(price);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView resultTitle;
        TextView resultSchedule;
        TextView resultDuration;
        TextView resultPrice;


        ViewHolder(View itemView) {
            super(itemView);
            resultTitle = itemView.findViewById(R.id.result_title);
            resultSchedule = itemView.findViewById(R.id.result_schedule);
            resultDuration = itemView.findViewById(R.id.result_duration);
            resultPrice = itemView.findViewById(R.id.result_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    Route getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}