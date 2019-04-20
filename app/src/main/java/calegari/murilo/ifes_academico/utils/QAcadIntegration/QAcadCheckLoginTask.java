package calegari.murilo.ifes_academico.utils.QAcadIntegration;

import android.os.AsyncTask;
import android.util.Log;

import java.net.ConnectException;

import javax.security.auth.login.LoginException;

import calegari.murilo.ifes_academico.utils.Constants;
import calegari.murilo.qacadscrapper.QAcadScrapper;
import calegari.murilo.qacadscrapper.utils.User;

public class QAcadCheckLoginTask extends AsyncTask<Integer, Integer, Integer> {

    private final User user;
    private final String TAG = getClass().getSimpleName();

    private int result;

    public static final int RESULT_LOGIN_INVALID = 0;
    public static final int RESULT_CONNECTION_FAILURE = 1;
    public static final int RESULT_UNKNOWN_ERROR = 2;
    public static final int RESULT_SUCCESS = 3;

    public QAcadCheckLoginTask(User user) {
       this.user = user;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        Log.d(TAG, "Called QACadChechLoginTask");

        QAcadScrapper qAcadScrapper = new QAcadScrapper(Constants.ACADEMIC_URL);

        try {
            qAcadScrapper.loginToQAcad(user);
            result = RESULT_SUCCESS;
        } catch (LoginException e) {
            result = RESULT_LOGIN_INVALID;
        } catch (ConnectException e) {
            result =  RESULT_CONNECTION_FAILURE;
        } catch (Exception e) {
            result = RESULT_UNKNOWN_ERROR;
        }

        return result;
    }

    public int getResult() {
        return result;
    }
}
