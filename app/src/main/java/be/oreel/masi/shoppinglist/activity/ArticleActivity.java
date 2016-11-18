package be.oreel.masi.shoppinglist.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.oreel.masi.shoppinglist.adapter.ArticleManager;
import be.oreel.masi.shoppinglist.adapter.ArticleAdapter;
import be.oreel.masi.shoppinglist.model.Article;
import be.oreel.masi.shoppinglist.model.ToolbarMode;
import be.oreel.masi.shoppinglist.db.ArticleDataSource;
import be.oreel.masi.shoppinglist.R;

/**
 * Activity displaying and handling the articles of a shop
 */
public class ArticleActivity extends RecyclerActivity implements ArticleManager {

    // =================
    // === VARIABLES ===
    // =================

    private String shopName;
    private ArticleDataSource datasource;
    private ArticleAdapter adapter;
    private List<Article> articles;
    private ToolbarMode toolbarMode;
    private ClipboardManager clipboard;

    private List<ArticleAdapter.ViewHolder> selectedItems;

    // ================
    // === ONCREATE ===
    // ================

    /**
     * Create the ArticleActivity
     * @param savedInstanceState The saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the selected shop name from the MainActivity
        Bundle b = getIntent().getExtras();
        shopName = b != null ? b.getString(getString(R.string.bundle_shopname_id)) : null;
        boolean shopNameRetrieved = shopName != null;
        // Set the shop name as the title of this activity if it is not null
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(
                    shopNameRetrieved ?
                            shopName :
                            getString(R.string.article_activity_title));
        }

        // Set variables
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        selectedItems = new ArrayList<>();
        toolbarMode = ToolbarMode.NORMAL;

        // Setup the recyclerView and the the articles from the db
        if(shopNameRetrieved){
            // Get all articles from the shop
            articles = getAllArticles(shopName);
            // Create the adapter for the recyclerView
            adapter = new ArticleAdapter(this, articles);
            getRecyclerView().setAdapter(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(getItemTouchCallback());
            itemTouchHelper.attachToRecyclerView(getRecyclerView());
        }
        else{
            //TODO set default shop name MyShop?
        }

    }

    //TODO document
    private ItemTouchHelper.Callback getItemTouchCallback(){
        return new ItemTouchHelper.Callback() {

            private boolean moved = false;

            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState){
                if(toolbarMode != ToolbarMode.NORMAL && moved && actionState == ItemTouchHelper.ACTION_STATE_IDLE){
                    moved = false;
                    showNormalToolbar();
                    //mActionMode.finish();
                }
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y){
                moved = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getLayoutPosition();
                int toPosition = target.getLayoutPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(articles, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(articles, i, i - 1);
                    }
                }
                adapter.notifyItemMoved(fromPosition, toPosition);

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                removeArticle(viewHolder.getAdapterPosition());
                if(toolbarMode != ToolbarMode.NORMAL){
                    showNormalToolbar();
                }
            }
        };
    }

    // =================================================
    // === ABSTRACT PARENT FUNCTIONS IMPLEMENTATIONS ===
    // =================================================

    /**
     * Sets the toolbar
     */
    @Override
    protected void setupToolbar() {
        setSupportActionBar(getToolbar());
        if (getSupportActionBar() != null) {
            //  Set the back arrow on top left of the toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Sets the FAB
     */
    @Override
    protected void setupFab() {
        FloatingActionButton fab = getFab();
        if (fab != null) {
            fab.bringToFront();
            fab.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    openAddArticleDialog();
                }
            });
            fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(),
                    R.drawable.ic_add_white_24px));
        }
    }

    // ========================
    // === DIALOG FUNCTIONS ===
    // ========================

    /**
     * Creates and shows a dialog to add an article to the list
     */
    private void openAddArticleDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the elements from the dialog content
        LinearLayout contentParent = (LinearLayout) getLayoutInflater().
                inflate(R.layout.dialog_create_article, null);
        final EditText inputName = (EditText) contentParent.getChildAt(0);
        final EditText inputAmount = (EditText) contentParent.getChildAt(1);

        builder.setView(contentParent).
                setTitle(R.string.dialog_title_add_article).
                setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addArticle(inputName.getText().toString(),
                                inputAmount.getText().toString());
                    }
                }).
                setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                }).
                create().
                show();
    }

    /**
     * Creates and show a dialog to edit an article in the list
     * @param position The position of the article in the list
     */
    private void openEditArticleDialog(final int position) {
        Article article = articles.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the elements from the dialog content
        LinearLayout contentParent = (LinearLayout) getLayoutInflater().
                inflate(R.layout.dialog_create_article, null);
        final EditText inputName = (EditText) contentParent.getChildAt(0);
        final EditText inputAmount = (EditText) contentParent.getChildAt(1);
        // Set the existing information in the dialog content
        inputName.setHint(R.string.placeholder_article_name);
        inputName.setText(article.getName());
        inputAmount.setHint(R.string.placeholder_article_amount);
        inputAmount.setText(article.getAmount());

        builder.setView(contentParent).
                setTitle(R.string.dialog_title_edit_article).
                setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateArticle(position, inputName.getText().toString(),
                                inputAmount.getText().toString());
                    }
                }).
                setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                }).
                create().
                show();
    }

    /**
     * Creates and shows a dialog to change the selected article's name
     * @param position The position of the article in the list
     */
    public void openEditArticleNameDialog(final int position) {
        // Create the dialog to change the name
        Article article = articles.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the dialog content
        LinearLayout contentParent = (LinearLayout) getLayoutInflater().
                inflate(R.layout.dialog_update_article, null);
        final EditText input = (EditText) contentParent.getChildAt(0);
        // Set the current name of the article in the textbox
        input.setHint(R.string.placeholder_article_name);
        input.setText(article.getName());

        builder.setView(contentParent).
                setTitle(R.string.dialog_title_edit_article_name).
                setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateArticleName(position, input.getText().toString());
                    }
                }).
                setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                }).
                create().
                show();
    }

    /**
     * Creates and shows a dialog to update the article's amount
     * @param position The postion of the article in the list
     */
    public void openEditArticleAmountDialog(final int position) {
        // Create the dialog to change the amount
        Article article = articles.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the dialog content
        LinearLayout contentParent = (LinearLayout) getLayoutInflater().
                inflate(R.layout.dialog_update_article, null);
        final EditText input = (EditText) contentParent.getChildAt(0);
        // Set the current amount of the article in the textbox
        input.setHint(R.string.placeholder_article_amount);
        input.setText(article.getAmount());

        builder.setView(contentParent).
                setTitle(R.string.dialog_title_edit_article_amount).
                setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateArticleAmount(position, input.getText().toString());
                    }
                }).
                setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                }).
                create().
                show();
    }

    // =====================
    // === ADD FUNCTIONS ===
    // =====================

    /**
     * Adds a new article to the list
     * @param name The name of the new article
     * @param amount The amount of the new article
     */
    private void addArticle(String name, String amount){
        Article newArticle = datasource.createArticle(shopName, name, amount,
                false, articles.size());
        articles.add(newArticle);
        adapter.notifyItemInserted(articles.size() - 1);
    }

    /**
     * Adds a deleted article to the list
     * @param article The deleted article
     */
    public void addArticle(Article article){
        Article newArticle = datasource.createArticle(shopName, article.getName(),
                article.getAmount(), article.isStrikethrough(), article.getPriority());
        articles.add(newArticle);
        adapter.notifyItemInserted(articles.size()-1);
    }

    // ========================
    // === UPDATE FUNCTIONS ===
    // ========================

    //TODO optimize

    /**
     * Updates the name and amount of an article at a certain position in the list
     * @param position The position of the article in the list
     * @param newName The new name of the article
     * @param newAmount The new amount of the article
     */
    private void updateArticle(int position, String newName, String newAmount) {
        Article article = articles.get(position);
        article.setName(newName);
        article.setAmount(newAmount);
        adapter.notifyItemChanged(position);
        datasource.updateArticle(article);
    }

    /**
     * Updates the name of an article
     * @param position The position of the article in the list
     * @param newName The new name of the article
     */
    private void updateArticleName(int position, String newName) {
        Article article = articles.get(position);
        article.setName(newName);
        adapter.notifyItemChanged(position);
        datasource.updateArticle(article);
    }

    /**
     * Updates the amount of an article
     * @param position The position of the article in the list
     * @param newAmount The new amount of the article
     */
    private void updateArticleAmount(int position, String newAmount) {
        Article article = articles.get(position);
        article.setAmount(newAmount);
        adapter.notifyItemChanged(position);
        datasource.updateArticle(article);
    }

    /**
     * Update an article in the db
     * @param article The article to update
     */
    private void updateArticle(Article article) {
        // TODO optimize?
        datasource.updateArticle(article);
    }

    // ========================
    // === DB GET FUNCTIONS ===
    // ========================

    /**
     * Returns all the articles of a shop
     * @param shopname The name of the shop
     * @return All the articles of the shop
     */
    public List<Article> getAllArticles(String shopname) {
        datasource = new ArticleDataSource(this);
        // Open the database
        datasource.open();
        // Get all the articles from the chosen shop
        return datasource.getAllArticles(shopname);
    }

    // ========================
    // === REMOVE FUNCTIONS ===
    // ========================

    /**
     * Removes an article from the list
     * @param position The position of the article in the list
     */
    public void removeArticle(final int position) {
        final Article article = articles.get(position);
        // Removes the article from the list
        articles.remove(position);
        adapter.notifyItemRemoved(position);
        // Removes the article from the db
        datasource.deleteArticle(article);
        // Show snackbar
        Snackbar snackbar = Snackbar.
                make(getCoordinatorLayout(), String.format(getString(R.string.snackbar_remove_article),
                        article.getName()), Snackbar.LENGTH_LONG).
                setAction(getString(R.string.snackbar_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        undoRemove(article, position);
                    }
                });
        snackbar.show();
    }

    /**
     * Removes all the articles of the shop
     */
    public void removeArticles(){
        final List<Article> articlesBackup;
        String snackbarText;
        //TODO
        //adapter.isEmpty()
        //adapter.getSelectedItemsSize()
        if(selectedItems.size() > 0){
            snackbarText = getString(R.string.snackbar_remove_selection);
            articlesBackup = new ArrayList<>();
            //TODO
            //adapter.getSelectedArticlePositions()
            for(ArticleAdapter.ViewHolder holder : selectedItems){
                int position = holder.getAdapterPosition();
                Article article = articles.get(position);
                articlesBackup.add(article);
                articles.remove(position);
                adapter.notifyItemRemoved(position);
                datasource.deleteArticle(article);
            }
        }
        else{
            snackbarText = String.format(getString(R.string.snackbar_remove_all), shopName);
            articlesBackup = new ArrayList<>(articles);
            articles.clear();
            adapter.notifyItemRangeRemoved(0, articlesBackup.size());
            datasource.deleteAllArticles(shopName);
        }
        // Show snackbar
        Snackbar snackbar = Snackbar.
                make(getCoordinatorLayout(), snackbarText, Snackbar.LENGTH_LONG).
                setAction(getString(R.string.snackbar_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        undoRemove(articlesBackup);
                    }
                });
        snackbar.show();
    }

    // ======================
    // === UNDO FUNCTIONS ===
    // ======================

    /**
     * Undo the removal of an article
     * @param article The article to bring back
     * @param position The position in which the article was before removal
     */
    private void undoRemove(Article article, int position){
        // Add removed article to the DB
        Article newArticle = datasource.createArticle(article.getShop(),
                article.getName(), article.getAmount(),
                article.isStrikethrough(), article.getPriority());
        // Add the article to the list at the right position
        articles.add(position, newArticle);
        adapter.notifyItemInserted(position);
    }

    /**
     * Undo the removal of multiple articles
     * @param removedArticles The removed articles to add back to the list
     */
    private void undoRemove(List<Article> removedArticles){
        for (Article article : removedArticles){
            addArticle(article);
        }
    }
    
    // =======================
    // === OTHER FUNCTIONS ===
    // =======================

    /**
     * Save all list item positions
     */
    private void savePositions(){
        // Save all positions
        // TODO THREAD THIS
        for (int i = 0; i < articles.size(); i++){
            Article article = articles.get(i);
            article.setPriority(i);
            System.out.print("Updated article " + article.getName() +
                    " to position " + article.getPriority());
            updateArticle(article);
        }
    }
    
    /**
     * Toggle strikethrough on all selected elements
     */
    private void toggleStrikethrough(){
        for (ArticleAdapter.ViewHolder holder : selectedItems){
            Article article = articles.get(holder.getAdapterPosition());
            boolean isStrikeThrough = article.isStrikethrough();
            if(isStrikeThrough){
                holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                holder.tvAmount.setPaintFlags(holder.tvAmount.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }
            else{
                holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvAmount.setPaintFlags(holder.tvAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            //TODO
            // adapter.toggleStrikethrough()

            article.setStrikethrough(!isStrikeThrough);
            updateArticle(article);
        }
    }

    private void copyArticlesToClipboard(){
        String articlesCopy = "";
        
        //TODO 
        //adapter.getSelectedItemsSize()
        if(selectedItems.size() > 0){
            //TODO
            //adapter.getSelectedItemsToString()
            for (ArticleAdapter.ViewHolder holder : selectedItems){
                articlesCopy += articles.get(holder.getAdapterPosition()).toString()  + "\n";
            }
        }
        else{
            for(Article article : articles){
                articlesCopy += article.toString() + "\n";
            }
        }

        //http://stackoverflow.com/questions/6624763/android-copy-to-clipboard-selected-text-from-a-textview
        //  Add details to clipboard
        ClipData clip = ClipData.newPlainText("Copied Details", articlesCopy);
        clipboard.setPrimaryClip(clip);

        //  Add snackbar notification
        Snackbar snackbar = Snackbar.make(
                getCoordinatorLayout(),
                getString(R.string.snackbar_copy_to_clipboard),
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Sort articles by name
     */
    private void sortArticlesByName(){
        Collections.sort(articles, new Comparator<Article>(){
            public int compare(Article articleA, Article articleB) {
                return articleA.getName().compareTo(articleB.getName());
            }
        });
        adapter.notifyItemRangeChanged(0, articles.size());
    }

    // ===========================
    // === SELECTION FUNCTIONS ===
    // ===========================

    /**
     * Start action mode
     * @param view
     * @param viewHolder
     * @param textViews
     * @return
     */
    @Override
    public boolean startActionMode(final View view, final ArticleAdapter.ViewHolder viewHolder,
                                   final List<TextView> textViews) {

        if(toolbarMode == ToolbarMode.NORMAL){
            toolbarMode = ToolbarMode.DETAIL;
            toggleSelection(viewHolder);
            //TODO
            //adapter.toggleSelection()? or getToolbarMode()
        }

        return true;
    }
    
    /**
     * Selects the item if it isn't, deselect the item if it already is selected
     * @param holder
     */
    @Override
    public void toggleSelection(ArticleAdapter.ViewHolder holder){
        //if(mActionMode != null){
        if(toolbarMode != ToolbarMode.NORMAL){
            //TODO
            //adapter.toggleSelection()
            boolean isSelected = holder.contentParent.isSelected();
            if(isSelected){
                selectedItems.remove(holder);
            }
            else{
                selectedItems.add(holder);
            }
            holder.contentParent.setSelected(!isSelected);
            //TODO
            //adapter.getSelectedItemsSize()
            if(toolbarMode != ToolbarMode.MULTIPLE && selectedItems.size() > 1){
                showMultipleModeToolbar();
            }
            else if(selectedItems.size() == 0){
                showNormalToolbar();
                //mActionMode.finish();
            }
            else if(selectedItems.size() == 1){
                showActionModeToolbar();
            }
        }
    }

    /**
     * Clears list item selection
     */
    private void clearSelections(){
        for(ArticleAdapter.ViewHolder holder : selectedItems){
            holder.contentParent.setSelected(false);
        }

        selectedItems.clear();

        //TODO
        //adapter.clearSelection();
    }

    // =========================================
    // === OVERRIDE BASIC ACTIVITY FUNCTIONS ===
    // =========================================

    /**
     * Sets the activity back in normal menu mode if it wasn't
     * If the activity is already in normal menu mode, default parent behavior is applied
     */
    @Override
    public void onBackPressed(){
        // If in one of the selection modes, go back to normal mode
        // Else default onBackPressed behavior
        if(toolbarMode != ToolbarMode.NORMAL){
            showNormalToolbar();
        }
        else{
            super.onBackPressed();
            // Set a left to right transition effect when going back to the main activity
            overridePendingTransition(R.anim.left_to_right_article, R.anim.right_to_left_article);
        }
    }

    /**
     * Creates the menu in the action bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article_menu, menu);
        return true;
    }

    /**
     * Defines all the actions to be performed for each element in the toolbar menu
     * @param item The menu item being pressed
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //TODO
        //adapter.getSelectedElement()

        // Check which menu item was selected
        switch (id){
            case android.R.id.home:
                // Leave selection mode or go back to the main activity
                onBackPressed();
                break;
            case R.id.action_remove_all:
                // Remove all articles of the shop
                removeArticles();
                break;
            case R.id.action_sort_alpha:
                // Sort all articles by name
                sortArticlesByName();
                break;
            case R.id.action_copy_to_clipboard:
                // Copy all articles to the clipboard
                copyArticlesToClipboard();
                break;
            case R.id.action_strikethrough:
                // Strikethrough selected elements
                toggleStrikethrough();
                break;
            case R.id.action_remove_article:
                // Remove the selected article
                removeArticle(selectedItems.get(0).getAdapterPosition());
                return true;
            case R.id.action_edit_article:
                // Open the edit article dialog for the selected element
                openEditArticleDialog(selectedItems.get(0).getAdapterPosition());
                break;
            case R.id.action_edit_article_name:
                // Open the edit article name dialog for the selected element
                openEditArticleNameDialog(selectedItems.get(0).getAdapterPosition());
                break;
            case R.id.action_edit_article_amount:
                // Open the edit article amount dialog for the selected element
                openEditArticleAmountDialog(selectedItems.get(0).getAdapterPosition());
                break;
            case R.id.action_exit_app:
                // Leave the app
                leaveApp();
                break;
        }

        // Go back to normal menu mode if it isn't already the case
        // after performing the selected action
        if(toolbarMode != ToolbarMode.NORMAL){
            showNormalToolbar();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Resume of the activity
     */
    @Override
    protected void onResume() {
        datasource.open();
        showNormalToolbar();
        super.onResume();
    }

    /**
     * Pause of the activity
     */
    @Override
    protected void onPause() {
        savePositions();
        datasource.close();
        super.onPause();
    }

    // =====================
    // === TOOLBAR MODES ===
    // =====================

    /**
     * Change the toolbar menu to the default list menu
     * Displays menu items available for the whole list
     */
    private void showNormalToolbar(){
        toolbarMode = ToolbarMode.NORMAL;
        getToolbar().getMenu().clear();
        getToolbar().inflateMenu(R.menu.article_menu);
        clearSelections();
    }

    /**
     * Change the toolbar menu to the action mode menu
     * Displays menu items available for one selected element in the list
     */
    private void showActionModeToolbar(){
        toolbarMode = ToolbarMode.DETAIL;
        getToolbar().getMenu().clear();
        getToolbar().inflateMenu(R.menu.article_detail_menu);
    }

    /**
     * Change the toolbar menu to the multiple mode menu
     * Displays menu items available for multiple selected elements in the list
     */
    private void showMultipleModeToolbar(){
        toolbarMode = ToolbarMode.MULTIPLE;
        getToolbar().getMenu().clear();
        getToolbar().inflateMenu(R.menu.article_multiple_menu);
    }
}
