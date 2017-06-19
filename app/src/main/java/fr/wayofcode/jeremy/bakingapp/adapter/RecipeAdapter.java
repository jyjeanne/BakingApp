package fr.wayofcode.jeremy.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import fr.wayofcode.jeremy.bakingapp.R;
import fr.wayofcode.jeremy.bakingapp.activity.IngredientStepActivity;
import fr.wayofcode.jeremy.bakingapp.data.Recipe;
import java.util.ArrayList;

/**
 * The RecipeAdapter.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

  public static final String RECIPE = "recipe";
  private Context context;
  private ArrayList<Recipe> recipes;

  public RecipeAdapter(final Context context, ArrayList<Recipe> recipes) {
    this.context = context;
    this.recipes = recipes;
  }

  @Override
  public RecipeViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);

    return new RecipeViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final RecipeViewHolder holder, final int position) {
    holder.setRecipeName(recipes.get(position).getName());
    holder.setRecipeImage(context, recipes.get(position).getImageUrl());
  }

  @Override
  public int getItemCount() {
    if (recipes == null) {
      return 0;
    }
    return recipes.size();
  }

  class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.tv_recipe_name) TextView vRecipeName;
    @BindView(R.id.iv_recipe_image) ImageView vRecipeImage;

    public RecipeViewHolder(final View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    void setRecipeName(final String recipeName) {
      vRecipeName.setText(recipeName);
    }

    void setRecipeImage(final Context context, final String recipeImage) {
      if (!recipeImage.isEmpty()) {
        vRecipeImage.setVisibility(View.VISIBLE);
        Glide.with(context)
            .load(recipeImage)
            .into(vRecipeImage);
      }
    }

    @Override
    public void onClick(View v) {
      Intent intent = new Intent(context, IngredientStepActivity.class);
      intent.putExtra(RECIPE, recipes.get(getAdapterPosition()));
      context.startActivity(intent);
    }
  }
}
