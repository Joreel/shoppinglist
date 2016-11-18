package be.oreel.masi.shoppinglist.model;

/**
 * The article model
 */
public class Article implements Comparable<Article> {

    // =================
    // === VARIABLES ===
    // =================

    private long id;
    private String shop;
    private String name;
    private String amount;
    private boolean strikethrough;
    private int priority;

    @Override
    public String toString(){
        return getAmount() + " " + getName();
    }

    @Override
    public int compareTo(Article article) {
        int result = this.getPriority() > article.getPriority() ? +1 :
                this.getPriority() < article.getPriority() ? -1 : 0;

        return result;
    }

    public static void swapPriority(Article articleA, Article articleB){
        int priorityA = articleA.getPriority();
        articleA.setPriority(articleB.getPriority());
        articleB.setPriority(priorityA);
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
     * Returns the name of the article's shop
     * @return The name of the article's shop
     */
    public String getShop() {
        return shop;
    }

    /**
     * Sets the name of the article's shop
     * @param shop The shop name to be set
     */
    public void setShop(String shop) {
        this.shop = shop;
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
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the article
     * @param amount The amount to be set
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    //TODO
    public boolean isStrikethrough() {
        return strikethrough;
    }

    public void setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
