package fr.wayofcode.jeremy.bakingapp;

import android.app.Application;
import com.facebook.stetho.Stetho;

/**
 * Main app class : BakingApp.
 */

public class BakingApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
  }
}
