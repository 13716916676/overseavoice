package com.aispeech.hotwords.ui.dialog.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aispeech.hotwords.R;
import com.aispeech.hotwords.ui.dialog.ItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author speech  12.19 2018
 */
public class DialogItemAdapter extends RecyclerView.Adapter<DialogItemAdapter.ItemViewHolder> {

    private List datas = new ArrayList();
    private ItemSelectedListener itemSelectedListener;
    private int currentSelectedPosition;

    public DialogItemAdapter() {
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_single_choose_item, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedPosition = (int) v.getTag();
                notifyDataSetChanged();
                if (itemSelectedListener != null) {
                    itemSelectedListener.onSelected(currentSelectedPosition);
                }
            }
        });
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.tvItemContent.setText((String) datas.get(position));
        if (currentSelectedPosition == position) {
            holder.tvItemContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_item_selected, 0);
        } else {
            holder.tvItemContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setItemSelectedListener(ItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemContent;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvItemContent = itemView.findViewById(R.id.tv_item_content);
        }


    }

    public String getCurrentSelected() {
        return (String) datas.get(currentSelectedPosition);
    }

    public void setCurrentselected(String item) {
        if (!datas.isEmpty()) {
            int index = datas.indexOf(item);
            currentSelectedPosition = index;
        }

    }

    /**
     * 更新数据
     */
    public void updateData(List datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }
}
