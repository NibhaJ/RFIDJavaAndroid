package com.example.rfidreadersample;

import com.example.rfidreadersample.entity.TagInfo;

import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<TagInfo> mTagList;
    private Integer thisPosition = null;

    public Integer getThisPosition() {
        return thisPosition;
    }

    public void setThisPosition(Integer thisPosition) {
        this.thisPosition = thisPosition;
    }

    public RecyclerViewAdapter(List<TagInfo> list) {
        mTagList = list;
    }

    public void notifyData(List<TagInfo> tagInfoList) {
        if (tagInfoList != null) {
            mTagList = tagInfoList;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TagInfo tag = mTagList.get(position);
        holder.index.setText(tag.getIndex().toString());
        holder.type.setText(tag.getType());
        holder.userData.setText(tag.getUserData());
        holder.reserveData.setText(tag.getReservedData());
        holder.epc.setText(tag.getEpc());
        holder.tid.setText(tag.getTid());
        holder.rssi.setText(tag.getRssi());
        holder.count.setText(tag.getCount().toString());

        if (getThisPosition() != null && position == getThisPosition()) {
            int highlightColor = Color.rgb(135, 206, 235);
            holder.index.setBackgroundColor(highlightColor);
            holder.type.setBackgroundColor(highlightColor);
            holder.tid.setBackgroundColor(highlightColor);
            holder.epc.setBackgroundColor(highlightColor);
            holder.count.setBackgroundColor(highlightColor);
            holder.rssi.setBackgroundColor(highlightColor);
            holder.userData.setBackgroundColor(highlightColor);
            holder.reserveData.setBackgroundColor(highlightColor);
        } else {
            holder.index.setBackgroundColor(Color.WHITE);
            holder.type.setBackgroundColor(Color.WHITE);
            holder.tid.setBackgroundColor(Color.WHITE);
            holder.epc.setBackgroundColor(Color.WHITE);
            holder.count.setBackgroundColor(Color.WHITE);
            holder.rssi.setBackgroundColor(Color.WHITE);
            holder.userData.setBackgroundColor(Color.WHITE);
            holder.reserveData.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return mTagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView index;
        TextView type;
        TextView epc;
        TextView tid;
        TextView rssi;
        TextView count;
        TextView userData;
        TextView reserveData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.index);
            type = itemView.findViewById(R.id.type);
            epc = itemView.findViewById(R.id.epc);
            tid = itemView.findViewById(R.id.tid);
            rssi = itemView.findViewById(R.id.rssi);
            count = itemView.findViewById(R.id.count);
            userData = itemView.findViewById(R.id.userData);
            reserveData = itemView.findViewById(R.id.reserveData);
        }

    }

}

