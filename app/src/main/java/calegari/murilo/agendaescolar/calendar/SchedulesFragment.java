package calegari.murilo.agendaescolar.calendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import calegari.murilo.agendaescolar.BaseFragment;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.R;
import de.tobiasschuerg.weekview.data.Event;
import de.tobiasschuerg.weekview.data.WeekData;
import de.tobiasschuerg.weekview.view.WeekView;

public class SchedulesFragment extends BaseFragment {

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

		FloatingActionButton fab = getView().findViewById(R.id.floatingActionButton);

		fab.setOnClickListener((l) -> {
			Intent newSubjectScheduleIntent = new Intent(view.getContext(), NewClassEventActivity.class);
			view.getContext().startActivity(newSubjectScheduleIntent);
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

	private void setupScheduleView() {

		AndroidThreeTen.init(getContext());

		WeekView weekView = getView().findViewById(R.id.week_view_foo);
		WeekData data = new WeekData();

		Event.Single event = new Event.Single(
				1,
				LocalDate.now(),
				"title",
				"Física",
				"sub",
				1,
				LocalTime.of(7,0),
				LocalTime.of(8,50),
				null,
				null,
				Color.WHITE,
				Color.BLACK
		);

		data.add(event);

		weekView.addLessonsToTimetable(data);
	}
}
