package be.oreel.masi.shoppinglist.model;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * The logo model
 */
public class Shop implements Comparable<Shop>, Serializable {

    // =================
    // === VARIABLES ===
    // =================

    private long id;
    private String name;
    private @DrawableRes int logoRes;

    // ===================
    // === CONSTRUCTOR ===
    // ===================

    /**
     * Empty constructor for database
     */
    public Shop(){}

    /**
     * The constructor
     * @param name The shop name
     * @param logoRes The drawable resource of the icon
     */
    public Shop(String name, @DrawableRes int logoRes){
        setName(name);
        setLogoRes(logoRes);
    }

    // =======================
    // === IMPLEMENTATIONS ===
    // =======================

    /**
     * Creates a string out of an shop (name)
     * @return A string representation of a shop
     */
    @Override
    public String toString(){
        return getName();
    }

    /**
     * Compares two shops by their name
     * @param shop The shop to be compared with
     * @return Comparison value of both names
     */
    @Override
    public int compareTo(Shop shop) {
        return this.getName().compareTo(shop.getName());
    }

    // =========================
    // === GETTERS & SETTERS ===
    // =========================

    /**
     * Returns the shop id
     * @return The shop id
     */
    public long getId(){
        return this.id;
    }

    /**
     * Sets the new shop id
     * @param id The new shop id
     */
    public void setId(long id){
        this.id = id;
    }

    /**
     * Returns the shop name
     * @return The shop name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the shop name
     * @param name The shop name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the drawable resource of the icon
     * @return The drawable resource of the icon
     */
    public int getLogoRes() {
        return logoRes;
    }

    /**
     * Sets the drawable resource of the icon
     * @param logoRes The drawable resource to be set
     */
    public void setLogoRes(int logoRes) {
        this.logoRes = logoRes;
    }
}
