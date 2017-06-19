package fr.wayofcode.jeremy.bakingapp.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import fr.wayofcode.jeremy.bakingapp.R;
import fr.wayofcode.jeremy.bakingapp.data.Ingredient;
import fr.wayofcode.jeremy.bakingapp.data.Step;
import fr.wayofcode.jeremy.bakingapp.fragment.IngredientStepDetailFragment;
import java.util.ArrayList;

import static fr.wayofcode.jeremy.bakingapp.activity.IngredientStepActivity.PANES;
import static fr.wayofcode.jeremy.bakingapp.activity.IngredientStepActivity.POSITION;
import static fr.wayofcode.jeremy.bakingapp.adapter.IngredientStepAdapter.INGREDIENTS;
import static fr.wayofcode.jeremy.bakingapp.adapter.IngredientStepAdapter.STEPS;

/**
 *  IngredientStepDetailActivity
 */
public class IngredientStepDetailActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ingredient_step_detail);
    if (savedInstanceState == null) {
      ArrayList<Ingredient> ingredients = getIntent().getParcelableArrayListExtra(INGREDIENTS);
      ArrayList<Step> steps = getIntent().getParcelableArrayListExtra(STEPS);
      int position = getIntent().getIntExtra(POSITION, 0);
      boolean mTwoPane = getIntent().getBooleanExtra(PANES, false);
      IngredientStepDetailFragment fragment = new IngredientStepDetailFragment();
      Bundle bundle = new Bundle();
      bundle.putInt(POSITION, position);
      bundle.putBoolean(PANES, mTwoPane);
      bundle.putParcelableArrayList(INGREDIENTS, ingredients);
      bundle.putParcelableArrayList(STEPS, steps);
      fragment.setArguments(bundle);
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.step_container, fragment)
          .commit();

    }
  }

}

