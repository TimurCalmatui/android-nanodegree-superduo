package barqsoft.footballscores.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Locale;

import barqsoft.footballscores.Compat;
import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.scoresAdapter;

/**
 * Populates today matches widgets with data.
 * </p>
 * This is based on {@link scoresAdapter}
 *
 * @author Timur Calmatui
 * @since 2015-11-16
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TodayMatchesWidgetRemoteViewsService extends RemoteViewsService {

    private static final boolean DEBUG = false;
    private static final String TAG = TodayMatchesWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        if (DEBUG) {
            Log.d(TAG, "onGetViewFactory");
        }

        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                if (DEBUG) {
                    Log.d(TAG, "Factory onCreate");
                }
            }

            @Override
            public void onDataSetChanged() {

                if (DEBUG) {
                    Log.d(TAG, "Factory onDataSetChanged");
                }

                if (data != null) {
                    data.close();
                    data = null;
                }

                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                // Get data from the ContentProvider
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        .format(System.currentTimeMillis());
                Uri scoresForDateUri = DatabaseContract.scores_table.buildScoreWithDate();
                data = getContentResolver().query(scoresForDateUri, null, null,
                        new String[]{date}, null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (DEBUG) {
                    Log.d(TAG, "Factory onDestroy");
                }

                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (DEBUG) {
                    Log.d(TAG, "Factory getViewAt " + position);
                }

                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.view_widget_score);

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

                if (DEBUG) {
                    Log.d(TAG, "showing data for position: " + position
                            + ", teams: " + homeTeamName + " - " + awayTeamName);
                }

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

                views.setOnClickFillInIntent(R.id.item_container, new Intent());

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.view_widget_score);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return 0;
                }

                return data.getLong(data.getColumnIndex((DatabaseContract.scores_table._ID)));
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
