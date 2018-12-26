package calegari.murilo.agendaescolar.SubjectSteps;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import calegari.murilo.agendaescolar.R;
import ernestoyaquello.com.verticalstepperform.Step;

public class SubjectAbbreviationStep extends Step<String> {
    private EditText subjectAbbreviationView;

    private Integer MINIMUM_CHARACTERS_PARAMETER = 2;
    private Integer MAXIMUM_CHARACTERS_PARAMETER = 6;

    public SubjectAbbreviationStep(String stepTitle) {
        super(stepTitle);
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
        return subjectAbbreviationView;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        /* The step's data (i.e., the user name) will be considered valid only if it is between
        two and forty characters. In case it is not, we will display an error message for feedback.
        n an optional step, you should implement this method to always return a valid value. */
        boolean isNameValid = (stepData.length() >= MINIMUM_CHARACTERS_PARAMETER) && (stepData.length() <= MAXIMUM_CHARACTERS_PARAMETER);
        String errorMessage = !isNameValid ? getContext().getResources().getString(R.string.min_max_character_abbreviation_error) : "";

        // TODO: Check if abbreviation is unique in database

        return new IsDataValid(isNameValid, errorMessage);
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