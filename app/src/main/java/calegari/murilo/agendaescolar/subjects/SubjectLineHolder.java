package calegari.murilo.agendaescolar.subjects;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.R;

public class SubjectLineHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public CardView subjectCard;
    public TextView subjectName;
    public TextView subjectProfessor;
    public TextView subjectAbbreviation;

    public SubjectLineHolder(final View itemView) {
        super(itemView);
        subjectCard = itemView.findViewById(R.id.cardView);
        subjectName = itemView.findViewById(R.id.subjectName);
        subjectProfessor = itemView.findViewById(R.id.subjectProfessor);
        subjectAbbreviation = itemView.findViewById(R.id.subjectAbbreviation);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent editSubjectIntent = new Intent(v.getContext(), EditSubjectActivity.class);

        // Sends the data from this line
        editSubjectIntent.putExtra("oldSubjectName", subjectName.getText());
        editSubjectIntent.putExtra("oldSubjectProfessor", subjectProfessor.getText());
        editSubjectIntent.putExtra("oldSubjectAbbreviation", subjectAbbreviation.getText());
        v.getContext().startActivity(editSubjectIntent);
    }

}
