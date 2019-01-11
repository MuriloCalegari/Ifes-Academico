package calegari.murilo.agendaescolar.grades;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mancj.slimchart.SlimChart;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.subjectgrades.SubjectGradesFragment;
import calegari.murilo.agendaescolar.subjects.Subject;

public class GradesLineAdapter extends RecyclerView.Adapter<GradesBaseLineHolder> {

	private final List<Subject> mSubjects;
	private Context context;
	private View view;

	public GradesLineAdapter(ArrayList subjects) {
		mSubjects = subjects;
	}

	public GradesLineAdapter(ArrayList subjects, Context context, View view) {
		this.mSubjects = subjects;
		this.context = context;
		this.view = view;
	}

	@NonNull
	@Override
	public GradesBaseLineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		return new GradesBaseLineHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.subject_grade_card,parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull final GradesBaseLineHolder holder, final int position) {

		float obtainedGrade = mSubjects.get(position).getObtainedGrade();
		float maximumGrade = mSubjects.get(position).getMaximumGrade();

		holder.subjectName.setText(mSubjects.get(position).getName());

		String gradeText = Math.round(obtainedGrade*100)/100 + " " +
				holder.itemView.getContext().getResources().getString(R.string.out_of) + " " +
				Math.round(maximumGrade*100f)/100f;

		holder.gradeText.setText(gradeText);

		holder.subjectName.setText(mSubjects.get(position).getName());

		GradeChart.setupGradeChart(holder, obtainedGrade, maximumGrade);

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				setupGradesView(holder, position);
			}
		});

	}

	public void setupGradesView(GradesBaseLineHolder holder, int position) {
		AppCompatActivity activity = (AppCompatActivity) view.getContext();

		Class fragmentClass = SubjectGradesFragment.class;

		Fragment fragment = null;
		try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Bundles the subject abbreviation to be send to SubjectGradesFragment
		String subjectAbbreviation = mSubjects.get(position).getAbbreviation();
		Bundle bundle = new Bundle();
		bundle.putString("subjectAbbreviation", subjectAbbreviation);
		fragment.setArguments(bundle);

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.frameLayoutContent, fragment).commit();

		// Keep the drawer closed
		MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


		GradesFragment.inboxRecyclerView.expandItem(view.getId());

		ActionBar actionBar = activity.getSupportActionBar();

		actionBar.setTitle(holder.subjectName.getText());

		MainActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GradesFragment.inboxRecyclerView.collapse();
			}
		});
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
