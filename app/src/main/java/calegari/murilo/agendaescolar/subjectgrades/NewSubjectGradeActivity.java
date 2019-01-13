package calegari.murilo.agendaescolar.subjectgrades;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.SubjectGradesDatabaseHelper;
import calegari.murilo.agendaescolar.subjectgrades.steps.GradeDescriptionStep;
import calegari.murilo.agendaescolar.subjectgrades.steps.GradeIsExtraCreditStep;
import calegari.murilo.agendaescolar.subjectgrades.steps.GradeMaximumStep;
import calegari.murilo.agendaescolar.subjectgrades.steps.GradeObtainedStep;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class NewSubjectGradeActivity extends AppCompatActivity implements StepperFormListener {

	private GradeDescriptionStep newGradeDescription;
	private GradeObtainedStep newObtainedGrade;
	private GradeMaximumStep newMaximumGrade;
	private GradeIsExtraCreditStep newIsExtraCredit;

	private VerticalStepperFormView verticalStepperForm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_subject_grade);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		setTitle(getString(R.string.title_activity_new_subject_grade));

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Finishes the activity
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		 /*
        Used library: VerticalStepperForm, available at:
        https://github.com/ernestoyaquello/VerticalStepperForm
        */

		// Create the steps
		newGradeDescription = new GradeDescriptionStep(getString(R.string.description));
		newObtainedGrade = new GradeObtainedStep(getString(R.string.obtained_grade));
		newMaximumGrade = new GradeMaximumStep(getString(R.string.maximum_grade));
		newIsExtraCredit = new GradeIsExtraCreditStep(getString(R.string.is_extra_credit));

		// Find the form view, set it up and initialize it
		verticalStepperForm = findViewById(R.id.stepper_form);
		verticalStepperForm
				.setup(this, newGradeDescription, newObtainedGrade, newMaximumGrade, newIsExtraCredit)
				.includeConfirmationStep(false)
				.lastStepNextButtonText(getString(R.string.save_grade))
				.lastStepCancelButtonText(getString(R.string.cancel))
				.displayCancelButtonInLastStep(true)
				.stepNextButtonText(getString(R.string.next))
				.init();
	}

	@Override
	public void onCompletedForm() {
		// This method will be called when the user clicks on the last confirmation button of the
		// form in an attempt to save or send the data.

		// Sends data to database
		SubjectGradesDatabaseHelper subjectGradeDbHelper = new SubjectGradesDatabaseHelper(this);

		SubjectGrade subjectGrade = new SubjectGrade(
				getIntent().getStringExtra("subjectAbbreviation"),
				newGradeDescription.getStepData(),
				newObtainedGrade.getStepData(),
				newMaximumGrade.getStepData(),
				newIsExtraCredit.getStepData()
		);

		subjectGradeDbHelper.insertData(subjectGrade);
		finish();
	}

	@Override
	public void onCancelledForm() {
		// This method will be called when the user clicks on the cancel button of the form.
		finish();
	}
}
