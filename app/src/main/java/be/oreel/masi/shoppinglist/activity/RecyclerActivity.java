package be.oreel.masi.shoppinglist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import be.oreel.masi.shoppinglist.R;

/**
 * Activity serving as common base class for the MainActivity & ArticleActivity
 */
public abstract class RecyclerActivity extends AppCompatActivity {

    // =================
    // === VARIABLES ===
    // =================

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    // ================
    // === ONCREATE ===
    // ================

    /**
     * Creates the common elements of the MainActivity & ArticleActivity
     * @param savedInstanceState The saved instance state
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_activity);

        // Get elements from the view
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // Set the layout manager for the recyclerview
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Call the two abstract methods
        setupToolbar();
        setupFab();
    }

    // =======================
    // === SETUP FUNCTIONS ===
    // =======================

    /**
     * Abstract function meant to be used to setup the toolbar
     */
    protected abstract void setupToolbar();

    /**
     * Abstract function meant to setup the FAB
     */
    protected abstract void setupFab();

    // ======================================
    // === FUNCTIONS AGAINST ANDROID WAYS ===
    // ======================================

    /**
     * Leaves the app and goes to the home screen, the app is still running in the background
     */
    protected void leaveApp(){
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    // ===============
    // === GETTERS ===
    // ===============

    /**
     * Returns the coordinatorLayout
     * @return The coordinatorLayout
     */
    public CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }

    /**
     * Returns the toolbar
     * @return The toolbar
     */
    protected Toolbar getToolbar(){
        return toolbar;
    }

    /**
     * Returns the recycler view
     * @return The recycler view
     */
    protected RecyclerView getRecyclerView(){
        return recyclerView;
    }

    /**
     * Returns the FAB
     * @return The FAB
     */
    protected FloatingActionButton getFab(){
        return fab;
    }

}
