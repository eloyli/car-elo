package mph.trunksku.apps.myssh;
import android.app.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.widget.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.fragment.*;
import mph.trunksku.apps.myssh.view.*;

public class AllowedAppsActivity extends AppCompatActivity{
	@Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
		//setTheme(R.style.AppTheme_Dark);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(1);
		ll.setId(1);
		//ll.setPadding(40,0,40,0);
        ll.setLayoutParams(layoutParams);
		AppBarLayout abl = new AppBarLayout(this);
		CenteredToolBar tb = new CenteredToolBar(this);
		tb.setTitle("Proxy Filter");
		tb.setTitleTextColor(Color.WHITE);
		tb.setBackgroundColor(Color.parseColor(getString(R.color.colorPrimary)));
		tb.setPopupTheme(R.style.ThemeOverlay_AppCompat_Dark);
		abl.addView(tb);
		ll.addView(abl);
		FrameLayout fl = new FrameLayout(this);
		ll.addView(fl);
	    setContentView(ll);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(ll.getId(), new AllowedAppFragment());
        beginTransaction.commit();
    }
	
}
