package com.example.android.bakingapp.ui;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.databinding.FragmentVideoStepsBinding;
import com.example.android.bakingapp.model.Recipe;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.media.session.MediaButtonReceiver;

/**
 * Created by margarita baltakiene on 25/06/2018.
 */

public class VideoStepsFragment extends Fragment implements ExoPlayer.EventListener {

    /**
     * Class name for logging
     */
    private static final String TAG = VideoStepsFragment.class.getSimpleName();

    /**
     * Extra key from MainActivity
     */
    private static final String EXTRA_KEY = "recipe";

    /**
     * Constant to get ExoPlayer position onSaveInstanceState
     */
    private static final String EXOPLAYER_POSITION = "position";

    /**
     * Constant to get ExoPlayer state onSaveInstanceState
     */
    private static final String EXOPLAYER_STATE = "state";

    /**
     * Constant to get ExoPlayer state onSaveInstanceState
     */
    private static final String TABLET_STATE = "tabletState";

    /**
     * Constant to get ExoPlayer state onSaveInstanceState
     */
    private static final String STEP_INDEX = "stepIndex";

    /**
     * Constant for the channel ID notification
     */
    private static final String CHANNEL_ID = "channelID";

    /**
     * MediaSession
     */
    private static MediaSessionCompat mMediaSession;

    /**
     * Button click listener callback
     */
    OnButtonClickListener mCallback;

    /**
     * Data binding object
     */
    FragmentVideoStepsBinding mBinding;

    /**
     * Variable to hold the ExoPlayer current position
     */
    private long mCurrentPosition;

    /**
     * Variable to hold the ExoPlayer current state
     */
    private boolean mCurrentState;

    /**
     * Variable that holds the video uri
     */
    private Uri mMediaUri;

    /**
     * Position of the element in the RecyclerView
     */
    private int mListIndex;

    /**
     * Variable to distinguish whether the device is a tablet or a phone
     */
    private boolean mTwoPane;

    /**
     * ExoPlayer object
     */
    private SimpleExoPlayer mExoPlayer;

    /**
     * PlaybackState builder
     */
    private PlaybackStateCompat.Builder mStateBuilder;

    /**
     * NotificationManager object
     */
    private NotificationManager mNotificationManager;

    /**
     * Recipe description
     */
    private String mDescription;

    /**
     * Recipe object
     */
    private Recipe mRecipe;

    /**
     * Current instance state
     */
    private Bundle mState;

