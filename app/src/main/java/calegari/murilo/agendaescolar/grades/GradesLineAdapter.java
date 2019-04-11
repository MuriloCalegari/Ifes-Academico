package calegari.murilo.agendaescolar.grades;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.subjectgrades.SubjectGradesFragment;
import calegari.murilo.agendaescolar.subjects.Subject;
import calegari.murilo.agendaescolar.utils.GradeChart;

public class GradesLineAdapter extends RecyclerView.Adapter<GradesBaseLineHolder> {

	private List<Subject> subjects;
	private Context context;

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	private View view;

	public GradesLineAdapter(ArrayList subjects) {
		this.subjects = subjects;
	}

	public GradesLineAdapter(ArrayList subjects, Context context, View view) {
		this.subjects = subjects;
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

		float obtainedGrade = subjects.get(position).getObtainedGrade();
		float maximumGrade = subjects.get(position).getMaximumGrade();

		holder.subjectName.setText(subjects.get(position).getName());

		String gradeText = Math.round(obtainedGrade*100f)/100f + " " +
				holder.itemView.getContext().getResources().getString(R.string.out_of) + " " +
				Math.round(maximumGrade*100f)/100f;

		holder.gradeText.setText(gradeText);

		holder.subjectName.setText(subjects.get(position).getName());

		GradeChart.setupGradeChart(holder, obtainedGrade, maximumGrade);

		holder.itemView.setOnClickListener(v -> setupGradesView(holder, position));

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

		GradesFragment.inboxRecyclerView.expandItem(position);

		// Bundles the subjectId to be send to SubjectGradesFragment
		int subjectId = subjects.get(position).getId();
		Bundle bundle = new Bundle();
		bundle.putInt("subjectId", subjectId);
		fragment.setArguments(bundle);

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.frameLayoutContent, fragment)
				.commit();

		// Keep the drawer closed
		MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		ActionBar actionBar = activity.getSupportActionBar();

		actionBar.setTitle(holder.subjectName.getText());

		MainActivity.toolbar.setNavigationOnClickListener(v -> GradesFragment.inboxRecyclerView.collapse());
	}

	@Override
	public int getItemCount() {
		return subjects != null ? subjects.size() : 0;
	}

	public void updateList(Subject subject) {
		insertItem(subject);
	}

	// Responsible to insert a new item in list and notify that there are new items on list.

	private void insertItem(Subject subject) {
		subjects.add(subject);
		notifyItemInserted(getItemCount());
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
