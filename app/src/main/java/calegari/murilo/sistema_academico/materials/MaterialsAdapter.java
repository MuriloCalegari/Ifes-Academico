package calegari.murilo.sistema_academico.materials;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressImageButton;
import calegari.murilo.qacadscrapper.utils.ClassMaterial;
import calegari.murilo.sistema_academico.R;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.MaterialsViewHolder> {

	private List<ClassMaterial> materials;

	public MaterialsAdapter(List<ClassMaterial> materials) {
		this.materials = materials;
	}


	@NonNull
	@Override
	public MaterialsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater
				.from(parent.getContext())
				.inflate(R.layout.material_card, parent,false);
		return new MaterialsViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull MaterialsViewHolder holder, int position) {
		holder.materialTitle.setText(materials.get(position).getTitle());
		holder.releaseDate.setText(materials.get(position).getReleaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		final int[] progress = {0};
		holder.downloadButton.setOnClickListener((v) -> {
			Crashlytics.getInstance().crash();
			if(progress[0] == 0) {
				holder.downloadButton.setImageDrawable(null);
				holder.downloadButton.startAnimation(() -> null);
			}
			progress[0] += 25;
			//holder.downloadButton.setProgress(progress[0]);
		});
	}

	@Override
	public int getItemCount() {
		return materials != null ? materials.size() : 0;
	}

	class MaterialsViewHolder extends RecyclerView.ViewHolder {

		CircularProgressImageButton downloadButton;
		TextView materialTitle;
		TextView releaseDate;

		MaterialsViewHolder(@NonNull View itemView) {
			super(itemView);
			downloadButton = itemView.findViewById(R.id.downloadButton);
			materialTitle = itemView.findViewById(R.id.materialTitle);
			releaseDate = itemView.findViewById(R.id.releaseDate);
		}
	}

	@Override
	public long getItemId(int position) {
		return materials.get(position).getId();
	}
}
