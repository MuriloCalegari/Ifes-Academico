package calegari.murilo.ifes_academico.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;


import androidx.preference.PreferenceManager;
import calegari.murilo.ifes_academico.R;

public abstract class Tools {

	public static int getGradeColor(float obtainedGrade, float maximumGrade, Context context) {
		int dangerColor = context.getResources().getColor(R.color.danger_color);
		int warningColor = context.getResources().getColor(R.color.warning_color);
		int okColor = context.getResources().getColor(R.color.ok_color);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		int dangerGradePercentage = sharedPreferences.getInt("minimumPercentage",60);
		int DANGER_WARNING_THRESHOLD = 10;
		int warningGradePercentage = dangerGradePercentage + DANGER_WARNING_THRESHOLD;

		float averageGradePercentage = obtainedGrade / maximumGrade * 100;

		if (averageGradePercentage >= 100 || averageGradePercentage >= warningGradePercentage) {
			return okColor;
		} else if (averageGradePercentage >= dangerGradePercentage) {
			return warningColor;
		} else {
			return dangerColor;
		}
	}

	public static int getRandomColorFromArray(int resId, Context context) {
		final String[] colorArray = context.getResources().getStringArray(resId);
		return Color.parseColor(colorArray[(int) Math.round(Math.random() * (colorArray.length - 1))]);
	}

	public static String shortenString(String originalString, int maximumCharacters, boolean displayEllipsisCharacters) {
		if(originalString.length() <= maximumCharacters) {
			return originalString;
		}

		String finalString = originalString.substring(0, maximumCharacters - 1);

		if(displayEllipsisCharacters) {
			finalString = finalString.concat("...");
		}

		return finalString;
	}
}
