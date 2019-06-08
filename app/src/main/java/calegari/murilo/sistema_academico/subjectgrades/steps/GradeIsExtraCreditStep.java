package calegari.murilo.sistema_academico.subjectgrades.steps;

import android.view.View;

import androidx.appcompat.widget.AppCompatCheckBox;
import calegari.murilo.sistema_academico.R;
import ernestoyaquello.com.verticalstepperform.Step;

public class GradeIsExtraCreditStep extends Step<Boolean> {

	private AppCompatCheckBox checkBoxView;

	public GradeIsExtraCreditStep(String title) {
		super(title);
	}

	public GradeIsExtraCreditStep(String title, String subtitle) {
		super(title, subtitle);
	}

	@Override
	protected View createStepContentLayout() {
		checkBoxView = new AppCompatCheckBox(getContext());
		return checkBoxView;
	}

	@Override
	public Boolean getStepData() {
		// We get the step's data from the value that the user has typed in the EditText view.
		return checkBoxView.isChecked();
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
	public void restoreStepData(Boolean isExtraCredit) {
		// To restore the step after a configuration change, we restore the text of its EditText view.
		checkBoxView.setChecked(isExtraCredit);
	}

	@Override
	protected IsDataValid isStepDataValid(Boolean isExtraCredit) {
		return new IsDataValid(true, "");
	}

	@Override
	public String getStepDataAsHumanReadableString() {
		// Because the step's data is already a human-readable string, we don't need to convert it.
		// However, we return "(Empty)" if the text is empty to avoid not having any text to display.
		// This string will be displayed in the subtitle of the step whenever the step gets closed.
		String isExtraCreditString = getStepData() ? getContext().getString(R.string.yes) :  getContext().getString(R.string.no);
		return !isExtraCreditString.isEmpty() ? isExtraCreditString : getContext().getResources().getString(R.string.empty);
	}
}
