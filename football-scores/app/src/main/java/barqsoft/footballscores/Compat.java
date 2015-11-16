package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

/**
 * @author Timur Calmatui
 * @since 2015-11-16.
 */
public class Compat {
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static void setRemoteContentDescription(RemoteViews views, int viewId, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            views.setContentDescription(viewId, description);
        }
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setRemoteAdapter(Context context, @NonNull final RemoteViews views,
                                  int adapterViewId, Class<?> cls) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            views.setRemoteAdapter(adapterViewId, new Intent(context, cls));
        } else {
            //noinspection deprecation
            views.setRemoteAdapter(0, adapterViewId, new Intent(context, cls));
        }
    }
}
