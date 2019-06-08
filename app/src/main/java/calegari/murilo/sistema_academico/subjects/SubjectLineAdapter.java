package calegari.murilo.sistema_academico.subjects;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.sistema_academico.R;
import calegari.murilo.qacadscrapper.utils.Subject;

public class SubjectLineAdapter extends RecyclerView.Adapter<SubjectLineHolder> {

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    private List<Subject> subjects;

    public SubjectLineAdapter(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public SubjectLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubjectLineHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_card_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectLineHolder holder, int position) {
        holder.subjectName.setText(subjects.get(position).getName());
        holder.subjectProfessor.setText(subjects.get(position).getProfessor());
        holder.subjectAbbreviation.setText(subjects.get(position).getAbbreviation());

        /*
        holder.itemView.setOnClickListener(v -> {
            Intent editSubjectIntent = new Intent(v.getContext(), EditSubjectActivity.class);

            // Sends the data from this line
            editSubjectIntent.putExtra("oldSubjectName", subjects.get(position).getName());
            editSubjectIntent.putExtra("oldSubjectProfessor", subjects.get(position).getProfessor());
            editSubjectIntent.putExtra("oldSubjectAbbreviation", subjects.get(position).getAbbreviation());
            editSubjectIntent.putExtra("subjectId", subjects.get(position).getId());
            v.getContext().startActivity(editSubjectIntent);
        });
        */
    }

    @Override
    public int getItemCount() {
        return subjects != null ? subjects.size() : 0;
    }

    /**
     * Public method called to update the list.
     * @param subject New object to be included in list.
     */

    public void updateList(Subject subject) {
        insertItem(subject);
    }

    // Responsible to insert a new item in list and notify that there are new items on list.

    private void insertItem(Subject subject) {
        subjects.add(subject);
        notifyItemInserted(getItemCount());
    }
}