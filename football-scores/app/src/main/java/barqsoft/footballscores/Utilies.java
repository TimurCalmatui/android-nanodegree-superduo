package barqsoft.footballscores;

import android.content.Context;
import android.support.annotation.StringRes;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies
{
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;
    
    public static @StringRes int getLeague(int league_num)
    {
        switch (league_num)
        {
            case SERIE_A : return  R.string.league_seria_a;
            case PREMIER_LEGAUE : return R.string.league_premier;
            case CHAMPIONS_LEAGUE : return R.string.league_uefa_champions;
            case PRIMERA_DIVISION : return  R.string.league_primera_division;
            case BUNDESLIGA : return R.string.league_bundesliga;
            default: return R.string.league_unknown;
        }
    }
    
    public static String getMatchDay(Context context, int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return context.getString(R.string.match_day_group);
            }
            else if(match_day == 7 || match_day == 8)
            {
                return context.getString(R.string.match_day_first_knockout);
            }
            else if(match_day == 9 || match_day == 10)
            {
                return context.getString(R.string.match_day_quarter_final);
            }
            else if(match_day == 11 || match_day == 12)
            {
                return context.getString(R.string.match_day_semi_final);
            }
            else
            {
                return context.getString(R.string.match_day_final);
            }
        }
        else
        {
            return context.getString(R.string.match_day_day, match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }

    public static String getMatchTimeContentDescription(Context context, String matchTime) {
        String[] parts = matchTime.split(":", 2);
        int hours = -1;
        int minutes = -1;
        if (parts.length == 2) {
            try {
                hours = Integer.parseInt(parts[0]);
                minutes = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignore) {
            }
        }

        if (hours != -1 && minutes != -1) {
            return context.getString(R.string.a11y_match_time_hours_minutes, hours, minutes);
        } else {
            return context.getString(R.string.a11y_match_time, matchTime);
        }
    }
}
