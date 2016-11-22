package be.oreel.masi.shoppinglist.adapter;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
     * @param parent The parent viewGroup
     * @param viewType The view type
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
     * @param holder The viewHolder being bind
     * @param position The position of the viewHolder in the list
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // getAdapterPosition is used instead of position, since it gives back the right position!
        // Get the targeted article
        Article article = articleDataset.get(holder.getAdapterPosition());
        // Set the fields of the article in the viewHolder
        holder.tvName.setText(article.getName());
        holder.tvAmount.setText(article.getAmount());
        // Put the textViews in a list
        List<TextView> textViews = new ArrayList<>();
        textViews.add(holder.tvName);
        textViews.add(holder.tvAmount);

        // Strike through if it has to be
        if(article.isStrikethrough()){
            for (TextView tv : textViews) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
        else{
            // Unstrike in case a viewHolder gets recycled
            for (TextView tv : textViews) {
                tv.setPaintFlags(tv.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        // Toggle the selection on a long press in toolbar mode normal
        holder.contentParent.setOnLongClickListener(new View.OnLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onLongClick(View view) {
                if (articleManager.getToolbarMode() == ToolbarMode.NORMAL){
                    toggleSelection(holder);
                }
                return true;
            }
        });

        // Toggle the selection on a short press outside toolbar mode normal
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

    // ================================
    // === SELECTED ITEMS FUNCTIONS ===
    // ================================

    /**
     * Toggle strikethrough on all selected elements
     */
    public void toggleStrikethrough(){
        // Strike through all selected items
        for (ViewHolder holder : selectedItems){
            // Get the selected article
            Article article = articleDataset.get(holder.getAdapterPosition());
            boolean isStrikeThrough = article.isStrikethrough();
            // If the article has to be strikethrough, do it
            if(isStrikeThrough){
                holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                holder.tvAmount.setPaintFlags(holder.tvAmount.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            } // If not, remove the strikethrough
            else{
                holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvAmount.setPaintFlags(holder.tvAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            // Update the strikethrough state of the article
            article.setStrikethrough(!isStrikeThrough);
            // Update the strikethrough state of the article in the datebase
            articleManager.updateArticle(article);
        }
    }

    /**
     * Returns the first selected item
     * @return The first selected item
     */
    public int getSelectedItemPosition(){
        // Returns the first selected item
        return selectedItems.get(0).getAdapterPosition();
    }

    /**
     * Clears list item selection
     */
    public void clearSelections(){
        // Unselect all viewHolders
        for(ViewHolder holder : selectedItems){
            holder.contentParent.setSelected(false);
        }
        // Clear the list of selected items
        selectedItems.clear();
    }

    /**
     * Selects the item if it isn't, deselect the item if it already is selected
     * @param holder The viewHolder
     */
    private void toggleSelection(ViewHolder holder){
        // Toggle selection
        boolean isSelected = holder.contentParent.isSelected();
        // If the holder was already selected, unselect it
        if(isSelected){
            // Remove the holder from the list of selected items
            selectedItems.remove(holder);
        } // If not, select it
        else{
            // Add the holder to the list of selected items
            selectedItems.add(holder);
        }
        // Change the selection state
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
        // If there are articles in the list
        if(!articleDataset.isEmpty()){
            // Sort the articles by name
            Collections.sort(articleDataset, new Comparator<Article>(){
                public int compare(Article articleA, Article articleB) {
                    return articleA.getName().compareTo(articleB.getName());
                }
            });
            // Notify the recyclerView that the list items have changed
            notifyItemRangeChanged(0, articleDataset.size());
        }
    }

    /**
     * Get the articles (all or selection) in string form
     * @return The list of articles in a string
     */
    public String getArticlesToString() {
        String articlesCopy = "";

        // If some articles are selected, create the string of only that selection
        if (selectedItems.size() > 0) {
            // For all selected items, add the article string
            for (ArticleAdapter.ViewHolder holder : selectedItems) {
                articlesCopy += articleDataset.get(holder.getAdapterPosition()).toString() + "\n";
            }
        } // If not, create the string based of all articles in the list
        else {
            // For all articles, add the article string
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
        // Save all positions to the db
        for (int i = 0; i < articleDataset.size(); i++){
            Article article = articleDataset.get(i);
            article.setPriority(i);
            articleManager.updateArticle(article);
        }
    }

    /**
     * Returns whether or not there are items selected
     * @return whether or not there are items selected
     */
    public boolean hasSelectedItems(){
        return selectedItems.size() > 0;
    }

    /**
     * Returns the list of selected positions
     * @return the list of selected positions
     */
    public List<Integer> getSelectedPositions(){
        List<Integer> positions = new ArrayList<>();
        for(ViewHolder holder : selectedItems){
            positions.add(holder.getAdapterPosition());
        }
        return positions;
    }

}