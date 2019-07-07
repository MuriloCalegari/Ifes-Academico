package calegari.murilo.sistema_academico.materials;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import calegari.murilo.qacadscrapper.utils.Subject;
import calegari.murilo.sistema_academico.BaseFragment;
import calegari.murilo.sistema_academico.R;
import calegari.murilo.sistema_academico.databases.DatabaseHelper;

public class MaterialsFragment extends BaseFragment {

	private RecyclerView recyclerView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_materials, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		recyclerView = getView().findViewById(R.id.panelRecyclerView);

		setupRecycler();
	}

	private void setupRecycler() {
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		DatabaseHelper db = new DatabaseHelper(getContext());
		List<Subject> subjects = db.getSubjectsWithMaterials();
		db.close();
		MaterialsPanelAdapter materialsPanelAdapter = new MaterialsPanelAdapter(subjects, getContext());
		materialsPanelAdapter.setHasStableIds(true);

		recyclerView.setAdapter(materialsPanelAdapter);
		recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
		recyclerView.setNestedScrollingEnabled(true);
	}
}
