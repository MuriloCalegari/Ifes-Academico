package calegari.murilo.sistema_academico;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;

import calegari.murilo.sistema_academico.utils.Constants;
import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {
	public void onCreate() {
		super.onCreate();

		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.Keys.IS_DATA_COLLECTION_AUTHORIZED, false)) {
			Fabric.with(this, new Crashlytics());
			FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
		}

		Stetho.initializeWithDefaults(this);
	}
}
