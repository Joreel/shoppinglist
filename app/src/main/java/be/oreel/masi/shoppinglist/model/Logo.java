package be.oreel.masi.shoppinglist.model;

import android.support.annotation.DrawableRes;

/**
 * The logo model
 */
public class Logo {

    // =================
    // === VARIABLES ===
    // =================

    private @DrawableRes int drawableRes;
    private String shopname;

    // ===================
    // === CONSTRUCTOR ===
    // ===================

    /**
     * The constructor
     * @param shopname The shop name
     * @param drawableRes The drawable resource of the icon
     */
    public Logo(String shopname, @DrawableRes int drawableRes){
        setShopname(shopname);
        setDrawableRes(drawableRes);
    }

    // =========================
    // === GETTERS & SETTERS ===
    // =========================

    /**
     * Returns the shop name
     * @return The shop name
     */
    public String getShopname() {
        return shopname;
    }

    /**
     * Sets the shop name
     * @param shopname The shop name to be set
     */
    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    /**
     * Returns the drawable resource of the icon
     * @return The drawable resource of the icon
     */
    public int getDrawableRes() {
        return drawableRes;
    }

    /**
     * Sets the drawable resource of the icon
     * @param drawableRes The drawable resource to be set
     */
    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }
}
