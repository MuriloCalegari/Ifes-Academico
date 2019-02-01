package calegari.murilo.agendaescolar.calendar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.DayPickerStep;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.SubjectSpinnerStep;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.TimeStep;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class NewClassEventActivity extends AppCompatActivity implements StepperFormListener {

	private SubjectSpinnerStep spinnerStep;

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
		SubjectSpinnerStep subjectSpinnerStep = new SubjectSpinnerStep(getString(R.string.subject), subjectDatabaseHelper.getAllSubjects());
		subjectDatabaseHelper.close();

		DayPickerStep dayPickerStep = new DayPickerStep(getString(R.string.day_of_the_week), true);
		TimeStep startTimeStep = new TimeStep(getString(R.string.start_time), getString(R.string.which_time_start));
		TimeStep endTimeStep = new TimeStep(getString(R.string.end_time), getString(R.string.which_time_end));


		// Find the form view, set it up and initialize it.
		VerticalStepperFormView verticalStepperForm = findViewById(R.id.stepper_form);
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

	}

	@Override
	public void onCancelledForm() {
		finish();
	}
}
