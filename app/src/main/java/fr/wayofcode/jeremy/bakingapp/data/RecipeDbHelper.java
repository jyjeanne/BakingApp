package fr.wayofcode.jeremy.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * RecipeDbHelper.
 */

public final class RecipeDbHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "recipe.db";

  private static final int DATABASE_VERSION = 1;
  private static final String CREATE_RECIPE_TABLE = "CREATE TABLE " + RecipeContract.RecipeEntry.RECIPE_TABLE + " (" +
      RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
      RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
      RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
      RecipeContract.RecipeEntry.COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
      RecipeContract.RecipeEntry.COLUMN_INGREDIENT_MEASURE + " TEXT NOT NULL, " +
      RecipeContract.RecipeEntry.COLUMN_INGREDIENT_QUANTITY + " INTEGER NOT NULL " +
      ");";

  public RecipeDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_RECIPE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.RECIPE_TABLE);
    onCreate(db);
  }
}
