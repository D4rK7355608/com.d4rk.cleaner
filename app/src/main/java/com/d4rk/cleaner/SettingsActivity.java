package com.d4rk.cleaner;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import com.heinrichreimersoftware.androidissuereporter.IssueReporterLauncher;
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout, new MyPreferenceFragment()).commit();
    }
    public static class MyPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.setHasOptionsMenu(true);
        }
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.settings);
        }
        @Override
        public boolean onPreferenceTreeClick(androidx.preference.Preference preference) {
            String key = preference.getKey();
            if ("suggestion".equals(key)) {
                reportIssue(getContext());
                return true;
            }
            return super.onPreferenceTreeClick(preference);
        }
        final void reportIssue(Context context) {
            IssueReporterLauncher.forTarget("D4rK7355608", "com.d4rk.cleaner")
                    .theme(R.style.CustomIssueReportTheme)
                    .guestEmailRequired(false)
                    .guestToken("194835cbf18259752d316f680ef4842aa7ca9dc5")
                    .minDescriptionLength(20)
                    .homeAsUpEnabled(true)
                    .launch(context);
        }
    }
}