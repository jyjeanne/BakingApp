package fr.wayofcode.jeremy.bakingapp.fragment;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import fr.wayofcode.jeremy.bakingapp.R;
import fr.wayofcode.jeremy.bakingapp.adapter.IngredientListAdapter;
import fr.wayofcode.jeremy.bakingapp.data.Ingredient;
import fr.wayofcode.jeremy.bakingapp.data.Step;
import java.util.ArrayList;

import static fr.wayofcode.jeremy.bakingapp.activity.IngredientStepActivity.PANES;
import static fr.wayofcode.jeremy.bakingapp.activity.IngredientStepActivity.POSITION;
import static fr.wayofcode.jeremy.bakingapp.adapter.IngredientStepAdapter.INGREDIENTS;
import static fr.wayofcode.jeremy.bakingapp.adapter.IngredientStepAdapter.STEPS;

/**
 *
 * IngredientStepDetailFragment test.
 */
public class IngredientStepDetailFragment extends Fragment
    implements View.OnClickListener, ExoPlayer.EventListener {

  public static final String AUTOPLAY = "autoplay";
  public static final String CURRENT_WINDOW_INDEX = "current_window_index";
  public static final String PLAYBACK_POSITION = "playback_position";
  private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
  private final String[] pictureFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};

  private final String TAG = IngredientStepDetailFragment.class.getSimpleName();
  private boolean autoPlay = false;
  private int currentWindow;
  private long playbackPosition;
  private TrackSelector trackSelector;
  private PlaybackStateCompat.Builder stateBuilder;
  private static MediaSessionCompat mediaSession;
  private RecyclerView mIngredientsRecyclerView;
  private LinearLayoutManager mLinearLayoutManager;
  private SimpleExoPlayerView mPlayerView;
  private SimpleExoPlayer mExoPlayer;
  private ImageView mThumbnail;
  private TextView mDescription;
  private Button mPrevious;
  private Button mNext;
  private View mStepDetail;
  private ArrayList<Ingredient> mIngredients;
  private ArrayList<Step> mSteps;
  private int mIndex;
  private int mPosition;
  private boolean mTwoPane;


  public IngredientStepDetailFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    if (savedInstanceState != null) {
      mIngredients = savedInstanceState.getParcelableArrayList(INGREDIENTS);
      mSteps = savedInstanceState.getParcelableArrayList(STEPS);
      mTwoPane = savedInstanceState.getBoolean(PANES);
      mPosition = savedInstanceState.getInt(POSITION);
      playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION, 0);
      currentWindow = savedInstanceState.getInt(CURRENT_WINDOW_INDEX, 0);
      autoPlay = savedInstanceState.getBoolean(AUTOPLAY, false);
    }

    View rootView = inflater.inflate(R.layout.fragment_ingredient_step_detail, container, false);

    mIngredientsRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_ingredients);
    mLinearLayoutManager = new LinearLayoutManager(getActivity());
    mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    mStepDetail = rootView.findViewById(R.id.step_detail_view);
    mThumbnail = (ImageView) rootView.findViewById(R.id.tv_thumbnail);
    mDescription = (TextView) rootView.findViewById(R.id.tv_description);
    mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.sepv_step_video);
    mPrevious = (Button) rootView.findViewById(R.id.bt_previous);
    mNext = (Button) rootView.findViewById(R.id.bt_next);

    mPrevious.setOnClickListener(this);
    mNext.setOnClickListener(this);

    mPosition = getArguments().getInt(POSITION);
    mIndex = mPosition - 1;
    mTwoPane = getArguments().getBoolean(PANES);


    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();

    if (mTwoPane && mPosition == 0) {
      showIngredients();
    } else if (mTwoPane) {
      showStepsForTab();
    } else if (mPosition == 0) {
      showIngredients();
    } else {
      showStepsForPhone();
    }
    if(mediaSession != null) {
      mediaSession.setActive(true);
    }
  }

  private void showIngredients() {
    mStepDetail.setVisibility(View.GONE);
    mIngredientsRecyclerView.setVisibility(View.VISIBLE);
    mIngredients = getArguments().getParcelableArrayList(INGREDIENTS);
    IngredientListAdapter adapter = new IngredientListAdapter(mIngredients);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mIngredientsRecyclerView.getContext(),
        LinearLayoutManager.VERTICAL);
    mIngredientsRecyclerView.addItemDecoration(dividerItemDecoration);
    mIngredientsRecyclerView.setHasFixedSize(true);
    mIngredientsRecyclerView.setLayoutManager(mLinearLayoutManager);
    mIngredientsRecyclerView.setAdapter(adapter);
  }


  public void showStepsForTab() {
    mIngredientsRecyclerView.setVisibility(View.GONE);
    mStepDetail.setVisibility(View.VISIBLE);
    mPlayerView.setVisibility(View.VISIBLE);
    mDescription.setVisibility(View.VISIBLE);
    mThumbnail.setVisibility(View.VISIBLE);
    mSteps = getArguments().getParcelableArrayList(STEPS);
    assert mSteps != null;
    mDescription.setText(mSteps.get(mIndex).getDescription());

    if(checkPictureUri(mSteps.get(mIndex).getThumbnailUrl())) {
      Picasso.with(getActivity().getApplicationContext())
          .load(mSteps.get(mIndex).getThumbnailUrl())
          .into(mThumbnail);
    }
    playStepVideo(mIndex);
  }

  public boolean checkPictureUri(String pictureUri)
  {
    if(!TextUtils.isEmpty(pictureUri)) {
    for (String extension : pictureFileExtensions) {
      if (pictureUri.toLowerCase().endsWith(extension)) {
        return true;
      }
    }
  }
    return false;
  }


  private void playStepVideo(int index) {
    mPlayerView.setVisibility(View.VISIBLE);
    mPlayerView.requestFocus();
    String videoUrl = mSteps.get(index).getVideoUrl();
    String thumbNailUrl = mSteps.get(index).getThumbnailUrl();
    if (!TextUtils.isEmpty(videoUrl)) {
      initializePlayer(Uri.parse(videoUrl));
      initializeMediaSession();
    } else if (!TextUtils.isEmpty(thumbNailUrl)) {

      Picasso.with(getActivity().getApplicationContext())
          .load(mSteps.get(mIndex).getThumbnailUrl())
          .into(mThumbnail);

      initializePlayer(Uri.parse(thumbNailUrl));
      initializeMediaSession();
    } else {
      mPlayerView.setVisibility(View.GONE);
    }
  }

  void isLandscape() {
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
      hideSystemUi();
  }

  private void showStepsForPhone() {
    showStepsForTab();
    isLandscape();
    mPrevious.setVisibility(View.VISIBLE);
    mNext.setVisibility(View.VISIBLE);
  }

  /**
   * Initialize ExoPlayer.
   *
   * @param mediaUri The URI of the sample to play.
   */
  private void initializePlayer(Uri mediaUri) {
    if(mExoPlayer!=null) {
      mExoPlayer.stop();
    }
    mExoPlayer = null;


    DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity(),
        null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

    TrackSelection.Factory adaptiveTrackSelectionFactory =
        new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

    trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
    LoadControl loadControl = new DefaultLoadControl();
    mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

    mExoPlayer.addListener(this);

    mPlayerView.setPlayer(mExoPlayer);
    mExoPlayer.setPlayWhenReady(true);

    mExoPlayer.seekTo(currentWindow, playbackPosition);

    // Prepare the MediaSource.
    String userAgent = Util.getUserAgent(getActivity(), getString(R.string.application_name));

    MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
        new DefaultDataSourceFactory(getActivity(), BANDWIDTH_METER,
            new DefaultHttpDataSourceFactory(userAgent, BANDWIDTH_METER)),
        new DefaultExtractorsFactory(), null, null);

    mExoPlayer.prepare(mediaSource);
    restExoPlayer(mPosition, false);
  }

  private void restExoPlayer(int position, boolean playWhenReady) {
    this.mPosition = position;
    if(mExoPlayer != null) {
      mExoPlayer.seekTo(position);
      mExoPlayer.setPlayWhenReady(playWhenReady);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    releasePlayer();
  }

  @Override
  public void onPause() {
    super.onPause();
    releasePlayer();
  }

  @Override
  public void onStop() {
    super.onStop();
    releasePlayer();
  }

  private void releasePlayer() {
    if (mExoPlayer != null) {
      mExoPlayer.stop();
      mExoPlayer.release();
      mExoPlayer = null;
    }
    if(mediaSession != null) {
      mediaSession.setActive(false);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bt_next:
        if (mIndex < mSteps.size() - 1) {
          int index = ++mIndex;
          mDescription.setText(mSteps.get(index).getDescription());
          if(!TextUtils.isEmpty(mSteps.get(index).getThumbnailUrl())) {
            Picasso.with(getActivity().getApplicationContext())
                .load(mSteps.get(index).getThumbnailUrl())
                .into(mThumbnail);
          }
            playStepVideo(index);
        } else {
          Toast.makeText(getActivity(), R.string.end_of_steps, Toast.LENGTH_LONG).show();
        }
        break;
      case R.id.bt_previous:
        if (mIndex > 0) {
          int index = --mIndex;
          mDescription.setText(mSteps.get(index).getDescription());
          if(!TextUtils.isEmpty(mSteps.get(index).getThumbnailUrl())) {
            Picasso.with(getActivity().getApplicationContext())
                .load(mSteps.get(index).getThumbnailUrl())
                .into(mThumbnail);
          }
          playStepVideo(index);
        } else {
          Toast.makeText(getActivity(), R.string.start_of_steps, Toast.LENGTH_LONG).show();
        }
        break;
    }
  }

  private void initializeMediaSession() {
    if(mediaSession!= null) {
      mediaSession.setActive(false);
    }
      mediaSession = new MediaSessionCompat(getContext(), TAG);
    mediaSession.setFlags(
        MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
    mediaSession.setMediaButtonReceiver(null);
    stateBuilder = new PlaybackStateCompat.Builder()
        .setActions(
            PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PAUSE |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_PLAY_PAUSE);
    mediaSession.setPlaybackState(stateBuilder.build());
    mediaSession.setCallback(new MySessionCallback());
    mediaSession.setActive(true);
  }

  @SuppressLint("InlinedApi")
  private void hideSystemUi() {
    View decorView = getActivity().getWindow().getDecorView();
    decorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE);
  }

  private void showSystemUI() {
    View decorView = getActivity().getWindow().getDecorView();
    decorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

  }


  private class MySessionCallback extends MediaSessionCompat.Callback {
    @Override
    public void onPlay() {
      mExoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onPause() {
      mExoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onSkipToPrevious() {
      restExoPlayer(0, false);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    if (mExoPlayer != null) {
      outState.putLong(PLAYBACK_POSITION, playbackPosition);
      outState.putInt(CURRENT_WINDOW_INDEX, currentWindow);
      outState.putBoolean(AUTOPLAY, autoPlay);
    }
    outState.putParcelableArrayList(INGREDIENTS, mIngredients);
    outState.putParcelableArrayList(STEPS, mSteps);
    outState.putBoolean(PANES, mTwoPane);
    outState.putInt(POSITION, mPosition);

  }

  @Override
  public void onTimelineChanged(Timeline timeline, Object manifest) {
  }

  @Override
  public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

  }

  @Override
  public void onLoadingChanged(boolean isLoading) {
  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady && getActivity() != null ) {
      Toast.makeText(getActivity(), R.string.toast_player_playing, Toast.LENGTH_LONG).show();
    } else if ((playbackState == ExoPlayer.STATE_READY)) {

    }
    if (playbackState == PlaybackStateCompat.STATE_PLAYING && mExoPlayer != null) {
      mPosition =(int)mExoPlayer.getCurrentPosition();
    }
    if(mediaSession != null && stateBuilder != null) {
      mediaSession.setPlaybackState(stateBuilder.build());
    }
  }

  @Override
  public void onPlayerError(ExoPlaybackException e) {
    String errorString = null;
    if (e.type == ExoPlaybackException.TYPE_RENDERER) {
      Exception cause = e.getRendererException();
      if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
        // Special case for decoder initialization failures.
        MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
            (MediaCodecRenderer.DecoderInitializationException) cause;
        if (decoderInitializationException.decoderName == null) {
          if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
            errorString = getString(R.string.error_querying_decoders);
          } else if (decoderInitializationException.secureDecoderRequired) {
            errorString = getString(R.string.error_no_secure_decoder,
                decoderInitializationException.mimeType);
          } else {
            errorString = getString(R.string.error_no_decoder,
                decoderInitializationException.mimeType);
          }
        } else {
          errorString = getString(R.string.error_instantiating_decoder,
              decoderInitializationException.decoderName);
        }
      }
    }
    if (errorString != null && getActivity() != null) {
      Toast.makeText(getActivity(), errorString, Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onPositionDiscontinuity() {
  }

  @Override
  public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
  }


}

