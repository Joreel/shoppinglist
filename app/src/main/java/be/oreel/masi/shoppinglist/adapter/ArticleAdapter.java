package be.oreel.masi.shoppinglist.adapter;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.oreel.masi.shoppinglist.R;
import be.oreel.masi.shoppinglist.model.Article;

/**
 * The adapter for the recyclerView of the ArticleActivity
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    // =================
    // === VARIABLES ===
    // =================

    private ArticleManager articleManager;
    private List<Article> articleDataset;

    // ===================
    // === CONSTRUCTOR ===
    // ===================

    /**
     * The conctructor
     * @param articleManager The article manager
     * @param articleDataset A list of articles
     */
    public ArticleAdapter(ArticleManager articleManager, List<Article> articleDataset) {
        this.articleManager = articleManager;
        this.articleDataset = articleDataset;
    }

    // ==================
    // === VIEWHOLDER ===
    // ==================

    /**
     * The article ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout contentParent;
        public TextView tvName;
        public TextView tvAmount;
        public ViewHolder(LinearLayout v) {
            super(v);
            contentParent = v;
            tvName = (TextView) v.findViewById(R.id.article_name);
            tvAmount = (TextView) v.findViewById(R.id.article_amount);
        }
    }

    // =========================
    // === ADAPTER FUNCTIONS ===
    // =========================

    /**
     * Creates the new views (invoked by the layout manager)
     * @param parent
     * @param viewType
     * @return The new viewHolder
     */
    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_template, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // getAdapterPosition is used instead of position, since it gives back the right position!
        // Get the targeted article
        final Article article = articleDataset.get(holder.getAdapterPosition());
        // Set the fields of the article in the viewHolder
        holder.tvName.setText(article.getName());
        holder.tvAmount.setText(article.getAmount());

        final List<TextView> textViews = new ArrayList<>();
        textViews.add(holder.tvName);
        textViews.add(holder.tvAmount);

        if(article.isStrikethrough()){
            for (TextView tv : textViews) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
        // Set the long click listener with the 3 options: change name, change amount, remove
        holder.contentParent.setOnLongClickListener(new View.OnLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onLongClick(View view) {
                return articleManager.startActionMode(view, holder, textViews);
            }
        });
        holder.contentParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articleManager.toggleSelection(holder);
                //articleManager.startMultipleSelectMode(holder);
            }
        });
    }

    /**
     * Returns the size of the dataset (invoked by the layout manager)
     * @return The size of the dataset
     */
    @Override
    public int getItemCount() {
        return articleDataset.size();
    }



}