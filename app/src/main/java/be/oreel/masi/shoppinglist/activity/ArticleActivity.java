package be.oreel.masi.shoppinglist.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
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
        // Set default value if no shop name was found
        Bundle b = getIntent().getExtras();
        shopName = b != null ? b.getString(getString(R.string.bundle_shopname_id)) :
                getString(R.string.article_activity_title);
        // Set the shop name as the title of this activity
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(shopName);
        }

        // Set the clipboard manager
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        // Get all articles from the shop
        articles = getAllArticles(shopName);
        // Create the adapter for the recyclerView
        adapter = new ArticleAdapter(this, articles);
        // Get the recyclerView
        RecyclerView recyclerView = getRecyclerView();
        // Set the adapter
        recyclerView.setAdapter(adapter);
        // Add the itemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(getItemTouchCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getRecyclerView().getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
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
                    setToolbarMode(ToolbarMode.NORMAL);
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
                    setToolbarMode(ToolbarMode.NORMAL);
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
            setToolbarMode(ToolbarMode.NORMAL);
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
                adapter.sortArticlesByName();
                break;
            case R.id.action_copy_to_clipboard:
                // Copy all articles to the clipboard
                copyArticlesToClipboard();
                break;
            case R.id.action_strikethrough:
                // Strikethrough selected elements
                adapter.toggleStrikethrough();
                break;
            case R.id.action_remove_article:
                // Remove the selected article
                removeArticle(adapter.getSelectedItemPosition());
                return true;
            case R.id.action_edit_article:
                // Open the edit article dialog for the selected element
                openEditArticleDialog(adapter.getSelectedItemPosition());
                break;
            case R.id.action_edit_article_name:
                // Open the edit article name dialog for the selected element
                openEditArticleNameDialog(adapter.getSelectedItemPosition());
                break;
            case R.id.action_edit_article_amount:
                // Open the edit article amount dialog for the selected element
                openEditArticleAmountDialog(adapter.getSelectedItemPosition());
                break;
            case R.id.action_exit_app:
                // Leave the app
                leaveApp();
                break;
        }

        // Go back to normal menu mode if it isn't already the case
        // after performing the selected action
        if(toolbarMode != ToolbarMode.NORMAL){
            setToolbarMode(ToolbarMode.NORMAL);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Resume of the activity
     */
    @Override
    protected void onResume() {
        datasource.open();
        setToolbarMode(ToolbarMode.NORMAL);
        super.onResume();
    }

    /**
     * Pause of the activity
     */
    @Override
    protected void onPause() {
        adapter.savePositions();
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
        getToolbar().getMenu().clear();
        getToolbar().inflateMenu(R.menu.article_menu);
        adapter.clearSelections();
    }

    /**
     * Change the toolbar menu to the action mode menu
     * Displays menu items available for one selected element in the list
     */
    private void showActionModeToolbar(){
        getToolbar().getMenu().clear();
        getToolbar().inflateMenu(R.menu.article_detail_menu);
    }

    /**
     * Change the toolbar menu to the multiple mode menu
     * Displays menu items available for multiple selected elements in the list
     */
    private void showMultipleModeToolbar(){
        getToolbar().getMenu().clear();
        getToolbar().inflateMenu(R.menu.article_multiple_menu);
    }

    /**
     * Returns the current toolbarMode
     * @return The current toolbarMode
     */
    @Override
    public ToolbarMode getToolbarMode(){
        return toolbarMode;
    }

    /**
     * Sets the toolbarMode
     * @param mode The new toolbarMode
     */
    @Override
    public void setToolbarMode(ToolbarMode mode){
        this.toolbarMode = mode;
        if(toolbarMode == ToolbarMode.NORMAL){
            System.out.println("NORMAL");
            showNormalToolbar();
        }
        else if(toolbarMode == ToolbarMode.DETAIL){
            System.out.println("DETAIL");
            showActionModeToolbar();
        }
        else if(toolbarMode == ToolbarMode.MULTIPLE){
            System.out.println("MULTIPLE");
            showMultipleModeToolbar();
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
                inflate(R.layout.dialog_article, null);
        final EditText inputName = (EditText) contentParent.getChildAt(0);
        final EditText inputAmount = (EditText) contentParent.getChildAt(1);

        builder.setView(contentParent).
                setTitle(R.string.dialog_title_add_article).
                setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = inputName.getText().toString();
                        if(!name.trim().isEmpty()){
                            addArticle(name,
                                    inputAmount.getText().toString());
                        }
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
                inflate(R.layout.dialog_article, null);
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
                        String name = inputName.getText().toString();
                        if(!name.trim().isEmpty()) {
                            updateArticle(position, name,
                                    inputAmount.getText().toString());
                        }
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
    private void openEditArticleNameDialog(final int position) {
        // Create the dialog to change the name
        Article article = articles.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the dialog content
        LinearLayout contentParent = (LinearLayout) getLayoutInflater().
                inflate(R.layout.dialog_article_name, null);
        final EditText input = (EditText) contentParent.getChildAt(0);
        // Set the current name of the article in the textbox
        input.setHint(R.string.placeholder_article_name);
        input.setText(article.getName());

        builder.setView(contentParent).
                setTitle(R.string.dialog_title_edit_article_name).
                setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = input.getText().toString();
                        if(!name.trim().isEmpty()) {
                            updateArticleName(position, name);
                        }
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
    private void openEditArticleAmountDialog(final int position) {
        // Create the dialog to change the amount
        Article article = articles.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the dialog content
        LinearLayout contentParent = (LinearLayout) getLayoutInflater().
                inflate(R.layout.dialog_article_amount, null);
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
    private void addArticle(Article article){
        Article newArticle = datasource.createArticle(shopName, article.getName(),
                article.getAmount(), article.isStrikethrough(), article.getPriority());
        articles.add(newArticle);
        adapter.notifyItemInserted(articles.size()-1);
    }

    // ========================
    // === UPDATE FUNCTIONS ===
    // ========================

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
    @Override
    public void updateArticle(Article article) {
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
        boolean hasArticles = !articles.isEmpty();

        if(adapter.hasSelectedItems()){
            snackbarText = getString(R.string.snackbar_remove_selection);
            articlesBackup = new ArrayList<>();
            List<Integer> positions = adapter.getSelectedPositions();
            // Make sure the positions are deleted from biggest to smallest
            Collections.sort(positions);
            Collections.reverse(positions);
            for(int position : positions){
                Article article = articles.get(position);
                articlesBackup.add(article);
                articles.remove(position);
                adapter.notifyItemRemoved(position);
                datasource.deleteArticle(article);
            }
        }
        else if(hasArticles) {
            snackbarText = String.format(getString(R.string.snackbar_remove_all), shopName);
            articlesBackup = new ArrayList<>(articles);
            articles.clear();
            adapter.notifyItemRangeRemoved(0, articlesBackup.size());
            datasource.deleteAllArticles(shopName);
        }
        else{
            snackbarText = getString(R.string.snackbar_no_articles_to_remove);
            articlesBackup = null;
        }
        // Show snackbar
        Snackbar snackbar = Snackbar.
                make(getCoordinatorLayout(), snackbarText, Snackbar.LENGTH_LONG);
        if(hasArticles){
            snackbar.setAction(getString(R.string.snackbar_undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    undoRemove(articlesBackup);
                }
            });
        }
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
        Collections.reverse(removedArticles);
        for (Article article : removedArticles){
            addArticle(article);
        }
    }

    // =======================
    // === OTHER FUNCTIONS ===
    // =======================

    private void copyArticlesToClipboard(){
        String snackbarText;
        boolean hasArticles = !articles.isEmpty();

        if(hasArticles){
            snackbarText = getString(R.string.snackbar_copy_to_clipboard);
            //  Add details to clipboard
            ClipData clip = ClipData.newPlainText("Copied Details", adapter.getArticlesToString());
            clipboard.setPrimaryClip(clip);
        }
        else{
            snackbarText = getString(R.string.snackbar_no_articles_to_copy);
        }

        //  Add snackbar notification
        Snackbar snackbar = Snackbar.make(
                getCoordinatorLayout(),
                snackbarText,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
