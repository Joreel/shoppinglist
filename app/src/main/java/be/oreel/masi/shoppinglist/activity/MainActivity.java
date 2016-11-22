package be.oreel.masi.shoppinglist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;

import be.oreel.masi.shoppinglist.adapter.LogoManager;
import be.oreel.masi.shoppinglist.R;
import be.oreel.masi.shoppinglist.adapter.LogoAdapter;
import be.oreel.masi.shoppinglist.model.Logo;

/**
 * Activity displaying the different shops
 */
public class MainActivity extends RecyclerActivity implements LogoManager {

    // ================
    // === ONCREATE ===
    // ================

    /**
     * Creates the MainActivity
     * @param savedInstanceState The saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the list of logos
        Logo[] logos = {
                new Logo(getString(R.string.carrefour), R.drawable.carrefour_logo),
                new Logo(getString(R.string.colruyt), R.drawable.colruyt_logo),
                new Logo(getString(R.string.spar), R.drawable.spar_logo),
        };

        // Set the logo adapter to the recyclerView
        getRecyclerView().setAdapter(new LogoAdapter(this, logos));
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
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    /**
     * Hides the FAB
     */
    @Override
    protected void setupFab() {
        FloatingActionButton fab = getFab();
        if(fab != null){
            // Hides the fab
            fab.hide();
        }
    }

    // ============================
    // === ACTION BAR FUNCTIONS ===
    // ============================

    /**
     * Sets the actions to perform for each element in the action bar
     * @param item The menu item being pressed
     * @return The parent method return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Leave the app when the action 'leave app' is pressed
        if (id == R.id.action_exit_app) {
            leaveApp();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates the menu in the action bar
     * @param menu The menu
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logo_menu, menu);
        return true;
    }

    // ==========================
    // === ACTIVITY FUNCTIONS ===
    // ==========================

    /**
     * Opens the ArticleActivity of the specified shop
     * @param shopName The shop name
     */
    public void openArticleActivity(String shopName){
        // Create the intent to open the ArticleActivity
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra(getString(R.string.bundle_shopname_id), shopName);
        // Start the ArticleActivity
        startActivity(intent);
        // Set the right to left animation
        overridePendingTransition(R.anim.right_to_left_main, R.anim.left_to_right_main);
    }
}
