package calegari.murilo.sistema_academico;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
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
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import calegari.murilo.sistema_academico.calendar.SchedulesFragment;
import calegari.murilo.sistema_academico.databases.DatabaseHelper;
import calegari.murilo.sistema_academico.materials.MaterialsFragment;
import calegari.murilo.sistema_academico.grades.GradesFragment;
import calegari.murilo.sistema_academico.grades.GradesLineAdapter;
import calegari.murilo.sistema_academico.home.HomeFragment;
import calegari.murilo.sistema_academico.settings.SettingsActivity;
import calegari.murilo.sistema_academico.subjectgrades.SubjectGradesFragment;
import calegari.murilo.sistema_academico.subjects.SubjectsFragment;
import calegari.murilo.sistema_academico.utils.Constants;
import calegari.murilo.sistema_academico.utils.QAcadIntegration.LoginManager;
import calegari.murilo.sistema_academico.utils.QAcadIntegration.QAcadFetchMaterialsURLsTask;
import calegari.murilo.sistema_academico.utils.QAcadIntegration.QAcadFetchGradesTask;

import android.text.method.LinkMovementMethod;
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

	private final String TAG = getClass().getSimpleName();

	public static Fragment currentFragment;

	public static DrawerLayout drawer;
	public static ValueAnimator anim;
	public static Toolbar toolbar;
	public static ActionBar actionBar;
	public static FragmentManager fragmentManager;
	public static NavigationView navigationView;
	public static SwipeRefreshLayout pullToRefresh;

	public static Map<String, String> qAcadCookieMap = null;
	public QAcadFetchGradesTask qAcadFetchGradesTask;
	public QAcadFetchMaterialsURLsTask qAcadFetchMaterialsURLsTask;

	private static ActionBarDrawerToggle drawerToggle;

	private ImageButton changeUsernameButton;
	TextView usernameTextView;

	int NAVBAR_CLOSE_DELAY;

	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

		setContentView(R.layout.activity_main);

		NAVBAR_CLOSE_DELAY = getResources().getInteger(R.integer.navigation_bar_close_delay);

		findViews();

		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();

		navigationView.setNavigationItemSelectedListener(this);

		fragmentManager = getSupportFragmentManager();

		drawerToggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawerToggle.syncState();

		updateUsername();
		setupListeners();

		startFragment(HomeFragment.class, false);

		Intent intent = getIntent();

		if(intent.getBooleanExtra(Constants.Keys.IS_FIRST_RUN_EVER, false)) {
			syncDataFromQAcad();
			intent.removeExtra(Constants.Keys.IS_FIRST_RUN_EVER);
		} else if(intent.getBooleanExtra(Constants.Keys.SHOULD_SYNC_GRADES, false)) {
			syncDataFromQAcadAccordingToContext();
			intent.removeExtra(Constants.Keys.SHOULD_SYNC_GRADES);
		}

		if(!PreferenceManager.getDefaultSharedPreferences(this).contains(Constants.Keys.IS_DATA_COLLECTION_AUTHORIZED)) {
			displayGetDataCollectionAuthorization();
		}
	}

	private void findViews() {
		toolbar = findViewById(R.id.toolbar);
		drawer = findViewById(R.id.drawer_layout);
		navigationView = findViewById(R.id.nav_view);
		View header = navigationView.getHeaderView(0);
		changeUsernameButton = header.findViewById(R.id.changeUserNameButton);
		usernameTextView = header.findViewById(R.id.username);
		pullToRefresh = findViewById(R.id.swiperefresh);
	}

	private void displayGetDataCollectionAuthorization() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.about_data_collection))
				.setMessage(R.string.data_collection_explanation)
				.setPositiveButton(R.string.accept, (dialogInterface, i) -> acceptOrDeclineDataCollection(true))
				.setNegativeButton(R.string.decline, (dialogInterface, i) -> acceptOrDeclineDataCollection(false))
				.show();

		try {
			((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
		} catch (NullPointerException ignored) {}
	}

	private void acceptOrDeclineDataCollection(boolean isAccepted) {
		PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Constants.Keys.IS_DATA_COLLECTION_AUTHORIZED, isAccepted).apply();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		setupListeners();
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

		pullToRefresh.setOnRefreshListener(this::syncDataFromQAcadAccordingToContext);
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
					SharedPreferences sharedPreferences = getSharedPreferences(Constants.Keys.APP_GLOBALS_PREFERENCES, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("username", String.valueOf(editText.getText()));
					editor.apply();
					updateUsername();
				}))
				.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {})
				.show();
	}

	private void updateUsername() {
		SharedPreferences sharedPreferences = getSharedPreferences(Constants.Keys.APP_GLOBALS_PREFERENCES, Context.MODE_PRIVATE);
		String username = sharedPreferences.getString(Constants.Keys.APP_USERNAME_PREFERENCE, getString(R.string.student));

		usernameTextView.setText(username);
	}

	@Override
	public void onBackPressed() {
		drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else if(currentFragment instanceof GradesFragment && GradesFragment.inboxRecyclerView.getPage().isExpandedOrExpanding()){
			GradesFragment.inboxRecyclerView.collapse();
		} else if(!(currentFragment instanceof HomeFragment)) {
			startFragment(HomeFragment.class, true);
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
					/* // TODO reimplement materials
				case R.id.nav_materials:
					startFragment(MaterialsFragment.class, useAnimations);
					break;
					*/
				case R.id.nav_logout:
					LoginManager.logout(this);
					break;
				default:
					break;
			}
		}, NAVBAR_CLOSE_DELAY);

		drawer.closeDrawer(GravityCompat.START);

		return true;
	}

	@SuppressLint("StaticFieldLeak")
	private void syncDataFromQAcad() {
		pullToRefresh.setRefreshing(true);

		qAcadFetchGradesTask = new QAcadFetchGradesTask(this, qAcadCookieMap) {
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				qAcadCookieMap = getCookieMap(); // update cookies to the last one generated

				qAcadFetchMaterialsURLsTask = new QAcadFetchMaterialsURLsTask(context, qAcadCookieMap) {
					@Override
					protected void onPostExecute(Integer result) {
						super.onPostExecute(result);
						qAcadCookieMap = getCookieMap(); // update cookies to the last one generated
						refreshCurrentFragment(true);
					}

					@Override
					protected void onCancelled(Integer result) {
				/*
				This is currently only being called by LoginManager.Logout(),
				so it's important for data security reasons that we clean all
				databases after the task is cancelled.
				*/

						DatabaseHelper databaseHelper = new DatabaseHelper(context);
						databaseHelper.recreateDatabase();
						databaseHelper.close();
					}
				};

				qAcadFetchMaterialsURLsTask.execute();
			}

			@Override
			protected void onCancelled(Integer result) {
				/*
				This is currently only being called by LoginManager.Logout(),
				so it's important for data security reasons that we clean all
				databases after the task is cancelled.
				*/

				DatabaseHelper databaseHelper = new DatabaseHelper(context);
				databaseHelper.recreateDatabase();
				databaseHelper.close();
			}
		};

		qAcadFetchGradesTask.execute();
	}

	@SuppressLint("StaticFieldLeak")
	private void syncDataFromQAcadAccordingToContext() {
		if(currentFragment instanceof MaterialsFragment) {
			syncMaterialsFromQAcad();
		} else {
			syncGradesFromQAcad();
		}
	}

	@SuppressLint("StaticFieldLeak")
	private void syncGradesFromQAcad() {
		pullToRefresh.setRefreshing(true);

		qAcadFetchGradesTask = new QAcadFetchGradesTask(this, qAcadCookieMap) {
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				qAcadCookieMap = getCookieMap(); // update cookies to the last one generated
				refreshCurrentFragment(true);
			}

			@Override
			protected void onCancelled(Integer result) {
				/*
				This is currently only being called by LoginManager.Logout(),
				so it's important for data security reasons that we clean all
				databases after the task is cancelled.
				*/

				DatabaseHelper databaseHelper = new DatabaseHelper(context);
				databaseHelper.recreateDatabase();
				databaseHelper.close();
			}
		};

		qAcadFetchGradesTask.execute();
	}

	@SuppressLint("StaticFieldLeak")
	private void syncMaterialsFromQAcad() {
		pullToRefresh.setRefreshing(true);

		qAcadFetchMaterialsURLsTask = new QAcadFetchMaterialsURLsTask(this, qAcadCookieMap) {
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				qAcadCookieMap = getCookieMap(); // update cookies to the last one generated
				refreshCurrentFragment(true);
			}

			@Override
			protected void onCancelled(Integer result) {
				/*
				This is currently only being called by LoginManager.Logout(),
				so it's important for data security reasons that we clean all
				databases after the task is cancelled.
				*/

				DatabaseHelper databaseHelper = new DatabaseHelper(context);
				databaseHelper.recreateDatabase();
				databaseHelper.close();
			}
		};

		qAcadFetchMaterialsURLsTask.execute();
	}

	public void refreshCurrentFragment() {
		refreshCurrentFragment(false);
	}

	public void refreshCurrentFragment(boolean stopPullToRefreshAnimation) {
		/*
		Fragment currentFragment = null;

		try {
			currentFragment = fragmentManager.findFragmentById(R.id.flContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		if(currentFragment != null) {

			// Refresh SubjectGradesFragment if it is expanded
			if(currentFragment instanceof GradesFragment && GradesFragment.inboxRecyclerView.getPage().isExpanded()) {
				Fragment fragment = null;
				try {
					fragment = SubjectGradesFragment.class.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}

				if(fragment != null) {
					int expandedItemPosition = (int) GradesFragment.inboxRecyclerView.getExpandedItem().getItemId();
					RecyclerView.Adapter lineAdapter = GradesFragment.inboxRecyclerView.getAdapter();
					if(lineAdapter instanceof GradesLineAdapter) {
						int subjectId = ((GradesLineAdapter) lineAdapter).getItem(expandedItemPosition).getId();
						Bundle bundle = new Bundle();
						bundle.putInt(Constants.Keys.SUBJECT_ID, subjectId);

						fragment.setArguments(bundle);

						fragmentManager
								.beginTransaction()
								.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
								.replace(R.id.frameLayoutContent, fragment)
								.commitAllowingStateLoss();
					}
				}

				if(stopPullToRefreshAnimation) {
					pullToRefresh.setRefreshing(false);

					// Since SubjectGradesFragment is expanded, pullToRefresh must be disabled
					// so the user can still close the expandable page layout by pulling the
					// page off

					pullToRefresh.setEnabled(false);
				}
			} else {
				if(stopPullToRefreshAnimation) {
					pullToRefresh.setRefreshing(false);
				}
				startFragment(currentFragment.getClass(), false);

				// Every time a fragment is refreshed, drawer should be in idle mode
				setDrawerIdleMode();
			}
		}
	}

	public void startFragment(Class fragmentClass, boolean useAnimations) {
		startFragment(fragmentClass, useAnimations, null);
	}

	public void startFragment(Class fragmentClass, boolean useAnimations, Bundle bundle) {

		Fragment fragment = null;
		try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Insert the fragment by replacing any existing fragment
		if (fragment != null) {

			if(bundle != null) {
				fragment.setArguments(bundle);
			}

			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			if(useAnimations) {
				fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}

			fragmentTransaction.replace(R.id.flContent, fragment).commitAllowingStateLoss();
			currentFragment = fragment;

			Intent intent = getIntent();

			// Every time a fragment is switched and it is not refreshing, check if there are any pending synchronizations
			if(!pullToRefresh.isRefreshing()) {
				if(fragmentClass == MaterialsFragment.class && intent.getBooleanExtra(Constants.Keys.SHOULD_SYNC_MATERIALS, false)) {
					syncMaterialsFromQAcad();
					intent.removeExtra(Constants.Keys.SHOULD_SYNC_MATERIALS);
				} else if(intent.getBooleanExtra(Constants.Keys.SHOULD_SYNC_GRADES, false)) {
					syncGradesFromQAcad();
					intent.removeExtra(Constants.Keys.SHOULD_SYNC_GRADES);
				}
			}
		}
	}

	public static void setDrawerIdleMode() {
		// The following lines makes the user able to open the drawer after coming from a
		// subject grade fragment
		toolbar.setNavigationOnClickListener(v -> drawer.openDrawer(GravityCompat.START));
		drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

		// If the expandable page from InboxRecyclerView was left expanded, reverse the
		// hamburger icon animation
		if(GradesFragment.inboxRecyclerView != null) {
			if(!GradesFragment.inboxRecyclerView.getPage().isCollapsedOrCollapsing()) {
				anim.reverse();
			}
		}
	}
}
