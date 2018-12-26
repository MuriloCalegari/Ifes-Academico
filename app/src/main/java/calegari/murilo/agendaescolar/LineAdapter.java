package calegari.murilo.agendaescolar;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LineAdapter extends RecyclerView.Adapter<SubjectLineHolder> {

    private final List<Subject> mSubjects;

    public LineAdapter(ArrayList subjects) {
        mSubjects = subjects;
    }

    @NonNull
    @Override
    public SubjectLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubjectLineHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_card_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectLineHolder holder, int position) {
        holder.subjectName.setText(mSubjects.get(position).getName());
        holder.subjectProfessor.setText(mSubjects.get(position).getProfessor());
        holder.subjectAbbreviation.setText(mSubjects.get(position).getAbbreviation());
    }

    @Override
    public int getItemCount() {
        return mSubjects != null ? mSubjects.size() : 0;
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
        mSubjects.add(subject);
        notifyItemInserted(getItemCount());
    }

}
