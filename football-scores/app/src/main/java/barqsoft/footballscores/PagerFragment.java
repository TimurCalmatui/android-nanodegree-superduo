package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    public static final int NUM_PAGES = 12;
    public static final String STATE_RTL = "rtl";
    public ViewPager mPagerHandler;
    private boolean mIsRTL;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        myPageAdapter mPagerAdapter = new myPageAdapter(getChildFragmentManager());

        mIsRTL = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration config = getResources().getConfiguration();
            if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                mIsRTL = true;
            }
        }

        mPagerHandler.setAdapter(mPagerAdapter);
        setInitialPosition();
        
        return rootView;
    }

    private void setInitialPosition() {
        if (mIsRTL) {
            mPagerHandler.setCurrentItem(NUM_PAGES - 3);
        } else {
            mPagerHandler.setCurrentItem(2);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RTL, mIsRTL);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_RTL) != mIsRTL) {
            // restart the app to reinitialize view pager for new RTL setting
            // (there are other ways to do it to better preserve current state
            // but this is irrelevant since users don't often change RTL setting)
            Intent i = getActivity().getPackageManager()
                    .getLaunchIntentForPackage(getActivity().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    private class myPageAdapter extends FragmentStatePagerAdapter
    {
        @Override
        public Fragment getItem(int i)
        {
            int day = mIsRTL ? NUM_PAGES - i - 3 : i - 2;
            Date fragmentDate = new Date(System.currentTimeMillis() + (day * 86400000));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            MainScreenFragment fragment = new MainScreenFragment();
            fragment.setFragmentDate(format.format(fragmentDate));

            return fragment;
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        public myPageAdapter(FragmentManager fm)
        {
            super(fm);
        }
        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position)
        {
            int day = mIsRTL ? NUM_PAGES - position - 3 : position - 2;
            return getDayName(getActivity(), System.currentTimeMillis() + (day * 86400000));
        }
        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                return context.getString(R.string.tomorrow);
            }
             else if ( julianDay == currentJulianDay -1)
            {
                return context.getString(R.string.yesterday);
            }
            else
            {
                // Otherwise, the format is the day of the week, month and day
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    return DateFormat.format(
                            DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEE, MMM dd"),
                            dateInMillis)
                            .toString();
                } else {
                    SimpleDateFormat format = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
                    if (DateFormat.getDateFormatOrder(getActivity())[0] == 'd') {
                        format = new SimpleDateFormat("EEEE, dd MMM", Locale.getDefault());
                    }

                    String date = format.format(dateInMillis);
                    return Character.toUpperCase(date.charAt(0)) + date.substring(1);
                }
            }
        }
    }
}
