package calegari.murilo.agendaescolar.gradehelper;

import android.view.View;
import android.widget.TextView;

import com.mancj.slimchart.SlimChart;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import calegari.murilo.agendaescolar.MainActivity;
import calegari.murilo.agendaescolar.R;
import calegari.murilo.agendaescolar.subjectgrades.SubjectGradesFragment;
import me.saket.inboxrecyclerview.page.SimplePageStateChangeCallbacks;


public class GradesLineHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView gradeText;
    public TextView subjectName;
    public SlimChart gradeChart;

    public GradesLineHolder(View itemView) {
        super(itemView);

        gradeText = itemView.findViewById(R.id.grade);
        subjectName = itemView.findViewById(R.id.subjectName);
        gradeChart = itemView.findViewById(R.id.slimChart);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {
        GradesFragment.inboxRecyclerView.expandItem(v.getId());
        AppCompatActivity activity = (AppCompatActivity) GradesFragment.inboxRecyclerView.getContext();

        ActionBar actionBar = activity.getSupportActionBar();

        actionBar.setTitle(subjectName.getText());

        MainActivity.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GradesFragment.inboxRecyclerView.collapse();
            }
        });
    }
}
