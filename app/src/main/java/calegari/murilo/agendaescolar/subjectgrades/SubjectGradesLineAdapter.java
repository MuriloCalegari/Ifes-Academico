package calegari.murilo.agendaescolar.subjectgrades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.grades.GradeChart;
import calegari.murilo.agendaescolar.grades.GradesBaseLineHolder;

public class SubjectGradesLineAdapter extends RecyclerView.Adapter<GradesBaseLineHolder> {

	private final List<SubjectGrade> mSubjectGrades;

	public SubjectGradesLineAdapter(ArrayList subjectGrades) {
		this.mSubjectGrades = subjectGrades;
	}

	@NonNull
	@Override
	public GradesBaseLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new GradesBaseLineHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.subject_grade_card, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull GradesBaseLineHolder holder, int position) {
		holder.subjectName.setText(mSubjectGrades.get(position).getGradeDescription());

		float obtainedGrade = mSubjectGrades.get(position).getObtainedGrade();
		float maximumGrade = mSubjectGrades.get(position).getMaximumGrade();

		String gradeText = Math.round(obtainedGrade *100f)/100f + " " +
				holder.itemView.getContext().getResources().getString(R.string.out_of) + " " +
				Math.round(maximumGrade *100f)/100f;

		holder.gradeText.setText(gradeText);

		GradeChart.setupGradeChart(holder, obtainedGrade, maximumGrade);

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Add behavior for click listener
			}
		});

	}

	@Override
	public int getItemCount() {
		return mSubjectGrades != null ? mSubjectGrades.size() : 0;
	}

	public void updateList(SubjectGrade subjectGrade) {
		insertItem(subjectGrade);
	}

	// Responsible to insert a new item in list and notify that there are new items on list.

	private void insertItem(SubjectGrade subjectGrade) {
		mSubjectGrades.add(subjectGrade);
		notifyItemInserted(getItemCount());
	}
}
