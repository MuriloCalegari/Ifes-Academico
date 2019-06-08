package calegari.murilo.sistema_academico.calendar;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import org.threeten.bp.LocalTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.sistema_academico.R;
import calegari.murilo.sistema_academico.databases.DatabaseHelper;
import calegari.murilo.sistema_academico.utils.verticalstepperform.steps.DayPickerStep;
import calegari.murilo.sistema_academico.utils.verticalstepperform.steps.SubjectSpinnerStep;
import calegari.murilo.sistema_academico.utils.verticalstepperform.steps.TimeStep;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class NewClassTimeActivity extends AppCompatActivity implements StepperFormListener {

	protected TimeStep endTimeStep;
	protected TimeStep startTimeStep;
	protected DayPickerStep dayPickerStep;
	protected SubjectSpinnerStep subjectSpinnerStep;
	protected VerticalStepperFormView verticalStepperForm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_class_event);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Finishes the Activity
		toolbar.setNavigationOnClickListener((v) -> finish());

		// Create the steps
		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		subjectSpinnerStep = new SubjectSpinnerStep(getString(R.string.subject), databaseHelper.getAllSubjects());
		databaseHelper.close();

		dayPickerStep = new DayPickerStep(getString(R.string.day_of_the_week), true);
		startTimeStep = new TimeStep(getString(R.string.start_time), getString(R.string.which_time_start));
		endTimeStep = new TimeStep(getString(R.string.end_time), getString(R.string.which_time_end)) {
			@Override
			protected IsDataValid isStepDataValid(TimeHolder stepData) {
				return new IsDataValid(isTimeIntervalValid(startTimeStep.getStepData(), stepData), getString(R.string.end_time_before_start_time));
			}
		};

		// Find the form view, set it up and initialize it.
		verticalStepperForm = findViewById(R.id.stepper_form);
		verticalStepperForm
				.setup(this, subjectSpinnerStep, dayPickerStep, startTimeStep, endTimeStep)
				.lastStepNextButtonText(getString(R.string.class_event_save_button))
				.displayCancelButtonInLastStep(true)
				.lastStepCancelButtonText(getString(R.string.cancel))
				.stepNextButtonText(getString(R.string.next))
				.init();

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
					dayOfTheWeek,
					startTime,
					endTime
			);

			DatabaseHelper databaseHelper = new DatabaseHelper(this);
			int id = databaseHelper.insertClassTime(classTime);
			classTime.setTimeId(id);
			databaseHelper.close();

			createNewEventOnReturn(classTime);

			finish();
		} else {
			verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
			displayStartTimeAfterEndTimeError();
		}
	}

	private void createNewEventOnReturn(ClassTime classTime) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("timeId", classTime.getTimeId());
		returnIntent.putExtra("subjectId", classTime.getSubjectId());
		returnIntent.putExtra("dayOfTheWeek", classTime.getDayOfTheWeek());
		returnIntent.putExtra("startTime", classTime.getStartTime());
		returnIntent.putExtra("endTime", classTime.getEndTime());
		setResult(SchedulesFragment.NEW_CLASS_EVENT_RESULT_CODE, returnIntent);
	}

	protected void displayStartTimeAfterEndTimeError() {
		Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.end_time_before_start_time ), Snackbar.LENGTH_SHORT);
		snackbar.show();
	}

	@Override
	public void onCancelledForm() {
		finish();
	}

	public boolean isTimeIntervalValid(TimeStep.TimeHolder startTime, TimeStep.TimeHolder endTime) {
		LocalTime startLocalTime = LocalTime.of(startTime.getHour(),startTime.getMinutes());
		LocalTime endLocalTime = LocalTime.of(endTime.getHour(), endTime.getMinutes());

		return endLocalTime.isAfter(startLocalTime);
	}
}
