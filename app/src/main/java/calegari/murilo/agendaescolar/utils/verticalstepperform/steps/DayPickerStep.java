package calegari.murilo.agendaescolar.utils.verticalstepperform.steps;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import calegari.murilo.agendaescolar.R;
import ernestoyaquello.com.verticalstepperform.Step;

public class DayPickerStep extends Step<boolean[]> {

	private boolean[] markDay;
	private View daysStepContent;
	private boolean markSingleDay = false; // By default you can mark any number of markDay

	private boolean firstSetup;

	public DayPickerStep(String title) {
		this(title, "");
	}

	public DayPickerStep(String title, boolean markSingleDay) {
		this(title, "");
		this.markSingleDay = markSingleDay;
	}

	public DayPickerStep(String title, String subtitle, boolean markSingleDay) {
		this(title, subtitle);
		this.markSingleDay = markSingleDay;
	}

	public DayPickerStep(String title, String subtitle) {
		super(title, subtitle);
	}

	@NonNull
	@Override
	protected View createStepContentLayout() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		daysStepContent = inflater.inflate(R.layout.step_days_of_week_layout, null, false);
		setupDays();

		return daysStepContent;
	}

	@Override
	protected void onStepOpened(boolean animated) {
		// No need to do anything here
	}
	@Override
	protected void onStepClosed(boolean animated) {
		// No need to do anything here
	}
	@Override
	protected void onStepMarkedAsCompleted(boolean animated) {
		// No need to do anything here
	}
	@Override
	protected void onStepMarkedAsUncompleted(boolean animated) {
		// No need to do anything here
	}

	@Override
	public boolean[] getStepData() {
		return markDay;
	}

	@Override
	public String getStepDataAsHumanReadableString() {
		String[] weekDayStrings = getContext().getResources().getStringArray(R.array.week_days_extended);
		List<String> selectedWeekDayStrings = new ArrayList<>();
		for (int i = 0; i < weekDayStrings.length; i++) {
			if (markDay[i]) {
				selectedWeekDayStrings.add(weekDayStrings[i]);
			}
		}

		return TextUtils.join(", ", selectedWeekDayStrings);
	}

	@Override
	public void restoreStepData(boolean[] data) {
		markDay = data;
		setupDays();
	}

	@Override
	protected IsDataValid isStepDataValid(boolean[] stepData) {
		boolean thereIsAtLeastOneDaySelected = false;
		for(int i = 0; i < stepData.length && !thereIsAtLeastOneDaySelected; i++) {
			if(stepData[i]) {
				thereIsAtLeastOneDaySelected = true;
			}
		}

		return thereIsAtLeastOneDaySelected
				? new IsDataValid(true)
				: new IsDataValid(false, getContext().getString(R.string.error_alarm_days_min_days));
	}

	private void setupDays() {
		firstSetup = markDay == null;
		markDay = firstSetup ? new boolean[7] : markDay;

		final String[] weekDays = getContext().getResources().getStringArray(R.array.week_days);

		for(int i = 0; i < weekDays.length; i++) {
			final int index = i;
			final View dayLayout = getDayLayout(index);

			if (firstSetup) {
				// By default, we don't mark any markDay as activated
				markDay[index] = false;
			}

			updateDayLayout(index, dayLayout, false);

			if(dayLayout != null) {
				dayLayout.setOnClickListener((v) -> {
					firstSetup = false;
					markDay[index] = !markDay[index]; // inverts clicked attribute
					updateDayLayout(index, dayLayout, true);
					markAsCompletedOrUncompleted(true);
				});
			}

			final TextView dayText = dayLayout.findViewById(R.id.day);
			dayText.setText(weekDays[index]);
		}

	}

	private void unmarkAllDaysWithException(int dayExceptionIndex) {
		for(int i = 0; i < markDay.length; i++) {
			View dayLayout = getDayLayout(i);
			if(i != dayExceptionIndex) {
				markDay[i] = false;
			}
			unmarkDay(i, dayLayout, true);
		}
	}

	private void updateDayLayout(int dayIndex, View dayLayout, boolean useAnimations) {
		if(markSingleDay && !firstSetup) {
			unmarkAllDaysWithException(dayIndex);
		}
		if (markDay[dayIndex] || (markSingleDay && !firstSetup)) {
			markDay(dayIndex, dayLayout, useAnimations);
		} else {
			unmarkDay(dayIndex, dayLayout, useAnimations);
		}
	}

	private void markDay(int dayIndex, View dayLayout, boolean useAnimations) {
		markDay[dayIndex] = true;

		if (dayLayout != null) {
			Drawable bg = ContextCompat.getDrawable(getContext(), ernestoyaquello.com.verticalstepperform.R.drawable.circle_step_done);
			int colorPrimary = ContextCompat.getColor(getContext(), R.color.colorPrimary);
			bg.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
			dayLayout.setBackground(bg);

			TextView dayText = dayLayout.findViewById(R.id.day);
			dayText.setTextColor(Color.rgb(255, 255, 255));
		}
	}

	private void unmarkDay(int dayIndex, View dayLayout, boolean useAnimations) {
		markDay[dayIndex] = false;

		dayLayout.setBackgroundResource(0);

		TextView dayText = dayLayout.findViewById(R.id.day);
		int colour = ContextCompat.getColor(getContext(), R.color.colorPrimary);
		dayText.setTextColor(colour);
	}

	private View getDayLayout(int i) {
		int id = daysStepContent.getResources().getIdentifier(
				"day_" + i, "id", getContext().getPackageName()
		);

		return daysStepContent.findViewById(id);
	}
}
