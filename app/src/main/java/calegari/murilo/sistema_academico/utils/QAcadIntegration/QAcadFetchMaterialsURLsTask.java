package calegari.murilo.sistema_academico.utils.QAcadIntegration;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import calegari.murilo.qacadscrapper.QAcadScrapper;
import calegari.murilo.qacadscrapper.utils.ClassMaterial;
import calegari.murilo.qacadscrapper.utils.User;
import calegari.murilo.sistema_academico.R;
import calegari.murilo.sistema_academico.databases.DatabaseHelper;
import calegari.murilo.sistema_academico.utils.Constants;
import calegari.murilo.sistema_academico.utils.Tools;

import static calegari.murilo.sistema_academico.utils.Constants.QAcad.ACADEMIC_URL;
import static calegari.murilo.sistema_academico.utils.Constants.QAcad.RESULT_CANCELLED;
import static calegari.murilo.sistema_academico.utils.Constants.QAcad.RESULT_SUCCESS;

public class QAcadFetchMaterialsURLsTask extends AsyncTask<Integer, Integer, Integer> {

	private final String TAG = getClass().getSimpleName();
	private WeakReference<Context> contextWeakReference;

	private Map<String, String> cookieMap;

	private int result;

	public QAcadFetchMaterialsURLsTask(Context context, Map<String, String> cookieMap) {
		this(context);
		this.cookieMap = cookieMap;
	}

	public QAcadFetchMaterialsURLsTask(Context context) {
		this.contextWeakReference = new WeakReference<>(context);
	}

	@Override
	protected Integer doInBackground(Integer... integers) {
		Log.d(TAG, "Called QAcadFetchMaterialsURLsTask doInBackground");

		Context context = contextWeakReference.get();

		if(context != null) {

			User user = LoginManager.getUser(context);
			user.setMultiThreadEnabled(true);

			QAcadScrapper qAcadScrapper = new QAcadScrapper(ACADEMIC_URL, user);
			qAcadScrapper.setCookieMap(cookieMap);

			DatabaseHelper databaseHelper = new DatabaseHelper(context);

			try {
				Log.d(TAG, "Getting all materials from QAcad");
				List<ClassMaterial> materials = qAcadScrapper.getAllMaterials();
				Log.d(TAG, "All materials were obtained");

				if(!isCancelled()) {
					databaseHelper.updateMaterialsDatabase(materials);
					cookieMap = qAcadScrapper.getCookieMap();
					result = RESULT_SUCCESS;
				} else {
					result = RESULT_CANCELLED;
				}

			} catch (LoginException e) {
				LoginManager.logout(context); // Logout if calling loginToQACad() failed
				result = Constants.QAcad.RESULT_LOGIN_INVALID;
			} catch (IOException e) {
				result = Constants.QAcad.RESULT_CONNECTION_FAILURE;
				Tools.displaySnackBarBackOnActivity(context, R.string.connection_failure);
			} catch (Exception e) {
				e.printStackTrace();
				Crashlytics.logException(e);
				Tools.displaySnackBarBackOnActivity(context, R.string.unknown_error);
			}

			databaseHelper.close();

		}

		return result;
	}

	public Map<String, String> getCookieMap() {
		return cookieMap;
	}
}
