package calegari.murilo.ifes_academico.utils.QAcadIntegration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import calegari.murilo.ifes_academico.LoginActivity;
import calegari.murilo.ifes_academico.MainActivity;
import calegari.murilo.ifes_academico.databases.DatabaseHelper;
import calegari.murilo.ifes_academico.utils.Constants;

public class LoginManager {

    private static String TAG = "LoginManager";

    public static boolean isLogged(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Keys.QACAD_USER_INFO_PREFERENCES, Context.MODE_PRIVATE);

        return !sharedPreferences.contains(Constants.Keys.QACAD_USERNAME_PREFERENCE) && !sharedPreferences.contains(Constants.Keys.QACAD_PASSWORD_PREFERENCE);
    }

    public static void logout(Context context) {
        if(context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            if(activity.qAcadFetchDataTask != null) {
                Log.d(TAG, "logout: Cancelling qAcadFetchDataTask");
                activity.qAcadFetchDataTask.cancel(false);
            }
        }

        SharedPreferences.Editor sharedPreferences = context.getSharedPreferences(Constants.Keys.QACAD_USER_INFO_PREFERENCES, Context.MODE_PRIVATE).edit();
        sharedPreferences.remove(Constants.Keys.QACAD_USERNAME_PREFERENCE);
        sharedPreferences.remove(Constants.Keys.QACAD_PASSWORD_PREFERENCE);
        sharedPreferences.remove(Constants.Keys.APP_USERNAME_PREFERENCE);
        sharedPreferences.apply();

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.recreateDatabase(); // Clear databases
        databaseHelper.close();

        MainActivity.qAcadCookieMap = null; // Clear cookies

        Intent loginIntent = new Intent(context, LoginActivity.class);
        context.startActivity(loginIntent);
        ((Activity) context).finish();
    }
}
