package com.example.maktel.messageboardumcs;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by maktel on 07.05.17.
 */

public class ScreenSlidePageFragment extends Fragment {
    private static final String DEBUG_TAG = "NewsFragment";
    // which news should be got from news table
    public static final String ARG_NEWS_INDEX = "news_index";
    public static final String ARG_NEWS_OBJECT = "news_object";

    private int mNewsIndex;
    private News mNews;

    public static ScreenSlidePageFragment create(int newsIndex, News news) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        // create bundle only because it's Android recommended way
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEWS_OBJECT, news);
        args.putInt(ARG_NEWS_INDEX, newsIndex);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNewsIndex = getArguments().getInt(ARG_NEWS_INDEX);
        Log.d(DEBUG_TAG, "mNewsIndex: " + mNewsIndex);

        mNews = (News) getArguments().getSerializable(ARG_NEWS_OBJECT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page,
                container, false);

        ((TextView) rootView.findViewById(R.id.news_title)).setText(mNews.title);
        ((TextView) rootView.findViewById(R.id.news_text)).setText(mNews.text);
        ((TextView) rootView.findViewById(R.id.news_author)).setText(mNews.author);
        ((TextView) rootView.findViewById(R.id.news_date)).setText(mNews.date.toString());
        Drawable image = getResources().getDrawable(mNews.logoDrawable);
        ((ImageView) rootView.findViewById(R.id.news_image)).setImageDrawable(image);


        return rootView;
    }
}
