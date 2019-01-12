package calegari.murilo.agendaescolar.subjectgrades;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import calegari.murilo.agendaescolar.databases.SubjectGradesDatabaseHelper;
import calegari.murilo.agendaescolar.subjects.NewSubjectActivity;

public class SubjectGradesFragment extends Fragment {

    RecyclerView mRecyclerView;
    FloatingActionButton fab;
    private SubjectGradesLineAdapter mAdapter;
    SubjectGradesDatabaseHelper subjectGradesDatabase;
	String gradeSubjectAbbreviation;

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
		gradeSubjectAbbreviation = bundle.getString("subjectAbbreviation");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newSubjectIntent = new Intent(view.getContext(), NewSubjectGradeActivity.class);

                newSubjectIntent.putExtra("subjectAbbreviation", gradeSubjectAbbreviation);
                v.getContext().startActivity(newSubjectIntent);
            }
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

        setupRecycler();

    }

    private void setupRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

		mAdapter = new SubjectGradesLineAdapter(new ArrayList<>(0));

		mRecyclerView.setAdapter(mAdapter);

		// Populates the list
		subjectGradesDatabase = new SubjectGradesDatabaseHelper(getContext());

		Cursor cursor = subjectGradesDatabase.getSubjectGradesData(gradeSubjectAbbreviation);

		Integer gradeDescriptionIndex = cursor.getColumnIndex(SubjectGradesDatabaseHelper.SubjectGradesEntry.COLUMN_GRADE_DESCRIPTION);
		Integer obtainedGradeIndex = cursor.getColumnIndex(SubjectGradesDatabaseHelper.SubjectGradesEntry.COLUMN_GRADE_OBTAINED);
		Integer maximumGradeIndex = cursor.getColumnIndex(SubjectGradesDatabaseHelper.SubjectGradesEntry.COLUMN_GRADE_MAXIMUM);

		while(cursor.moveToNext()) {
			String gradeDescription = cursor.getString(gradeDescriptionIndex);
			float obtainedGrade = cursor.getFloat(obtainedGradeIndex);
			float maximumGrade = cursor.getFloat(maximumGradeIndex);

			SubjectGrade subjectGrade = new SubjectGrade(gradeDescription, obtainedGrade, maximumGrade);

			mAdapter.updateList(subjectGrade);
		}

		cursor.close();
		subjectGradesDatabase.close();

    }
}
