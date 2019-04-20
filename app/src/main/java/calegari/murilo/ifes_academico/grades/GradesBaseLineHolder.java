package calegari.murilo.ifes_academico.grades;

import android.view.View;
import android.widget.TextView;

import com.mancj.slimchart.SlimChart;

import androidx.recyclerview.widget.RecyclerView;

import calegari.murilo.ifes_academico.R;


public class GradesBaseLineHolder extends RecyclerView.ViewHolder{

    public TextView gradeText;
    public TextView subjectName;
    public SlimChart gradeChart;

    public GradesBaseLineHolder(View itemView) {
        super(itemView);

        gradeText = itemView.findViewById(R.id.grade);
        subjectName = itemView.findViewById(R.id.subjectName);
        gradeChart = itemView.findViewById(R.id.slimChart);
    }
}
