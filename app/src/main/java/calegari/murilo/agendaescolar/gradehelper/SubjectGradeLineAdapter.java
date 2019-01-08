package calegari.murilo.agendaescolar.gradehelper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.subjecthelper.Subject;

public class SubjectGradeLineAdapter extends RecyclerView.Adapter<SubjectGradeLineHolder> {

    private final List<Subject> mSubjects;

    public SubjectGradeLineAdapter (ArrayList subjects) {
        mSubjects = subjects;
    }

    @NonNull
    @Override
    public SubjectGradeLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubjectGradeLineHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_grade_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectGradeLineHolder holder, int position) {

        holder.subjectName.setText(mSubjects.get(position).getName());

        String gradeText = mSubjects.get(position).getObtainedGrade() +
                holder.itemView.getContext().getResources().getString(R.string.out_of) +
                mSubjects.get(position).getMaximumGrade();

        holder.gradeText.setText(gradeText);

        holder.subjectName.setText(mSubjects.get(position).getName());

        String gradeChartText = String.valueOf(mSubjects.get(position).getObtainedGrade() / mSubjects.get(position).getObtainedGrade() * 100);
        holder.gradeChart.setText(gradeChartText);
    }

    @Override
    public int getItemCount() {
        return mSubjects != null ? mSubjects.size() : 0;
    }

    public void updateList(Subject subject) {
        insertItem(subject);
    }

    // Responsible to insert a new item in list and notify that there are new items on list.

    private void insertItem(Subject subject) {
        mSubjects.add(subject);
        notifyItemInserted(getItemCount());
    }

}
