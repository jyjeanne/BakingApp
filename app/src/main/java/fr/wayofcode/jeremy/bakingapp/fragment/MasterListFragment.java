package fr.wayofcode.jeremy.bakingapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.wayofcode.jeremy.bakingapp.R;
import fr.wayofcode.jeremy.bakingapp.adapter.IngredientStepAdapter;
import fr.wayofcode.jeremy.bakingapp.data.Recipe;

import static fr.wayofcode.jeremy.bakingapp.adapter.RecipeAdapter.RECIPE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class MasterListFragment extends Fragment {


  @BindView(R.id.rv_ingredients_steps) RecyclerView mIngredientStepRecyclerView;
  private Recipe mRecipe;
  private IngredientStepAdapter mIngredientStepAdapter;
  private IngredientStepAdapter.OnIngredientStepListener mClickListener;

  public MasterListFragment() {
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      mClickListener = (IngredientStepAdapter.OnIngredientStepListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
          + " must implement OnImageClickListener");
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      mRecipe = savedInstanceState.getParcelable(RECIPE);
    }
    final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);
    ButterKnife.bind(this, rootView);
    mRecipe = getActivity().getIntent().getParcelableExtra(RECIPE);
    mIngredientStepAdapter = new IngredientStepAdapter(getContext(), mRecipe, mClickListener);
    mIngredientStepRecyclerView.setAdapter(mIngredientStepAdapter);
    return rootView;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(RECIPE, mRecipe);
  }
}
