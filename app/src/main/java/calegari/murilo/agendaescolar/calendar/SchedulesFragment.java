package calegari.murilo.agendaescolar.calendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
*/

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
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

		setupScheduleView();

		fab = getView().findViewById(R.id.floatingActionButton);

		fab.setOnClickListener((l) -> {
			Intent newSubjectScheduleIntent = new Intent(view.getContext(), NewClassTimeActivity.class);
			if(!isSubjectDatabaseEmpty()) {
				getContext().startActivity(newSubjectScheduleIntent);
			} else {
				displayEmptyDatabaseError();
			}
		});



		/*

		weekView = getView().findViewById(R.id.weekView);

		weekView.setNumberOfVisibleDays(3);

		weekView.setOnEventClickListener((WeekViewEvent event, RectF eventRect) -> {

		});

		weekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
			@Override
			public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

				List<WeekViewEvent> events = new ArrayList<>();

				Calendar startTime = Calendar.getInstance();

				startTime.set(newYear, newMonth, 28, 13, 0);

				Calendar endTime = Calendar.getInstance();
				endTime.set(newYear,newMonth,13,13,50);

				WeekViewEvent weekViewEvent = new WeekViewEvent(0, "Teste", startTime, endTime);

				events.add(weekViewEvent);

				return events;
			}
		});

		weekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
			@Override
			public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

			}
		});
		*/

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

		WeekView weekView = getView().findViewById(R.id.week_view_foo);
		WeekData data = new WeekData();

		DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
		SubjectDatabaseHelper subjectDatabaseHelper = new SubjectDatabaseHelper(getContext());

		List<ClassTime> classTimeList = databaseHelper.getSchedule();

		for(ClassTime classtime : classTimeList) {
			Log.d(getClass().getSimpleName(), String.valueOf(classtime.getDayOfTheWeek()));
			Event.Single event = new Event.Single(
					classtime.getTimeId(),
					LocalDate.now(),
					"",
					subjectDatabaseHelper.getSubject(classtime.getSubjectId()).getAbbreviation(),
					"",
					classtime.getDayOfTheWeek(),
					LocalTime.of(classtime.getStartTimeHour(), classtime.getStartTimeMinute()),
					LocalTime.of(classtime.getEndTimeHour(), classtime.getEndTimeMinute()),
					null, null,
					Color.WHITE,
					Color.BLACK
			);
			data.add(event);
		}
		if(!data.isEmpty()) {
			weekView.addLessonsToTimetable(data);
		}
	}
}
