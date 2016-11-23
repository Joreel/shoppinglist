package be.oreel.masi.shoppinglist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import be.oreel.masi.shoppinglist.R;
import be.oreel.masi.shoppinglist.model.Shop;

/**
 * The adapter for the recyclerView of the MainActivity
 */
public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    // =================
    // === VARIABLES ===
    // =================

    private ShopManager shopManager;
    private Shop[] shops;

    // ===================
    // === CONSTRUCTOR ===
    // ===================

    /**
     * The conctructor
     * @param shopManager The logo manager
     * @param shops An array of shops
     */
    public ShopAdapter(ShopManager shopManager, Shop[] shops) {
        this.shopManager = shopManager;
        this.shops = shops;
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
            ivLogo = (ImageView) v.findViewById(R.id.shop);
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
    public ShopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.logo_template, parent, false);
        return new ShopAdapter.ViewHolder(v);
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     * @param holder The viewHolder
     * @param position The position of the viewHolder in the list
     */
    @Override
    public void onBindViewHolder(ShopAdapter.ViewHolder holder, int position) {
        // Get the targeted shop
        final Shop shop = shops[position];
        // Set the fields of the shop to the viewHolder
        holder.ivLogo.setImageResource(shop.getLogoRes());
        // On click open the corresponding ArticleActivity
        holder.ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopManager.openArticleActivity(shop.getName());
            }
        });
    }

    /**
     * Returns the size of the dataset (invoked by the layout manager)
     * @return The size of the dataset
     */
    @Override
    public int getItemCount() {
        return shops.length;
    }
}
