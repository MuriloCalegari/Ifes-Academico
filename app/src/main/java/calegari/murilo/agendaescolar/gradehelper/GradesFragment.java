package calegari.murilo.agendaescolar.gradehelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import calegari.murilo.agendaescolar.R;
import me.saket.inboxrecyclerview.InboxRecyclerView;

public class GradesFragment extends Fragment {

    InboxRecyclerView inboxRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return inflater.inflate(R.layout.fragment_grades, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inboxRecyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.inbox_recyclerview);

        setupThreadView();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setupThreadView() {
        inboxRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
