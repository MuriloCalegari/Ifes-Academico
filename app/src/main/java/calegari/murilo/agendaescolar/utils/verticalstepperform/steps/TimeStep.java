package calegari.murilo.agendaescolar.utils.verticalstepperform.steps;

import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import calegari.murilo.agendaescolar.R;
import ernestoyaquello.com.verticalstepperform.Step;

public class TimeStep extends Step<TimeStep.TimeHolder> {

	private static final int DEFAULT_HOURS = 7;
	private static final int DEFAULT_MINUTES = 0;

	private TextView timeTextView;
	private TimePickerDialog timePicker;

	private int timeHour;
	private int timeMinutes;

	public TimeStep(String title) {
		this(title, "");
	}

	public TimeStep(String title, String subtitle) {
		super(title, subtitle);

		timeHour = DEFAULT_HOURS;
		timeMinutes = DEFAULT_MINUTES;
	}

	@NonNull
	@Override
	protected View createStepContentLayout() {

		// We create this step view by inflating an XML layout
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View timeStepContent = inflater.inflate(R.layout.step_time_layout, null, false);
		timeTextView = timeStepContent.findViewById(R.id.time);
		setupTime();

		return timeStepContent;
	}

	private void setupTime() {
		if (timePicker == null) {
			timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							timeHour = hourOfDay;
							timeMinutes = minute;

							updateTimeText();
							markAsCompletedOrUncompleted(true);
						}
					}, timeHour, timeMinutes, true);
			updateTimeText();
		} else {
			timePicker.updateTime(timeHour, timeMinutes);
		}

		if (timeTextView != null) {
			timeTextView.setOnClickListener(v -> timePicker.show());
		}
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
	public TimeHolder getStepData() {
		return new TimeHolder(timeHour, timeMinutes);
	}

	@Override
	public String getStepDataAsHumanReadableString() {
		String hourString = ((timeHour > 9) ?
				String.valueOf(timeHour) : ("0" + timeHour));
		String minutesString = ((timeMinutes > 9) ?
				String.valueOf(timeMinutes) : ("0" + timeMinutes));
		return hourString + ":" + minutesString;
	}

	@Override
	public void restoreStepData(TimeHolder data) {
		timeHour = data.hour;
		timeMinutes = data.minutes;

		timePicker.updateTime(timeHour, timeMinutes);
		updateTimeText();

		markAsCompletedOrUncompleted(false);
	}

	@Override
	protected IsDataValid isStepDataValid(TimeHolder stepData) {
		return new IsDataValid(true);
	}

	private void updateTimeText() {
		timeTextView.setText(getStepDataAsHumanReadableString());
	}

	public static class TimeHolder {

		private int hour;
		private int minutes;

		public int getHour() {
			return hour;
		}

		public void setHour(int hour) {
			this.hour = hour;
		}

		public int getMinutes() {
			return minutes;
		}

		public void setMinutes(int minutes) {
			this.minutes = minutes;
		}

		public TimeHolder(int hour, int minutes) {
			this.hour = hour;
			this.minutes = minutes;
		}

		public String getDateTime() {
			return String.valueOf(hour) + ":" + String.valueOf(minutes);
		}
	}
}
