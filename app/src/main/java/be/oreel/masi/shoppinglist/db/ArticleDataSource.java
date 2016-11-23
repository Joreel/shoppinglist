package be.oreel.masi.shoppinglist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.oreel.masi.shoppinglist.model.Article;

/**
 * Article DAO
 */
public class ArticleDataSource {

    // =================
    // === VARIABLES ===
    // =================

    private SQLiteDatabase database;
    private ArticleDBHelper dbHelper;
    private String[] allColumns = {
            ArticleDBHelper.COLUMN_ID,
            ArticleDBHelper.COLUMN_SHOP_NAME,
            ArticleDBHelper.COLUMN_ARTICLE_NAME,
            ArticleDBHelper.COLUMN_AMOUNT,
            ArticleDBHelper.COLUMN_MEASURE,
            ArticleDBHelper.COLUMN_STRIKETHROUGH,
            ArticleDBHelper.COLUMN_PRIORITY};

    // ===================
    // === CONSTRUCTOR ===
    // ===================

    /**
     * The constructor
     * @param context The context
     */
    public ArticleDataSource(Context context) {
        dbHelper = new ArticleDBHelper(context);
    }

    // ==========================
    // === DATABASE FUNCTIONS ===
    // ==========================

    /**
     * Opens the database
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the database
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Adds an article to the database
     * @param shop The name of the shop in which the article is added
     * @param name The name of the article
     * @param amount The amount of the article
     * @param strikethrough Whether or not the element is strikethrough
     * @param priority The priority of the article in the list
     * @return The added article
     */
    public Article createArticle(String shop, String name, int amount, String measure,
                                 boolean strikethrough, int priority) {
        // Add all the values of the article
        ContentValues values = new ContentValues();
        values.put(ArticleDBHelper.COLUMN_SHOP_NAME, shop);
        values.put(ArticleDBHelper.COLUMN_ARTICLE_NAME, name);
        values.put(ArticleDBHelper.COLUMN_AMOUNT, amount > 0 ? amount : 1);
        values.put(ArticleDBHelper.COLUMN_MEASURE, measure);
        values.put(ArticleDBHelper.COLUMN_STRIKETHROUGH, strikethrough ? 1 : 0);
        values.put(ArticleDBHelper.COLUMN_PRIORITY, priority);
        // Insert the article in the database
        long insertId = database.insert(ArticleDBHelper.TABLE_ARTICLE, null,
                values);
        // Take the data of the new article with its newly created id
        Cursor cursor = database.query(ArticleDBHelper.TABLE_ARTICLE,
                allColumns, ArticleDBHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Article newArticle = cursorToArticle(cursor);
        cursor.close();
        // Return the new article
        return newArticle;
    }

    /**
     * Updates the article
     * @param article The article to update
     */
    public void updateArticle(Article article){
        // Add the values to be update
        ContentValues values = new ContentValues();
        values.put(ArticleDBHelper.COLUMN_ARTICLE_NAME, article.getName());
        values.put(ArticleDBHelper.COLUMN_AMOUNT, article.getAmount() > 0 ? article.getAmount() : 1);
        values.put(ArticleDBHelper.COLUMN_MEASURE, article.getMeasure());
        values.put(ArticleDBHelper.COLUMN_STRIKETHROUGH, article.isStrikethrough() ? 1 : 0);
        values.put(ArticleDBHelper.COLUMN_PRIORITY, article.getPriority());
        // Update the article
        database.update(ArticleDBHelper.TABLE_ARTICLE, values,
                ArticleDBHelper.COLUMN_ID + " = " + article.getId(), null);
    }

    /**
     * Deletes an article
     * @param article The article to delete
     */
    public void deleteArticle(Article article) {
        long id = article.getId();
        // Remove the article from the database
        database.delete(ArticleDBHelper.TABLE_ARTICLE,
                ArticleDBHelper.COLUMN_ID
                + " = " + id, null);
    }

    /**
     * Deletes all articles of a shop
     * @param shop The name of the shop to clear
     */
    public void deleteAllArticles(String shop){
        // Delete all articles of a shop from the database
        database.delete(ArticleDBHelper.TABLE_ARTICLE,
                ArticleDBHelper.COLUMN_SHOP_NAME + " = '" + shop + "'", null);
    }

    /**
     * Returns all the articles of a shop
     * @param shop The name of the shop
     * @return all the articles of the specified shop
     */
    public List<Article> getAllArticles(String shop) {
        List<Article> articles = new ArrayList<>();
        // Get all articles of a shop from the database
        Cursor cursor = database.query(ArticleDBHelper.TABLE_ARTICLE,
                allColumns, ArticleDBHelper.COLUMN_SHOP_NAME + " = '" + shop + "'", null, null, null, null);
        // Convert all data to an article list
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Article article = cursorToArticle(cursor);
            articles.add(article);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();

        // Reset priority of each article to avoid extreme evergrowing priority sizes
        Collections.sort(articles);
        for(int i=0; i<articles.size(); i++){
            articles.get(i).setPriority(i);
        }

        return articles;
    }

    /**
     * Takes an article out of a cursor
     * @param cursor The cursor with the article data
     * @return The article from the cursor
     */
    private Article cursorToArticle(Cursor cursor) {
        // Convert article data from a cursor to an article object
        Article article = new Article();
        article.setId(cursor.getLong(0));
        article.setShop(cursor.getString(1));
        article.setName(cursor.getString(2));
        article.setAmount(cursor.getInt(3));
        article.setMeasure(cursor.getString(4));
        article.setStrikethrough(cursor.getInt(5) == 1);
        article.setPriority(cursor.getInt(6));
        return article;
    }

}
