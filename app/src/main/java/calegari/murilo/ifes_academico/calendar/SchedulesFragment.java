package calegari.murilo.ifes_academico.calendar;

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
import androidx.appcompat.app.AppCompatActivity;
import calegari.murilo.ifes_academico.BaseFragment;
import calegari.murilo.ifes_academico.MainActivity;
import calegari.murilo.ifes_academico.R;
import calegari.murilo.ifes_academico.databases.DatabaseHelper;
import calegari.murilo.ifes_academico.utils.Tools;
import de.tobiasschuerg.weekview.data.Event;
import de.tobiasschuerg.weekview.data.WeekData;
import de.tobiasschuerg.weekview.view.EventView;
import de.tobiasschuerg.weekview.view.WeekView;

public class SchedulesFragment extends BaseFragment {

	public static final int NEW_CLASS_EVENT_REQUEST_CODE = 1;
	public static final int EDIT_CLASS_EVENT_REQUEST_CODE = 2;

	public static final int UPDATE_CLASS_EVENT_RESULT_CODE = 3;
	public static final int DELETE_EVENT_RESULT_CODE = 4;
	public static final int NEW_CLASS_EVENT_RESULT_CODE = 5;

	private FloatingActionButton fab;
	private WeekView weekView;
	private DatabaseHelper databaseHelper;

	private EventView selectedEventView;

	//WeekView weekView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_calendar, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		fab = getView().findViewById(R.id.floatingActionButton);

		fab.setOnClickListener((l) -> {
			if(!isSubjectDatabaseEmpty()) {
				Intent newSubjectScheduleIntent = new Intent(getContext(), NewClassTimeActivity.class);
				startActivityForResult(newSubjectScheduleIntent, NEW_CLASS_EVENT_REQUEST_CODE);
			} else {
				displayEmptyDatabaseError();
			}
		});

		// Sets the toolbar name and item checked on nav bar
		AppCompatActivity activity = (AppCompatActivity) getContext();
		activity.getSupportActionBar().setTitle(getString(R.string.schedule));
		MainActivity.navigationView.setCheckedItem(R.id.nav_schedules);

		setupScheduleView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		getActivity();
		if(resultCode == DELETE_EVENT_RESULT_CODE) {
			weekView.removeView(selectedEventView);
		}
		if(resultCode == NEW_CLASS_EVENT_RESULT_CODE) {
			ClassTime classTime = new ClassTime(
					data.getIntExtra("subjectId", 0),
					data.getIntExtra("timeId", 0),
					data.getIntExtra("dayOfTheWeek", 0),
					data.getStringExtra("startTime"),
					data.getStringExtra("endTime")
			);

			WeekData weekData = new WeekData();
			weekData.add(createEvent(classTime));
			weekView.addLessonsToTimetable(weekData);
		}
		if(resultCode == SchedulesFragment.UPDATE_CLASS_EVENT_RESULT_CODE) {
			weekView.removeView(selectedEventView);

			ClassTime classTime = new ClassTime(
					data.getIntExtra("subjectId", 0),
					data.getIntExtra("timeId", 0),
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
		DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

		boolean isSubjectDatabaseEmpty = databaseHelper.getAllSubjects().isEmpty();
		databaseHelper.close();

		return isSubjectDatabaseEmpty;
	}

	private void setupScheduleView() {
		AndroidThreeTen.init(getContext());

		weekView = getView().findViewById(R.id.week_view_foo);

		databaseHelper = new DatabaseHelper(getContext());
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
			Intent editSubjectIntent = new Intent(getContext(), EditClassTimeActivity.class);
			databaseHelper = new DatabaseHelper(getContext());

			ClassTime classTime = databaseHelper.getClassTime((int) eventView.getEvent().getId());
			editSubjectIntent.putExtra("timeId", classTime.getTimeId());
			editSubjectIntent.putExtra("oldSubjectId", classTime.getSubjectId());
			editSubjectIntent.putExtra("oldDayOfTheWeek", classTime.getDayOfTheWeek());
			editSubjectIntent.putExtra("oldStartTime", classTime.getStartTime());
			editSubjectIntent.putExtra("oldEndTime", classTime.getEndTime());

			selectedEventView = eventView;
			startActivityForResult(editSubjectIntent, EDIT_CLASS_EVENT_REQUEST_CODE);

			return null;
		});
	}

	private Event.Single createEvent(ClassTime classTime) {
		DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

		Event.Single event = new Event.Single(
				classTime.getTimeId(),
				LocalDate.now(),
				"",
				databaseHelper.getSubject(classTime.getSubjectId()).getAbbreviation(),
				"",
				classTime.getDayOfTheWeek(),
				LocalTime.of(classTime.getStartTimeHour(), classTime.getStartTimeMinute()),
				LocalTime.of(classTime.getEndTimeHour(), classTime.getEndTimeMinute()),
				null, null,
				Color.WHITE,
				Tools.getRandomColorFromArray(R.array.schedule_colors, getContext())
		);
		databaseHelper.close();

		return(event);
	}
}
