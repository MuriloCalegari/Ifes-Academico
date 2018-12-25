package calegari.murilo.agendaescolar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import calegari.murilo.agendaescolar.SubjectSteps.SubjectAbbreviationStep;
import calegari.murilo.agendaescolar.SubjectSteps.SubjectNameStep;
import calegari.murilo.agendaescolar.SubjectSteps.SubjectProfessorStep;
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
        newSubjectName = new SubjectNameStep(getResources().getString(R.string.name));
        newSubjectAbbreviation = new SubjectAbbreviationStep(getResources().getString(R.string.abbreviation));
        newSubjectProfessor = new SubjectProfessorStep(getResources().getString(R.string.professor));

        // Find the form view, set it up and initialize it.
        verticalStepperForm = findViewById(R.id.stepper_form);
        verticalStepperForm.setup(this, newSubjectName, newSubjectAbbreviation, newSubjectProfessor).init();

    }

    @Override
    public void onCompletedForm() {
        // This method will be called when the user clicks on the last confirmation button of the
        // form in an attempt to save or send the data.

        Intent resultIntent = new Intent();

        // Sends obtained data to SubjectsFragment
        resultIntent.putExtra("newSubjectName",newSubjectName.getStepDataAsHumanReadableString());
        resultIntent.putExtra("newSubjectAbbreviation",newSubjectAbbreviation.getStepDataAsHumanReadableString());
        resultIntent.putExtra("newSubjectProfessor",newSubjectProfessor.getStepDataAsHumanReadableString());

        setResult(NewSubjectActivity.RESULT_OK, resultIntent);

        finish();

    }

    @Override
    public void onCancelledForm() {
        // This method will be called when the user clicks on the cancel button of the form.
    }

}
