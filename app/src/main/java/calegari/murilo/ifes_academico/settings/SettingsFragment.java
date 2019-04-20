package calegari.murilo.ifes_academico.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import calegari.murilo.ifes_academico.R;

import android.view.View;
import android.widget.ListView;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView lv = view.findViewById(android.R.id.list);
        if (lv != null)
            ViewCompat.setNestedScrollingEnabled(lv, true);
    }
}
