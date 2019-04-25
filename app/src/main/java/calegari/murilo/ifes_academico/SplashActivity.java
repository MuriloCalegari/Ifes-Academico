package calegari.murilo.ifes_academico;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import calegari.murilo.ifes_academico.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SharedPreferencesKeys.USER_INFO_PREFERENCES, MODE_PRIVATE);
        boolean isLoginNull = !sharedPreferences.contains(Constants.SharedPreferencesKeys.USERNAME_PREFERENCE) && !sharedPreferences.contains(Constants.SharedPreferencesKeys.PASSWORD_PREFERENCE);

        Intent intent;

        if(isLoginNull) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}