package be.oreel.masi.shoppinglist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class for the ArticleDataSource class
 */
public class ArticleDBHelper extends SQLiteOpenHelper {

    // =================
    // === VARIABLES ===
    // =================

    public static final String TABLE_ARTICLE = "article";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SHOP_NAME = "shop";
    public static final String COLUMN_ARTICLE_NAME = "name";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_STRIKETHROUGH = "strikethrough";
    public static final String COLUMN_PRIORITY = "priority";

    private static final String DATABASE_NAME = "article.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ARTICLE +"( "+
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_SHOP_NAME + " TEXT NOT NULL,"+
            COLUMN_ARTICLE_NAME + " TEXT NOT NULL,"+
            COLUMN_AMOUNT + " TEXT NOT NULL," +
            COLUMN_STRIKETHROUGH + " INTEGER NOT NULL DEFAULT 0 CHECK("+
                    COLUMN_STRIKETHROUGH+" IN (0,1))," +
            COLUMN_PRIORITY + " INTEGER NOT NULL DEFAULT 0" + ");";

    // ===================
    // === CONSTRUCTOR ===
    // ===================

    /**
     * The constructor
     * @param context The context
     */
    public ArticleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ========================================
    // === PARENT FUNCTIONS IMPLEMENTATIONS ===
    // ========================================

    /**
     * Creates the tables if they don't exist
     * @param database The database in which the tables will be created
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    /**
     * Upgrades the database of the database version changes
     * @param db The database
     * @param oldVersion The old version number
     * @param newVersion The new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLE);
        // Recreate the database
        onCreate(db);
    }
}
