package calegari.murilo.sistema_academico;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		//Stetho.initializeWithDefaults(this);
	}
}
