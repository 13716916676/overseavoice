package com.example.hotwordsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ListviewAdapter extends BaseAdapter {

    private Context mContext;
    private List<ListViewBean> mListViewBeans;
    private ViewHolder mViewHolder;

    public ListviewAdapter(Context context, List<ListViewBean> listViewBeans) {
        this.mContext =context;
        mListViewBeans = listViewBeans;
    }

    @Override
    public int getCount() {
        return mListViewBeans != null ? mListViewBeans.size() : 0 ;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leftlist_itemview, null);
            mViewHolder = new ViewHolder();
            mViewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            mViewHolder.textView = (TextView) convertView.findViewById(R.id.textview);
            convertView.setTag(mViewHolder);

        } else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        ListViewBean item = mListViewBeans.get(position);
        if (item!=null){
            mViewHolder.imageView.setImageDrawable(item.getDrawable());
            mViewHolder.textView.setText(item.getBeanName());
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView textView;
    }
}
