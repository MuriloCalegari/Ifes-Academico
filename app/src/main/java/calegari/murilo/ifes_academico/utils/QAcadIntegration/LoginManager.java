package calegari.murilo.ifes_academico.utils.QAcadIntegration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import calegari.murilo.ifes_academico.LoginActivity;
import calegari.murilo.ifes_academico.MainActivity;
import calegari.murilo.ifes_academico.databases.DatabaseHelper;
import calegari.murilo.ifes_academico.utils.Constants;

public class LoginManager {

    public static void logout(Context context) {
        SharedPreferences.Editor sharedPreferences = context.getSharedPreferences(Constants.Keys.USER_INFO_PREFERENCES, Context.MODE_PRIVATE).edit();
        sharedPreferences.remove(Constants.Keys.USERNAME_PREFERENCE);
        sharedPreferences.remove(Constants.Keys.PASSWORD_PREFERENCE);
        sharedPreferences.apply();

        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.recreateDatabases(); // Clear databases

        MainActivity.qAcadCookieMap = null; // Clear cookies

        Intent loginIntent = new Intent(context, LoginActivity.class);
        context.startActivity(loginIntent);
        ((Activity) context).finish();
    }
}
