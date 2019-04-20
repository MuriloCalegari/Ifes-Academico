package calegari.murilo.ifes_academico.grades;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import calegari.murilo.ifes_academico.BaseFragment;
import calegari.murilo.ifes_academico.MainActivity;
import calegari.murilo.ifes_academico.R;
import calegari.murilo.ifes_academico.databases.DatabaseHelper;
import calegari.murilo.ifes_academico.utils.QAcadIntegration.QAcadFetchDataTask;
import me.saket.inboxrecyclerview.InboxRecyclerView;
import me.saket.inboxrecyclerview.page.ExpandablePageLayout;
import me.saket.inboxrecyclerview.page.InterceptResult;
import me.saket.inboxrecyclerview.page.SimplePageStateChangeCallbacks;

public class GradesFragment extends BaseFragment {

	public static InboxRecyclerView inboxRecyclerView;
	private GradesLineAdapter mAdapter;
	DatabaseHelper subjectDatabase;
	private SwipeRefreshLayout pullToRefresh;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//returning our layout file
		return inflater.inflate(R.layout.fragment_grades, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		inboxRecyclerView = getView().findViewById(R.id.inbox_recyclerview);

		// Sets the toolbar name and item checked on nav bar
		AppCompatActivity activity = (AppCompatActivity) getContext();
		activity.getSupportActionBar().setTitle(getString(R.string.grades));
		MainActivity.navigationView.setCheckedItem(R.id.nav_grades);

		pullToRefresh = view.findViewById(R.id.swiperefresh);
		pullToRefresh.setOnRefreshListener(() -> {
			@SuppressLint("StaticFieldLeak")
            QAcadFetchDataTask qAcadFetchDataTask = new QAcadFetchDataTask(getContext(), MainActivity.qAcadCookieMap) {
				@Override
				protected void onPostExecute(Void aVoid) {
					super.onPostExecute(aVoid);
					pullToRefresh.setRefreshing(false);
					MainActivity.startFragment(GradesFragment.class, false);
					MainActivity.qAcadCookieMap = getCookieMap(); // update cookies to the last one generated
				}
			};
			qAcadFetchDataTask.execute();
		});

		setupInboxRecyclerView();
		initInboxRecyclerView();
	}

	@Override
	public void onStart() {
		super.onStart();

		/*
		initInboxRecyclerView() needs to be separated and called alone at onStart()
		since inboxRecyclerView will crash if setExpandablePage is called twice.
		 */
		initInboxRecyclerView();
	}

	private void setupInboxRecyclerView() {
		inboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		final ExpandablePageLayout expandablePageLayout = getView().findViewById(R.id.expandablePageLayout);

		// Trigger pull-to-collapse only if the page cannot be scrolled any further in the direction of scroll.
		// Code from https://github.com/saket/InboxRecyclerView/wiki/Pull-to-collapse
		expandablePageLayout.setPullToCollapseInterceptor((downX, downY, upwardPull) -> {
			int directionInt = upwardPull ? 1 : -1;
			boolean canScrollFurther = expandablePageLayout.findViewById(R.id.recyclerView).canScrollVertically(directionInt);
			return canScrollFurther ? InterceptResult.INTERCEPTED : InterceptResult.IGNORED;
		});

		inboxRecyclerView.setExpandablePage(expandablePageLayout);

		expandablePageLayout.addStateChangeCallbacks(new SimplePageStateChangeCallbacks() {

			AppCompatActivity activity = (AppCompatActivity) getView().getContext();

			@Override
			public void onPageAboutToCollapse(long collapseAnimDuration) {
				super.onPageAboutToCollapse(collapseAnimDuration);
				MainActivity.anim.reverse();
				activity.getSupportActionBar().setTitle(getString(R.string.grades));

				// The following lines makes the user able to open the drawer after coming from a
				// subject grade fragment
				MainActivity.toolbar.setNavigationOnClickListener(v -> MainActivity.drawer.openDrawer(GravityCompat.START));
				MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			}

			@Override
			public void onPageAboutToExpand(long expandAnimDuration) {
				super.onPageAboutToExpand(expandAnimDuration);
				MainActivity.anim.start();
				pullToRefresh.setEnabled(false);
			}

			@Override
			public void onPageCollapsed() {
				super.onPageCollapsed();
				activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
				pullToRefresh.setEnabled(true);
			}

			@Override
			public void onPageExpanded() {
				super.onPageExpanded();
			}
		});
	}

	private void initInboxRecyclerView() {
		mAdapter = new GradesLineAdapter(new ArrayList<>(0), getContext(), getView());

		// Needed to avoid "Adapter needs to have stable IDs so that the expanded item can be restored across orientation changes." exception
		mAdapter.setHasStableIds(true);

		inboxRecyclerView.setAdapter(mAdapter);

		// Populates the list:
		subjectDatabase = new DatabaseHelper(getContext());

		mAdapter.setSubjects(subjectDatabase.getAllSubjects());

		subjectDatabase.close();
	}
}