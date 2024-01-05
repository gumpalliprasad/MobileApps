/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package myschoolapp.com.gsnedutech.Util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.C.ContentType;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import myschoolapp.com.gsnedutech.R;


/** An activity that plays media using {@link SimpleExoPlayer}. */
public class PlayerActivity extends AppCompatActivity
    implements PlaybackPreparer, PlayerControlView.VisibilityListener {

  private static final String KEY_WINDOW = "window";
  private static final String KEY_POSITION = "position";
  private static final String KEY_AUTO_PLAY = "auto_play";

  private static final CookieManager DEFAULT_COOKIE_MANAGER;
  static {
    DEFAULT_COOKIE_MANAGER = new CookieManager();
    DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
  }

  private PlayerView playerView;

  private DataSource.Factory dataSourceFactory;
  private SimpleExoPlayer player;
  private MediaSource mediaSource;

  private boolean startAutoPlay;
  private int startWindow;
  private long startPosition;

  long percentage;

  // Activity lifecycle

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dataSourceFactory = buildDataSourceFactory();
    if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
      CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
    }

    setContentView(R.layout.player_activity);
    percentage = getIntent().getLongExtra("percentage",0);
    playerView = findViewById(R.id.player_view);
    playerView.setControllerVisibilityListener(this);
    playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
    playerView.requestFocus();
    if (savedInstanceState != null) {
      startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
      startWindow = savedInstanceState.getInt(KEY_WINDOW);
      startPosition = savedInstanceState.getLong(KEY_POSITION);
    } else {
      clearStartPosition();
    }
  }


  private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

    @Override
    public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
      String errorString = getString(R.string.error_generic);
      if (e.type == ExoPlaybackException.TYPE_RENDERER) {
        Exception cause = e.getRendererException();
        if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
          // Special case for decoder initialization failures.
          MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                  (MediaCodecRenderer.DecoderInitializationException) cause;
          if (decoderInitializationException.codecInfo == null) {
            if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
              errorString = getString(R.string.error_querying_decoders);
            } else if (decoderInitializationException.secureDecoderRequired) {
              errorString =
                      getString(
                              R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
            } else {
              errorString =
                      getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
            }
          } else {
            errorString =
                    getString(
                            R.string.error_instantiating_decoder,
                            decoderInitializationException.codecInfo.name);
          }
        }
      }
      return Pair.create(0, errorString);
    }
  }



  public DataSource.Factory buildDataSourceFactory() {
    return new DefaultDataSourceFactory(this,
            Util.getUserAgent(this, getResources().getString(R.string.app_name)));
  }

  @Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    releasePlayer();
    clearStartPosition();
    setIntent(intent);
  }

  @Override
  public void onStart() {
    super.onStart();
    if (Util.SDK_INT > 23) {
      initializePlayer();
      if (playerView != null) {
        playerView.onResume();
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (Util.SDK_INT <= 23 || player == null) {
      initializePlayer();
      if (playerView != null) {
        playerView.onResume();
      }
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (Util.SDK_INT <= 23) {
      if (playerView != null) {
        playerView.onPause();
      }
      releasePlayer();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (Util.SDK_INT > 23) {
      if (playerView != null) {
        playerView.onPause();
      }
      releasePlayer();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (grantResults.length == 0) {
      // Empty results are triggered if a permission is requested while another request was already
      // pending and can be safely ignored in this case.
      return;
    }
    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      initializePlayer();
    } else {
      showToast();
      finish();
    }
  }

  @Override
  public void onSaveInstanceState(@NotNull Bundle outState) {
    super.onSaveInstanceState(outState);
    updateStartPosition();
    outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
    outState.putInt(KEY_WINDOW, startWindow);
    outState.putLong(KEY_POSITION, startPosition);
  }

  // Activity input

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    // See whether the player view wants to handle media or DPAD keys events.
    return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
  }

  // PlaybackControlView.PlaybackPreparer implementation

  @Override
  public void preparePlayback() {
    player.retry();
  }

  // PlaybackControlView.VisibilityListener implementation

  @Override
  public void onVisibilityChange(int visibility) {
//    debugRootView.setVisibility(visibility);
  }

  // Internal methods

  private void initializePlayer() {
    if (player == null) {
      Intent intent = getIntent();

      mediaSource = createTopLevelMediaSource(intent.getStringExtra("url"));
      RenderersFactory renderersFactory = new DefaultRenderersFactory(/* context= */ this)
              .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

      player =
          new SimpleExoPlayer.Builder(/* context= */ this, renderersFactory)
              .build();
      player.addListener(new PlayerEventListener());
      player.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
      player.setPlayWhenReady(startAutoPlay);
      playerView.setPlayer(player);
      playerView.setPlaybackPreparer(this);
    }
    boolean haveStartPosition = startWindow != C.INDEX_UNSET;
    if (haveStartPosition) {
      player.seekTo(startWindow, startPosition);
    }
    player.prepare(mediaSource, !haveStartPosition, false);
  }

  @NotNull
  private MediaSource createTopLevelMediaSource(String url) {

    MediaSource[] mediaSources = new MediaSource[1];
    mediaSources[0] = createLeafMediaSource(Uri.parse(url));

    return mediaSources[0];
  }

  private MediaSource createLeafMediaSource(
          Uri uri) {
    @ContentType int type = Util.inferContentType(uri);
    switch (type) {
      case C.TYPE_DASH:
        return new DashMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri);
      case C.TYPE_SS:
        return new SsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri);
      case C.TYPE_HLS:
        return new HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri);
      case C.TYPE_OTHER:
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(uri);
      default:
        throw new IllegalStateException("Unsupported type: " + type);
    }
  }

  private void releasePlayer() {
    if (player != null) {
      updateStartPosition();
      player.release();
      player = null;
      mediaSource = null;
    }
  }


  private void updateStartPosition() {
    if (player != null) {
      startAutoPlay = player.getPlayWhenReady();
      startWindow = player.getCurrentWindowIndex();
      startPosition = Math.max(0, player.getContentPosition());
    }
  }

  private void clearStartPosition() {
    startAutoPlay = true;
    startWindow = C.INDEX_UNSET;
    startPosition = C.TIME_UNSET;
  }


  private void showToast() {
    showToast(getString(R.string.storage_permission_denied));
  }

  private void showToast(String message) {
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
  }

  private static boolean isBehindLiveWindow(ExoPlaybackException e) {
    if (e.type != ExoPlaybackException.TYPE_SOURCE) {
      return false;
    }
    Throwable cause = e.getSourceException();
    while (cause != null) {
      if (cause instanceof BehindLiveWindowException) {
        return true;
      }
      cause = cause.getCause();
    }
    return false;
  }

  private class PlayerEventListener implements Player.EventListener {

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, @Player.State int playbackState) {
      if (playbackState == Player.STATE_READY){
        if (percentage!=0) {
          player.seekTo(percentage);
          percentage =0;
        }

      }
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
      if (isBehindLiveWindow(e)) {
        clearStartPosition();
        initializePlayer();
      }
    }

  }


  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
  }
}