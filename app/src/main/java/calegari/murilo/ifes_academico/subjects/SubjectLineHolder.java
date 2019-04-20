package calegari.murilo.ifes_academico.subjects;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.ifes_academico.R;

public class SubjectLineHolder extends RecyclerView.ViewHolder {

    public TextView subjectName;
    public TextView subjectProfessor;
    public TextView subjectAbbreviation;

    public SubjectLineHolder(final View itemView) {
        super(itemView);
        subjectName = itemView.findViewById(R.id.subjectName);
        subjectProfessor = itemView.findViewById(R.id.subjectProfessor);
        subjectAbbreviation = itemView.findViewById(R.id.subjectAbbreviation);
    }
}
