package calegari.murilo.agendaescolar.grades;

import android.view.View;
import android.widget.TextView;

import com.mancj.slimchart.SlimChart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.R;


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
