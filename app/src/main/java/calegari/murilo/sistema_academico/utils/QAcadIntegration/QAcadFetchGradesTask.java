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

import calegari.murilo.sistema_academico.R;
import calegari.murilo.sistema_academico.databases.DatabaseHelper;
import calegari.murilo.sistema_academico.utils.Constants;
import calegari.murilo.qacadscrapper.QAcadScrapper;
import calegari.murilo.qacadscrapper.utils.Subject;
import calegari.murilo.qacadscrapper.utils.User;
import calegari.murilo.sistema_academico.utils.Tools;

public class QAcadFetchGradesTask extends AsyncTask<Integer, Integer, Integer>{

	private final String TAG = "QAcadFetchGradesTask";
	private WeakReference<Context> contextWeakReference;

	private Map<String, String> cookieMap;

	public QAcadFetchGradesTask(Context context, Map<String, String> cookieMap) {
		this(context);
		this.cookieMap = cookieMap;
	}

	public QAcadFetchGradesTask(Context context) {
		this.contextWeakReference = new WeakReference<>(context);
	}

	@Override
	protected Integer doInBackground(Integer... integers) {
		Log.d(TAG, "Called QAcadFetchGradesTask doInBackground");
		int result;

		Context context = contextWeakReference.get();

		if(context != null) {

			User user = LoginManager.getUser(context);
			user.setMultiThreadEnabled(true);

			QAcadScrapper qAcadScrapper = new QAcadScrapper(Constants.QAcad.ACADEMIC_URL, user);

			DatabaseHelper databaseHelper = new DatabaseHelper(context);

			try {
				qAcadScrapper.setCookieMap(cookieMap);

				Log.d(TAG, "Getting all subjects and grades from QAcad");
				List<Subject> subjectList = qAcadScrapper.getAllSubjectsAndGrades();
				Log.d(TAG, "All subjects and grades from QAcad were obtained!");

				if(!isCancelled()) {
					databaseHelper.updateSubjectsDatabase(subjectList);
					cookieMap = qAcadScrapper.getCookieMap();
					result = Constants.QAcad.RESULT_SUCCESS;
				} else {
					result = Constants.QAcad.RESULT_CANCELLED;
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
				result = Constants.QAcad.RESULT_UNKNOWN_ERROR;
				Tools.displaySnackBarBackOnActivity(context, R.string.unknown_error);
			}

			databaseHelper.close();
		} else {
			result = Constants.QAcad.RESULT_CANCELLED;
		}

		return result;
	}

	public Map<String, String> getCookieMap() {
		return cookieMap;
	}

	public void setCookieMap(Map<String, String> cookieMap) {
		this.cookieMap = cookieMap;
	}
}
