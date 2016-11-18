package be.oreel.masi.shoppinglist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import be.oreel.masi.shoppinglist.R;
import be.oreel.masi.shoppinglist.model.Logo;

/**
 * The adapter for the recyclerView of the MainActivity
 */
public class LogoAdapter extends RecyclerView.Adapter<LogoAdapter.ViewHolder> {

    // =================
    // === VARIABLES ===
    // =================

    private LogoManager logoManager;
    private Logo[] logos;

    // ===================
    // === CONSTRUCTOR ===
    // ===================

    /**
     * The conctructor
     * @param logoManager The logo manager
     * @param logos An array of logos
     */
    public LogoAdapter(LogoManager logoManager, Logo[] logos) {
        this.logoManager = logoManager;
        this.logos = logos;
    }

    // ==================
    // === VIEWHOLDER ===
    // ==================

    /**
     * The logo ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivLogo;
        public ViewHolder(LinearLayout v) {
            super(v);
            ivLogo = (ImageView) v.findViewById(R.id.logo);
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
    public LogoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.logo_template, parent, false);
        return new LogoAdapter.ViewHolder(v);
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(LogoAdapter.ViewHolder holder, int position) {
        // Get the targeted logo
        final Logo logo = logos[position];
        // Set the fields of the logo to the viewHolder
        holder.ivLogo.setImageResource(logo.getDrawableRes());
        // On click open the corresponding ArticleActivity
        holder.ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoManager.openArticleActivity(logo.getShopname());
            }
        });
    }

    /**
     * Returns the size of the dataset (invoked by the layout manager)
     * @return The size of the dataset
     */
    @Override
    public int getItemCount() {
        return logos.length;
    }
}
