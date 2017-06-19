package fr.wayofcode.jeremy.bakingapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import fr.wayofcode.jeremy.bakingapp.R;
import fr.wayofcode.jeremy.bakingapp.adapter.RecipeAdapter;
import fr.wayofcode.jeremy.bakingapp.api.ApiClient;
import fr.wayofcode.jeremy.bakingapp.api.ApiInterface;
import fr.wayofcode.jeremy.bakingapp.data.Recipe;
import java.lang.reflect.Type;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static fr.wayofcode.jeremy.bakingapp.adapter.RecipeAdapter.RECIPE;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.rv_recipe) RecyclerView mRecipeRecyclerView;
  @BindView(R.id.progressBar) ProgressBar mLoadingIndicator;
  private ArrayList<Recipe> mRecipesList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    if (savedInstanceState != null) {
      mRecipesList = savedInstanceState.getParcelableArrayList(RECIPE);
      loadData();
    } else {
      getRecipes();
    }
  }

  private void getRecipes() {
    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
    final Type TYPE = new TypeToken<ArrayList<Recipe>>() {
    }.getType();
    Call<JsonArray> call = apiInterface.getData();
    call.enqueue(new Callback<JsonArray>() {
      @Override
      public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
        mRecipesList = new Gson().fromJson(response.body(), TYPE);
        loadData();
      }

      @Override
      public void onFailure(Call<JsonArray> call, Throwable t) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Toast.makeText(MainActivity.this, R.string.error_no_internet, Toast.LENGTH_LONG).show();
      }
    });
  }

  private void loadData() {
    mLoadingIndicator.setVisibility(View.INVISIBLE);
    RecipeAdapter recipeAdapter = new RecipeAdapter(MainActivity.this, mRecipesList);
    mRecipeRecyclerView.setAdapter(recipeAdapter);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList(RECIPE, mRecipesList);
  }
}
