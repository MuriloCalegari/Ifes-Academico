package calegari.murilo.agendaescolar.gradehelper;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.subjectgrades.SubjectGradesFragment;
import calegari.murilo.agendaescolar.subjecthelper.Subject;
import me.saket.inboxrecyclerview.InboxRecyclerView;
import me.saket.inboxrecyclerview.page.ExpandablePageLayout;
import me.saket.inboxrecyclerview.page.SimplePageStateChangeCallbacks;

public class GradesFragment extends Fragment {

    static InboxRecyclerView inboxRecyclerView;
    private GradesLineAdapter mAdapter;
    SubjectDatabaseHelper subjectDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return inflater.inflate(R.layout.fragment_grades, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inboxRecyclerView = getView().findViewById(R.id.inbox_recyclerview);

        // Sets the toolbar name
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportActionBar().setTitle(getString(R.string.grades));

        setupThreadView(view);

    }

    private void setupThreadView(final View view) {

        ExpandablePageLayout expandablePageLayout = getView().findViewById(R.id.expandablePageLayout);

        inboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new GradesLineAdapter(new ArrayList<>(0));

        // Needed to avoid "Adapter needs to have stable IDs so that the expanded item can be restored across orientation changes." error
        mAdapter.setHasStableIds(true);

        inboxRecyclerView.setAdapter(mAdapter);

        // Populates the list:
        subjectDatabase = new SubjectDatabaseHelper(getContext());
        Cursor cursor = subjectDatabase.getAllDataInAlphabeticalOrder();

        Integer subjectNameIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_NAME);
        Integer subjectProfessorIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_PROFESSOR);
        Integer subjectMaximumGradeIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE);
        Integer subjectObtainedGradeIndex = cursor.getColumnIndex(SubjectDatabaseHelper.SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE);

        while(cursor.moveToNext()) {

            String subjectName = cursor.getString(subjectNameIndex);
            String subjectProfessor = cursor.getString(subjectProfessorIndex);
            Float subjectMaximumGrade = cursor.getFloat(subjectMaximumGradeIndex);
            Float subjectObtainedGrade = cursor.getFloat(subjectObtainedGradeIndex);

            Subject subject = new Subject(subjectName,subjectProfessor,subjectMaximumGrade,subjectObtainedGrade);

            mAdapter.updateList(subject);
        }
        cursor.close();
        subjectDatabase.close();

        inboxRecyclerView.setExpandablePage(expandablePageLayout);

        expandablePageLayout.addStateChangeCallbacks(new SimplePageStateChangeCallbacks() {

            AppCompatActivity activity = (AppCompatActivity) view.getContext();

            @Override
            public void onPageAboutToCollapse(long collapseAnimDuration) {
                super.onPageAboutToCollapse(collapseAnimDuration);
                MainActivity.anim.reverse();
                activity.getSupportActionBar().setTitle(getString(R.string.grades));

                // The following lines makes the user able to open the drawer after coming from a
                // subject grade fragment
                MainActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.drawer.openDrawer(GravityCompat.START);
                    }
                });
            }

            @Override
            public void onPageAboutToExpand(long expandAnimDuration) {
                super.onPageAboutToExpand(expandAnimDuration);
                MainActivity.anim.start();
            }

            @Override
            public void onPageCollapsed() {
                super.onPageCollapsed();
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }

            @Override
            public void onPageExpanded() {
                super.onPageExpanded();

                Class fragmentClass = SubjectGradesFragment.class;

                Fragment fragment = null;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frameLayoutContent, fragment).commit();
            }
        });
    }
}
