package calegari.murilo.sistema_academico.materials;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fenjuly.library.ArrowDownloadButton;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import java.util.ArrayList;
import java.util.List;

import calegari.murilo.qacadscrapper.utils.ClassMaterial;
import calegari.murilo.qacadscrapper.utils.Subject;
import calegari.murilo.sistema_academico.R;
import calegari.murilo.sistema_academico.databases.DatabaseHelper;

public class MaterialsPanelAdapter extends RecyclerView.Adapter<MaterialsPanelAdapter.MaterialsPanelViewHolder> {

	private final Context context;
	private List<Subject> subjects;
	private final ExpansionLayoutCollection expansionsCollection = new ExpansionLayoutCollection();


	public MaterialsPanelAdapter(List<Subject> subjects, Context context) {
		this.subjects = subjects;
		this.context = context;
	}

	@NonNull
	@Override
	public MaterialsPanelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater
				.from(parent.getContext())
				.inflate(R.layout.materials_expandable_layout,parent,false);
		return new MaterialsPanelViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull MaterialsPanelViewHolder holder, int position) {
		expansionsCollection.add(holder.expansionLayout);
		expansionsCollection.openOnlyOne(false);
		holder.subjectName.setText(subjects.get(position).getName());

		// For smoother interaction, set the recycler view adapter even if the materials aren't set
		// to be seen soon (for example on a clickListener)

		MaterialsAdapter adapter = new MaterialsAdapter(subjects.get(position).getMaterialsList());
		adapter.setHasStableIds(true);
		holder.recyclerView.setAdapter(adapter);
	}

	@Override
	public int getItemCount() {
		return subjects.size();
	}

	@Override
	public long getItemId(int position) {
		return subjects.get(position).getId();
	}

	class MaterialsPanelViewHolder extends RecyclerView.ViewHolder {
		TextView subjectName;
		ExpansionLayout expansionLayout;
		RecyclerView recyclerView;

		MaterialsPanelViewHolder(@NonNull View itemView) {
			super(itemView);
			subjectName = itemView.findViewById(R.id.subjectName);
			expansionLayout = itemView.findViewById(R.id.expansionLayout);

			recyclerView = itemView.findViewById(R.id.materialsRecyclerView);
			recyclerView.setNestedScrollingEnabled(false);
			recyclerView.setLayoutManager(new LinearLayoutManager(context));
		}
	}

}
