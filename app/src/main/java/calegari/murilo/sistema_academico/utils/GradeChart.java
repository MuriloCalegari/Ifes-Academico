package calegari.murilo.sistema_academico.utils;

import android.content.SharedPreferences;

import com.mancj.slimchart.SlimChart;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import calegari.murilo.sistema_academico.R;
import calegari.murilo.sistema_academico.grades.GradesBaseLineHolder;

public abstract class GradeChart {

	public static void setupGradeChart(@NonNull GradesBaseLineHolder holder, float obtainedGrade, float maximumGrade) {
		SlimChart gradeChart = holder.gradeChart;
		int dangerColor = holder.itemView.getResources().getColor(R.color.danger_color);
		int warningColor = holder.itemView.getResources().getColor(R.color.warning_color);
		int okColor = holder.itemView.getResources().getColor(R.color.ok_color);

		float averageGradePercentage = Math.round(obtainedGrade / maximumGrade * 100);
		final float[] stats = new float[1];

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext());
		Integer dangerGradePercentage = sharedPreferences.getInt("minimumPercentage",60);
		Integer DANGER_WARNING_THRESHOLD = 10;
		Integer warningGradePercentage = dangerGradePercentage + DANGER_WARNING_THRESHOLD;

		String gradeChartText;
		if(maximumGrade != 0) {
			// TODO Decide if this is going to be rounded
			gradeChartText = Math.round(averageGradePercentage) + "%";
		} else if (obtainedGrade != 0){
			gradeChartText = "100%";
		} else {
			gradeChartText = "0%";
		}
		gradeChart.setText(gradeChartText);

		if (averageGradePercentage >= 100){ // Static behavior for percentage >= 100
			stats[0] = 100;
			gradeChart.setTextColor(R.color.ok_color);
		} else if(averageGradePercentage >= warningGradePercentage) { // If grade is in "safe zone"
			stats[0] = averageGradePercentage;
			gradeChart.setTextColor(R.color.ok_color);
		} else if (averageGradePercentage >= dangerGradePercentage && averageGradePercentage < warningGradePercentage) {
			stats[0] = averageGradePercentage;
			gradeChart.setTextColor(R.color.warning_color);
		} else {
			stats[0] = averageGradePercentage;
			gradeChart.setTextColor(R.color.danger_color);
		}

		int[] colors = new int[]{Tools.getGradeColor(obtainedGrade, maximumGrade, holder.itemView.getContext())};

		gradeChart.setColors(colors); // setColor() was producing some inaccurate colors
		gradeChart.setStats(stats);

		//Play animation
		gradeChart.setStartAnimationDuration(2000);
		gradeChart.playStartAnimation();

	}

}
