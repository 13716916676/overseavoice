package com.aispeech.hotwords.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aispeech.hotwords.App;
import com.aispeech.hotwords.R;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.SPUtils;

import java.io.File;

/**
 * @author aispeech
 */
public class CommandsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommandsAdapter adapter;
    private String[] titles;
    private String[] commands;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) LayoutInflater.from(getContext()).inflate(R.layout.fragment_commands, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));


        titles = getResources().getStringArray(R.array.commands);

        String systemControlWords = FileIOUtils.readFile2String(new File(App.getApp().getFilesDir() + "/systemControlWords"));
        String mediaControlWords = FileIOUtils.readFile2String(new File(App.getApp().getFilesDir() + "/mediaControlWords"));
        String naviControlWords = FileIOUtils.readFile2String(new File(App.getApp().getFilesDir() + "/naviControlWords"));
        String carControlWords = FileIOUtils.readFile2String(new File(App.getApp().getFilesDir() + "/carControlWords"));
        if (!TextUtils.isEmpty(systemControlWords) &&
                !TextUtils.isEmpty(mediaControlWords) &&
                !TextUtils.isEmpty(naviControlWords) &&
                !TextUtils.isEmpty(carControlWords)) {
            commands = new String[]{
                    systemControlWords, mediaControlWords, naviControlWords, carControlWords
            };
        }
        if (commands == null) {
            commands = getResources().getStringArray(R.array.commands_detail);
        }
        adapter = new CommandsAdapter(titles, commands);
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }

    private static class CommandsAdapter extends RecyclerView.Adapter<CommandsAdapter.CommandsViewHolder> {

        private String[] titles;
        private String[] commands;

        public CommandsAdapter(String[] titles, String[] commands) {
            this.titles = titles;
            this.commands = commands;
            SPUtils.getInstance("commands_item_click").clear();
        }

        @NonNull
        @Override
        public CommandsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_commands_item, parent, false);
            final CommandsViewHolder holder = new CommandsViewHolder(itemView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    boolean isExpand = SPUtils.getInstance("commands_item_click").getBoolean("tag_" + position, false);
                    SPUtils.getInstance("commands_item_click").put("tag_" + position, !isExpand);
                    notifyDataSetChanged();
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull CommandsViewHolder holder, int position) {
            holder.tvItemTitle.setText(titles[position]);
            holder.tvItemContent.setText(commands[position]);
            holder.itemView.setTag(position);

            boolean isExpand = SPUtils.getInstance("commands_item_click").getBoolean("tag_" + position, false);
            holder.tvItemContent.setMaxLines(isExpand ? Integer.MAX_VALUE : 1);
            holder.ivArrow.animate().rotation(isExpand ? -180f : 0f).setDuration(100).start();
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }

        private static class CommandsViewHolder extends RecyclerView.ViewHolder {

            TextView tvItemTitle, tvItemContent;
            ImageView ivArrow;

            public CommandsViewHolder(View itemView) {
                super(itemView);
                tvItemTitle = itemView.findViewById(R.id.tv_title);
                tvItemContent = itemView.findViewById(R.id.tv_content);
                ivArrow = itemView.findViewById(R.id.iv_arrow);
            }
        }
    }
}
