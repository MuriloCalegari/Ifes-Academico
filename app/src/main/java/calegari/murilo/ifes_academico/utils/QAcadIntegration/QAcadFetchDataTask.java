package calegari.murilo.ifes_academico.utils.QAcadIntegration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import calegari.murilo.ifes_academico.R;
import calegari.murilo.ifes_academico.databases.DatabaseHelper;
import calegari.murilo.ifes_academico.utils.Constants;
import calegari.murilo.qacadscrapper.QAcadScrapper;
import calegari.murilo.qacadscrapper.utils.Subject;
import calegari.murilo.qacadscrapper.utils.User;

public class QAcadFetchDataTask extends AsyncTask<Integer, Integer, Integer>{

	private final String TAG = "QAcadFetchDataTask";
	private Context context;
	private QAcadScrapper qAcadScrapper;

	private int result;

	private Map<String, String> cookieMap;

	public QAcadFetchDataTask(Context context, Map<String, String> cookieMap) {
		this(context);
		this.cookieMap = cookieMap;
	}

	public QAcadFetchDataTask(Context context) {
		this.context = context;
		qAcadScrapper = new QAcadScrapper(Constants.QAcad.ACADEMIC_URL);
	}

	@Override
	protected Integer doInBackground(Integer... integers) {
		Log.d(TAG, "Called QAcadFetchDataTask doInBackground");

		SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Keys.USER_INFO_PREFERENCES, Context.MODE_PRIVATE);

		String username = sharedPreferences.getString(Constants.Keys.USERNAME_PREFERENCE, "");
		String password = sharedPreferences.getString(Constants.Keys.PASSWORD_PREFERENCE, "");

		User user = new User(username, password);
		DatabaseHelper databaseHelper = new DatabaseHelper(context);

		try {
			qAcadScrapper.setCookieMap(cookieMap);
			if (!qAcadScrapper.isLogged()) {
				Log.d(TAG, "Status is not logged");
				cookieMap = qAcadScrapper.loginToQAcad(user);
				Log.d(TAG, "Finished logging into QAcad");
			} else {
				Log.d(TAG, "We've already logged");
			}

			Log.d(TAG, "Getting all subjects and grades from QAcad");
			List<Subject> subjectList = qAcadScrapper.getAllSubjectsAndGrades();
			Log.d(TAG, "All subjects and grades from QAcad were obtained!");

			if(!isCancelled()) {
				databaseHelper.recreateSubjectsAndGradesTables();

				databaseHelper.insertQAcadSubjectList(subjectList);

				result = Constants.QAcad.RESULT_SUCCESS;
			} else {
				result = Constants.QAcad.RESULT_CANCELLED;
			}
		} catch (LoginException e) {
			LoginManager.logout(context); // Logout if calling loginToQACad() failed
			result = Constants.QAcad.RESULT_LOGIN_INVALID;
		} catch (ConnectException e) {
			result = Constants.QAcad.RESULT_CONNECTION_FAILURE;
			View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
			Snackbar snackbar = Snackbar.make(rootView, context.getString(R.string.connection_failure) ,Snackbar.LENGTH_LONG);
			snackbar.show();
		} catch (Exception e) {
			result = Constants.QAcad.RESULT_UNKNOWN_ERROR;
			View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
			Snackbar snackbar = Snackbar.make(rootView, context.getString(R.string.unknown_error) ,Snackbar.LENGTH_LONG);
			snackbar.show();
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
