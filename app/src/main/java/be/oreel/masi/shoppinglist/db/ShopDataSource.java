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
import be.oreel.masi.shoppinglist.model.Shop;

/**
 * Shop DAO
 */
public class ShopDataSource {

    // =================
    // === VARIABLES ===
    // =================

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = {
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_SHOP_NAME};

    // ===================
    // === CONSTRUCTOR ===
    // ===================

    /**
     * The constructor
     * @param context The context
     */
    public ShopDataSource(Context context) {
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
     * Returns the shop object with the given name from the database
     * @param shopName The name of the shop
     * @return The shop with the given name
     */
    public Shop getShop(String shopName) {
        // Get the shop with the given shop name
        Cursor cursor = database.query(DBHelper.TABLE_SHOP,
                allColumns, DBHelper.COLUMN_SHOP_NAME + " = '" + shopName + "'",
                null, null, null, null);
        // If the shop doesn't exist yet, add it
        if(cursor.getCount() == 0){
            // Add all the values of the article
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_SHOP_NAME, shopName);
            // Insert the article in the database
            long insertId = database.insert(DBHelper.TABLE_SHOP, null, values);
            // Take the data of the new article with its newly created id
            cursor = database.query(DBHelper.TABLE_SHOP,
                    allColumns, DBHelper.COLUMN_ID + " = " + insertId,
                    null, null, null, null);
        }
        // Get the shop out of the cursor
        cursor.moveToFirst();
        Shop shop = cursorToShop(cursor);
        cursor.close();

        return shop;
    }

    /**
     * Takes a shop out of a cursor
     * @param cursor The cursor with the shop data
     * @return The shop from the cursor
     */
    private Shop cursorToShop(Cursor cursor) {
        // Convert article data from a cursor to an article object
        Shop shop = new Shop();
        shop.setId(cursor.getLong(0));
        shop.setName(cursor.getString(1));
        return shop;
    }

}
