package calegari.murilo.agendaescolar.gradehelper;

import android.view.View;
import android.widget.TextView;

import com.mancj.slimchart.SlimChart;

import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.R;

public class GradeSubjectLineHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView gradeText;
    public TextView subjectName;
    public SlimChart gradeChart;

    public GradeSubjectLineHolder(View itemView) {
        super(itemView);

        gradeText = itemView.findViewById(R.id.grade);
        subjectName = itemView.findViewById(R.id.subjectName);
        gradeText = itemView.findViewById(R.id.slimChart);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Do something here, probably related to the Inbox Recycler View
    }



}
