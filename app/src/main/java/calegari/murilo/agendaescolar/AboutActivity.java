package calegari.murilo.agendaescolar;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import android.view.View;
import android.widget.LinearLayout;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appDescription = getString(R.string.app_about_1) + "\n \n" + getString(R.string.app_about_2);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(appDescription)
                .setImage(R.mipmap.ic_launcher)
                .addItem(new Element().setTitle(getString(R.string.code_version) + " " + BuildConfig.VERSION_NAME))
                .addPlayStore(getApplicationContext().getPackageName())
                .addEmail("murilo.calegari.souza@gmail.com")
                .addGitHub("MuriloCalegari/Agenda-Escolar")
                .create();

        setContentView(R.layout.toolbar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Finishes the Activity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        LinearLayout mActivityRoot = (findViewById(R.id.main_toolbar)); // our host view
        mActivityRoot.addView(aboutPage, 1); // Add about-page view to position 1 (since 0 is already taken by the toolbar)
    }
}
