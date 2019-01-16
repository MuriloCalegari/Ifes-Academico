package calegari.murilo.agendaescolar.home;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;

public class HomeFragment extends Fragment {

	private BarData data;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setupGradesChart();

	}

	private void setupGradesChart() {
		SubjectDatabaseHelper subjectDatabaseHelper = new SubjectDatabaseHelper(getContext());

		Cursor cursor = subjectDatabaseHelper.getAllDataInAverageGradeOrder();

		BarChart chart = getView().findViewById(R.id.chart);

		Integer subjectAbbreviationIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_ABBREVIATION);
		Integer obtainedGradeIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE);
		Integer maximumGradeIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);

		Integer MAXIMUM_COLUMN_NUMBER = 5;
		Integer i = 0;

		List<IBarDataSet> barDataSetList = new ArrayList<>();

		while(cursor.moveToNext() && i < MAXIMUM_COLUMN_NUMBER) {
			float maximumGrade = cursor.getFloat(maximumGradeIndex);
			float obtainedGrade = cursor.getFloat(obtainedGradeIndex);

			if(maximumGrade != 0) { // Do not include subjects that don't have a maximum grade defined
				List<BarEntry> entries = new ArrayList<>();

				String subjectAbbreviation = cursor.getString(subjectAbbreviationIndex);
				Integer averageGradePercentage = Math.round(obtainedGrade / maximumGrade * 100f);

				entries.add(new BarEntry(i + 1, averageGradePercentage));
				BarDataSet dataSet = new BarDataSet(entries, subjectAbbreviation);
				dataSet.setColor(getGradeColor(obtainedGrade, maximumGrade));
				barDataSetList.add(dataSet);
				i++;
			}
		}

		data = new BarData(barDataSetList);
		data.setBarWidth(0.9f);
		data.setValueTextSize(10f);
		data.setValueTypeface(Typeface.DEFAULT_BOLD);
		data.setValueTextColor(Color.WHITE);
		data.setValueFormatter(new MyDataValueFormatter());
		chart.setData(data);

		chart.setDragEnabled(false);
		chart.setScaleEnabled(false);
		chart.setDoubleTapToZoomEnabled(false);
		chart.setPinchZoom(false);

		Description description = new Description();
		description.setText("");
		chart.setDescription(description);

		chart.setFitBars(true);
		chart.setDrawValueAboveBar(false);

		Legend l = chart.getLegend();
		l.setFormSize(10f); // set the size of the legend forms/shapes
		l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
		l.setForm(Legend.LegendForm.SQUARE); // set what type of form/shape should be used
		l.setTextSize(12f);
		l.setTextColor(Color.BLACK);
		l.setXEntrySpace(10f); // set the space between the legend entries on the x-axis
		l.setYEntrySpace(5f); // set the space between the legend entries on the y-axis

		YAxis left = chart.getAxisLeft();
		left.setDrawLabels(true); // no axis labels
		left.setDrawAxisLine(false); // no axis line
		left.setDrawGridLines(true); // no grid lines
		left.setDrawZeroLine(true); // draw a zero line
		chart.getAxisRight().setEnabled(false); // no right axis
		left.setValueFormatter(new MyYAxisValueFormatter());

		// Since data is ordered from minimum to maximum, getDataSetByIndex(0) will
		// return the minimumValue of the chart
		float minimumValue = data.getDataSetByIndex(0).getEntryForIndex(0).getY();
		float maximumValue = data.getDataSetByIndex(data.getDataSetCount() - 1).getEntryForIndex(0).getY();
		float valueThreshold = 10f;

		left.setAxisMinimum(minimumValue - valueThreshold);
		left.setAxisMaximum(maximumValue);

		XAxis xAxis = chart.getXAxis();
		xAxis.setDrawLabels(false);
		xAxis.setDrawAxisLine(false);
		xAxis.setDrawGridLines(false);

		chart.animateY(1500, Easing.EaseInOutExpo);
	}

	private int getGradeColor(float obtainedGrade, float maximumGrade) {
		int dangerColor = getResources().getColor(R.color.slimchart_danger_color);
		int warningColor = getResources().getColor(R.color.slimchart_warning_color);
		int okColor = getResources().getColor(R.color.slimchart_ok_color);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		Integer dangerGradePercentage = sharedPreferences.getInt("minimumPercentage",60);
		Integer DANGER_WARNING_THRESHOLD = 10;
		Integer warningGradePercentage = dangerGradePercentage + DANGER_WARNING_THRESHOLD;

		float averageGradePercentage = obtainedGrade / maximumGrade * 100;

		if (averageGradePercentage >= 100 || averageGradePercentage >= warningGradePercentage) {
			return okColor;
		} else if (averageGradePercentage >= dangerGradePercentage) {
			return warningColor;
		} else {
			return dangerColor;
		}
	}

	public class MyYAxisValueFormatter implements IAxisValueFormatter {

		@Override
		public String getFormattedValue(float value, AxisBase axis) {
			return String.valueOf(Math.round(value)) + "%";
		}

	}

	private class MyDataValueFormatter implements com.github.mikephil.charting.formatter.IValueFormatter {

		@Override
		public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
			return String.valueOf(Math.round(value)) + "%"; // + "\n" + data.getDataSetForEntry(entry).getLabel(); // Unfortunately line breaking doesn't work
		}
	}
}