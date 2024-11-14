
package com.cwp.game.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cwp.game.R;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleDemoItemAdapter extends RecyclerView.Adapter<SimpleDemoItemAdapter.MyViewHolder> implements View.OnClickListener {
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    OnListItemClickMessageListener mOnItemClickListener;

    public SimpleDemoItemAdapter(OnListItemClickMessageListener clickListener) {
        mOnItemClickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_minimal, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        vh.itemView.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText("Item " + position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public void onClick(@NonNull View v) {
        RecyclerView rv = RecyclerViewAdapterUtils.getParentRecyclerView(v);
        RecyclerView.ViewHolder vh = rv.findContainingViewHolder(v);

        int rootPosition = vh.getAdapterPosition();
        if (rootPosition == RecyclerView.NO_POSITION) {
            return;
        }

        // need to determine adapter local position like this:
        RecyclerView.Adapter rootAdapter = rv.getAdapter();
        int localPosition = WrapperAdapterUtils.unwrapPosition(rootAdapter, this, rootPosition);

        String message = "CLICKED: Normal item " + localPosition;

        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClicked(message);
        }
    }
}
