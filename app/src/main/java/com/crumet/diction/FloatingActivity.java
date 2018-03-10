package com.crumet.diction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crumet.diction.adapter.FloatingResultsAdapter;
import com.crumet.diction.model.Results;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FloatingActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {
    private String apiKey = "0pZg7j5WRkmshUEzrhTYXsZmWv9tp1505KZjsn5A0bNGGCcQtm";
    private String apiHost = "wordsapiv1.p.mashape.com";
    private String apiUrl = "https://wordsapiv1.p.mashape.com/words/";


    List<Results> resultsList = new ArrayList<>();

    RecyclerView recyclerView;
    FloatingResultsAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    MaterialSearchBar materialSearchBar;
    Button btnReload;
    LinearLayout layoutNoInternet;
    LinearLayout layoutNoResults;
    LinearLayout layoutInitial;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpWindow();
        setContentView(R.layout.activity_floating);
        final CharSequence text = getIntent()
                .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);


        recyclerView = findViewById(R.id.rv_results);
        materialSearchBar = findViewById(R.id.searchBar);
        layoutNoInternet = findViewById(R.id.layout_no_internet);
        layoutNoResults = findViewById(R.id.layout_no_results);
        layoutInitial = findViewById(R.id.layout_initial);
        progressBar = findViewById(R.id.progressBar_cyclic);
        btnReload = findViewById(R.id.btn_reload);


        // initial Layout
        layoutInitial.setVisibility(View.VISIBLE);

        materialSearchBar.setOnSearchActionListener(this);
        adapter = new FloatingResultsAdapter(recyclerView, resultsList, getApplicationContext());
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        if (text == null) {
            materialSearchBar.setPlaceHolder(getString(R.string.app_name));
        } else {
            materialSearchBar.setPlaceHolder(text);
            materialSearchBar.setText(String.valueOf(text));
            performSearch(text);
        }

        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != text) {
                    performSearch(text);
                }
            }
        });
    }

    private void performSearch(CharSequence text) {
        resultsList.clear();
        String query = apiUrl + text;

        layoutInitial.setVisibility(View.GONE);
        layoutNoInternet.setVisibility(View.GONE);
        layoutNoResults.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, query, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RESPONSE", response.toString());
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                JSONArray results;
                try {
                    results = response.getJSONArray("results");
                    Log.d("sjh", results.toString());
                    layoutNoResults.setVisibility(View.GONE);
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject resObj = (JSONObject) results.get(i);
                        String word = response.getString("word");
                        String part = resObj.getString("partOfSpeech");
                        String definition = resObj.getString("definition");
                        StringBuilder exampleString = new StringBuilder();
                        try {
                            JSONArray examples = resObj.getJSONArray("examples");
                            for (int j = 0; j < examples.length(); j++) {
                                exampleString.append("- ").append(examples.get(j)).append("<br />");
                                if (j == 2) break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        resultsList.add(new Results(word, definition, exampleString.toString(), part));
                    }

                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                recyclerView.setVisibility(View.GONE);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    //This indicates that the reuest has either time out or there is no connection
                    layoutNoInternet.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    layoutInitial.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                    Log.d("ssd", "connection");
                } else if (error instanceof AuthFailureError) {
                    // Error indicating that there was an Authentication Failure while performing the request
                } else if (error instanceof ServerError) {
                    //Indicates that the server responded with a error response
                    layoutNoResults.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    layoutInitial.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Log.d("ssd", "server");
                } else if (error instanceof NetworkError) {
                    //Indicates that there was network error while performing the request
                    Log.d("ssd", "network");
                } else if (error instanceof ParseError) {
                    // Indicates that the server response could not be parsed
                    Log.d("ssd", "parse");
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-Mashape-Key", apiKey);
                params.put("wordsapiv1.p.mashape.com", apiHost);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "headerRequest");
    }

    public void setUpWindow() {
        // Nothing here because we don't need to set up anything extra for the full app.
        // Creates the layout for the window and the look of it
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Params for the window.
        // You can easily set the alpha and the dim behind the window from here
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;    // lower than one makes it more transparent
        params.dimAmount = 0.7f;  // set it higher if you want to dim behind the window
        getWindow().setAttributes(params);

        // Gets the display size so that you can set the window to a percent of that
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // You could also easily used an integer value from the shared preferences to set the percent
        if (height > width) {
            getWindow().setLayout((int) (width * 0.9), (int) (height * .55));
        } else {
            getWindow().setLayout((int) (width * 0.85), (int) (height * .55));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //save last queries to disk
        // saveSearchSuggestionToDisk(searchBar.getLastSuggestions());
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            materialSearchBar.setPlaceHolder(getString(R.string.app_name));
            layoutInitial.setVisibility(View.VISIBLE);
            layoutNoInternet.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            layoutNoResults.setVisibility(View.GONE);
        } else {
            layoutNoInternet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

        performSearch(text.toString().toLowerCase());
        // Hide soft keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

    }

    @Override
    public void onButtonClicked(int buttonCode) {

        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_BACK:
                Log.d("Button Clicker", "BACK");
                break;
        }

    }
}
