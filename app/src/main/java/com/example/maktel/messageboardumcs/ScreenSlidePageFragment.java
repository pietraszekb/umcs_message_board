package com.example.maktel.messageboardumcs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by maktel on 07.05.17.
 */

public class ScreenSlidePageFragment extends Fragment {
    private static final String DEBUG_TAG = "NewsFragment";
    // which news should be got from news table
    public static final String ARG_NEWS_INDEX = "news_index";

    private int mNewsIndex;
    private String mNewsContent;

    public static ScreenSlidePageFragment create(int newsIndex) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        // create bundle only because it's Android recommended way
        Bundle args = new Bundle();
        args.putInt(ARG_NEWS_INDEX, newsIndex);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsIndex = getArguments().getInt(ARG_NEWS_INDEX);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MainActivity",
                Context.MODE_PRIVATE);
        // TODO get some random fact as a default value (on error for example)
        String defaultValue = "No value with this key";
        mNewsContent = sharedPreferences.getString("news_content_" + Integer.toString(mNewsIndex)
                , defaultValue);
        Log.d(DEBUG_TAG, "mNewsIndex: " + mNewsIndex);
        Log.d(DEBUG_TAG, "Preferences id: " + ("news_content_" + Integer.toString(mNewsIndex)));
        Log.d(DEBUG_TAG, "mNewsContent: " + mNewsContent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page,
                container, false);

        ((TextView) rootView.findViewById(R.id.news_content)).setText(mNewsContent);

        return rootView;
    }
}
