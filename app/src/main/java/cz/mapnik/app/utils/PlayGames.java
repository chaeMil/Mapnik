package cz.mapnik.app.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import cz.mapnik.app.R;

public class PlayGames {

    public static void unlockAchievement(GoogleApiClient client, String achievementId) {
        if(client != null && client.isConnected()) {
            Games.Achievements.unlock(client, achievementId);
        } else {
            /* TODO Alternative implementation (or warn user that they must
            sign in to use this feature) */
        }
    }

    public static void submitHighScore(GoogleApiClient client, String leaderboard, int score) {
        if(client != null && client.isConnected()) {
            Games.Leaderboards.submitScore(client, leaderboard, score);
        } else {
            /* TODO Alternative implementation (or warn user that they must
            sign in to use this feature) */
        }
    }

    public static Dialog signinDisabledByUser(final Activity a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(R.string.signin_failure)
                .setMessage(R.string.sign_in_explanation)
                .setCancelable(false)
                .setNegativeButton(a.getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                a.finish();
                            }
                        });

        return builder.create();
    }

}
