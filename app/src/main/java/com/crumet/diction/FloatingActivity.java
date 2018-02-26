package com.crumet.diction;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.crumet.diction.adapter.FloatingResultsAdapter;
import com.crumet.diction.model.Results;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FloatingActivity extends AppCompatActivity {
    private String apiKey = "a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";
    private String urlJsonObj1 = "http://api.wordnik.com/v4/word.json/";
    private String getUrlJsonObj2 = "/definitions?limit=5&includeRelated=false&sourceDictionaries=webster&useCanonical=false&includeTags=false&api_key=";
    private String query = "";

    List<Results> resultsList = new ArrayList<>();
    Results results;

    RecyclerView recyclerView;
    FloatingResultsAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpWindow();
        setContentView(R.layout.activity_floating);
        CharSequence text = getIntent()
                .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        TextView textView = findViewById(R.id.text);
        textView.setText(text);

        recyclerView = findViewById(R.id.rv_results);

        adapter = new FloatingResultsAdapter(resultsList,getApplicationContext());
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        query = urlJsonObj1 + text + getUrlJsonObj2 + apiKey;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(query, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("RESPONSE", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = (JSONObject) response.get(i);
                        String word = object.getString("word");
                        String part = object.getString("partOfSpeech");
                        String meaning = object.getString("text");
                        JSONObject examples = (JSONObject) object.getJSONArray("exampleUses").get(0);
                        String example = examples.getString("text");
                        resultsList.add(new Results(word,meaning,example,part));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
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
        params.dimAmount = 0.5f;  // set it higher if you want to dim behind the window
        getWindow().setAttributes(params);

        // Gets the display size so that you can set the window to a percent of that
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // You could also easily used an integer value from the shared preferences to set the percent
        if (height > width) {
            getWindow().setLayout((int) (width * .9), (int) (height * .4));
        } else {
            getWindow().setLayout((int) (width * .7), (int) (height * .5));
        }
    }
}
