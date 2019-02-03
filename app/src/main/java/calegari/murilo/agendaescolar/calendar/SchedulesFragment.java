package calegari.murilo.agendaescolar.calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import calegari.murilo.agendaescolar.BaseFragment;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.DatabaseHelper;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import de.tobiasschuerg.weekview.data.Event;
import de.tobiasschuerg.weekview.data.WeekData;
import de.tobiasschuerg.weekview.view.WeekView;

public class SchedulesFragment extends BaseFragment {
	private FloatingActionButton fab;
	private WeekView weekView;

	//WeekView weekView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_calendar, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		MainActivity.navigationView.getMenu().getItem(2).setChecked(true);
		MainActivity.toolbar.setTitle(getString(R.string.schedule));

		fab = getView().findViewById(R.id.floatingActionButton);

		fab.setOnClickListener((l) -> {
			if(!isSubjectDatabaseEmpty()) {
				Intent newSubjectScheduleIntent = new Intent(getContext(), NewClassTimeActivity.class);
				startActivityForResult(newSubjectScheduleIntent, 1);
			} else {
				displayEmptyDatabaseError();
			}
		});

		setupScheduleView();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		getActivity();
		if (resultCode == Activity.RESULT_OK && requestCode == 1) {
			ClassTime classTime = new ClassTime(
					data.getIntExtra("subjectId", 0),
					data.getIntExtra("dayOfTheWeek", 0),
					data.getStringExtra("startTime"),
					data.getStringExtra("endTime")
			);

			WeekData weekData = new WeekData();
			weekData.add(createEvent(classTime));
			weekView.addLessonsToTimetable(weekData);
		}
	}

	private void displayEmptyDatabaseError() {
		Snackbar snackbar = Snackbar.make(getView(), getString(R.string.create_subject_first), Snackbar.LENGTH_SHORT);

		// Displays SnackBar above the floating action button, as stated in the material docs
		snackbar.setAnchorView(fab);

		snackbar.show();
	}

	private boolean isSubjectDatabaseEmpty() {
		SubjectDatabaseHelper subjectDatabaseHelper = new SubjectDatabaseHelper(getContext());

		boolean isSubjectDatabaseEmpty = subjectDatabaseHelper.getAllSubjects().isEmpty();
		subjectDatabaseHelper.close();

		return isSubjectDatabaseEmpty;
	}

	private void setupScheduleView() {
		AndroidThreeTen.init(getContext());

		weekView = getView().findViewById(R.id.week_view_foo);

		DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
		List<ClassTime> classTimeList = databaseHelper.getSchedule();
		databaseHelper.close();

		WeekData data = new WeekData();

		for(ClassTime classtime : classTimeList) {
			data.add(createEvent(classtime));
		}

		if(!data.isEmpty()) {
			weekView.addLessonsToTimetable(data);
		}

		weekView.setLessonClickListener(eventView -> {
			eventView.getEvent().getId();
			return null;
		});
	}

	private Event.Single createEvent(ClassTime classTime) {
		SubjectDatabaseHelper subjectDatabaseHelper = new SubjectDatabaseHelper(getContext());

		Event.Single event = new Event.Single(
				classTime.getTimeId(),
				LocalDate.now(),
				"",
				subjectDatabaseHelper.getSubject(classTime.getSubjectId()).getAbbreviation(),
				"",
				classTime.getDayOfTheWeek(),
				LocalTime.of(classTime.getStartTimeHour(), classTime.getStartTimeMinute()),
				LocalTime.of(classTime.getEndTimeHour(), classTime.getEndTimeMinute()),
				null, null,
				Color.WHITE,
				Color.BLACK
		);
		subjectDatabaseHelper.close();

		return(event);
	}
}
