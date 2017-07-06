package com.example.maktel.messageboardumcs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;


public class ScreenSlideActivity extends FragmentActivity {
    static final long serialVersionUID = 732485234L;

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

        try {
            File newsListFile = new File(getApplicationContext().getFilesDir() +
                    NEWS_LIST_FILENAME);
            Log.d(DEBUG_TAG, "Reading file of length: " + newsListFile.length());
            if (newsListFile.length() != 0) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(newsListFile));

                // safe read; ensures list if not empty
                // mNewsList = (ArrayList<News>) ois.readObject();
                Object object = ois.readObject();
                if (object instanceof ArrayList<?>) {
                    ArrayList<?> arrayList = (ArrayList<?>) object;
                    if (arrayList.size() > 0) {
                        for (int i = 0; i < arrayList.size(); ++i) {
                            Object listElement = arrayList.get(i);
                            if (listElement instanceof News) {
                                News news = (News) listElement;
                                // list was empty, simple add() will suffice
                                mNewsList.add(news);
                            }
                        }
                    }
                }

                Log.d(DEBUG_TAG, "Read list from file: " + mNewsList.toString());
                ois.close();

                if (!newsListFile.delete()) {
                    Log.w(DEBUG_TAG, "File with saved list has not been deleted");
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            Log.e(DEBUG_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }

        queue = Volley.newRequestQueue(getApplicationContext());
        networkRunnable.run();

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        // populate list if saved state couldn't recover previous elements
        if (mNewsList.size() == 0) {
            setOrAddToMNewsList(0, new News());  // placeholder
            setOrAddToMNewsList(1, new News("Czy wiesz, że...", "To jest przykładowa wiadomość informująca o " +
                    "studencie, który wyskoczył z budynku rektoratu." +
                    "No nie dość że debil przypału narobił to jeszcze wziął i szybe rozbił.", new Date(117,6,30),
                    "Asemblerowy" +
                            " Świrek", News.NewsType.FACT));
            setOrAddToMNewsList(2, new News("Czy wiesz, że...", "W dniu 21.05 za dr Iksińskeigo zostają wyznaczone zastępstwa:" +
                    "\n-grupa o godzinie 16:00 z dr ... w sali 205" +
                    "\n-grupa o godzinie 17:30 z mgr ... w sali 400" +
                    "\n-grupa o godzinie 19:00 ma odwołane zajęcia"
                    , new Date(117,7,1),
                    "Grzegorz Pędziwiatr", News.NewsType.FACT));
        }
        mPagerAdapter.setSize(mNewsList.size() - 1);

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

            Log.d(DEBUG_TAG, mNewsList.toString());

            scrollHandler.postDelayed(ViewPagerAutomaticScroll, SCROLL_DELAY);
        }
    };

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private static final String DEBUG_TAG = "PagerAdapter";
        private int mSize;

        ScreenSlidePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int newsIndex) {
            Log.d(DEBUG_TAG, "Created item: " + newsIndex);
            News news = new News();
            if (newsIndex < mNewsList.size()) news = mNewsList.get(newsIndex);
            return ScreenSlidePageFragment.create(newsIndex, news);
        }

        void setSize(int size) {
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

    ArrayList<News> mNewsList = new ArrayList<>();

    private final Handler networkHandler = new Handler();
    private final static int REQUEST_DELAY = 3000;

    private RequestQueue queue;

    private Runnable networkRunnable = new Runnable() {
        @Override
        public void run() {
//            String url = "http://192.168.1.9:3000";
            String url = "http://192.168.0.22:3000";
//            String url = "http://umcs-tablica.azurewebsites.net/api/v1/ogloszenia";

            final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                    null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    parseJSonResponse(response);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO handle an error somehow
                    Log.e(DEBUG_TAG, "Request error: " + error.getMessage());
                }
            });
            queue.add(jsonArrayRequest);

            networkHandler.postDelayed(networkRunnable, REQUEST_DELAY);
        }
    };

    private void parseJSonResponse(JSONArray responseArray) {
        try {
            int position = 1;
            for (int i = 0; i < responseArray.length(); ++i) {
                Log.d(DEBUG_TAG, "Str: " + responseArray.getString(i));

                JSONObject jsonObject = responseArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String text = jsonObject.getString("text");
                String author = jsonObject.getString("author");

                Date date = new Date(jsonObject.getLong("date") * 1000);  // java expects
                // milliseconds
                Log.d(DEBUG_TAG, "Date: " + date + " parsed: " + date.toString());

                News news = new News(title, text, date, author, News.NewsType.NEWS);
                setOrAddToMNewsList(position++, news);
            }

            mPagerAdapter.setSize(responseArray.length());
            Log.d(DEBUG_TAG, "Size of mPagerAdapter: " + mPagerAdapter.getCount());
            mPagerAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            Log.e(DEBUG_TAG, e.getMessage());
        }
    }

    private static final String NEWS_LIST_FILENAME = "NewsArrayList.ser";

    @Override
    protected void onDestroy() {
        Log.d(DEBUG_TAG, "onDestroy() called, leaving activity");

        networkHandler.removeCallbacks(networkRunnable);
        scrollHandler.removeCallbacks(ViewPagerAutomaticScroll);

        try {
            File newsListFile = new File(getApplicationContext().getFilesDir() +
                    NEWS_LIST_FILENAME);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(newsListFile));
            Log.d(DEBUG_TAG, "List to be saved: " + mNewsList.toString());
            oos.writeObject(mNewsList);

            Log.d(DEBUG_TAG, "News list saved to " + NEWS_LIST_FILENAME);
            oos.close();

        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Saving error: " + e.getMessage());
            e.printStackTrace();
            if (e.getCause() != null)
                Log.d(DEBUG_TAG, "Saving error cause: " + e.getCause().getMessage());
        }

        super.onDestroy();
    }

    /**
     * This method exists only because somehow I couldn't make serialization work with custom class
     * that extended ArrayList
     */
    private void setOrAddToMNewsList(int index, News news) {
        try {
            mNewsList.set(index, news);
        } catch (IndexOutOfBoundsException e) {
            mNewsList.add(news);
        }
    }

    /*
      TODO fix it if you can and remove setOrAddToMNewsList()
     */
//    private class SerializableArrayList extends ArrayList<News> {
//        static final long serialVersionUID = 1L;
//
//        void setOrAdd(int index, News element) {
//            try {
//                super.set(index, element);
//            } catch (IndexOutOfBoundsException e) {
//                super.add(element);
//            }
//        }
//    }

}
