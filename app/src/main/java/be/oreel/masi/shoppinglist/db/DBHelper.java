package be.oreel.masi.shoppinglist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class for the ArticleDataSource class
 */
public class DBHelper extends SQLiteOpenHelper {

    // =================
    // === VARIABLES ===
    // =================

    public static final String COLUMN_ID = "_id";

    public static final String TABLE_SHOP = "shop";
    public static final String COLUMN_SHOP_NAME = "name";

    public static final String TABLE_ARTICLE = "article";
    public static final String COLUMN_SHOP_ID = "shop_id";
    public static final String COLUMN_ARTICLE_NAME = "name";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_MEASURE = "measure";
    public static final String COLUMN_STRIKETHROUGH = "strikethrough";
    public static final String COLUMN_PRIORITY = "priority";

    private static final String DATABASE_NAME = "article.db";
    private static final int DATABASE_VERSION = 4;

    // Database creation sql statement
    private static final String DATABASE_CREATE_SHOP =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SHOP +"( "+
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_SHOP_NAME + " TEXT NOT NULL UNIQUE);";

    private static final String DATABASE_CREATE_ARTICLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ARTICLE +"( "+
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_SHOP_ID + " INTEGER NOT NULL," +
            COLUMN_ARTICLE_NAME + " TEXT NOT NULL," +
            COLUMN_AMOUNT + " INTEGER NOT NULL DEFAULT 1 CHECK(" +
                    COLUMN_AMOUNT + " > 0), " +
            COLUMN_MEASURE + " TEXT," +
            COLUMN_STRIKETHROUGH + " INTEGER NOT NULL DEFAULT 0 CHECK("+
                    COLUMN_STRIKETHROUGH+" IN (0,1))," +
            COLUMN_PRIORITY + " INTEGER NOT NULL DEFAULT 0," +
            " FOREIGN KEY(" + COLUMN_SHOP_ID + ") REFERENCES " + TABLE_SHOP + "("+COLUMN_ID+"));";

    // ===================
    // === CONSTRUCTOR ===
    // ===================

    /**
     * The constructor
     * @param context The context
     */
    public DBHelper(Context context) {
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
        database.execSQL(DATABASE_CREATE_SHOP);
        database.execSQL(DATABASE_CREATE_ARTICLE);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOP);
        // Recreate the database
        onCreate(db);
    }
}
