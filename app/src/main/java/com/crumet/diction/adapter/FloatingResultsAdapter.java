package com.crumet.diction.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.crumet.diction.R;
import com.crumet.diction.model.Results;


import java.util.List;

public class FloatingResultsAdapter extends RecyclerView.Adapter<FloatingResultsAdapter.ViewHolder> {
    private List<Results> resultsList;
    private Context context;

    private static final int UNSELECTED = -1;

    private RecyclerView recyclerView;
    private int selectedItem = UNSELECTED;

    public FloatingResultsAdapter(RecyclerView recyclerView, List<Results> resultsList, Context context) {
        this.resultsList = resultsList;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder h, int position) {


        final boolean[] expanded = {false};
        Results results = resultsList.get(position);
        String examples = results.getExample();
        String parts = results.getPartOfSpeech();
        String meaning = results.getMeaning();
        h.meaning.setText(Html.fromHtml("<b>" + parts + ". </b>" + meaning));
        if (examples.length() > 0) {
            h.example.setText(Html.fromHtml("<b>Examples: </b><br/>" + examples));
        } else {
            h.example.setVisibility(View.GONE);
            h.expandButton.setVisibility(View.GONE);
        }

        int number = position + 1;
        String num = number + ". ";
        h.number.setText(num);

        h.expandMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!expanded[0]) {
                    expanded[0] = true;
                    expand(h.expandableLayout);
                    h.expandButton.setImageResource(R.drawable.ic_arrow_drop_up);
                } else {
                    expanded[0] = false;
                    collapse(h.expandableLayout);
                    h.expandButton.setImageResource(R.drawable.ic_arrow_drop_down);
                }
            }
        });
    }

    private void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetheight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetheight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targetheight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout expandableLayout;
        private ImageView expandButton;
        private TableRow expandMore;
        private TextView meaning, example;
        private TextView number;

        ViewHolder(View v) {
            super(v);
            meaning = v.findViewById(R.id.meaning);
            example = v.findViewById(R.id.example);
            number = v.findViewById(R.id.number);

            expandableLayout = v.findViewById(R.id.more_about_word);

            expandButton = v.findViewById(R.id.expand_button);
            expandMore = v.findViewById(R.id.expand_more);

        }


    }
}
