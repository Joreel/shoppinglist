package be.oreel.masi.shoppinglist.model;

import android.support.annotation.NonNull;

/**
 * The article model
 */
public class Article implements Comparable<Article> {

    // =================
    // === VARIABLES ===
    // =================

    private long id;
    private long shopId;
    private String name;
    private int amount;
    private String measure;
    private boolean strikethrough;
    private int priority;

    // =======================
    // === IMPLEMENTATIONS ===
    // =======================

    /**
     * Creates a string out of an article (amount + name)
     * @return A string representation of an article
     */
    @Override
    public String toString(){
        return getAmount() + (getMeasure() != null ? getMeasure() : "") + " " + getName();
    }

    /**
     * Determines the order of articles in a list according to their priority
     * @param article The article to be compared with
     * @return Positive if priority is greater, negative if priority is lower, 0 if priority is equal
     */
    @Override
    public int compareTo(@NonNull Article article) {
        return this.getPriority() > article.getPriority() ? +1 :
                this.getPriority() < article.getPriority() ? -1 : 0;
    }

    // =========================
    // === GETTERS & SETTERS ===
    // =========================

    /**
     * Returns the id of the article
     * @return The id of the article
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id of an article
     * @param id The id to be set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the name of the article's shopId
     * @return The name of the article's shopId
     */
    public long getShopId() {
        return shopId;
    }

    /**
     * Sets the name of the article's shopId
     * @param shopId The shopId name to be set
     */
    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    /**
     * Returns the name of the article
     * @return The name of the article
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the article
     * @param name The name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the amount of the article
     * @return The amount of the article
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the article (minimum 1)
     * @param amount The amount to be set
     */
    public void setAmount(int amount) {
        this.amount = amount > 0 ? amount : 1;
    }

    /**
     * Returns the measure of the article
     * @return The measure of the article
     */
    public String getMeasure(){
        return this.measure;
    }

    /**
     * Sets the measure of the article
     * @param measure The new measure
     */
    public void setMeasure(String measure){
        this.measure = measure;
    }

    /**
     * Returns whether or not the article is strikethrough
     * @return whether or not the article is strikethrough
     */
    public boolean isStrikethrough() {
        return strikethrough;
    }

    /**
     * Sets whether or not the article is strikethrough
     * @param strikethrough Whether or not the article is strikethrough
     */
    public void setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    /**
     * Returns the priority in a list of the article
     * @return The priority in a list of the article
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority in a list of an article
     * @param priority The priority to be set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
}
