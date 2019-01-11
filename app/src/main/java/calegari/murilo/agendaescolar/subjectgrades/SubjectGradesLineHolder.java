package calegari.murilo.agendaescolar.subjectgrades;

import android.view.View;
import android.widget.TextView;

import com.mancj.slimchart.SlimChart;

import org.w3c.dom.Text;

import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.R;

public class SubjectGradesLineHolder extends RecyclerView.ViewHolder {

	public TextView gradeText;
	public TextView subjectName;
	public SlimChart gradeChart;

	public SubjectGradesLineHolder(View itemView) {
		super(itemView);

		gradeText = itemView.findViewById(R.id.grade);
		subjectName = itemView.findViewById(R.id.subjectName);
		gradeChart = itemView.findViewById(R.id.slimChart);
	}
}
