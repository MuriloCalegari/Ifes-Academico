package calegari.murilo.agendaescolar;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class SubjectLineHolder extends RecyclerView.ViewHolder {

    public TextView subjectName;
    public TextView subjectProfessor;
    public TextView subjectAbbreviation;

    public SubjectLineHolder(View itemView) {
        super(itemView);
        subjectName = itemView.findViewById(R.id.subjectName);
        subjectProfessor = itemView.findViewById(R.id.subjectProfessor);
        subjectAbbreviation = itemView.findViewById(R.id.subjectAbbreviation);
    }

}
