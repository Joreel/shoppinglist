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
    private DBHelper dbHelper;
    private String[] allColumns = {
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_SHOP_ID,
            DBHelper.COLUMN_ARTICLE_NAME,
            DBHelper.COLUMN_AMOUNT,
            DBHelper.COLUMN_MEASURE,
            DBHelper.COLUMN_STRIKETHROUGH,
            DBHelper.COLUMN_PRIORITY};

    // ===================
    // === CONSTRUCTOR ===
    // ===================

    /**
     * The constructor
     * @param context The context
     */
    public ArticleDataSource(Context context) {
        dbHelper = new DBHelper(context);
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
     * @param shopId The id of the shop in which the article is added
     * @param name The name of the article
     * @param amount The amount of the article
     * @param strikethrough Whether or not the element is strikethrough
     * @param priority The priority of the article in the list
     * @return The added article
     */
    public Article createArticle(long shopId, String name, int amount, String measure,
                                 boolean strikethrough, int priority) {
        // Add all the values of the article
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_SHOP_ID, shopId);
        values.put(DBHelper.COLUMN_ARTICLE_NAME, name);
        values.put(DBHelper.COLUMN_AMOUNT, amount > 0 ? amount : 1);
        values.put(DBHelper.COLUMN_MEASURE, measure);
        values.put(DBHelper.COLUMN_STRIKETHROUGH, strikethrough ? 1 : 0);
        values.put(DBHelper.COLUMN_PRIORITY, priority);
        // Insert the article in the database
        long insertId = database.insert(DBHelper.TABLE_ARTICLE, null,
                values);
        // Take the data of the new article with its newly created id
        Cursor cursor = database.query(DBHelper.TABLE_ARTICLE,
                allColumns, DBHelper.COLUMN_ID + " = " + insertId, null,
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
        values.put(DBHelper.COLUMN_ARTICLE_NAME, article.getName());
        values.put(DBHelper.COLUMN_AMOUNT, article.getAmount() > 0 ? article.getAmount() : 1);
        values.put(DBHelper.COLUMN_MEASURE, article.getMeasure());
        values.put(DBHelper.COLUMN_STRIKETHROUGH, article.isStrikethrough() ? 1 : 0);
        values.put(DBHelper.COLUMN_PRIORITY, article.getPriority());
        // Update the article
        database.update(DBHelper.TABLE_ARTICLE, values,
                DBHelper.COLUMN_ID + " = " + article.getId(), null);
    }

    /**
     * Deletes an article
     * @param article The article to delete
     */
    public void deleteArticle(Article article) {
        long id = article.getId();
        // Remove the article from the database
        database.delete(DBHelper.TABLE_ARTICLE,
                DBHelper.COLUMN_ID
                + " = " + id, null);
    }

    /**
     * Deletes all articles of a shop
     * @param shopId The id of the shop to clear
     */
    public void deleteAllArticles(long shopId){
        // Delete all articles of a shop from the database
        database.delete(DBHelper.TABLE_ARTICLE,
                DBHelper.COLUMN_SHOP_ID + " = '" + shopId + "'", null);
    }

    /**
     * Returns all the articles of a shop
     * @param shopId The id of the shop
     * @return all the articles of the specified shop
     */
    public List<Article> getAllArticles(long shopId) {
        List<Article> articles = new ArrayList<>();
        // Get all articles of a shop from the database
        Cursor cursor = database.query(DBHelper.TABLE_ARTICLE,
                allColumns, DBHelper.COLUMN_SHOP_ID + " = '" + shopId + "'",
                null, null, null, null);
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
        article.setShopId(cursor.getLong(1));
        article.setName(cursor.getString(2));
        article.setAmount(cursor.getInt(3));
        article.setMeasure(cursor.getString(4));
        article.setStrikethrough(cursor.getInt(5) == 1);
        article.setPriority(cursor.getInt(6));
        return article;
    }

}
