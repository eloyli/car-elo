package mph.trunksku.apps.myssh;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import android.widget.LinearLayout.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.util.*;
import mph.trunksku.apps.myssh.view.*;
import android.support.v4n.a;

public class SettingActivity extends AppCompatActivity {

	public static class SettingFragment extends PreferenceFragment implements SecurePreferences.OnSharedPreferenceChangeListener {
		public static final String KEY_PREF_LANGUAGE = "pref_key_language";

		private Preference button;

		private SharedPreferences mSecurePrefs;

		//private EditTextPreference mRepoPref;

		private PreferenceCategory advanceCat;

		private PreferenceCategory systemCat;

		private SwitchPreference mShowNotif;

		private SwitchPreference mSoundNotif;

		private SwitchPreference mVibrateNotif;

		/*private CheckBoxPreference mDnsForwardPref;

		private CheckBoxPreference mEnableDnsPref;

		private EditTextPreference mPrimaryDnsPref;

		private EditTextPreference mSecondaryDnsPref;*/

		private Preference buffer;

		private Preference apps_filter;

		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			a ct = new a();
			mSecurePrefs = ApplicationBase.getSharedPreferences();
		    advanceCat = (PreferenceCategory) findPreference("ssh_setting");
			systemCat = (PreferenceCategory) findPreference("system_setting");
			
			
			/*mDnsForwardPref = (CheckBoxPreference) findPreference("dns_forwarder_key");
			//mDnsForwardPref.setDefaultValue(mSecurePrefs.getBoolean("dns_forwarder_key", false));
			
			mEnableDnsPref = (CheckBoxPreference) findPreference("enable_custom_dns_key");
			//mEnableDnsPref.setDefaultValue(mSecurePrefs.getBoolean("enable_dns_key", false));
			
			mPrimaryDnsPref = (EditTextPreference) findPreference("custom_primary_dns");
			mPrimaryDnsPref.setDependency("enable_custom_dns_key");
			mPrimaryDnsPref.setDefaultValue("8.8.8.8");
			
			mSecondaryDnsPref = (EditTextPreference) findPreference("custom_secondary_dns");
			mSecondaryDnsPref.setDependency("enable_custom_dns_key");
			mSecondaryDnsPref.setDefaultValue("8.8.4.4");
			
			if(mDnsForwardPref.isChecked()){
				advanceCat.addPreference(mEnableDnsPref);
				if(mEnableDnsPref.isChecked()){
					advanceCat.addPreference(mPrimaryDnsPref);
					advanceCat.addPreference(mSecondaryDnsPref);
				}else{
					advanceCat.removePreference(mPrimaryDnsPref);
					advanceCat.removePreference(mSecondaryDnsPref);
				}
			}else{
				advanceCat.removePreference(mEnableDnsPref);
				advanceCat.removePreference(mPrimaryDnsPref);
				advanceCat.removePreference(mSecondaryDnsPref);
			}*/
			
			
			
			mShowNotif = (SwitchPreference) findPreference("show_notification");
			mSoundNotif = (SwitchPreference) findPreference("sound_notification");
			mVibrateNotif = (SwitchPreference) findPreference("vibrate_notification");
			
			//mRepoPref = (EditTextPreference) findPreference("custom_update_url");
			SwitchPreference darkTheme = (SwitchPreference) findPreference("use_dark_theme");
			
			
			if(mShowNotif.isChecked()){
				systemCat.addPreference(mSoundNotif);
				systemCat.addPreference(mVibrateNotif);
			}else{
				systemCat.removePreference(mSoundNotif);
				systemCat.removePreference(mVibrateNotif);
			}
			buffer = findPreference("buffer_size");
			buffer.setSummary(new StringBuffer().append("Send: ").append(mSecurePrefs.getString("buffer_send", "16384")).append(" | Receive: ").append(mSecurePrefs.getString("buffer_receive", "32768")));
			buffer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						new BufferSizeDialog(getActivity(), buffer).show();
						return true;
					}
				});
			apps_filter = findPreference("apps_filter");
			//apps_filter.setSummary(new StringBuffer().append("Send: ").append(mSecurePrefs.getString("buffer_send", "16384")).append(" | Receive: ").append(mSecurePrefs.getString("buffer_receive", "32768")));
			apps_filter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						getActivity().startActivity(new Intent(getActivity(), AllowedAppsActivity.class));
						return true;
					}
				});
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			switch (key) {
				
				case "dns_forwarder_key":
					/*if(mDnsForwardPref.isChecked()){
						advanceCat.addPreference(mEnableDnsPref);
						if(mEnableDnsPref.isChecked()){
							advanceCat.addPreference(mPrimaryDnsPref);
							advanceCat.addPreference(mSecondaryDnsPref);
						}else{
							advanceCat.removePreference(mPrimaryDnsPref);
							advanceCat.removePreference(mSecondaryDnsPref);
						}
					}else{
						advanceCat.removePreference(mEnableDnsPref);
						advanceCat.removePreference(mPrimaryDnsPref);
						advanceCat.removePreference(mSecondaryDnsPref);
					}*/
					break;
				case "enable_custom_dns_key":
					/*if(mEnableDnsPref.isChecked()){
						advanceCat.addPreference(mPrimaryDnsPref);
						advanceCat.addPreference(mSecondaryDnsPref);
					}else{
						advanceCat.removePreference(mPrimaryDnsPref);
						advanceCat.removePreference(mSecondaryDnsPref);
					}*/
					break;/*
				case "show_notification":
					if(mShowNotif.isChecked()){
						systemCat.addPreference(mSoundNotif);
						systemCat.addPreference(mVibrateNotif);
					}else{
						systemCat.removePreference(mSoundNotif);
						systemCat.removePreference(mVibrateNotif);
					}
					break;
				//case "use_dark_theme":
					//getActivity().startActivity(new Intent(getActivity(), MainActivity.class))/*.putExtra(MainActivity.LAUNCH_FRAGMENT, MainActivity.FRAGMENT_SETTINGS).putExtra(MainActivity.LAUNCH_NEED_RECREATE, true))*/
					//break;
			}
			//getActivity().recreate(); // necessary here because this Activity is currently running and thus a recreate() in onResume() would be too late
		}

		@Override
		public void onResume() {
			super.onResume();
			// documentation requires that a reference to the listener is kept as long as it may be called, which is the case as it can only be called from this Fragment
			//getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
			mSecurePrefs.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			//getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
			mSecurePrefs.unregisterOnSharedPreferenceChangeListener(this);
		}
	}

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		//setTheme(R.style.AppTheme_Dark);
        
		LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(1);
		ll.setId(1);
		//ll.setPadding(40,0,40,0);
        ll.setLayoutParams(layoutParams);
		AppBarLayout abl = new AppBarLayout(this);
		CenteredToolBar tb = new CenteredToolBar(this);
		tb.setTitle("Setting");
		tb.setTitleTextColor(Color.WHITE);
		tb.setBackgroundColor(Color.parseColor(getString(R.color.colorPrimary)));
		tb.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
		abl.addView(tb);
		ll.addView(abl);
		FrameLayout fl = new FrameLayout(this);
		ll.addView(fl);
	    setContentView(ll);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(ll.getId(), new SettingFragment());
        beginTransaction.commit();
    }

	@Override
	public void onBackPressed()
	{
		// TODO: Implement this method
		super.onBackPressed();
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}


