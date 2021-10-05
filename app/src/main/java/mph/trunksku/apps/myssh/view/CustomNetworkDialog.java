package mph.trunksku.apps.myssh.view;
import android.content.*;
import android.support.v7.app.*;
import android.support.v7.app.AlertDialog.*;
import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.widget.*;
import mph.trunksku.apps.myssh.fragment.*;
import mph.trunksku.apps.myssh.*;
import android.view.*;

public class CustomNetworkDialog
{

	private AlertDialog.Builder adb;

	private SharedPreferences sp;

	private AlertDialog ad;
	
	public CustomNetworkDialog(final Context c) {
		sp = ApplicationBase.getSharedPreferences();
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        LinearLayout ll = new LinearLayout(c);
        ll.setOrientation(1);
		ll.setPadding(40,0,40,0);
        ll.setLayoutParams(layoutParams);
		final TextInputLayout til = new TextInputLayout(c);
		final AppCompatEditText acet = new AppCompatEditText(c);
		acet.setHint("HTTP Payload");
		acet.setText(sp.getString("custom_payload", "CONNECT [host_port] [protocol][crlf]Host: bug.com[crlf][crlf]"));
		til.addView(acet);
		final TextInputLayout til0 = new TextInputLayout(c);
		final AppCompatEditText acet0 = new AppCompatEditText(c);
		acet0.setHint("SSL Sni");
		acet0.setText(sp.getString("custom_sni", "bughost.com"));
		til0.addView(acet0);
		final SwitchCompat sc = new SwitchCompat(c);
		sc.setText("Use custom proxy");
		sc.setChecked(sp.getBoolean("custom_remote_key", false));
		final TextInputLayout til3 = new TextInputLayout(c);
		final AppCompatEditText acet3 = new AppCompatEditText(c);
		acet3.setHint("Custom Proxy");
		acet3.setText(sp.getString("custom_remote", "127.0.0.1:8080"));
		til3.addView(acet3);
		if(sp.getBoolean("custom_remote_key", false)){
			til3.setEnabled(true);
		}else{
			til3.setEnabled(false);
		}
		sc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton p1, boolean p2)
				{
					// TODO: Implement this method
					if(p2){
						til3.setEnabled(true);
					}else{
						til3.setEnabled(false);
					}
				}
			});
		ll.addView(til);
		ll.addView(til0);
		ll.addView(sc);
		ll.addView(til3);
		adb = new AlertDialog.Builder(c);
		adb.setCancelable(false);
	    adb.setTitle("Custom Network");
	    //adb.setMessage("Select VPN Mode");
		adb.setView(ll);
		adb.setNeutralButton("GENERATE", null);
		adb.setPositiveButton("SAVE", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
					sp.edit().putString("custom_payload", acet.getText().toString()).commit();
					sp.edit().putString("custom_sni", acet0.getText().toString()).commit();
					sp.edit().putBoolean("custom_payload_key", true).commit();
					sp.edit().putBoolean("custom_remote_key", sc.isChecked()).commit();
					sp.edit().putString("custom_remote", acet3.getText().toString()).commit();
					HomeFragment.switchButton.setChecked(true);
					HomeFragment.networkSpin.setEnabled(false);
				}
			});
		adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					// TODO: Implement this method
					sp.edit().putBoolean("custom_payload_key", false).commit();
					HomeFragment.switchButton.setChecked(false);
					HomeFragment.networkSpin.setEnabled(true);
				}
			});
		ad = adb.create();
		ad.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialogInterface) {
					Button button = ((AlertDialog) ad).getButton(AlertDialog.BUTTON_NEUTRAL);
					button.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								// TODO Do something

								//Dismiss once everything is OK.
								new PayloadGenerator(c, acet).show();
							}
						});
				}
			});
	}
	
	public void show() {
		ad.show();
	}
}
