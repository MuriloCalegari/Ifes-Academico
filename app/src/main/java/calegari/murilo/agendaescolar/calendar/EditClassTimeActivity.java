package calegari.murilo.agendaescolar.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.DatabaseHelper;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.TimeStep;

public class EditClassTimeActivity extends NewClassTimeActivity {

	private ImageButton deleteButton;

	private int classTimeId;
	private int oldSubjectId;
	private int oldDayOfTheWeek;
	private String oldStartTime;
	private String oldEndTime;

	private ClassTime oldClassTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		deleteButton = findViewById(R.id.deleteButton);
		deleteButton.setVisibility(View.VISIBLE);

		classTimeId = getIntent().getIntExtra("timeId", 0);
		oldSubjectId = getIntent().getIntExtra("oldSubjectId", 0);
		oldDayOfTheWeek = getIntent().getIntExtra("oldDayOfTheWeek", 0);
		oldStartTime = getIntent().getStringExtra("oldStartTime");
		oldEndTime = getIntent().getStringExtra("oldEndTime");

		oldClassTime = new ClassTime(
				oldSubjectId,
				classTimeId,
				oldDayOfTheWeek,
				oldStartTime,
				oldEndTime
		);

		deleteButton.setOnClickListener(view -> deleteClassTime(oldClassTime.getTimeId()));

		reloadSteps(oldClassTime);
	}

	private void reloadSteps(ClassTime oldClassTime) {
		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		this.subjectSpinnerStep.restoreStepData(databaseHelper.getSubject(oldClassTime.getSubjectId()));
		databaseHelper.close();

		boolean[] markedDays = new boolean[7];

		markedDays[oldClassTime.getDayOfTheWeek() - 1] = true; // -1 because getDayOfTheWeek return counting sunday as day 1
		this.dayPickerStep.restoreStepData(markedDays);

		this.startTimeStep.restoreStepData(new TimeStep.TimeHolder(oldClassTime.getStartTimeHour(), oldClassTime.getStartTimeMinute()));
		this.endTimeStep.restoreStepData(new TimeStep.TimeHolder(oldClassTime.getEndTimeHour(), oldClassTime.getEndTimeMinute()));
	}

	private void deleteClassTime(int oldClassTimeId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
				.setTitle(getString(R.string.confirm_class_event_delete_title))
				.setMessage(R.string.confirm_class_event_delete_message)
				.setPositiveButton(R.string.delete, (dialogInterface, i) -> {
					DatabaseHelper database = new DatabaseHelper(this);
					database.deleteClassTime(oldClassTimeId);
					database.close();
					deleteEventOnReturn();
					finish();
				})
				.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {})
				.show();
	}

	private void deleteEventOnReturn() {
		Intent returnIntent = new Intent();
		setResult(SchedulesFragment.DELETE_EVENT_RESULT_CODE, returnIntent);
	}

	@Override
	public void onCompletedForm() {
		String startTime = startTimeStep.getStepData().getDateTime();
		String endTime = endTimeStep.getStepData().getDateTime();

		// Verification is necessary since the user might set up the start time after setting the end time
		if(isTimeIntervalValid(startTimeStep.getStepData(), endTimeStep.getStepData())) {
			int dayOfTheWeek = dayPickerStep.getDayOfTheWeek();

			int subjectId = subjectSpinnerStep.getStepData().getId();
			ClassTime classTime = new ClassTime(
					subjectId,
					classTimeId,
					dayOfTheWeek,
					startTime,
					endTime
			);

			DatabaseHelper databaseHelper = new DatabaseHelper(this);
			databaseHelper.updateClassTime(classTime);
			databaseHelper.close();

			updateEventOnReturn(classTime);
			finish();
		} else {
			verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
			displayStartTimeAfterEndTimeError();
		}
	}

	private void updateEventOnReturn(ClassTime classTime) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("timeId", classTime.getTimeId());
		returnIntent.putExtra("subjectId", classTime.getSubjectId());
		returnIntent.putExtra("dayOfTheWeek", classTime.getDayOfTheWeek());
		returnIntent.putExtra("startTime", classTime.getStartTime());
		returnIntent.putExtra("endTime", classTime.getEndTime());
		setResult(SchedulesFragment.UPDATE_CLASS_EVENT_RESULT_CODE, returnIntent);
	}
}
