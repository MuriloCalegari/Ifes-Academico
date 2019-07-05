package calegari.murilo.sistema_academico;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import calegari.murilo.sistema_academico.utils.Constants;
import calegari.murilo.sistema_academico.utils.QAcadIntegration.LoginManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;

        if(LoginManager.isLogged(this)) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.Keys.SHOULD_SYNC_GRADES, true);
            intent.putExtra(Constants.Keys.SHOULD_SYNC_MATERIALS, true);
        }

        startActivity(intent);
        finish();
    }
}