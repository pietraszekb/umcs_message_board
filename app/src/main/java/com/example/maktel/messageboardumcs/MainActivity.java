package com.example.maktel.messageboardumcs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "MainActivity";

    private SharedPreferences.Editor mSharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mSharedPreferencesEditor = getPreferences(Context.MODE_PRIVATE).edit();
        mSharedPreferencesEditor.clear();
        mSharedPreferencesEditor.apply();
        networkRunnable.run();

        startActivity(new Intent(this, ScreenSlideActivity.class));
    }

    private final Handler networkHandler = new Handler();
    private final static int REQUEST_DELAY = 3000;

    // TODO convert it into AsyncTask or something
    private Runnable networkRunnable = new Runnable() {
        @Override
        public void run() {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://192.168.0.22:3000";

            // TODO instead of StringRequest, use JsonArrayRequest
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(DEBUG_TAG, "response: " + response);

                            // TODO elements are never automatically deleted -- need to clear
                            // SharedPrefs
                            try {
                                JSONArray jsonArray = new JSONArray(response);

                                for (int i = 0; i < jsonArray.length(); ++i) {
                                    mSharedPreferencesEditor.putString("news_content_" + (i + 1),
                                            jsonArray.getString(i));
                                }
                                mSharedPreferencesEditor.apply();

                                // TODO extract this value in SSActivity somehow
                                // mind order when adding clear()
                                mSharedPreferencesEditor.putInt("slides_number", jsonArray.length
                                        ());

                            } catch (JSONException e) {
                                Log.e(DEBUG_TAG, e.getMessage());
                            }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(DEBUG_TAG, "error: " + error);
                    // TODO handle an error, retry or something
                    // first elements is always a placeholder
                    mSharedPreferencesEditor.putString("news_content_0", ""); // placeholder


                    mSharedPreferencesEditor.apply();  // TODO potential problems, maybe use
                    // commit() ?
                }
            });

            Log.d(DEBUG_TAG, "Request has been sent");

            queue.add(stringRequest);

            networkHandler.postDelayed(networkRunnable, REQUEST_DELAY);
        }
    };
}
