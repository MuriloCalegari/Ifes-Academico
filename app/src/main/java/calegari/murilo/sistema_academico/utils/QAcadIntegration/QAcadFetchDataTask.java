package calegari.murilo.sistema_academico.utils.QAcadIntegration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import calegari.murilo.sistema_academico.R;
import calegari.murilo.sistema_academico.databases.DatabaseHelper;
import calegari.murilo.sistema_academico.utils.Constants;
import calegari.murilo.qacadscrapper.QAcadScrapper;
import calegari.murilo.qacadscrapper.utils.Subject;
import calegari.murilo.qacadscrapper.utils.User;

public class QAcadFetchDataTask extends AsyncTask<Integer, Integer, Integer>{

	private final String TAG = "QAcadFetchDataTask";
	private Context context;

	private Map<String, String> cookieMap;

	public QAcadFetchDataTask(Context context, Map<String, String> cookieMap) {
		this(context);
		this.cookieMap = cookieMap;
	}

	public QAcadFetchDataTask(Context context) {
		this.context = context;
	}

	@Override
	protected Integer doInBackground(Integer... integers) {
		Log.d(TAG, "Called QAcadFetchDataTask doInBackground");

		SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Keys.QACAD_USER_INFO_PREFERENCES, Context.MODE_PRIVATE);

		String username = sharedPreferences.getString(Constants.Keys.QACAD_USERNAME_PREFERENCE, "");
		String password = sharedPreferences.getString(Constants.Keys.QACAD_PASSWORD_PREFERENCE, "");
		User user = new User(username, password);
		user.setMultiThreadEnabled(true);

		QAcadScrapper qAcadScrapper = new QAcadScrapper(Constants.QAcad.ACADEMIC_URL, user);

		DatabaseHelper databaseHelper = new DatabaseHelper(context);

		int result;
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
			if(context instanceof Activity) {
				try {
					View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
					Snackbar snackbar = Snackbar.make(rootView, context.getString(R.string.connection_failure), Snackbar.LENGTH_LONG);
					snackbar.show();
				} catch (Exception ignored) {} // Just in case something bad happens when trying to show things on the activity
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = Constants.QAcad.RESULT_UNKNOWN_ERROR;
			if(context instanceof Activity) {
				try {
					View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
					Snackbar snackbar = Snackbar.make(rootView, context.getString(R.string.unknown_error), Snackbar.LENGTH_LONG);
					snackbar.show();
				} catch (Exception ignored) {}
			}
		} finally {
			databaseHelper.close();
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
