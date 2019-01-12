package calegari.murilo.agendaescolar.grades;

import android.database.Cursor;
import android.graphics.Rect;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.subjectgrades.SubjectGradesFragment;
import calegari.murilo.agendaescolar.subjects.Subject;
import me.saket.inboxrecyclerview.InboxRecyclerView;
import me.saket.inboxrecyclerview.Views;
import me.saket.inboxrecyclerview.ViewsKt;
import me.saket.inboxrecyclerview.page.ExpandablePageLayout;
import me.saket.inboxrecyclerview.page.InterceptResult;
import me.saket.inboxrecyclerview.page.SimplePageStateChangeCallbacks;

public class GradesFragment extends Fragment {

	static InboxRecyclerView inboxRecyclerView;
	private GradesLineAdapter mAdapter;
	SubjectDatabaseHelper subjectDatabase;

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

		// Sets the toolbar name
		AppCompatActivity activity = (AppCompatActivity) view.getContext();
		activity.getSupportActionBar().setTitle(getString(R.string.grades));

		setupInboxRecyclerView(view);

	}

	private void setupInboxRecyclerView(final View view) {

		inboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		mAdapter = new GradesLineAdapter(new ArrayList<>(0), getContext(), view);

		// Needed to avoid "Adapter needs to have stable IDs so that the expanded item can be restored across orientation changes." exception
		mAdapter.setHasStableIds(true);

		inboxRecyclerView.setAdapter(mAdapter);

		// Populates the list:
		subjectDatabase = new SubjectDatabaseHelper(getContext());
		Cursor cursor = subjectDatabase.getAllDataInAlphabeticalOrder();

		Integer subjectNameIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_NAME);
		Integer subjectAbbreviationIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_ABBREVIATION);
		Integer subjectMaximumGradeIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);
		Integer subjectObtainedGradeIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE);

		while(cursor.moveToNext()) {

			String subjectName = cursor.getString(subjectNameIndex);
			String subjectAbbreviation = cursor.getString(subjectAbbreviationIndex);
			Float subjectObtainedGrade = cursor.getFloat(subjectObtainedGradeIndex);
			Float subjectMaximumGrade = cursor.getFloat(subjectMaximumGradeIndex);

			Subject subject = new Subject(subjectName,subjectAbbreviation,subjectObtainedGrade,subjectMaximumGrade);

			mAdapter.updateList(subject);
		}
		cursor.close();
		subjectDatabase.close();

		final ExpandablePageLayout expandablePageLayout = getView().findViewById(R.id.expandablePageLayout);

		// Trigger pull-to-collapse only if the page cannot be scrolled any further in the direction of scroll.
		// Code from https://github.com/saket/InboxRecyclerView/wiki/Pull-to-collapse
		expandablePageLayout.setPullToCollapseInterceptor((downX, downY, upwardPull) -> {
			Integer directionInt = upwardPull ? 1 : -1;
			boolean canScrollFurther = expandablePageLayout.findViewById(R.id.recyclerView).canScrollVertically(directionInt);
			return canScrollFurther ? InterceptResult.INTERCEPTED : InterceptResult.IGNORED;
		});

		inboxRecyclerView.setExpandablePage(expandablePageLayout);
		expandablePageLayout.addStateChangeCallbacks(new SimplePageStateChangeCallbacks() {

			AppCompatActivity activity = (AppCompatActivity) view.getContext();

			@Override
			public void onPageAboutToCollapse(long collapseAnimDuration) {
				super.onPageAboutToCollapse(collapseAnimDuration);
				MainActivity.anim.reverse();
				activity.getSupportActionBar().setTitle(getString(R.string.grades));

				// The following lines makes the user able to open the drawer after coming from a
				// subject grade fragment
				MainActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						MainActivity.drawer.openDrawer(GravityCompat.START);
					}
				});
				MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			}

			@Override
			public void onPageAboutToExpand(long expandAnimDuration) {
				super.onPageAboutToExpand(expandAnimDuration);
				MainActivity.anim.start();
			}

			@Override
			public void onPageCollapsed() {
				super.onPageCollapsed();
				activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			}

			@Override
			public void onPageExpanded() {
				super.onPageExpanded();
			}
		});
	}

	public static Rect globalVisibleRect(View view) {
		Rect rect = new Rect();
		view.getGlobalVisibleRect(rect);
		return rect;
	}

}
