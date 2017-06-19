package fr.wayofcode.jeremy.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import fr.wayofcode.jeremy.bakingapp.R;
import fr.wayofcode.jeremy.bakingapp.data.Recipe;

/**
 * IngredientStepAdapter class.
 */

public class IngredientStepAdapter extends RecyclerView.Adapter<IngredientStepAdapter.IngredientStepViewHolder> {

  public static final String INGREDIENTS = "ingredients";
  public static final String STEPS = "steps";

  private final OnIngredientStepListener mClickListener;
  private Context mContext;
  private Recipe mRecipe;

  public IngredientStepAdapter(Context context, Recipe recipe, OnIngredientStepListener listener) {
    this.mContext = context;
    this.mRecipe = recipe;
    mClickListener = listener;
  }

  @Override
  public IngredientStepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_step_list_item, parent, false);
    return new IngredientStepViewHolder(rootView);
  }

  @Override
  public void onBindViewHolder(IngredientStepViewHolder holder, int position) {
    if (position == 0) {
      holder.setIngredientStep(mContext.getString(R.string.recipe_ingredients));
    } else {
      String shortDescription = mRecipe.getSteps().get(position - 1).getShortDescription();
      holder.setIngredientStep(shortDescription);
    }
  }

  @Override
  public int getItemCount() {
    if (mRecipe == null)
      return 0;
    return mRecipe.getSteps().size() + 1;
  }

  public interface OnIngredientStepListener {
    void onIngredientStepSelected(int position);
  }

  class IngredientStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    @BindView(R.id.tv_ingredient_step) TextView mIngredientStep;

    public IngredientStepViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    void setIngredientStep(String ingredientStep) {
      mIngredientStep.setText(ingredientStep);
    }

    @Override
    public void onClick(View v) {
      mClickListener.onIngredientStepSelected(getAdapterPosition());

    }
  }
}
