package calegari.murilo.sistema_academico;

import android.app.Application;
import androidx.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import calegari.murilo.sistema_academico.utils.Constants;
import calegari.murilo.sistema_academico.utils.QAcadIntegration.LoginManager;
import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {
	public void onCreate() {
		super.onCreate();

		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.Keys.IS_DATA_COLLECTION_AUTHORIZED, false)) {
			Fabric.with(this, new Crashlytics());
			FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
			if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.Keys.IS_USERNAME_COLLECTION_AUTHORIZED, false)) {
				Crashlytics.setUserIdentifier(LoginManager.getUserId(this));
			}
		}

		//Stetho.initializeWithDefaults(this);
	}
}
