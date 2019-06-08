package calegari.murilo.sistema_academico.subjectgrades;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.sistema_academico.R;
import calegari.murilo.sistema_academico.databases.DatabaseHelper;

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
		mRecyclerView = view.findViewById(R.id.recyclerView);

		Bundle bundle = this.getArguments();
		subjectId = bundle.getInt("subjectId");

		/*
		fab = view.findViewById(R.id.floatingActionButton);
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
		*/
	}

	@Override
	public void onStart() {
		super.onStart();
		setupRecycler();
	}

	private void setupRecycler() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		mRecyclerView.setLayoutManager(layoutManager);

		DatabaseHelper db = new DatabaseHelper(getContext());
		mAdapter = new SubjectGradesLineAdapter(db.getAllGrades(subjectId));
		mRecyclerView.setAdapter(mAdapter);
	}
}
