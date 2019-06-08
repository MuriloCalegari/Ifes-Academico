package calegari.murilo.sistema_academico.utils.QAcadIntegration;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.login.LoginException;

import calegari.murilo.sistema_academico.utils.Constants;
import calegari.murilo.qacadscrapper.QAcadScrapper;
import calegari.murilo.qacadscrapper.utils.User;

public class QAcadCheckLoginTask extends AsyncTask<Integer, Integer, Integer> {

    private final User user;
    private final String TAG = getClass().getSimpleName();
    protected Map<String, String> cookieMap;

    private int result;
    protected QAcadScrapper qAcadScrapper;

    public QAcadCheckLoginTask(User user) {
       this.user = user;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        Log.d(TAG, "Called QACadChechLoginTask");

        qAcadScrapper = new QAcadScrapper(Constants.QAcad.ACADEMIC_URL, user);

        try {
            user.setMultiThreadEnabled(true);
            cookieMap = qAcadScrapper.loginToQAcad();
            result = Constants.QAcad.RESULT_SUCCESS;
        } catch (LoginException e) {
            result = Constants.QAcad.RESULT_LOGIN_INVALID;
        } catch (IOException e) {
            result =  Constants.QAcad.RESULT_CONNECTION_FAILURE;
        } catch (Exception e) {
            result = Constants.QAcad.RESULT_UNKNOWN_ERROR;
        }

        return result;
    }

    public int getResult() {
        return result;
    }
}
