package calegari.murilo.ifes_academico.subjects;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.ifes_academico.databases.DatabaseHelper;
import calegari.murilo.ifes_academico.R;
import calegari.murilo.ifes_academico.subjects.steps.SubjectAbbreviationStep;
import calegari.murilo.ifes_academico.subjects.steps.SubjectNameStep;
import calegari.murilo.ifes_academico.subjects.steps.SubjectProfessorStep;
import calegari.murilo.qacadscrapper.utils.Subject;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class EditSubjectActivity extends AppCompatActivity implements StepperFormListener {

	private SubjectNameStep subjectNameStep;
	private SubjectAbbreviationStep subjectAbbreviationStep;
	private SubjectProfessorStep subjectProfessorStep;
	private VerticalStepperFormView verticalStepperForm;

	String oldSubjectName;
	String oldSubjectProfessor;
	String oldSubjectAbbreviation;
	int subjectId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_subject);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Finishes the Activity
		toolbar.setNavigationOnClickListener(v -> finish());

        /*
        Used library: VerticalStepperForm, available at:
        https://github.com/ernestoyaquello/VerticalStepperForm
        */

		// Gets information from which input the user would like to edit
		oldSubjectName = getIntent().getStringExtra("oldSubjectName");
		oldSubjectProfessor = getIntent().getStringExtra("oldSubjectProfessor");
		oldSubjectAbbreviation = getIntent().getStringExtra("oldSubjectAbbreviation");
		subjectId = getIntent().getIntExtra("subjectId", 0);

		// Create the steps
		subjectNameStep = new SubjectNameStep(getResources().getString(R.string.name));
		subjectAbbreviationStep = new SubjectAbbreviationStep(getResources().getString(R.string.abbreviation));
		subjectProfessorStep = new SubjectProfessorStep(getResources().getString(R.string.professor));

		// Find the form view, set it up and initialize it.
		verticalStepperForm = findViewById(R.id.stepper_form);
		verticalStepperForm
				.setup(this, subjectNameStep, subjectAbbreviationStep, subjectProfessorStep)
				.lastStepNextButtonText(getString(R.string.subject_confirm_save_button))
				.displayCancelButtonInLastStep(true)
				.lastStepCancelButtonText(getString(R.string.cancel))
				.stepNextButtonText(getString(R.string.next))
				.init();

		reloadSteps(oldSubjectName, oldSubjectProfessor, oldSubjectAbbreviation);

		ImageButton deleteButton = findViewById(R.id.deleteButton);

		deleteButton.setOnClickListener(view -> deleteSubject(subjectId));

	}

	@Override
	public void onCompletedForm() {
		// This method will be called when the user clicks on the last confirmation button of the
		// form in an attempt to save or send the data.

		// Sends data to database
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		dbHelper.updateSubject(
				subjectId,
				new Subject(
						subjectNameStep.getStepDataAsHumanReadableString(),
						subjectProfessorStep.getStepDataAsHumanReadableString(),
						subjectAbbreviationStep.getStepDataAsHumanReadableString()
				)
		);
		dbHelper.close();

		finish();
	}

	@Override
	public void onCancelledForm() {
		// This method will be called when the user clicks on the cancel button of the form.
		finish();
	}

	public void reloadSteps(String subjectName, String subjectProfessor, String subjectAbbreviation) {
		subjectNameStep.restoreStepData(subjectName);
		subjectProfessorStep.restoreStepData(subjectProfessor);
		subjectAbbreviationStep.restoreStepData(subjectAbbreviation);
	}

	public void deleteSubject(final int subjectId) {
		final DatabaseHelper dbHelper = new DatabaseHelper(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
				.setTitle(getString(R.string.confirm_subject_delete_title))
				.setMessage(getString(R.string.confirm_subject_delete_message))
				.setPositiveButton(R.string.delete, (dialogInterface, i) -> {
					dbHelper.removeSubject(subjectId);
					dbHelper.close();
					finish();
				})
				.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dbHelper.close())
				.show();
	}
}
