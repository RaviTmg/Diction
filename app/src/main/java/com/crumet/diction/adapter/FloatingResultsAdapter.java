package com.crumet.diction.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crumet.diction.R;
import com.crumet.diction.model.Results;

import net.cachapa.expandablelayout.ExpandableLayout;

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
    public void onBindViewHolder(ViewHolder h, int position) {
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
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        private ExpandableLayout expandableLayout;
        private ImageView expandButton;
        private RelativeLayout expandMore;
        private TextView meaning, example;
        private TextView number;

        ViewHolder(View v) {
            super(v);
            meaning = v.findViewById(R.id.meaning);
            example = v.findViewById(R.id.example);
            number = v.findViewById(R.id.number);

            expandableLayout = v.findViewById(R.id.more_about_word);
            expandableLayout.setInterpolator(new OvershootInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);
            expandButton = v.findViewById(R.id.expand_button);
            expandMore = v.findViewById(R.id.expand_more);

            expandMore.setOnClickListener(this);
        }

        public void bind() {
            int position = getAdapterPosition();
            boolean isSelected = position == selectedItem;
            expandMore.setSelected(isSelected);
            expandableLayout.setExpanded(isSelected, false);
        }

        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
                holder.expandMore.setSelected(false);
                holder.expandableLayout.collapse();
            }

            int position = getAdapterPosition();
            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                expandMore.setSelected(true);
                expandableLayout.expand();
                selectedItem = position;
            }
        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            Log.d("ExpandableLayout", "State: " + state);
            if (state == ExpandableLayout.State.EXPANDING) {
                recyclerView.smoothScrollToPosition(getAdapterPosition());
            }
        }
    }
}
