package calegari.murilo.sistema_academico.subjects;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import calegari.murilo.sistema_academico.databases.DatabaseHelper;
import calegari.murilo.sistema_academico.R;
import calegari.murilo.sistema_academico.subjects.steps.SubjectAbbreviationStep;
import calegari.murilo.sistema_academico.subjects.steps.SubjectNameStep;
import calegari.murilo.sistema_academico.subjects.steps.SubjectProfessorStep;
import calegari.murilo.qacadscrapper.utils.Subject;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class NewSubjectActivity extends AppCompatActivity implements StepperFormListener {

    private SubjectNameStep newSubjectName;
    private SubjectAbbreviationStep newSubjectAbbreviation;
    private SubjectProfessorStep newSubjectProfessor;
    private VerticalStepperFormView verticalStepperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subject);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Finishes the Activity
        toolbar.setNavigationOnClickListener(v -> finish());

        /*
        Used library: VerticalStepperForm, available at:
        https://github.com/ernestoyaquello/VerticalStepperForm
        */

        // Create the steps
        newSubjectName = new SubjectNameStep(getResources().getString(R.string.name));
        newSubjectAbbreviation = new SubjectAbbreviationStep(getResources().getString(R.string.abbreviation));
        newSubjectProfessor = new SubjectProfessorStep(getResources().getString(R.string.professor));

        // Find the form view, set it up and initialize it.
        verticalStepperForm = findViewById(R.id.stepper_form);
        verticalStepperForm
                .setup(this, newSubjectName, newSubjectAbbreviation, newSubjectProfessor)
                .lastStepNextButtonText(getString(R.string.subject_confirm_save_button))
                .displayCancelButtonInLastStep(true)
                .lastStepCancelButtonText(getString(R.string.cancel))
                .stepNextButtonText(getString(R.string.next))
                .init();
    }

    @Override
    public void onCompletedForm() {
        // This method will be called when the user clicks on the last confirmation button of the
        // form in an attempt to save or send the data.

        // Sends data to database

        DatabaseHelper database = new DatabaseHelper(this);
        database.insertSubject(
                new Subject(
                		newSubjectName.getStepDataAsHumanReadableString(),
						newSubjectProfessor.getStepDataAsHumanReadableString(),
                        newSubjectAbbreviation.getStepDataAsHumanReadableString()
                )
        );
        database.close();
        finish();
    }

    @Override
    public void onCancelledForm() {
        // This method will be called when the user clicks on the cancel button of the form.
        finish();
    }

}
