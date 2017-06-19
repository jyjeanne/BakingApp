package fr.wayofcode.jeremy.bakingapp.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import fr.wayofcode.jeremy.bakingapp.R;
import fr.wayofcode.jeremy.bakingapp.adapter.IngredientStepAdapter;
import fr.wayofcode.jeremy.bakingapp.data.Ingredient;
import fr.wayofcode.jeremy.bakingapp.data.Recipe;
import fr.wayofcode.jeremy.bakingapp.data.RecipeContract;
import fr.wayofcode.jeremy.bakingapp.fragment.IngredientStepDetailFragment;
import java.util.ArrayList;

import static fr.wayofcode.jeremy.bakingapp.adapter.IngredientStepAdapter.INGREDIENTS;
import static fr.wayofcode.jeremy.bakingapp.adapter.IngredientStepAdapter.STEPS;
import static fr.wayofcode.jeremy.bakingapp.adapter.RecipeAdapter.RECIPE;
import static fr.wayofcode.jeremy.bakingapp.data.RecipeContract.RECIPE_CONTENT_URI;

/**
 *  IngredientStepActivity
 */
public class IngredientStepActivity extends AppCompatActivity implements IngredientStepAdapter.OnIngredientStepListener {

  public static final String POSITION = "position";
  public static final String PANES = "panes";
  private boolean mTwoPane;
  private Recipe mRecipe;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ingredient_step);
    mRecipe = getIntent().getParcelableExtra(RECIPE);

    mTwoPane = findViewById(R.id.detail_linear_layout) != null;
  }

  @Override
  public void onIngredientStepSelected(int position) {
    Bundle bundle = new Bundle();
    if (mTwoPane && position == 0) {
      IngredientStepDetailFragment fragment = new IngredientStepDetailFragment();
      bundle.putInt(POSITION, position);
      bundle.putBoolean(PANES, mTwoPane);
      bundle.putParcelableArrayList(INGREDIENTS, mRecipe.getIngredients());
      fragment.setArguments(bundle);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.step_ingredient_container, fragment)
          .commit();

    } else if (mTwoPane) {
      IngredientStepDetailFragment fragment = new IngredientStepDetailFragment();
      bundle.putInt(POSITION, position);
      bundle.putBoolean(PANES, mTwoPane);
      bundle.putParcelableArrayList(STEPS, mRecipe.getSteps());
      fragment.setArguments(bundle);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.step_ingredient_container, fragment)
          .commit();
    } else if (position == 0) {
      bundle.putInt(POSITION, position);
      bundle.putBoolean(PANES, mTwoPane);
      bundle.putParcelableArrayList(INGREDIENTS, mRecipe.getIngredients());
      Intent intent = new Intent(IngredientStepActivity.this, IngredientStepDetailActivity.class);
      intent.putExtras(bundle);
      startActivity(intent);
    } else {
      bundle.putInt(POSITION, position);
      bundle.putBoolean(PANES, mTwoPane);
      bundle.putParcelableArrayList(STEPS, mRecipe.getSteps());
      Intent intent = new Intent(IngredientStepActivity.this, IngredientStepDetailActivity.class);
      intent.putExtras(bundle);
      startActivity(intent);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_ingredient_step, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      case R.id.action_favorite:
        if (isFavorite()) {
          removeRecipeFromFavorites();
          item.setIcon(R.drawable.ic_favorite_normal);
          Toast.makeText(this, String.format(getString(R.string.favorite_removed_message), mRecipe.getName()), Toast.LENGTH_LONG).show();
        } else {
          addRecipeToFavorites();
          item.setIcon(R.drawable.ic_favorite_added);
          Toast.makeText(this, String.format(getString(R.string.favorite_added_message), mRecipe.getName()), Toast.LENGTH_LONG).show();
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem menuItem = menu.findItem(R.id.action_favorite);
    if (isFavorite()) {
      menuItem.setIcon(R.drawable.ic_favorite_added);
    } else {
      menuItem.setIcon(R.drawable.ic_favorite_normal);
    }
    return true;
  }


  private boolean isFavorite() {
    String[] projection = { RecipeContract.RecipeEntry.COLUMN_RECIPE_ID};
    String selection = RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " = " + mRecipe.getId();
    Cursor cursor = getContentResolver().query(RECIPE_CONTENT_URI,
        projection,
        selection,
        null,
        null,
        null);

    return (cursor != null ? cursor.getCount() : 0) > 0;
  }

  synchronized private void removeRecipeFromFavorites() {
    getContentResolver().delete(RECIPE_CONTENT_URI, null, null);
  }

  synchronized private void addRecipeToFavorites() {
    getContentResolver().delete(RECIPE_CONTENT_URI, null, null);
    getIngredient(mRecipe.getIngredients());
  }

  private void getIngredient(ArrayList<Ingredient> ingredients) {

    for (Ingredient ingredient : ingredients) {
      ContentValues values = new ContentValues();
      values.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID, mRecipe.getId());
      values.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME, mRecipe.getName());
      values.put(RecipeContract.RecipeEntry.COLUMN_INGREDIENT_NAME, ingredient.getIngredient());
      values.put(RecipeContract.RecipeEntry.COLUMN_INGREDIENT_MEASURE, ingredient.getMeasure());
      values.put(RecipeContract.RecipeEntry.COLUMN_INGREDIENT_QUANTITY, ingredient.getQuantity());
      getContentResolver().insert(RECIPE_CONTENT_URI, values);
    }
  }

}