    // Mandatory constructor for instantiating the fragment
    public VideoStepsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnButtonClickListener");
        }
    }

    /**
     * Set the index of the step clicked
     *
     * @param index
     */
    public void setListIndex(int index) {
        mListIndex = index;
    }

    /**
     * Check whether the tablet or phone is used
     *
     * @param twoPane is true when the tablet mode si detected
     */
    public void setTwoPane(boolean twoPane) {
        mTwoPane = twoPane;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_steps,
                container, false);
        mState = savedInstanceState;
        Bundle bundle = getArguments();
        mRecipe = (Recipe) bundle.getParcelable(EXTRA_KEY);

        if (savedInstanceState != null) {
            setListIndex(savedInstanceState.getInt(STEP_INDEX));
            mTwoPane = savedInstanceState.getBoolean(TABLET_STATE);
        }

        String url = mRecipe.getSteps().get(mListIndex).getVideoUrl();

        if (!TextUtils.isEmpty(url)) {

            // If there is a video, show it
            mBinding.noVideoImageView.setVisibility(View.GONE);
            mBinding.playerView.setVisibility(View.VISIBLE);

            mMediaUri = Uri.parse(url);
            // initialize MediaSession
            initializeMediaSession();

            if (!mTwoPane && getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is in the landscape view on the phone, resize video to full screen
                mBinding.playerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                mBinding.playerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        } else {
            // If there is no video, show ImageView
            mBinding.noVideoImageView.setVisibility(View.VISIBLE);
            // Load thumbnail if present
            mBinding.playerView.setVisibility(View.GONE);
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }

        mDescription = mRecipe.getSteps().get(mListIndex).getDescription();
        mBinding.stepDescriptionTextView.setText(mDescription);

        String thumbnailURL = mRecipe.getSteps().get(mListIndex).getThumbnailUrl();
        if (!TextUtils.isEmpty(thumbnailURL)) {
            Picasso.with(getContext())
                    .load(thumbnailURL)
                    .into(mBinding.thumbnailImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //picture loaded successfully
                            mBinding.thumbnailImageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            mBinding.thumbnailImageView.setVisibility(View.GONE);
                        }
                    });
        }

        if (mTwoPane) {
            // Hide the buttons for the tablets
            mBinding.prevButton.setVisibility(View.GONE);
            mBinding.nextButton.setVisibility(View.GONE);
        } else {
            // Show the buttons for the phones
            mBinding.prevButton.setVisibility(View.VISIBLE);
            mBinding.nextButton.setVisibility(View.VISIBLE);
            if (mListIndex == 0) {
                mBinding.prevButton.setVisibility(View.INVISIBLE);
            } else if (mListIndex == mRecipe.getSteps().size() - 1) {
                mBinding.nextButton.setVisibility(View.INVISIBLE);
            }
            // get the index of the previous step to be displayed
            mBinding.prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onButtonClick(mListIndex - 1);
                }
            });
            // get the index of the next step to be displayed
            mBinding.nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onButtonClick(mListIndex + 1);
                }
            });
        }
        return mBinding.getRoot();
    }

    /**
     * Handles device rotation to the landscape and back to portrait when the screen shows the video
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!mTwoPane && mExoPlayer != null) {
            // If the screen is in the landscape view on the phone, resize video to full screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                mBinding.prevButton.setVisibility(View.GONE);
                mBinding.nextButton.setVisibility(View.GONE);
                mBinding.stepDescriptionTextView.setVisibility(View.GONE);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                        mBinding.playerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                mBinding.playerView.setLayoutParams(params);
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Restore original settings
                mBinding.prevButton.setVisibility(View.VISIBLE);
                mBinding.nextButton.setVisibility(View.VISIBLE);
                if (mListIndex == 0) {
                    mBinding.prevButton.setVisibility(View.INVISIBLE);
                } else if (mListIndex == mRecipe.getSteps().size() - 1) {
                    mBinding.nextButton.setVisibility(View.INVISIBLE);
                }
                mBinding.stepDescriptionTextView.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                        mBinding.playerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = 0;
                params.weight = 3;
                mBinding.playerView.setLayoutParams(params);
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }
        }

    }


    /**
     * Saving the ExoPlayer state and position for screen rotations
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STEP_INDEX, mListIndex);
        outState.putLong(EXOPLAYER_POSITION, mCurrentPosition);
        outState.putBoolean(EXOPLAYER_STATE, mCurrentState);
        outState.putBoolean(TABLET_STATE, mTwoPane);
        mState = outState;
    }


    /**
     * Initializing ExoPlayer
     */
    private void initializePlayer() {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector,
                    loadControl);
            mBinding.playerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mMediaUri,
                    new DefaultDataSourceFactory(getContext(), userAgent),
                    new DefaultExtractorsFactory(), null, null);

            if (mState != null) {
                long restoredPosition = mState.getLong(EXOPLAYER_POSITION);
                boolean restoredState = mState.getBoolean(EXOPLAYER_STATE);
                mExoPlayer.prepare(mediaSource, false, false);
                mExoPlayer.seekTo(restoredPosition);
                mExoPlayer.setPlayWhenReady(restoredState);
            } else {
                mExoPlayer.prepare(mediaSource);
                mExoPlayer.setPlayWhenReady(true);
            }
        }
    }

    private void releasePlayer() {
        mNotificationManager.cancelAll();
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mCurrentPosition = mExoPlayer.getCurrentPosition();
            mCurrentState = mExoPlayer.getPlayWhenReady();
            releasePlayer();
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // initialize ExoPlayer or restore the state and the position
        if (mMediaUri != null) {
            initializePlayer();
        }
    }


    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    /**
     * Shows notification that the video is playing
     *
     * @param state
     */
    private void showNotification(PlaybackStateCompat state) {
        // Create the NotificationChannel for API 26+
        mNotificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),
                CHANNEL_ID);

        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = com.google.android.exoplayer2.R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);
        } else {
            icon = com.google.android.exoplayer2.R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(getContext(),
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new androidx.core.app.NotificationCompat
                .Action(com.google.android.exoplayer2.R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (getContext(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (getContext(), 0, new Intent(getContext(), VideoStepsFragment.class),
                        0);


        builder.setContentTitle(getString(R.string.notification_description))
                .setContentText(mDescription)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_music_note)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));

        mNotificationManager.notify(0, builder.build());
    }


    // ExoPlayer Event Listeners
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
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);

        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);

        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
        showNotification(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    public interface OnButtonClickListener {
        void onButtonClick(int position);
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
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
            mExoPlayer.seekTo(0);
        }
    }

}
