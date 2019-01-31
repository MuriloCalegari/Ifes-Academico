package calegari.murilo.agendaescolar.subjectgrades;

import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.SubjectGradesDatabaseHelper;
import calegari.murilo.agendaescolar.utils.verticalstepperform.steps.GradeDescriptionStep;
import calegari.murilo.agendaescolar.subjectgrades.steps.GradeIsExtraCreditStep;
import calegari.murilo.agendaescolar.subjectgrades.steps.GradeMaximumStep;
import calegari.murilo.agendaescolar.subjectgrades.steps.GradeObtainedStep;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class EditSubjectGradeActivity extends AppCompatActivity implements StepperFormListener {

	private GradeDescriptionStep newGradeDescription;
	private GradeObtainedStep newObtainedGrade;
	private GradeMaximumStep newMaximumGrade;
	private GradeIsExtraCreditStep newIsExtraCredit;

	Integer oldGradeId;
	String subjectAbbreviation;
	String oldGradeDescription;
	Float oldObtainedGrade;
	Float oldMaximumGrade;
	boolean oldIsExtraCredit;

	private VerticalStepperFormView verticalStepperForm;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_subject_grade);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		setTitle(getString(R.string.title_activity_edit_subject_grade));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		toolbar.setNavigationOnClickListener(v -> finish());

		/*
        Used library: VerticalStepperForm, available at:
        https://github.com/ernestoyaquello/VerticalStepperForm
        */

		// Gets information from which input the user would like to edit
		oldGradeId = getIntent().getIntExtra("oldGradeId", 0);
		subjectAbbreviation = getIntent().getStringExtra("subjectAbbreviation");
		oldGradeDescription = getIntent().getStringExtra("oldGradeDescription");
		oldObtainedGrade = getIntent().getFloatExtra("oldObtainedGrade", 0);
		oldMaximumGrade = getIntent().getFloatExtra("oldMaximumGrade", 0);
		oldIsExtraCredit = getIntent().getBooleanExtra("oldIsExtraCredit", false);

		// Create the steps
		newGradeDescription = new GradeDescriptionStep(getString(R.string.description));
		newObtainedGrade = new GradeObtainedStep(getString(R.string.obtained_grade));
		newMaximumGrade = new GradeMaximumStep(getString(R.string.maximum_grade));
		newIsExtraCredit = new GradeIsExtraCreditStep(getString(R.string.is_extra_credit), getString(R.string.extra_credit_subtitle));

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

		reloadSteps(oldGradeDescription, oldObtainedGrade, oldMaximumGrade, oldIsExtraCredit);

		ImageButton deleteButton = findViewById(R.id.imageButton);

		deleteButton.setOnClickListener(view -> deleteGrade(oldGradeId));
	}

	@Override
	public void onCompletedForm() {
		// This method will be called when the user clicks on the last confirmation button of the
		// form in an attempt to save or send the data.

		// Updates data on database
		SubjectGradesDatabaseHelper subjectGradesDatabase = new SubjectGradesDatabaseHelper(this);
		subjectGradesDatabase.updateData(
				oldGradeId,
				new SubjectGrade(
						subjectAbbreviation,
						newGradeDescription.getStepData(),
						newObtainedGrade.getStepData(),
						newMaximumGrade.getStepData(),
						newIsExtraCredit.getStepData()
				)
		);

		finish();
	}

	@Override
	public void onCancelledForm() {
		// This method will be called when the user clicks on the cancel button of the form.
		finish();
	}

	private void deleteGrade(Integer oldGradeId) {
		final SubjectGradesDatabaseHelper subjectGradesDatabase = new SubjectGradesDatabaseHelper(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
				.setTitle(getString(R.string.confirm_subject_grade_delete_title))
				.setMessage(getString(R.string.confirm_subject_grade_delete_message))
				.setPositiveButton(getString(R.string.delete), ((dialogInterface, i) -> {
					subjectGradesDatabase.removeData(oldGradeId);
					subjectGradesDatabase.close();
					finish();
				}))
				.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {})
				.show();
	}

	private void reloadSteps(String oldGradeDescription, float oldObtainedGrade, float oldMaximumGrade, boolean oldIsExtraCredit) {
		newGradeDescription.restoreStepData(oldGradeDescription);
		newObtainedGrade.restoreStepData(oldObtainedGrade);
		newMaximumGrade.restoreStepData(oldMaximumGrade);
		newIsExtraCredit.restoreStepData(oldIsExtraCredit);
	}
}
