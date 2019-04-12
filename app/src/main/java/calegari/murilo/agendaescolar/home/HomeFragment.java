package calegari.murilo.agendaescolar.home;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.DatabaseHelper;
import calegari.murilo.agendaescolar.grades.GradesFragment;
import calegari.murilo.agendaescolar.subjects.Subject;
import calegari.murilo.agendaescolar.utils.QAcadIntegration;
import calegari.murilo.agendaescolar.utils.Tools;

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

		TextView gradesChartTitle = view.findViewById(R.id.titleTextView);
		gradesChartTitle.setText(R.string.your_grades);

		TextView gradesChartSubtitle = view.findViewById(R.id.subtitleTextView);
		gradesChartSubtitle.setText(R.string.to_keep_an_eye);

		MainActivity.navigationView.setCheckedItem(R.id.nav_home);
		AppCompatActivity activity = (AppCompatActivity) getContext();
		activity.getSupportActionBar().setTitle(R.string.app_name);

		setupGradesChart();

		CardView gradesChartCardView = view.findViewById(R.id.cardView);

		gradesChartCardView.setOnClickListener(v -> MainActivity.startFragment(GradesFragment.class, true));
	}

	private void setupGradesChart() {
		DatabaseHelper dbHelper = new DatabaseHelper(getContext());
		List<Subject> subjectList = dbHelper.getAllSubjectsInAverageGradeOrder();
		dbHelper.close();

		BarChart chart = getView().findViewById(R.id.chart);
		chart.setNoDataText(getString(R.string.no_grades_availabe));

		List<IBarDataSet> barDataSetList = new ArrayList<>();

		int MAXIMUM_COLUMN_NUMBER = 5;
		int i = 0;

		for(Subject subject : subjectList) {
			if(i == MAXIMUM_COLUMN_NUMBER) break;
			float obtainedGrade = subject.getObtainedGrade();
			float maximumGrade = subject.getMaximumGrade();

			if(maximumGrade != 0) { // Do not include subjects that don't have a maximum grade defined
				List<BarEntry> entries = new ArrayList<>();

				String subjectAbbreviation = subject.getAbbreviation();
				int averageGradePercentage = Math.round(obtainedGrade / maximumGrade * 100f);

				entries.add(new BarEntry(
						i, // x value
						averageGradePercentage // y value
				));

				BarDataSet dataSet = new BarDataSet(entries, subjectAbbreviation);
				dataSet.setColor(Tools.getGradeColor(obtainedGrade, maximumGrade, getContext()));
				barDataSetList.add(dataSet);
			}

			i++;
		}

		data = new BarData(barDataSetList);

		if(data.getDataSetCount() != 0) {
			// Defines behavior for the data, including labels

			data.setBarWidth(0.9f);
			data.setValueTextSize(10f);
			data.setValueTypeface(Typeface.DEFAULT_BOLD);
			data.setValueTextColor(Color.WHITE);
			data.setValueFormatter(new MyDataValueFormatter());
			chart.setData(data);

			// Defines the behavior for graph interaction

			chart.setDragEnabled(false);
			chart.setScaleEnabled(false);
			chart.setDoubleTapToZoomEnabled(false);
			chart.setPinchZoom(false);

			// Defines behavior for description

			Description description = new Description();
			description.setText("");
			chart.setDescription(description);

			// Defines the graph's appearance

			chart.setFitBars(true);
			chart.setDrawValueAboveBar(false);
			chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
				@Override
				public void onValueSelected(Entry e, Highlight h) {
					MainActivity.startFragment(GradesFragment.class, true);
				}

				@Override
				public void onNothingSelected() {

				}
			});

			YAxis left = chart.getAxisLeft();
			left.setDrawLabels(true); // axis labels
			left.setDrawAxisLine(false); // no axis line
			left.setDrawGridLines(true); // grid lines
			left.setDrawZeroLine(true); // draw a zero line
			chart.getAxisRight().setEnabled(false); // no right axis
			left.setValueFormatter(new MyYAxisValueFormatter());
			left.setLabelCount(5);

			// Defines maximum and minimum Y on graph

			// Since data is ordered from minimum to maximum, getDataSetByIndex(0) will
			// return the minimumValue of the chart
			//float valueThreshold = 10f;
			//float minimumValue = data.getDataSetByIndex(0).getEntryForIndex(0).getY() - valueThreshold;
			float maximumValue = data.getDataSetByIndex(data.getDataSetCount() - 1).getEntryForIndex(0).getY();

			/*
			Currently MPAndroidChart doesn't support starting animation from custom point,
			so I need to wait for a fix or decide if: I use it without an animation, use
			an animation but with delay (from 0 to minimumValue) or just set minimum value
			to 0
			 */
			left.setAxisMinimum(0);
			left.setAxisMaximum(maximumValue);

			XAxis xAxis = chart.getXAxis();
			xAxis.setDrawLabels(true);
			xAxis.setDrawAxisLine(true);
			xAxis.setDrawGridLines(false);
			xAxis.setValueFormatter(new MyXAxisValueFormatter());
			xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
			chart.getLegend().setEnabled(false);

			// Initializes the graph
			chart.animateY(getResources().getInteger(R.integer.anim_graph_home_page), Easing.EaseInOutExpo);

			// For putting this to work, I need to wait for
			// PullCollapsibleActivity from InboxRecyclerView
			// to support setting an interceptor
			/*
			chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
				@Override
				public void onValueSelected(Entry e, Highlight h) {

					Intent baseFragmentActivity = new Intent(getActivity(), BaseFragmentActivity.class);
					baseFragmentActivity.putExtra("fragment", SubjectGradesFragment.class.getName());
					baseFragmentActivity.putExtra("subjectAbbreviation",data.getDataSetForEntry(e).getLabel());
					startActivity(baseFragmentActivity);

				}

				@Override
				public void onNothingSelected() {}
			});
			*/
		}
	}

	public class MyYAxisValueFormatter implements IAxisValueFormatter {

		@Override
		public String getFormattedValue(float value, AxisBase axis) {
			return Math.round(value) + "%";
		}

	}

	public class MyXAxisValueFormatter implements IAxisValueFormatter {

		@Override
		public String getFormattedValue(float value, AxisBase axis) {
			// Since X entries are created by counting the values (i++),
			// it is safe to getDataSetByIndex using Math.round(value)

			axis.setGranularity(1f); // So value are displayed in counted mode (1, 2, 3, ..., 4)

			return data.getDataSetByIndex(Math.round(value)) != null ? data.getDataSetByIndex(Math.round(value)).getLabel() : "";
		}
	}

	private class MyDataValueFormatter implements IValueFormatter {

		@Override
		public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
			return Math.round(value) + "%"; // + "\n" + data.getDataSetForEntry(entry).getLabel(); // Unfortunately line breaking doesn't work
		}
	}
}