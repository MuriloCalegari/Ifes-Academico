package calegari.murilo.agendaescolar.calendar;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import org.threeten.bp.LocalTime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.DatabaseHelper;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.DayPickerStep;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.SubjectSpinnerStep;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.TimeStep;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class NewClassTimeActivity extends AppCompatActivity implements StepperFormListener {

	private SubjectSpinnerStep spinnerStep;
	private TimeStep endTimeStep;
	private TimeStep startTimeStep;
	private DayPickerStep dayPickerStep;
	private SubjectSpinnerStep subjectSpinnerStep;
	private VerticalStepperFormView verticalStepperForm;

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
		SubjectDatabaseHelper subjectDatabaseHelper = new SubjectDatabaseHelper(this);
		subjectSpinnerStep = new SubjectSpinnerStep(getString(R.string.subject), subjectDatabaseHelper.getAllSubjects());
		subjectDatabaseHelper.close();

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
			int dayOfTheWeek = 0;
			// loops through the marked days and get the unique day marked
			for(int i = 0; i < dayPickerStep.getStepData().length; i++) {
				if(dayPickerStep.getStepData()[i]) {
					if(i == 6) { // If it's sunday
						// Library counts days from sunday, but I chose to display the date picker having sunday as the last day,
						// since the library also displays (but doesn't consider) sunday as the last day of the week
						dayOfTheWeek = 0;
					} else {
						dayOfTheWeek = i + 2; // +2 because the library counts day of the week with sunday as day 1
					}
					break;
				}
			}
			int subjectId = subjectSpinnerStep.getStepData().getId();
			ClassTime classTime = new ClassTime(
					subjectId,
					dayOfTheWeek,
					startTime,
					endTime
			);

			DatabaseHelper databaseHelper = new DatabaseHelper(this);
			databaseHelper.insertClassTime(classTime);
			databaseHelper.close();

			Intent returnIntent = new Intent();
			returnIntent.putExtra("subjectId", subjectId);
			returnIntent.putExtra("dayOfTheWeek", dayOfTheWeek);
			returnIntent.putExtra("startTime", startTime);
			returnIntent.putExtra("endTime", endTime);
			setResult(RESULT_OK, returnIntent);

			finish();
		} else {
			verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
			displayStartTimeAfterEndTimeError();
		}
	}

	private void displayStartTimeAfterEndTimeError() {
		Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.end_time_before_start_time ), Snackbar.LENGTH_SHORT);
		snackbar.show();
	}

	@Override
	public void onCancelledForm() {
		finish();
	}

	private boolean isTimeIntervalValid(TimeStep.TimeHolder startTime, TimeStep.TimeHolder endTime) {
		LocalTime startLocalTime = LocalTime.of(startTime.getHour(),startTime.getMinutes());
		LocalTime endLocalTime = LocalTime.of(endTime.getHour(), endTime.getMinutes());

		return endLocalTime.isAfter(startLocalTime);
	}
}
