package com.crumet.diction.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crumet.diction.R;
import com.crumet.diction.model.Results;

import java.util.List;

/**
 * Created by ravi on 2/25/2018.
 */

public class FloatingResultsAdapter extends RecyclerView.Adapter<FloatingResultsAdapter.ViewHolder> {
    List<Results> resultsList ;
    Context context;

    public FloatingResultsAdapter(List<Results> resultsList, Context context) {
        this.resultsList = resultsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        Results results = resultsList.get(position);
        h.meaning.setText(Html.fromHtml("<b>"+results.getPartOfSpeech()+": </b>"+ results.getMeaning()));
        h.example.setText(Html.fromHtml(results.getExample()));
        int number  = position+1;
        h.number.setText(number+") ");
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView meaning,textExample,example;
        ImageView imageView;
        TextView number;

        ViewHolder(View v) {
            super(v);
            meaning = v.findViewById(R.id.meaning);
            textExample = v.findViewById(R.id.text_example);
            example = v.findViewById(R.id.example);
            imageView = v.findViewById(R.id.expand);
            number = v.findViewById(R.id.number);
        }
    }
}
