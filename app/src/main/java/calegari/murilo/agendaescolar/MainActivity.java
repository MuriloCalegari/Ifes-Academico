package calegari.murilo.agendaescolar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;

import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.agendaescolar.grades.GradesFragment;
import calegari.murilo.agendaescolar.settings.SettingsActivity;
import calegari.murilo.agendaescolar.subjects.SubjectsFragment;

import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static DrawerLayout drawer;
    public static ValueAnimator anim;
    public static Toolbar toolbar;

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
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        anim = ValueAnimator.ofFloat(0f, 1f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                toggle.onDrawerSlide(drawer, slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        // You can change this duration to more closely match that of the default animation.
        anim.setDuration(250);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true); // Defines this activity as checked
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

        int NAVBAR_CLOSE_DELAY = getResources().getInteger(R.integer.navigation_bar_close_delay);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    case R.id.nav_about:
                        Intent aboutIntent = new Intent(context,AboutActivity.class);
                        startActivity(aboutIntent);
                        break;
                    case R.id.nav_settings:
                        Intent settingsIntent = new Intent(context,SettingsActivity.class);
                        startActivity(settingsIntent);
                        break;
                    case R.id.nav_subjects:
                        startFragment(SubjectsFragment.class, item);
                        break;
                    case R.id.nav_grades:
                        startFragment(GradesFragment.class, item);
                        break;
                    default:
                        break;
                }
            }
        }, NAVBAR_CLOSE_DELAY);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void startFragment(Class fragmentClass, MenuItem item) {

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

    }
}
