package calegari.murilo.ifes_academico.utils.QAcadIntegration;

import android.os.AsyncTask;
import android.util.Log;

import java.net.ConnectException;
import java.util.Map;

import javax.security.auth.login.LoginException;

import calegari.murilo.ifes_academico.utils.Constants;
import calegari.murilo.qacadscrapper.QAcadScrapper;
import calegari.murilo.qacadscrapper.utils.User;

public class QAcadCheckLoginTask extends AsyncTask<Integer, Integer, Integer> {

    private final User user;
    private final String TAG = getClass().getSimpleName();
    protected Map<String, String> cookieMap;

    private int result;

    public QAcadCheckLoginTask(User user) {
       this.user = user;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        Log.d(TAG, "Called QACadChechLoginTask");

        QAcadScrapper qAcadScrapper = new QAcadScrapper(Constants.ACADEMIC_URL);

        try {
            cookieMap = qAcadScrapper.loginToQAcad(user);
            result = Constants.RESULT_SUCCESS;
        } catch (LoginException e) {
            result = Constants.RESULT_LOGIN_INVALID;
        } catch (ConnectException e) {
            result =  Constants.RESULT_CONNECTION_FAILURE;
        } catch (Exception e) {
            result = Constants.RESULT_UNKNOWN_ERROR;
        }

        return result;
    }

    public int getResult() {
        return result;
    }
}
