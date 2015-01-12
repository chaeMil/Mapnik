package cz.mapnik.app.utils;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class PlayGames {

    public static void unlockAchievement(GoogleApiClient client, String achievementId) {
        if(client != null) {
            Games.Achievements.unlock(client, achievementId);
        } else {
            /* TODO Alternative implementation (or warn user that they must
            sign in to use this feature) */
        }
    }

    public static void submitHighScore(GoogleApiClient client, String leaderboard, int score) {
        if(client != null) {
            Games.Leaderboards.submitScore(client, leaderboard, score);
        } else {
            /* TODO Alternative implementation (or warn user that they must
            sign in to use this feature) */
        }
    }

}
