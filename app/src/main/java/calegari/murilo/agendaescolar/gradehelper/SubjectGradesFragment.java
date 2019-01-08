package calegari.murilo.agendaescolar.gradehelper;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.databases.SubjectDatabaseHelper;
import calegari.murilo.agendaescolar.subjecthelper.Subject;
import me.saket.inboxrecyclerview.InboxRecyclerView;

public class SubjectGradesFragment extends Fragment {

    InboxRecyclerView inboxRecyclerView;
    private SubjectGradeLineAdapter mAdapter;
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

        setupThreadView();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setupThreadView() {
        inboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new SubjectGradeLineAdapter(new ArrayList<>(0));

        // Needed to avoid "Adapter needs to have stable IDs so that the expanded item can be restored across orientation changes." error
        // TODO Check if this brokes the app in some usage

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
    }

}
