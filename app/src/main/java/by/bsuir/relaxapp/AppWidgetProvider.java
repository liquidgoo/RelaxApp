package by.bsuir.relaxapp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Track;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    enum PLAYER_STATE {GONNA_PLAY, GONNA_RESUME, GONNA_PAUSE}

    ;
    private static PLAYER_STATE currState = PLAYER_STATE.GONNA_PLAY;

    private static int CURR_PLAYLIST_INDEX = 0;

    private static final String MyOnClick0 = "0";
    private static final String MyOnClick1 = "1";
    private static final String MyOnClick2 = "2";
    private static final String MyOnClick3 = "3";
    private static final String MyOnClick4 = "4";
    private static final String MyOnClick5 = "5";

    private static final String prevSong = "previous";
    private static final String stopPlaySong = "stopPlay";
    private static final String nextSong = "next";

    private static String songName = "Artist - Song";

    private RemoteViews remoteViews;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context, AppWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            remoteViews.setTextViewText(R.id.songArtistWidget, songName);

            if (currState == PLAYER_STATE.GONNA_PLAY || currState == PLAYER_STATE.GONNA_RESUME) {
                remoteViews.setImageViewResource(R.id.playStopSongWidget, R.drawable.ic_play_arrow);
            } else {
                remoteViews.setImageViewResource(R.id.playStopSongWidget, R.drawable.ic_pause);
            }

            remoteViews.setOnClickPendingIntent(R.id.calmWidget, getPendingSelfIntent(context, MyOnClick0));
            remoteViews.setOnClickPendingIntent(R.id.relaxWidget, getPendingSelfIntent(context, MyOnClick1));
            remoteViews.setOnClickPendingIntent(R.id.focusWidget, getPendingSelfIntent(context, MyOnClick2));
            remoteViews.setOnClickPendingIntent(R.id.excitedWidget, getPendingSelfIntent(context, MyOnClick3));
            remoteViews.setOnClickPendingIntent(R.id.authenticWidget, getPendingSelfIntent(context, MyOnClick4));


            remoteViews.setOnClickPendingIntent(R.id.previousSongWidget, getPendingSelfIntent(context, prevSong));
            remoteViews.setOnClickPendingIntent(R.id.playStopSongWidget, getPendingSelfIntent(context, stopPlaySong));
            remoteViews.setOnClickPendingIntent(R.id.nextSongWidget, getPendingSelfIntent(context, nextSong));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ComponentName thisWidget = new ComponentName(context, AppWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);


        if (MyOnClick0.equals(intent.getAction())) {
            CURR_PLAYLIST_INDEX = 0;
            currState = PLAYER_STATE.GONNA_PLAY;
            if (mSpotifyAppRemote != null) {
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        } else if (MyOnClick1.equals(intent.getAction())) {
            CURR_PLAYLIST_INDEX = 1;
            currState = PLAYER_STATE.GONNA_PLAY;
            if (mSpotifyAppRemote != null) {
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        } else if (MyOnClick2.equals(intent.getAction())) {
            CURR_PLAYLIST_INDEX = 2;
            currState = PLAYER_STATE.GONNA_PLAY;
            if (mSpotifyAppRemote != null) {
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        } else if (MyOnClick3.equals(intent.getAction())) {
            CURR_PLAYLIST_INDEX = 3;
            currState = PLAYER_STATE.GONNA_PLAY;
            if (mSpotifyAppRemote != null) {
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        } else if (MyOnClick4.equals(intent.getAction())) {
            CURR_PLAYLIST_INDEX = 4;
            currState = PLAYER_STATE.GONNA_PLAY;
            if (mSpotifyAppRemote != null) {
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        } else if (MyOnClick5.equals(intent.getAction())) {
            CURR_PLAYLIST_INDEX = 5;
            currState = PLAYER_STATE.GONNA_PLAY;
            if (mSpotifyAppRemote != null) {
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        } else if (prevSong.equals(intent.getAction())) {
            if (mSpotifyAppRemote != null) {
                mSpotifyAppRemote.getPlayerApi().skipPrevious();
            }
        } else if (stopPlaySong.equals(intent.getAction())) {

            if (currState == PLAYER_STATE.GONNA_PLAY) {
                currState = PLAYER_STATE.GONNA_PAUSE;

                getSpotifyAccess(context, appWidgetManager, appWidgetIds);
            } else if (currState == PLAYER_STATE.GONNA_PAUSE) {
                currState = PLAYER_STATE.GONNA_RESUME;

                mSpotifyAppRemote.getPlayerApi().pause();
            } else if (currState == PLAYER_STATE.GONNA_RESUME) {
                currState = PLAYER_STATE.GONNA_PAUSE;

                mSpotifyAppRemote.getPlayerApi().resume();
            }


        } else if (nextSong.equals(intent.getAction())) {
            if (mSpotifyAppRemote != null) {
                mSpotifyAppRemote.getPlayerApi().skipNext();
            }
        } else {
            super.onReceive(context, intent);
        }


        onUpdate(context, appWidgetManager, appWidgetIds);
    }


    private void getSpotifyAccess(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        connected(context, appWidgetManager, appWidgetIds);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                    }
                });
    }


    private void connected(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mSpotifyAppRemote.getPlayerApi().play(SUPP_PLAYLIST_PLAY_STR + PLAYLIST_IDS[CURR_PLAYLIST_INDEX]);

        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        songName = track.artist.name + " - " + track.name;
                        onUpdate(context, appWidgetManager, appWidgetIds);
                    }
                });
    }


    private static final String CLIENT_ID = "0725108c5d5c415688516442fc114295";
    private static final String REDIRECT_URI = "https://localhost:8080";
    private static SpotifyAppRemote mSpotifyAppRemote = null;

    public static final String SUPP_PLAYLIST_PLAY_STR = "spotify:playlist:";
    public static final String[] PLAYLIST_IDS = new String[]
            {"3z6x5IHQCgKiEHwZvNsAxW",
                    "1ideWr9gUZPVh9Jzgy0oKX",
                    "6GVGBXMovkEGSES9S13ino",
                    "2Smq4YoqQ4OK4p0fdCDkRO",
                    "0picEkE6rbSiYiqjxv5NcA"};
}
