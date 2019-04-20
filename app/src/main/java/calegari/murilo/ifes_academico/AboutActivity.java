package calegari.murilo.ifes_academico;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import android.view.View;
import android.widget.LinearLayout;

import com.franmontiel.attributionpresenter.AttributionPresenter;
import com.franmontiel.attributionpresenter.entities.Attribution;
import com.franmontiel.attributionpresenter.entities.License;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appDescription = getString(R.string.app_about_1) + "\n \n" + getString(R.string.app_about_2);

        Element openSourceElement = new Element();
        openSourceElement.setTitle(getString(R.string.open_source_libraries));
        openSourceElement.setIconDrawable(R.drawable.ic_code_black_24dp);
        openSourceElement.setOnClickListener(v -> openLibrariesDialog());

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(appDescription)
                .setImage(R.mipmap.ic_launcher)
                .addItem(new Element().setTitle(getString(R.string.code_version) + " " + BuildConfig.VERSION_NAME))
                .addPlayStore(getApplicationContext().getPackageName())
                .addEmail("murilo.calegari.souza@gmail.com")
                .addGitHub("MuriloCalegari/Agenda-Escolar")
                .addItem(openSourceElement)
                .create();

        setContentView(R.layout.toolbar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Finishes the Activity
        toolbar.setNavigationOnClickListener(v -> finish());

        LinearLayout mActivityRoot = (findViewById(R.id.main_toolbar)); // our host view
        mActivityRoot.addView(aboutPage, 1); // Add about-page view to position 1 (since 0 is already taken by the toolbar)
    }

    private void openLibrariesDialog() {

        Attribution VERTICAL_STEPPER_FORM = ((new Attribution.Builder("Vertical Stepper Form")
		        .addCopyrightNotice("Copyright 2018 Julio Ernesto Cabañas")
		        .addLicense(License.APACHE)
		        .setWebsite("https://github.com/ernestoyaquello/VerticalStepperForm"))
		        .build());
        Attribution MATERIAL_SEEK_BAR_PREFERENCE = ((new Attribution.Builder("Material SeekBarPreference")
		        .addCopyrightNotice("Copyright 2016 Pavel Sikun")
		        .addLicense(License.APACHE)
		        .setWebsite("https://github.com/MrBIMC/MaterialSeekBarPreference"))
		        .build());
		Attribution ANDROID_ABOUT_PAGE = (new Attribution.Builder("Android About Page")
				.addCopyrightNotice("Copyright 2019 Mehdi Sakout")
				.addLicense(License.MIT).setWebsite("https://github.com/medyo/android-about-page"))
				.build();
		Attribution INBOX_RECYCLER_VIEW = (new Attribution.Builder("InboxRecyclerView ")
				.addCopyrightNotice("Copyright 2018 Saket Narayan")
				.addLicense(License.APACHE).setWebsite("https://github.com/saket/InboxRecyclerView"))
				.build();
		Attribution SLIM_CHART = (new Attribution.Builder("SlimChart")
				.addCopyrightNotice("Copyright 2018 Mansur")
				.setWebsite("https://github.com/mancj/SlimChart"))
				.build();
		Attribution MPANDROIDCHART = (new Attribution.Builder("MPAndroidChart")
				.addCopyrightNotice("Copyright 2018 Philipp Jahoda")
				.addLicense(License.APACHE)
				.setWebsite("https://github.com/PhilJay/MPAndroidChart"))
				.build();
		Attribution ANDROID_WEEK_VIEW = (new Attribution.Builder("Android Week View")
				.addCopyrightNotice("Copyright 2018 Tobias Schürg")
				.setWebsite("https://github.com/tobiasschuerg/android-week-view"))
				.build();

        AttributionPresenter attributionPresenter = new AttributionPresenter.Builder(this)
		        .addAttributions(new Attribution.Builder("AttributionPresenter")
				        .addCopyrightNotice("Copyright 2017 Francisco José Montiel Navarro")
				        .addLicense(License.APACHE)
				        .setWebsite("https://github.com/franmontiel/AttributionPresenter")
				        .build())
                .addAttributions(
                        VERTICAL_STEPPER_FORM,
                        MATERIAL_SEEK_BAR_PREFERENCE,
		                ANDROID_ABOUT_PAGE,
		                INBOX_RECYCLER_VIEW,
		                SLIM_CHART,
		                MPANDROIDCHART,
		                ANDROID_WEEK_VIEW)
                .build();

        attributionPresenter.showDialog(getString(R.string.open_source_libraries));
    }
}
