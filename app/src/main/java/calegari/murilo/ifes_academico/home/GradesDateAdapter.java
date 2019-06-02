package calegari.murilo.ifes_academico.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import calegari.murilo.ifes_academico.R;
import calegari.murilo.ifes_academico.databases.DatabaseHelper;
import calegari.murilo.ifes_academico.subjectgrades.SubjectGrade;
import calegari.murilo.qacadscrapper.utils.Subject;

public class GradesDateAdapter extends RecyclerView.Adapter<GradesDateAdapter.GradesDateViewHolder>{

	private int MAXIMUM_CHARACTERS_DATE_DESCRIPTION = 10;

	private final Context context;
	private List<SubjectGrade> grades;

	public GradesDateAdapter(List<SubjectGrade> grades, Context context) {
		this.context = context;
		this.grades = grades;
	}

	@NonNull
	@Override
	public GradesDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater
				.from(parent.getContext())
				.inflate(R.layout.grade_date_recycler_row,parent,false);
		return new GradesDateViewHolder(view, viewType);
	}

	@Override
	public void onBindViewHolder(@NonNull GradesDateViewHolder holder, int position) {
		DatabaseHelper db = new DatabaseHelper(context);
		Subject subject = db.getSubject(grades.get(position).getSubjectId());
		db.close();

		String gradeDescriptionText = String.format("%s: %s", subject.getAbbreviation(), grades.get(position).getGradeDescription(), MAXIMUM_CHARACTERS_DATE_DESCRIPTION, true);
		holder.gradeDescription.setText(gradeDescriptionText);

		String dateText = grades.get(position).getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		holder.gradeDateText.setText(dateText);
	}

	@Override
	public int getItemCount() {
		return grades != null ? grades.size() : 0;
	}

	@Override
	public int getItemViewType(int position) {
		return TimelineView.getTimeLineViewType(position, getItemCount());
	}

	public class GradesDateViewHolder extends RecyclerView.ViewHolder{

		private final TimelineView timelineView;
		public TextView gradeDescription;
		public TextView gradeDateText;

		public GradesDateViewHolder(View itemView, int viewType) {
			super(itemView);

			timelineView = itemView.findViewById(R.id.timeline);
			timelineView.initLine(viewType);
			gradeDescription = itemView.findViewById(R.id.gradeDescription);
			gradeDateText = itemView.findViewById(R.id.gradeDate);
		}
	}
}
