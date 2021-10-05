package mph.trunksku.apps.myssh;

import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.widget.*;
import android.widget.LinearLayout.*;
import com.masfha.id.*;
import mph.trunksku.apps.myssh.view.*;
public class ExceptionActivity extends AppCompatActivity {
    TextView error;
    private CenteredToolBar toolbar;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(layoutParams);
        setContentView(linearLayout);
        AppBarLayout abl = new AppBarLayout(this);
		toolbar = new CenteredToolBar(this);
        setSupportActionBar(this.toolbar);
        toolbar.setTitle("Application Error");
		toolbar.setBackgroundColor(Color.parseColor(getString(R.color.colorPrimary)));
        abl.addView(toolbar);
		ScrollView sv = new ScrollView(this);
        TextView error = new TextView(this);
        sv.addView(error);
        linearLayout.addView(abl);
        linearLayout.addView(sv);
        error.setText(getIntent().getStringExtra("error"));
    }
}




