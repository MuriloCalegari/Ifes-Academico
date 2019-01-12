package calegari.murilo.agendaescolar.subjectgrades;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.subjectgrades.steps.GradeDescriptionStep;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class EditSubjectGradeActivity extends Activity {

	private GradeDescriptionStep gradeDescriptionStep;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_subject_grade);
	}

}
