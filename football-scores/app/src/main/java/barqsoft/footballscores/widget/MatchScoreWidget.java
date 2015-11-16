package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import barqsoft.footballscores.service.MatchScoreWidgetService;
import barqsoft.footballscores.service.myFetchService;

/**
 * Shows information about today's first match.
 *
 * @author Timur Calmatui
 * @since 2015-11-15
 */
public class MatchScoreWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, myFetchService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, myFetchService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (myFetchService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, MatchScoreWidgetService.class));
        }
    }

    @Override
    public void onEnabled(Context context) {
        context.startService(new Intent(context, myFetchService.class));
    }
}
