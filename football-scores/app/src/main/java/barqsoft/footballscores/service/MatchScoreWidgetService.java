package barqsoft.footballscores.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import barqsoft.footballscores.Compat;
import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.scoresAdapter;
import barqsoft.footballscores.widget.MatchScoreWidget;

/**
 * Populates match score widgets with data.
 * </p>
 * This is based on {@link scoresAdapter}
 *
 * @author Timur Calmatui
 * @since 2015-11-15
 */
public class MatchScoreWidgetService extends IntentService {

    private static final String TAG = MatchScoreWidgetService.class.getSimpleName();
    private static final boolean DEBUG = false;

    public MatchScoreWidgetService() {
        super("MatchScoreWidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (DEBUG) {
            Log.d(TAG, "onHandleIntent");
        }

        // Retrieve all of the widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, MatchScoreWidget.class));

        if (DEBUG) {
            Log.d(TAG, "appWidgetIds: " + Arrays.toString(appWidgetIds));
        }

        if (appWidgetIds.length == 0) {
            return;
        }

        // Get data from the ContentProvider
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(System.currentTimeMillis());
        Uri scoresForDateUri = DatabaseContract.scores_table.buildScoreWithDate();
        Cursor data = getContentResolver().query(scoresForDateUri, null, null,
                new String[]{date}, null);

        if (data == null || !data.moveToFirst()) {
            if (data != null) {
                data.close();
            }

            if (DEBUG) {
                Log.d(TAG, "no matches today => showing empty widget");
            }

            // Perform this loop procedure for each widget
            for (int appWidgetId : appWidgetIds) {
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.view_widget_empty);

                // Create an Intent to launch MainActivity
                Intent launchIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                views.setOnClickPendingIntent(R.id.text, pendingIntent);
                
                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }

            return;
        }

        // Extract the weather data from the Cursor
        String homeTeamName = data.getString(scoresAdapter.COL_HOME);
        String awayTeamName = data.getString(scoresAdapter.COL_AWAY);
        String matchTime = data.getString(scoresAdapter.COL_MATCHTIME);
        String score = Utilies.getScores(data.getInt(scoresAdapter.COL_HOME_GOALS),
                data.getInt(scoresAdapter.COL_AWAY_GOALS));
        int homeCrestResourceId = Utilies.getTeamCrestByTeamName(
                data.getString(scoresAdapter.COL_HOME));
        int awayCrestResourceId = Utilies.getTeamCrestByTeamName(
                data.getString(scoresAdapter.COL_AWAY));
        data.close();

        if (DEBUG) {
            Log.d(TAG, "showing data for date: " + date
                    + ", teams: " + homeTeamName + " - " + awayTeamName);
        }

        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_match_score);

            // Add the data to the RemoteViews
            views.setTextViewText(R.id.home_name, homeTeamName);
            Compat.setRemoteContentDescription(views, R.id.home_name,
                    getString(R.string.a11y_home_team_name, homeTeamName));

            views.setTextViewText(R.id.away_name, awayTeamName);
            Compat.setRemoteContentDescription(views, R.id.away_name,
                    getString(R.string.a11y_away_team_name, awayTeamName));

            views.setTextViewText(R.id.data_textview, matchTime);
            Compat.setRemoteContentDescription(views, R.id.data_textview,
                    Utilies.getMatchTimeContentDescription(getBaseContext(), matchTime));

            views.setTextViewText(R.id.score_textview, score);
            if (score.length() > 3) {
                Compat.setRemoteContentDescription(views, R.id.score_textview,
                        getString(R.string.a11y_score, score));
            }

            views.setImageViewResource(R.id.home_crest, homeCrestResourceId);
            views.setImageViewResource(R.id.away_crest, awayCrestResourceId);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.item_container, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
