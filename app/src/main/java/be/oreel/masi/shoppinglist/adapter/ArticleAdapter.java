package be.oreel.masi.shoppinglist.adapter;

import android.graphics.Paint;
import android.support.annotation.IntegerRes;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.oreel.masi.shoppinglist.R;
import be.oreel.masi.shoppinglist.model.Article;
import be.oreel.masi.shoppinglist.model.ToolbarMode;

/**
 * The adapter for the recyclerView of the ArticleActivity
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    // =================
    // === VARIABLES ===
    // =================

    private ArticleManager articleManager;
    private List<Article> articleDataset;
    private List<ViewHolder> selectedItems;

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
        selectedItems = new ArrayList<>();
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
        holder.contentParent.setOnLongClickListener(new View.OnLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onLongClick(View view) {
                if (articleManager.getToolbarMode() == ToolbarMode.NORMAL){
                    toggleSelection(holder);
                }
                return true;
            }
        });
        holder.contentParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(articleManager.getToolbarMode() != ToolbarMode.NORMAL) {
                    toggleSelection(holder);
                }
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


    // ==========================================================

    /**
     * Toggle strikethrough on all selected elements
     */
    public void toggleStrikethrough(){
        for (ViewHolder holder : selectedItems){
            Article article = articleDataset.get(holder.getAdapterPosition());
            boolean isStrikeThrough = article.isStrikethrough();
            if(isStrikeThrough){
                holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                holder.tvAmount.setPaintFlags(holder.tvAmount.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }
            else{
                holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvAmount.setPaintFlags(holder.tvAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            article.setStrikethrough(!isStrikeThrough);
            articleManager.updateArticle(article);
        }
    }

    public int getSelectedItemPosition(){
        return selectedItems.get(0).getAdapterPosition();
    }

    /**
     * Clears list item selection
     */
    public void clearSelections(){
        for(ViewHolder holder : selectedItems){
            holder.contentParent.setSelected(false);
        }
        selectedItems.clear();
    }

    /**
     * Selects the item if it isn't, deselect the item if it already is selected
     * @param holder
     */
    public void toggleSelection(ViewHolder holder){

        // Toggle selection
        boolean isSelected = holder.contentParent.isSelected();
        if(isSelected){
            selectedItems.remove(holder);
        }
        else{
            selectedItems.add(holder);
        }
        holder.contentParent.setSelected(!isSelected);

        // Change ToolbarMode if necessary
        if(articleManager.getToolbarMode() != ToolbarMode.MULTIPLE && selectedItems.size() > 1){
            articleManager.setToolbarMode(ToolbarMode.MULTIPLE);
        }
        else if(selectedItems.size() == 0){
            articleManager.setToolbarMode(ToolbarMode.NORMAL);
        }
        else if(selectedItems.size() == 1){
            articleManager.setToolbarMode(ToolbarMode.DETAIL);
        }
    }

    /**
     * Sort articles by name
     */
    public void sortArticlesByName(){
        Collections.sort(articleDataset, new Comparator<Article>(){
            public int compare(Article articleA, Article articleB) {
                return articleA.getName().compareTo(articleB.getName());
            }
        });
        notifyItemRangeChanged(0, articleDataset.size());
    }


    public String getArticlesToString() {
        String articlesCopy = "";

        if (selectedItems.size() > 0) {
            for (ArticleAdapter.ViewHolder holder : selectedItems) {
                articlesCopy += articleDataset.get(holder.getAdapterPosition()).toString() + "\n";
            }
        } else {
            for (Article article : articleDataset) {
                articlesCopy += article.toString() + "\n";
            }
        }
        return articlesCopy;
    }

    /**
     * Save all list item positions
     */
    public void savePositions(){
        // Save all positions
        // TODO THREAD THIS
        for (int i = 0; i < articleDataset.size(); i++){
            Article article = articleDataset.get(i);
            article.setPriority(i);
            System.out.print("Updated article " + article.getName() +
                    " to position " + article.getPriority());
            articleManager.updateArticle(article);
        }
    }

    public boolean hasSelectedItems(){
        return selectedItems.size() > 0;
    }

    public List<Integer> getSelectedPositions(){
        List<Integer> positions = new ArrayList<>();
        for(ViewHolder holder : selectedItems){
            positions.add(holder.getAdapterPosition());
        }
        return positions;
    }

}