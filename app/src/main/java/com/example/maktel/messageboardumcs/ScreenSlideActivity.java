package com.example.maktel.messageboardumcs;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ScreenSlideActivity extends FragmentActivity {
    private static final String DEBUG_TAG = "ScreenSlideActivity";
    // handles animations and swipes
    private ViewPager mPager;

    // provides pages to view pager widget
    private ScreenSlidePagerAdapter mPagerAdapter;
    private int mCurrentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setSize(5);  // TODO change this hardcoded value
        mPager.setAdapter(mPagerAdapter);
        // extracts which page is currently displayed
        mPager.addOnPageChangeListener(new PageListener());

        // freezes automatic slide change if users touches screen
        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollHandler.removeCallbacks(ViewPagerAutomaticScroll);
                scrollHandler.postDelayed(ViewPagerAutomaticScroll, SCROLL_DELAY);
                Log.d(DEBUG_TAG, "Callbacks to automatic swipe cleared");
                return false;
            }
        });

        // initiates automatic slide change loop
        scrollHandler.postDelayed(ViewPagerAutomaticScroll, SCROLL_DELAY);
        // skip first placeholder item
        mPager.setCurrentItem(1, false);
    }

    private final Handler scrollHandler = new Handler();
    public static final int SCROLL_DELAY = 2500;
    Runnable ViewPagerAutomaticScroll = new Runnable() {
        @Override
        public void run() {
            mPager.setCurrentItem(++mCurrentItem, true);

            scrollHandler.postDelayed(ViewPagerAutomaticScroll, SCROLL_DELAY);
        }
    };

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private static final String DEBUG_TAG = "PagerAdapter";
        private int mSize;

        public ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int newsIndex) {
            Log.d(DEBUG_TAG, "Created item: " + newsIndex);
            return ScreenSlidePageFragment.create(newsIndex);
        }

        public void setSize(int size) {
            mSize = size + 2;
        }

        @Override
        public int getCount() {
            return mSize;
        }
    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        private static final String DEBUG_TAG = "PageListener";

        public void onPageSelected(int position) {
            mCurrentItem = position;
            Log.d(DEBUG_TAG, "Current page: " + position);

            // make so that list overscrolls from last to first and from first to last
            // elements on positions 0 and mSize - 1 are placeholders and are not displayed
            int pageCount = mPagerAdapter.getCount();
            final boolean animate = true;  // set according to end user preferences
            if (position == 0) {
                mPager.setCurrentItem(pageCount - 2, animate);
            } else if (position == pageCount - 1) {
                mPager.setCurrentItem(1, animate);
            }
        }
    }
}
