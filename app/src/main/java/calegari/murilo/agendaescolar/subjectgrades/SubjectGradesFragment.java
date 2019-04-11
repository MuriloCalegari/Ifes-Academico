package calegari.murilo.agendaescolar.subjectgrades;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.DatabaseHelper;
import calegari.murilo.agendaescolar.grades.GradesFragment;

public class SubjectGradesFragment extends Fragment {

	RecyclerView mRecyclerView;
	FloatingActionButton fab;
	private SubjectGradesLineAdapter mAdapter;
	private int subjectId;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_subject_grade, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		fab = view.findViewById(R.id.floatingActionButton);
		mRecyclerView = view.findViewById(R.id.recyclerView);

		Bundle bundle = this.getArguments();
		subjectId = bundle.getInt("subjectId");

		fab.setOnClickListener(v -> {
			Intent newSubjectIntent = new Intent(view.getContext(), NewSubjectGradeActivity.class);

			newSubjectIntent.putExtra("subjectId", subjectId);
			v.getContext().startActivity(newSubjectIntent);
		});

		// Hides floating action button on scroll down
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				if (dy > 0) {
					fab.hide();
				} else {
					fab.show();
				}
			}
		});

		// Handles back button pressed
		boolean shouldCollapse = bundle.getBoolean("shouldCollapse", true);
		if(shouldCollapse) {
			view.setFocusableInTouchMode(true);
			view.requestFocus();
			view.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					GradesFragment.inboxRecyclerView.collapse();
					return true;
				}
				return false;
			});
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		setupRecycler();
	}



	private void setupRecycler() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(layoutManager);

		mAdapter = new SubjectGradesLineAdapter(new ArrayList<>(0));

		mRecyclerView.setAdapter(mAdapter);

		// Populates the list

		DatabaseHelper db = new DatabaseHelper(getContext());

		for(SubjectGrade subjectGrade : db.getAllGrades(subjectId)) {
			mAdapter.updateList(subjectGrade);
		}
	}
}
