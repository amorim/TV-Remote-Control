package com.lucas.remotecontroller;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ChannelViewHolder> {

    private List<String> dataset;



    public static class ChannelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtChannelName;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            txtChannelName = itemView.findViewById(R.id.txtChannel);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            TVAPI.switchChannel(getAdapterPosition(), txtChannelName.getContext());
        }
    }

    public ListAdapter(List<String> dataset) {
        this.dataset = dataset;
    }

    @Override
    public ListAdapter.ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ChannelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        holder.txtChannelName.setText(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


}
