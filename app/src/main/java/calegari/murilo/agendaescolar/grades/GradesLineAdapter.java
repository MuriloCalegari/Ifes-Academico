package calegari.murilo.agendaescolar.grades;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mancj.slimchart.SlimChart;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.subjects.Subject;

public class GradesLineAdapter extends RecyclerView.Adapter<GradesLineHolder> {

    private final List<Subject> mSubjects;

    public GradesLineAdapter(ArrayList subjects) {
        mSubjects = subjects;
    }

    @NonNull
    @Override
    public GradesLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new GradesLineHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_grade_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GradesLineHolder holder, int position) {

        float obtainedGrade = mSubjects.get(position).getObtainedGrade();
        float maximumGrade = mSubjects.get(position).getMaximumGrade();

        holder.subjectName.setText(mSubjects.get(position).getName());

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US); // Just to keep consistency on float default representation
        // Define the maximum number of decimals (number of symbols #)
        DecimalFormat df = new DecimalFormat("#.##", otherSymbols);

        String gradeText = df.format(obtainedGrade) + " " +
                holder.itemView.getContext().getResources().getString(R.string.out_of) + " " +
                maximumGrade;

        holder.gradeText.setText(gradeText);

        holder.subjectName.setText(mSubjects.get(position).getName());

        setupGradeChart(holder, obtainedGrade, maximumGrade);
    }

    private void setupGradeChart(@NonNull GradesLineHolder holder, float obtainedGrade, float maximumGrade) {
        SlimChart gradeChart = holder.gradeChart;
        int dangerColor = holder.itemView.getResources().getColor(R.color.slimchart_danger_color);
        int warningColor = holder.itemView.getResources().getColor(R.color.slimchart_warning_color);
        int okColor = holder.itemView.getResources().getColor(R.color.slimchart_ok_color);

        // TODO Decide if this is going to be rounded
        float averageGradePercentage = Math.round(obtainedGrade / maximumGrade * 100);
        final float[] stats = new float[4];

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
        Integer dangerGradePercentage = sharedPreferences.getInt("minimumPercentage",60);
        Integer DANGER_WARNING_THRESHOLD = 10;
        Integer warningGradePercentage = dangerGradePercentage + DANGER_WARNING_THRESHOLD;

        String gradeChartText;
        if(maximumGrade != 0) {
            // TODO Decide if this is going to be rounded
            gradeChartText = String.valueOf(Math.round(averageGradePercentage)) + "%";
        } else {
            gradeChartText = "0%";
        }
        gradeChart.setText(gradeChartText);

        // Create color array for slimChart
        int[] graphColors = new int[4];

        if(averageGradePercentage >= warningGradePercentage) { // If grade is in "safe zone"
            graphColors[0] = okColor;
            graphColors[1] = warningColor;
            graphColors[2] = dangerColor;

            stats[0] = averageGradePercentage;
            stats[1] = warningGradePercentage;
            stats[2] = dangerGradePercentage;

            gradeChart.setTextColor(R.color.slimchart_ok_color);

        } else if (averageGradePercentage >= dangerGradePercentage && averageGradePercentage < warningGradePercentage) {
            graphColors[0] = warningColor;
            graphColors[1] = dangerColor;

            gradeChart.setColors(graphColors);
            stats[0] = averageGradePercentage;
            stats[1] = dangerGradePercentage;

            gradeChart.setTextColor(R.color.slimchart_warning_color);

        } else {
            graphColors[0] = dangerColor;
            stats[0] = averageGradePercentage;

            gradeChart.setTextColor(R.color.slimchart_danger_color);
        }

        gradeChart.setColors(graphColors);
        gradeChart.setStats(stats);

        //Play animation
        gradeChart.setStartAnimationDuration(2000);
        gradeChart.playStartAnimation();

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
