package calegari.murilo.agendaescolar;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.Databases.SubjectDatabaseHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SubjectsFragment extends Fragment {

    RecyclerView mRecyclerView;
    FloatingActionButton fab;
    private LineAdapter mAdapter;
    SubjectDatabaseHelper subjectDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return inflater.inflate(R.layout.fragment_subjects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = getView().findViewById(R.id.floatingActionButton);
        mRecyclerView = getView().findViewById(R.id.recyclerView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSubject();
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
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
         The setupRecycler is throwing a silent error when returning from NewSubjectActivity or after unlocking,
         most likely because there's already a RecyclerView set up when it is being called. The error does not
         crash the app, but I should come up with a way of solving this.
         One method is to call setupRecycler at onViewCreated, but I'd need to manually updated the RecyclerView
         when coming from NewSubjectActivity, that's the hole point of an RecyclerView, actually.

         TODO: Put setupRecycler at onViewCreated and setup a new method for when returning from NewSubjectActivity
         */

        setupRecycler();
    }

    private void newSubject() {
        Intent newSubjectIntent = new Intent(getContext(), NewSubjectActivity.class);
        startActivity(newSubjectIntent);
    }

    private void setupRecycler() {

        // Configures the layout manager so it becomes a list
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new LineAdapter(new ArrayList<>(0));
        mRecyclerView.setAdapter(mAdapter);

        // Populates the list:
        subjectDatabase = new SubjectDatabaseHelper(getContext());
        Cursor cursor = subjectDatabase.getAllData();

        while(cursor.moveToNext()) {

            Integer subjectNameIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_NAME);
            Integer subjectProfessorIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_PROFESSOR);
            Integer subjectAbbreviationIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_ABBREVIATION);

            String subjectName = cursor.getString(subjectNameIndex);
            String subjectProfessor = cursor.getString(subjectProfessorIndex);
            String subjectAbbreviation = cursor.getString(subjectAbbreviationIndex);

            Log.d("SubjectsFragment", "Creating new subject object and updating RecyclerView with param: " + subjectName + ", " + subjectProfessor + ", " + subjectAbbreviation);

            Subject subject = new Subject(subjectName,subjectProfessor,subjectAbbreviation);

            mAdapter.updateList(subject);
        }
    }

}