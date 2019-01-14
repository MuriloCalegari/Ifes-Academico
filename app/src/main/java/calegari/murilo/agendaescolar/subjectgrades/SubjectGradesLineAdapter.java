package calegari.murilo.agendaescolar.subjectgrades;

import android.content.Intent;
import android.util.Log;
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
		Integer gradeId = mSubjectGrades.get(position).getGradeId();
		String subjectAbbreviation = mSubjectGrades.get(position).getSubjectAbbreviation();
		String gradeDescription = mSubjectGrades.get(position).getGradeDescription();
		float obtainedGrade = mSubjectGrades.get(position).getObtainedGrade();
		float maximumGrade = mSubjectGrades.get(position).getMaximumGrade();
		boolean isExtraCredit = mSubjectGrades.get(position).isExtraGrade();

		holder.subjectName.setText(gradeDescription);

		String gradeText = Math.round(obtainedGrade *100f)/100f + " " +
				holder.itemView.getContext().getResources().getString(R.string.out_of) + " " +
				Math.round(maximumGrade *100f)/100f;

		holder.gradeText.setText(gradeText);

		GradeChart.setupGradeChart(holder, obtainedGrade, maximumGrade);

		holder.itemView.setOnClickListener((v) -> {
			Intent editGradeIntent = new Intent(v.getContext(), EditSubjectGradeActivity.class);

			// Sends the data from this line
			editGradeIntent.putExtra("oldGradeId", gradeId);
			editGradeIntent.putExtra("subjectAbbreviation", subjectAbbreviation);
			editGradeIntent.putExtra("oldGradeDescription", gradeDescription);
			editGradeIntent.putExtra("oldObtainedGrade", obtainedGrade);
			editGradeIntent.putExtra("oldMaximumGrade", maximumGrade);
			editGradeIntent.putExtra("oldIsExtraCredit", isExtraCredit);

			v.getContext().startActivity(editGradeIntent);
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
