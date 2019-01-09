package calegari.murilo.agendaescolar.subjects;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.subjects.steps.SubjectAbbreviationStep;
import calegari.murilo.agendaescolar.subjects.steps.SubjectNameStep;
import calegari.murilo.agendaescolar.subjects.steps.SubjectProfessorStep;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_subject);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Finishes the Activity
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

		// Gets information from which input the user would like to edit
		oldSubjectName = getIntent().getStringExtra("oldSubjectName");
		oldSubjectProfessor = getIntent().getStringExtra("oldSubjectProfessor");
		oldSubjectAbbreviation = getIntent().getStringExtra("oldSubjectAbbreviation");

		// Create the steps
		subjectNameStep = new SubjectNameStep(getResources().getString(R.string.name));
		subjectAbbreviationStep = new SubjectAbbreviationStep(getResources().getString(R.string.abbreviation), true, oldSubjectAbbreviation);
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

		ImageButton deleteButton = findViewById(R.id.imageButton);

		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				deleteSubject(oldSubjectAbbreviation);
			}
		});

	}

	@Override
	public void onCompletedForm() {
		// This method will be called when the user clicks on the last confirmation button of the
		// form in an attempt to save or send the data.

		// Sends data to database
		SubjectDatabaseHelper subjectDbHelper = new SubjectDatabaseHelper(this);
		subjectDbHelper.updateData(
				oldSubjectAbbreviation,
				new Subject(
						subjectNameStep.getStepDataAsHumanReadableString(),
						subjectProfessorStep.getStepDataAsHumanReadableString(),
						subjectAbbreviationStep.getStepDataAsHumanReadableString()
				)
		);

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

	public void deleteSubject(final String subjectAbbreviation) {
		final SubjectDatabaseHelper subjectDbHelper = new SubjectDatabaseHelper(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
				.setTitle(getString(R.string.confirm_subject_delete_title))
				.setMessage(getString(R.string.confirm_subject_delete_message))
				.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						subjectDbHelper.removeData(subjectAbbreviation);
						// TODO: Remove all entries in grades database
						finish();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				})
				.show();
	}
}
