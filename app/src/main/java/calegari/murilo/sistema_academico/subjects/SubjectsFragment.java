package calegari.murilo.sistema_academico.subjects;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import calegari.murilo.sistema_academico.BaseFragment;
import calegari.murilo.sistema_academico.MainActivity;
import calegari.murilo.sistema_academico.databases.DatabaseHelper;
import calegari.murilo.sistema_academico.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SubjectsFragment extends BaseFragment {

    RecyclerView mRecyclerView;
    FloatingActionButton fab;
    private SubjectLineAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return inflater.inflate(R.layout.fragment_subjects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = getView().findViewById(R.id.recyclerView);

        /*
        fab = getView().findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> newSubject());

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

        // Sets the toolbar name and item checked on nav bar
        AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.getSupportActionBar().setTitle(getString(R.string.subjects));
        MainActivity.navigationView.setCheckedItem(R.id.nav_subjects);
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseHelper database = new DatabaseHelper(getContext());
        mAdapter = new SubjectLineAdapter(database.getAllSubjects());
        database.close();
        mRecyclerView.setAdapter(mAdapter);
    }
}