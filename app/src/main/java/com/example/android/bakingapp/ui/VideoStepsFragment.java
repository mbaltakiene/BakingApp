package com.example.android.bakingapp.ui;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp.R;
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

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
     * MediaSession
     */
    private static MediaSessionCompat mMediaSession;
    /**
     * Button click listener callback
     */
    OnButtonClickListener mCallback;
    /**
     * ExoPlayer display view
     */
    @BindView(R.id.player_view)
    SimpleExoPlayerView mPlayerView;
    /**
     * Button to navigate to previous step
     */
    @BindView(R.id.prev_button)
    Button mPrevButton;
    /**
     * Button to navigate to the next step
     */
    @BindView(R.id.next_button)
    Button mNextButton;
    /**
     * Step description TextView
     */
    @BindView(R.id.step_description_text_view)
    TextView mDescriptionTV;
    /**
     * Thumbnail image ImageView
     */
    @BindView(R.id.thumbnail_image_view)
    ImageView mThumbnailImageView;
    /**
     * ImageView when no video preview is available
     */
    @BindView(R.id.no_video_image_view)
    ImageView mNoVideoImageView;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mState = savedInstanceState;

        View rootView = inflater.inflate(R.layout.fragment_video_steps, container, false);
        Bundle bundle = getArguments();
        mRecipe = (Recipe) bundle.getParcelable(EXTRA_KEY);

        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            setListIndex(savedInstanceState.getInt(STEP_INDEX));
            mTwoPane = savedInstanceState.getBoolean(TABLET_STATE);
        }

        String url = mRecipe.getSteps().get(mListIndex).getVideoUrl();

        if (!TextUtils.isEmpty(url)) {


            // If there is a video, show it
            mNoVideoImageView.setVisibility(View.GONE);
            mPlayerView.setVisibility(View.VISIBLE);

            mMediaUri = Uri.parse(url);
            // initialize MediaSession
            initializeMediaSession();

            if (!mTwoPane && getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is in the landscape view on the phone, resize video to full screen
                mPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                mPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
        } else {
            // If there is no video, show ImageView
            mNoVideoImageView.setVisibility(View.VISIBLE);
            // Load thumbnail if present
            mPlayerView.setVisibility(View.GONE);
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }

        mDescription = mRecipe.getSteps().get(mListIndex).getDescription();
        mDescriptionTV.setText(mDescription);

        String thumbnailURL = mRecipe.getSteps().get(mListIndex).getThumbnailUrl();
        if (!TextUtils.isEmpty(thumbnailURL)) {
            Picasso.with(getContext())
                    .load(thumbnailURL)
                    .into(mThumbnailImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //picture loaded successfully
                            mThumbnailImageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            mThumbnailImageView.setVisibility(View.GONE);
                        }
                    });
        }

        if (mTwoPane) {
            // Hide the buttons for the tablets
            mPrevButton.setVisibility(View.GONE);
            mNextButton.setVisibility(View.GONE);
        } else {
            // Show the buttons for the phones
            mPrevButton.setVisibility(View.VISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
            if (mListIndex == 0) {
                mPrevButton.setVisibility(View.INVISIBLE);
            } else if (mListIndex == mRecipe.getSteps().size() - 1) {
                mNextButton.setVisibility(View.INVISIBLE);
            }
            // get the index of the previous step to be displayed
            mPrevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onButtonClick(mListIndex - 1);
                }
            });
            // get the index of the next step to be displayed
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onButtonClick(mListIndex + 1);
                }
            });
        }
        return rootView;
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

                mPrevButton.setVisibility(View.GONE);
                mNextButton.setVisibility(View.GONE);
                mDescriptionTV.setVisibility(View.GONE);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                mPlayerView.setLayoutParams(params);
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Restore original settings
                mPrevButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
                if (mListIndex == 0) {
                    mPrevButton.setVisibility(View.INVISIBLE);
                } else if (mListIndex == mRecipe.getSteps().size() - 1) {
                    mNextButton.setVisibility(View.INVISIBLE);
                }
                mDescriptionTV.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = 0;
                params.weight = 3;
                mPlayerView.setLayoutParams(params);
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
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mMediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);

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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "channelID");

        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(getContext(),
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
                .Action(R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (getContext(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (getContext(), 0, new Intent(getContext(), VideoStepsFragment.class), 0);

        builder.setContentTitle(getString(R.string.notification_description))
                .setContentText(mDescription)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_music_note)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));


        mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    /**
     * +     * Shows Media Style notification, with an action that depends on the current MediaSession
     * +     * PlaybackState.
     * +     * @param state The PlaybackState of the MediaSession.
     * +
     */

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    // ExoPlayer Event Listeners

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
        String test = Boolean.toString(playWhenReady) + ", " + playbackState;
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
