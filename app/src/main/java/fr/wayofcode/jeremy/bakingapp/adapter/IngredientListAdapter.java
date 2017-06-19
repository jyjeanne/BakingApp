package fr.wayofcode.jeremy.bakingapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import fr.wayofcode.jeremy.bakingapp.R;
import fr.wayofcode.jeremy.bakingapp.data.Ingredient;
import java.util.ArrayList;

/**
 * IngredientListAdapter.
 */
public class IngredientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int TYPE_HEADER = 0;
  private static final int TYPE_ITEM = 1;
  private static final String EXCEPTION_MESSAGE = "there is no type that matches the type %d + make sure your using types correctly";
  private ArrayList<Ingredient> ingredients;

  public IngredientListAdapter(ArrayList<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == TYPE_HEADER) {
      View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_header, parent, false);
      return new IngredientHeaderViewHolder(rootView);
    } else if (viewType == TYPE_ITEM) {
      View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list_item, parent, false);
      return new IngredientViewHolder(rootView);
    }
    throw new RuntimeException(String.format(
        EXCEPTION_MESSAGE,
        viewType));
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof IngredientViewHolder) {
      ((IngredientViewHolder) holder).vIngredient.setText(ingredients.get(position - 1).getIngredient());
      ((IngredientViewHolder) holder).vMeasure.setText(ingredients.get(position - 1).getMeasure());
      ((IngredientViewHolder) holder).vQuantity.setText(String.valueOf(
          ingredients.get(position - 1).getQuantity()));
    }

  }

  @Override
  public int getItemCount() {
    if (ingredients == null) {
      return 0;
    }
    return ingredients.size() + 1;
  }

  @Override
  public int getItemViewType(int position) {
    if (isPositionHeader(position))
      return TYPE_HEADER;

    return TYPE_ITEM;
  }

  private boolean isPositionHeader(int position) {
    return position == 0;
  }


  static class IngredientHeaderViewHolder extends RecyclerView.ViewHolder {

    public IngredientHeaderViewHolder(View itemView) {
      super(itemView);
    }
  }

  static class IngredientViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_ingredient) TextView vIngredient;
    @BindView(R.id.tv_measure)
    TextView vMeasure;
    @BindView(R.id.tv_quantity)
    TextView vQuantity;

    public IngredientViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
