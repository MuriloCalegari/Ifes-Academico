package calegari.murilo.agendaescolar;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import calegari.murilo.agendaescolar.home.HomeFragment;

public abstract class BaseFragment extends Fragment {

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
			if(keyCode == KeyEvent.KEYCODE_BACK) {
				MainActivity.startFragment(HomeFragment.class, true);
				return true;
			}
			return false;
		});

	}
}
