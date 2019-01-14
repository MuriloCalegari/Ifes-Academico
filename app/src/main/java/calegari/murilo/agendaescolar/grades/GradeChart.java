package calegari.murilo.agendaescolar.grades;

import android.content.SharedPreferences;

import com.mancj.slimchart.SlimChart;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import calegari.murilo.agendaescolar.R;

public abstract class GradeChart {

	public static void setupGradeChart(@NonNull GradesBaseLineHolder holder, float obtainedGrade, float maximumGrade) {
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
		} else if (obtainedGrade != 0){
			gradeChartText = "100%";
		} else {
			gradeChartText = "0%";
		}
		gradeChart.setText(gradeChartText);

		// Create color array for slimChart
		int[] graphColors = new int[4]; // For some reason this must be 4 and not 3

		if (averageGradePercentage >= 100){ // Static behavior for percentage >= 100
			graphColors[0] = okColor;
			graphColors[1] = warningColor;
			graphColors[2] = dangerColor;

			stats[0] = 100;
			stats[1] = warningGradePercentage;
			stats[2] = dangerGradePercentage;

			gradeChart.setTextColor(R.color.slimchart_ok_color);
		} else if(averageGradePercentage >= warningGradePercentage) { // If grade is in "safe zone"
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

}
