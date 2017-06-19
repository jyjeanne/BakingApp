package fr.wayofcode.jeremy.bakingapp.api;

import com.google.gson.JsonArray;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * ApiInterface.
 */

public interface ApiInterface {
  @GET("topher/2017/May/59121517_baking/baking.json") Call<JsonArray> getData();

}
