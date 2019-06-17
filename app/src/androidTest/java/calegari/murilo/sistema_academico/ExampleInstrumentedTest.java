package calegari.murilo.sistema_academico;

import android.content.Context;
import android.os.AsyncTask;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import calegari.murilo.sistema_academico.utils.QAcadIntegration.LoginManager;
import calegari.murilo.sistema_academico.utils.QAcadIntegration.QAcadCheckLoginTask;
import calegari.murilo.sistema_academico.utils.QAcadIntegration.QAcadFetchMaterialsURLsTask;
import calegari.murilo.sistema_academico.utils.QAcadIntegration.QAcadFetchGradesTask;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = getContext();

        assertEquals("calegari.murilo.ifes_academico", appContext.getPackageName());
    }

    @Test
    public void testFetchGrades() {
        Context appContext = getContext();
        QAcadFetchGradesTask qAcadFetchGradesTask = new QAcadFetchGradesTask(appContext);
        qAcadFetchGradesTask.execute();
        try {
            qAcadFetchGradesTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFetchMaterials() {
        Context appContext = getContext();
        QAcadFetchMaterialsURLsTask qAcadFetchMaterialsURLsTask = new QAcadFetchMaterialsURLsTask(appContext);
        qAcadFetchMaterialsURLsTask.execute();

        try {
            qAcadFetchMaterialsURLsTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimultaneousFetchData() {
        Context appContext = getContext();

        QAcadCheckLoginTask qAcadCheckLoginTask = new QAcadCheckLoginTask(LoginManager.getUser(appContext));
        qAcadCheckLoginTask.execute();

        try {
            qAcadCheckLoginTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, String> cookieMap = qAcadCheckLoginTask.getCookieMap();

        for(int i = 0; i <= 100; i++) {
            QAcadFetchGradesTask qAcadFetchGradesTask = new QAcadFetchGradesTask(appContext, cookieMap);
            qAcadFetchGradesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            QAcadFetchMaterialsURLsTask qAcadFetchMaterialsURLsTask = new QAcadFetchMaterialsURLsTask(appContext, cookieMap);
            qAcadFetchMaterialsURLsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            try {
                qAcadFetchGradesTask.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                qAcadFetchMaterialsURLsTask.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }
}
