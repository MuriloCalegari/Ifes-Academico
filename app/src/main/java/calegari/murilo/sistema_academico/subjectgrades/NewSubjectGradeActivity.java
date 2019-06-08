package calegari.murilo.sistema_academico.subjectgrades;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.sistema_academico.R;
import calegari.murilo.sistema_academico.databases.DatabaseHelper;
import calegari.murilo.sistema_academico.utils.verticalstepperform.steps.DescriptionStep;
import calegari.murilo.sistema_academico.subjectgrades.steps.GradeIsExtraCreditStep;
import calegari.murilo.sistema_academico.subjectgrades.steps.GradeMaximumStep;
import calegari.murilo.sistema_academico.subjectgrades.steps.GradeObtainedStep;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class NewSubjectGradeActivity extends AppCompatActivity implements StepperFormListener {

	private DescriptionStep newGradeDescription;
	private GradeObtainedStep newObtainedGrade;
	private GradeMaximumStep newMaximumGrade;
	private GradeIsExtraCreditStep newIsExtraGrade;

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
		toolbar.setNavigationOnClickListener(v -> finish());

		 /*
        Used library: VerticalStepperForm, available at:
        https://github.com/ernestoyaquello/VerticalStepperForm
        */

		// Create the steps
		newGradeDescription = new DescriptionStep(getString(R.string.description));
		newObtainedGrade = new GradeObtainedStep(getString(R.string.obtained_grade));
		newMaximumGrade = new GradeMaximumStep(getString(R.string.maximum_grade));
		newIsExtraGrade = new GradeIsExtraCreditStep(getString(R.string.is_extra_credit), getString(R.string.extra_credit_subtitle));

		// Find the form view, set it up and initialize it
		verticalStepperForm = findViewById(R.id.stepper_form);
		verticalStepperForm
				.setup(this, newGradeDescription, newObtainedGrade, newMaximumGrade, newIsExtraGrade)
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
		DatabaseHelper db = new DatabaseHelper(this);

		SubjectGrade subjectGrade = new SubjectGrade();

		subjectGrade.setSubjectId(getIntent().getIntExtra("subjectId", 0));
		subjectGrade.setGradeDescription(newGradeDescription.getStepData());
		subjectGrade.setObtainedGrade(newObtainedGrade.getStepData());
		subjectGrade.setMaximumGrade(newMaximumGrade.getStepData());
		subjectGrade.setIsExtraGrade(newIsExtraGrade.getStepData());

		db.insertGrade(subjectGrade);
		db.close();

		finish();
	}

	@Override
	public void onCancelledForm() {
		// This method will be called when the user clicks on the cancel button of the form.
		finish();
	}
}
