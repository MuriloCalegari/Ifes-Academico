package calegari.murilo.agendaescolar.subjects.steps;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.R;
import ernestoyaquello.com.verticalstepperform.Step;

public class SubjectAbbreviationStep extends Step<String> {
    private EditText subjectAbbreviationView;

    private Integer MINIMUM_CHARACTERS_PARAMETER = 2;
    private Integer MAXIMUM_CHARACTERS_PARAMETER = 6;
    private boolean checkOnDatabase = true;
    private String ignoreStringOnDatabase;


    public SubjectAbbreviationStep(String stepTitle) {
        super(stepTitle);
    }

    public SubjectAbbreviationStep(String stepTitle, boolean checkDatabase) {
        super(stepTitle);
        checkOnDatabase = checkDatabase;
    }

    public SubjectAbbreviationStep(String stepTitle, boolean checkDatabase, String ignoreString) {
        super(stepTitle);
        checkOnDatabase = checkDatabase;
        ignoreStringOnDatabase = ignoreString;
    }

    @Override
    protected View createStepContentLayout() {
        subjectAbbreviationView = new EditText(getContext());
        subjectAbbreviationView.setSingleLine(true);
        subjectAbbreviationView.setHint(getContext().getResources().getString(R.string.abbreviation));

        subjectAbbreviationView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Whenever the user updates the user name text, we update the state of the step.
                // The step will be marked as completed only if its data is valid, which will be
                // checked with a call to isStepDataValid().
                markAsCompletedOrUncompleted(true);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        subjectAbbreviationView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    getFormView().goToNextStep(true);
                    return true;
                }
                return false;
            }
        });

        return subjectAbbreviationView;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        /* The step's data (i.e., the subject abbreviation) will be considered valid only if this method
         * returns a new IsDataValid(true, "") object */
        boolean isNameSizeValid = (stepData.length() >= MINIMUM_CHARACTERS_PARAMETER) && (stepData.length() <= MAXIMUM_CHARACTERS_PARAMETER);
        String errorMessage;

        if(!isNameSizeValid) {
            errorMessage = getContext().getResources().getString(R.string.min_max_character_abbreviation_error);
            return new IsDataValid(false, errorMessage);
        }

        /* Only checks if entry is unique after consulting if name size is valid
         * and if stepData is different of ignore param, this is important on
         * EditSubjectActivity */

        if(checkOnDatabase && !stepData.equals(ignoreStringOnDatabase)) {
            SubjectDatabaseHelper subjectDatabase = new SubjectDatabaseHelper(getContext());

            boolean isAbbreviationOnDataBase = subjectDatabase.hasObject(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_ABBREVIATION, stepData);

            if (isAbbreviationOnDataBase) {
                errorMessage = getContext().getResources().getString(R.string.abbreviation_already_on_database);
                return new IsDataValid(false, errorMessage);
            }
        }

        return new IsDataValid(true, "");
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        Editable subjectAbbreviation = subjectAbbreviationView.getText();
        return subjectAbbreviation != null ? subjectAbbreviation.toString() : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        String subjectAbbreviation = getStepData();
        return !subjectAbbreviation.isEmpty() ? subjectAbbreviation : getContext().getResources().getString(R.string.empty);
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as completed.
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as uncompleted.
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        subjectAbbreviationView.setText(stepData);
    }

}