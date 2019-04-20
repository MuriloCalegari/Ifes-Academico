package calegari.murilo.ifes_academico.utils.QAcadIntegration;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Map;

import calegari.murilo.ifes_academico.databases.DatabaseHelper;
import calegari.murilo.ifes_academico.subjectgrades.SubjectGrade;
import calegari.murilo.ifes_academico.utils.Constants;
import calegari.murilo.qacadscrapper.QAcadScrapper;
import calegari.murilo.qacadscrapper.utils.Grade;
import calegari.murilo.qacadscrapper.utils.Subject;
import calegari.murilo.qacadscrapper.utils.User;

public class QAcadFetchDataTask extends AsyncTask<Integer, Integer, Void>{

	private final String TAG = "QAcadFetchDataTask";
	private Context context;
	private QAcadScrapper qAcadScrapper;

	private Map<String, String> cookieMap;

	public QAcadFetchDataTask(Context context, Map<String, String> cookieMap) {
		this(context);
		this.cookieMap = cookieMap;
	}

	public QAcadFetchDataTask(Context context) {
		this.context = context;
		qAcadScrapper = new QAcadScrapper(Constants.ACADEMIC_URL);
	}

	@Override
	protected Void doInBackground(Integer... integers) {
		Log.d(TAG, "Called QAcadFetchDataTask doInBackground");

		SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.USER_INFO_PREFERENCES, Context.MODE_PRIVATE);

		String username = sharedPreferences.getString(Constants.USERNAME_PREFERENCE, "");
		String password = sharedPreferences.getString(Constants.PASSWORD_PREFERENCE, "");

		User user = new User(username, password);
		DatabaseHelper databaseHelper = new DatabaseHelper(context);

		try {
			qAcadScrapper.setCookieMap(cookieMap);
			if(!qAcadScrapper.isLogged()) {
				Log.d(TAG, "Status is not logged");
				cookieMap = qAcadScrapper.loginToQAcad(user);
				Log.d(TAG, "Finished logging into QAcad");
			} else { Log.d(TAG, "We've already logged"); }

			Log.d(TAG, "Getting all subjects and grades from QAcad");
			List<Subject> subjectList = qAcadScrapper.getAllSubjectsAndGrades();
			Log.d(TAG, "All subjects and grades from QAcad were obtained!");

			databaseHelper.recreateDatabases();

			for(Subject subject: subjectList) {

				int subjectId = databaseHelper.insertSubject(
						new Subject(
								subject.getName(),
								subject.getProfessor(),
								subject.getName().substring(0, 3)
						)
				);

				for(Grade grade : subject.getGradeList()) {
					databaseHelper.insertGrade(
							new SubjectGrade(
								subjectId,
								grade.getGradeDescription(),
								grade.getObtainedGrade(),
								grade.getMaximumGrade(),
								false,
								grade.isObtainedGradeNull()
							)
					);
				}
			}
		} catch (Exception e) {
			// TODO Catch connection and login exceptions
			e.printStackTrace();
		} finally {
			databaseHelper.close();
		}
		return null;
	}

	public Map<String, String> getCookieMap() {
		return cookieMap;
	}

	public void setCookieMap(Map<String, String> cookieMap) {
		this.cookieMap = cookieMap;
	}
}
