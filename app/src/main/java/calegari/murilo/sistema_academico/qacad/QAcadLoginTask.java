package calegari.murilo.sistema_academico.qacad;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.login.LoginException;

import calegari.murilo.sistema_academico.utils.Constants;
import calegari.murilo.qacadscrapper.QAcadScrapper;
import calegari.murilo.qacadscrapper.utils.User;

public class QAcadLoginTask extends AsyncTask<Integer, Integer, Integer> {

    private final User user;
    private final String TAG = getClass().getSimpleName();
    protected Map<String, String> cookieMap;

    private int result;
    protected QAcadScrapper qAcadScrapper;

    public QAcadLoginTask(User user) {
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
            Crashlytics.logException(e);
            result = Constants.QAcad.RESULT_UNKNOWN_ERROR;
        }

        return result;
    }

    public int getResult() {
        return result;
    }

    public Map<String, String> getCookieMap() {
        return cookieMap;
    }

    public void setCookieMap(Map<String, String> cookieMap) {
        this.cookieMap = cookieMap;
    }
}
