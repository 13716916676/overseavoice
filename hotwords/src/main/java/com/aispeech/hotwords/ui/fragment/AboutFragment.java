package com.aispeech.hotwords.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aispeech.hotwords.BuildConfig;
import com.aispeech.hotwords.R;

import java.util.Calendar;

/**
 * @author aispeech
 */
public class AboutFragment extends Fragment {

    private TextView tv_version;
    private TextView tv_copyright;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_about, container, false);
        tv_version = view.findViewById(R.id.tv_version);
        tv_version.setText(BuildConfig.VERSION_NAME);

        tv_copyright = view.findViewById(R.id.tv_copyright);


        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        tv_copyright.setText(String.format(getActivity().getString(R.string.about_copyright), current_year + ""));

        return view;
    }
}
