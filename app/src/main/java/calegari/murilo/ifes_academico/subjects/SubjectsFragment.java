package calegari.murilo.ifes_academico.subjects;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import calegari.murilo.ifes_academico.BaseFragment;
import calegari.murilo.ifes_academico.MainActivity;
import calegari.murilo.ifes_academico.databases.DatabaseHelper;
import calegari.murilo.ifes_academico.R;
import calegari.murilo.ifes_academico.utils.QAcadIntegration.QAcadFetchDataTask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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

        fab = getView().findViewById(R.id.floatingActionButton);
        mRecyclerView = getView().findViewById(R.id.recyclerView);

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

        // Sets the toolbar name and item checked on nav bar
        AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.getSupportActionBar().setTitle(getString(R.string.subjects));
        MainActivity.navigationView.setCheckedItem(R.id.nav_subjects);

        SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(() -> {

            @SuppressLint("StaticFieldLeak")
            QAcadFetchDataTask qAcadFetchDataTask = new QAcadFetchDataTask(getContext(), MainActivity.qAcadCookieMap) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    pullToRefresh.setRefreshing(false);
                    MainActivity.startFragment(SubjectsFragment.class, false);
                    MainActivity.qAcadCookieMap = getCookieMap(); // update cookies to the last ones generated
                }
            };
            qAcadFetchDataTask.execute();
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

        mAdapter = new SubjectLineAdapter(new ArrayList<>(0));
        mRecyclerView.setAdapter(mAdapter);

        // Populates the list:
        DatabaseHelper subjectDatabase = new DatabaseHelper(getContext());

        mAdapter.setSubjects(subjectDatabase.getAllSubjects());

        subjectDatabase.close();
    }
}