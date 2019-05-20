package in.blackpaper.instasp.base;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {
    protected final String TAG = BasePreferenceFragment.class.getSimpleName() + "@" + Integer.toHexString(hashCode());

    protected SharedPreferences defaultPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDivider(null);
        updateTitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTitle();
    }

    private void updateTitle() {
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) actionBar.setTitle(getPreferenceScreen().getTitle());
        }
    }
}
