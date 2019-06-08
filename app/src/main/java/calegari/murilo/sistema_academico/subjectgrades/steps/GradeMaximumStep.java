package calegari.murilo.sistema_academico.subjectgrades.steps;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import calegari.murilo.sistema_academico.R;
import ernestoyaquello.com.verticalstepperform.Step;

public class GradeMaximumStep extends Step<Float> {

	private EditText maximumGradeView;

	public GradeMaximumStep(String stepTitle) {
		super(stepTitle);
	}

	@Override
	protected View createStepContentLayout() {
		maximumGradeView = new EditText(getContext());
		maximumGradeView.setSingleLine(true);
		maximumGradeView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		maximumGradeView.setHint(getContext().getResources().getString(R.string.obtained_grade));

		maximumGradeView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				markAsCompletedOrUncompleted(true);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		maximumGradeView.setOnKeyListener(new View.OnKeyListener() {
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

		return maximumGradeView;
	}

	@Override
	protected IsDataValid isStepDataValid(Float stepData) {
		boolean isGradeValid = stepData != 0;
		String errorMessage = !isGradeValid ? getContext().getResources().getString(R.string.maximum_grade_different_from_zero) : "";
		return new IsDataValid(isGradeValid, errorMessage);
	}

	@Override
	public Float getStepData() {
		// We get the step's data from the value that the user has typed in the EditText view.
		Editable maximumGrade = maximumGradeView.getText();
		if (!maximumGrade.toString().equals("") && !maximumGrade.toString().equals(".") && !maximumGrade.toString().equals(",")) {
			return Float.valueOf(maximumGrade.toString());
		} else { return 0f;}
	}

	@Override
	public String getStepDataAsHumanReadableString() {
		// Because the step's data is already a human-readable string, we don't need to convert it.
		// However, we return "(Empty)" if the text is empty to avoid not having any text to display.
		// This string will be displayed in the subtitle of the step whenever the step gets closed.
		String obtainedGrade = String.valueOf(getStepData());
		return !obtainedGrade.isEmpty() ? obtainedGrade : getContext().getResources().getString(R.string.empty);
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
	public void restoreStepData(Float stepData) {
		// To restore the step after a configuration change, we restore the text of its EditText view.
		maximumGradeView.setText(String.valueOf(stepData));
	}
}
