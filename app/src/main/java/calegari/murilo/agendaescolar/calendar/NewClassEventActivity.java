package calegari.murilo.agendaescolar.calendar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.DayPickerStep;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.DescriptionStep;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class NewClassEventActivity extends AppCompatActivity implements StepperFormListener {

	private DescriptionStep descriptionStep;
	private DayPickerStep dayPickerStep;

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
		dayPickerStep = new DayPickerStep("Dia da semana", true);

		// Find the form view, set it up and initialize it.
		VerticalStepperFormView verticalStepperForm = findViewById(R.id.stepper_form);
		verticalStepperForm
				.setup(this, dayPickerStep)
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
