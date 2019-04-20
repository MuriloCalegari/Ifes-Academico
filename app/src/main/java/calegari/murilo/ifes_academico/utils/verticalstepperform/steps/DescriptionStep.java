package calegari.murilo.ifes_academico.utils.verticalstepperform.steps;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import calegari.murilo.ifes_academico.R;
import ernestoyaquello.com.verticalstepperform.Step;

public class DescriptionStep extends Step<String> {

	private EditText gradeDescriptionView;

	private Integer MINIMUM_CHARACTERS_PARAMETER = 2;
	private Integer MAXIMUM_CHARACTERS_PARAMETER = 40;

	public DescriptionStep(String stepTitle) {
		super(stepTitle);
	}

	@Override
	protected View createStepContentLayout() {
		// Here we generate the view that will be used by the library as the content of the step.
		gradeDescriptionView = new EditText(getContext());
		gradeDescriptionView.setSingleLine(true);
		gradeDescriptionView.setHint(getContext().getResources().getString(R.string.description));

		gradeDescriptionView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				markAsCompletedOrUncompleted(true);
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});

		gradeDescriptionView.setOnKeyListener(new View.OnKeyListener() {
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

		return gradeDescriptionView;
	}

	@Override
	protected IsDataValid isStepDataValid(String stepData) {
		/* The step's data (i.e., the user name) will be considered valid only if it is between
        two and forty characters. In case it is not, we will display an error message for feedback.
        n an optional step, you should implement this method to always return a valid value. */
		boolean isNameValid = (stepData.length() >= MINIMUM_CHARACTERS_PARAMETER) && (stepData.length() <= MAXIMUM_CHARACTERS_PARAMETER);
		String errorMessage = !isNameValid ? getContext().getResources().getString(R.string.min_max_character_subject_grade_description_error) : "";

		return new IsDataValid(isNameValid, errorMessage);
	}

	@Override
	public String getStepData() {
		// We get the step's data from the value that the user has typed in the EditText view.
		Editable subjectName = gradeDescriptionView.getText();
		return subjectName != null ? subjectName.toString() : "";
	}

	@Override
	public String getStepDataAsHumanReadableString() {
		// Because the step's data is already a human-readable string, we don't need to convert it.
		// However, we return "(Empty)" if the text is empty to avoid not having any text to display.
		// This string will be displayed in the subtitle of the step whenever the step gets closed.
		String subjectName = getStepData();
		return !subjectName.isEmpty() ? subjectName : getContext().getResources().getString(R.string.empty);
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
		gradeDescriptionView.setText(stepData);
	}
}
