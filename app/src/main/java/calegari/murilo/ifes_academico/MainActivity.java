package calegari.murilo.ifes_academico;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import calegari.murilo.ifes_academico.calendar.SchedulesFragment;
import calegari.murilo.ifes_academico.grades.GradesFragment;
import calegari.murilo.ifes_academico.home.HomeFragment;
import calegari.murilo.ifes_academico.settings.SettingsActivity;
import calegari.murilo.ifes_academico.subjects.SubjectsFragment;
import calegari.murilo.ifes_academico.utils.QAcadIntegration.LoginManager;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static DrawerLayout drawer;
    public static ValueAnimator anim;
    public static Toolbar toolbar;
    public static FragmentManager fragmentManager;
    public static NavigationView navigationView;
    public static Map<String, String> qAcadCookieMap = null;


    private ActionBarDrawerToggle drawerToggle;

    private ImageButton changeUsernameButton;
	TextView usernameTextView;

	int NAVBAR_CLOSE_DELAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NAVBAR_CLOSE_DELAY = getResources().getInteger(R.integer.navigation_bar_close_delay);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();

        drawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerToggle.syncState();

        View header = navigationView.getHeaderView(0);
	    changeUsernameButton = header.findViewById(R.id.changeUserNameButton);
	    usernameTextView = header.findViewById(R.id.username);

	    updateUsername();

        setupListeners();

        startFragment(HomeFragment.class, false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setupListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setupListeners() {
        drawer.addDrawerListener(drawerToggle);

        anim = ValueAnimator.ofFloat(0f, 1f);
        anim.addUpdateListener(valueAnimator -> {
            float slideOffset = (Float) valueAnimator.getAnimatedValue();
            drawerToggle.onDrawerSlide(drawer, slideOffset);
        });

        anim.setInterpolator(new DecelerateInterpolator());
        // You can change this duration to more closely match that of the default animation.
        anim.setDuration(250);

	    changeUsernameButton.setOnClickListener(view -> setUsernameDialog());
	    usernameTextView.setOnClickListener(view -> setUsernameDialog());
    }

    private void setUsernameDialog() {
	    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

	    TextInputLayout textInputLayout = new TextInputLayout(this);
	    TextInputEditText editText = new TextInputEditText(textInputLayout.getContext());

	    FrameLayout editTextContainer = new FrameLayout(this);
	    FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
	    params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
	    editText.setLayoutParams(params);
	    editTextContainer.addView(editText);

	    dialogBuilder
			    .setTitle(getString(R.string.edit_username))
			    .setView(editTextContainer)
			    //.setMessage(getString(R.string.confirm_subject_grade_delete_message))
			    .setPositiveButton(getString(R.string.edit), ((dialogInterface, i) -> {
				    SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
				    SharedPreferences.Editor editor = sharedPreferences.edit();
				    editor.putString("username", String.valueOf(editText.getText()));
				    editor.apply();
				    updateUsername();
			    }))
			    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {})
			    .show();
    }

	private void updateUsername() {
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("username", getString(R.string.student));

		usernameTextView.setText(username);
	}

	@Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        final Context context = this;

        /*
        Adds a delay to the fragment calling after navigation bar is being closed, I've tested three different methods
        in order to improve the stutter that happens during the animation: hardware acceleration, which didn't solve the
        issue by itself; only calling fragment after navigation bar is closed, which introduced a too-long blank screen
        before fragment is setup. This "solve" was found in
        https://stackoverflow.com/questions/27234580/stuttering-when-opening-new-fragment-from-navigation-drawer
        and is said to be within an app example presented by Google in Google IO 2014
        */

		boolean useAnimations = !item.isChecked();

        new Handler().postDelayed(() -> {
            switch (id) {
                case R.id.nav_home:
                    toolbar.setTitle(getString(R.string.app_name));
					startFragment(HomeFragment.class, useAnimations);
                    break;
                case R.id.nav_about:
                    Intent aboutIntent = new Intent(context, AboutActivity.class);
                    startActivity(aboutIntent);
                    break;
                case R.id.nav_settings:
                    Intent settingsIntent = new Intent(context,SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;
                case R.id.nav_subjects:
                    startFragment(SubjectsFragment.class, useAnimations);
                    break;
                case R.id.nav_grades:
                    startFragment(GradesFragment.class, useAnimations);
                    break;
                case R.id.nav_schedules:
                    startFragment(SchedulesFragment.class, useAnimations);
                    break;
                case R.id.nav_logout:
                    LoginManager.logout(this);
                default:
                    break;
            }
        }, NAVBAR_CLOSE_DELAY);

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public static void startFragment(Class fragmentClass, boolean useAnimations) {

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if(useAnimations) {
                fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }

            fragmentTransaction.replace(R.id.flContent, fragment).commit();
        }

    }
}
