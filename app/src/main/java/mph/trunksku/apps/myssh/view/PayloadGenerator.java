package mph.trunksku.apps.myssh.view;

import android.content.*;
import android.preference.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import com.masfha.id.*;


public class PayloadGenerator
{

	private LayoutInflater inflater;

	private AlertDialog.Builder adb;

	private RadioGroup A;
    private RadioButton B;
    private Spinner C;
    private Spinner D;
    private EditText E;
    private Spinner F;

	private Button n;
	private CheckBox o;
    private CheckBox p;
    private CheckBox q;
    private CheckBox r;
    private CheckBox s;
    private CheckBox t;
    private CheckBox u;
    private CheckBox vj;
    private CheckBox w;
    private CheckBox x;
    private CheckBox y;

	private AlertDialog ad;

	private SharedPreferences sp;


	public PayloadGenerator(Context c, final EditText edt){
		sp = PreferenceManager.getDefaultSharedPreferences(c);
		inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = inflater.inflate(R.layout.dialog_generator, (ViewGroup) null);
		this.E = (EditText) v.findViewById(R.id.editTextInjectUrl);
		E.setText(sp.getString("xHost", ""));
        this.C = (Spinner) v.findViewById(R.id.spinnerRequestMethod);
		C.setSelection(sp.getInt("RequestMethod", 0));
		this.D = (Spinner) v.findViewById(R.id.spinnerInjectMethod);
        D.setSelection(sp.getInt("InjectMethod", 0));
		this.o = (CheckBox) v.findViewById(R.id.checkBoxFrontQuery);
        o.setChecked(sp.getBoolean("xFrontQuery", false));
		this.p = (CheckBox) v.findViewById(R.id.checkBoxBackQuery);
        p.setChecked(sp.getBoolean("xBackQuery", false));
		this.q = (CheckBox) v.findViewById(R.id.checkBoxOnlineHost);
        q.setChecked(sp.getBoolean("xOnlineHost", false));
		this.r = (CheckBox) v.findViewById(R.id.checkBoxForwardedFor);
		r.setChecked(sp.getBoolean("xForwardedFor", false));
		this.s = (CheckBox) v.findViewById(R.id.checkBoxForwardHost);
        s.setChecked(sp.getBoolean("xForwardHost", false));
		this.t = (CheckBox) v.findViewById(R.id.checkBoxKeepAlive);
        t.setChecked(sp.getBoolean("xKeepAlive", false));
		this.u = (CheckBox) v.findViewById(R.id.checkBoxUserAgent);
        u.setChecked(sp.getBoolean("xUserAgent", false));
		this.F = (Spinner) v.findViewById(R.id.spinner2);
        F.setSelection(sp.getInt("UserAgent", 0));

		this.vj = (CheckBox) v.findViewById(R.id.checkBoxRealRequest);
        vj.setChecked(sp.getBoolean("xRealRequest", false));
		this.w = (CheckBox) v.findViewById(R.id.checkBoxDualConnect);
		w.setChecked(sp.getBoolean("xDualConnect", false));
		this.n = (Button) v.findViewById(R.id.buttonGenerate);

        this.F.setEnabled(false);
        this.o.setOnClickListener(new View.OnClickListener() {
				public final void onClick(View view) {
					p.setChecked(false);
				}
			});
        this.p.setOnClickListener(new View.OnClickListener() {
				public final void onClick(View view) {
					o.setChecked(false);
				}
			});
        this.u.setOnClickListener(new View.OnClickListener() {
				public final void onClick(View view) {
					if (F.isEnabled()) {
						F.setEnabled(false);
					} else {
						F.setEnabled(true);
					}
				}
			});
        this.A = (RadioGroup) v.findViewById(R.id.radio1);

        this.B = (RadioButton) v.findViewById(A.getCheckedRadioButtonId());
        this.x = (CheckBox) v.findViewById(R.id.rotationMethodCheckbox);
       	this.y = (CheckBox) v.findViewById(R.id.splitNoDelayCheckbox);
        A.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				public final void onCheckedChanged(RadioGroup radioGroup, int i) {
					B = (RadioButton) A.findViewById(i);
					if (A.indexOfChild(B) == 1) {
						y.setEnabled(true);
						y.setChecked(sp.getBoolean("xSplitNoDelay", false));
						return;
					}
					y.setEnabled(false);
					y.setChecked(false);
				}
			});
		A.check(sp.getInt("xRadioGroup", R.id.radioMerger));
        this.x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
					if (z) {
						E.setHint("ex. bug1.com;bug2.com");
					} else {
						E.setHint("ex. bug.com");
					}
				}
			});
		x.setChecked(sp.getBoolean("xRotation", false));
		this.n.setOnClickListener(new View.OnClickListener() {
				private String b;
				private String I;
				public final void onClick(View view) {
					I = B.getText().toString();
					StringBuilder stringBuilder = new StringBuilder();
					String obj = D.getSelectedItem().toString();
					String obj2 = C.getSelectedItem().toString();
					String replace = E.getText().toString().replace("http://", "").replace("https://", "");
					if (x.isChecked()) {
						replace = "[rotation=" + replace + "]";
					}
					StringBuilder stringBuilder2 = new StringBuilder();
					if (o.isChecked()) {
						stringBuilder2.append(replace + "@");
					}
					stringBuilder2.append("[host_port]");
					if (p.isChecked()) {
						stringBuilder2.append("@" + replace);
					}
					String stringBuilder3 = stringBuilder2.toString();
					String str = "";
					if (I.equals("SPLIT")) {
						str = y.isChecked() ? obj.equals("Back Inject") ? "[crlf][splitNoDelay]" : "[splitNoDelay]" : obj.equals("Back Inject") ? "[crlf][split]" : "[split]";
					}
					if (obj.equals("Front Inject")) {
						stringBuilder.append(obj2 + " http://" + replace + "/ HTTP/1.1[crlf]");
					} else if (obj.equals("Back Inject")) {
						stringBuilder.append("CONNECT " + stringBuilder3 + " HTTP/1.1[crlf][crlf]" + str + obj2 + " http://" + replace + "/ [protocol][crlf]");
					} else if (!vj.isChecked()) {
						stringBuilder.append(obj2 + " " + stringBuilder3 + " [protocol][crlf]");
					} else if (obj.equals("Back Inject") || o.isChecked() || p.isChecked()) {
						stringBuilder.append(obj2 + " " + stringBuilder3 + " [protocol][crlf]");
					} else {
						stringBuilder.append("[netData][crlf]");
					}
					stringBuilder.append("Host: " + replace + "[crlf]");
					if (q.isChecked()) {
						stringBuilder.append("X-Online-Host: " + replace + "[crlf]");
					}
					if (s.isChecked()) {
						stringBuilder.append("X-Forward-Host: " + replace + "[crlf]");
					}
					if (r.isChecked()) {
						stringBuilder.append("X-Forwarded-For: " + replace + "[crlf]");
					}
					if (t.isChecked()) {
						stringBuilder.append("Connection: Keep-Alive[crlf]");
					}
					if (u.isChecked()) {
						this.b = F.getSelectedItem().toString();
						if (this.b.equals("Firefox")) {
							stringBuilder.append("User-Agent: Mozilla/5.0 (Android; Mobile; rv:35.0) Gecko/35.0 Firefox/35.0\r\n");
						} else if (this.b.equals("Chrome")) {
							stringBuilder.append("User-Agent: Mozilla/5.0 (Linux; Android 4.4.2; SAMSUNG-SM-T537A Build/KOT49H) AppleWebKit/537.36 (KHTML like Gecko) Chrome/35.0.1916.141 Safari/537.36[crlf]");
						} else if (this.b.equals("Opera Mini")) {
							stringBuilder.append("User-Agent: Mozilla/5.0 (Linux; Android 5.1.1; Nexus 7 Build/LMY47V) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.78 Safari/537.36 OPR/30.0.1856.93524[crlf]");
						} else if (this.b.equals("Puffin")) {
							stringBuilder.append("User-Agent: Mozilla/5.0 (X11; U; Linux x86_64; en-gb) AppleWebKit/534.35 (KHTML, like Gecko) Chrome/11.0.696.65 Safari/534.35 Puffin/2.9174AP[crlf]");
						} else if (this.b.equals("Safari")) {
							stringBuilder.append("User-Agent: Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17[crlf]");
						} else if (this.b.equals("UCBrowser")) {
							stringBuilder.append("User-Agent: Mozilla/5.0 (Linux; U; Android 2.3.3; en-us ; LS670 Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1/UCBrowser/8.6.1.262/145/355[crlf]");
						} else if (this.b.equals("Default")) {
							stringBuilder.append("User-Agent: [ua][crlf]");
						}
					}
					if (w.isChecked()) {
						stringBuilder.append("CONNECT [host_port] [protocol][crlf]");
					}
					stringBuilder.append("[crlf]");
					if (obj.equals("Front Inject")) {
						if (!vj.isChecked()) {
							stringBuilder.append(str + "CONNECT " + stringBuilder3 + " [protocol][crlf][crlf]");
						} else if (o.isChecked() || p.isChecked()) {
							stringBuilder.append(str + "CONNECT " + stringBuilder3 + " [protocol][crlf][crlf]");
						} else {
							stringBuilder.append(str + "[netData][crlf][crlf]");
						}
					}
					//sp.edit().putString("custom_payload", stringBuilder.toString()).commit();
					//sp.edit().putString("custom_payload", stringBuilder.toString()).commit();
					edt.setText(stringBuilder.toString());
					sp.edit().putString("xHost", E.getText().toString()).commit();
					sp.edit().putBoolean("xFrontQuery", o.isChecked()).commit();
					sp.edit().putBoolean("xBackQuery", p.isChecked()).commit();
					sp.edit().putBoolean("xOnlineHost", q.isChecked()).commit();
					sp.edit().putBoolean("xForwardedFor", r.isChecked()).commit();
					sp.edit().putBoolean("xForwardHost", s.isChecked()).commit();
					sp.edit().putBoolean("xKeepAlive", t.isChecked()).commit();
					sp.edit().putBoolean("xUserAgent", u.isChecked()).commit();
					sp.edit().putBoolean("xRealRequest", vj.isChecked()).commit();
					sp.edit().putBoolean("xDualConnect", w.isChecked()).commit();
					sp.edit().putBoolean("xRotation", x.isChecked()).commit();
					sp.edit().putBoolean("xSplitNoDelay", y.isChecked()).commit();
					sp.edit().putInt("xRadioGroup", A.getCheckedRadioButtonId()).commit();
					sp.edit().putInt("RequestMethod", C.getSelectedItemPosition()).commit();
					sp.edit().putInt("InjectMethod", D.getSelectedItemPosition()).commit();
					sp.edit().putInt("UserAgent", F.getSelectedItemPosition()).commit();
					dismiss();
				}
			});
		adb = new AlertDialog.Builder(c);
	    adb.setTitle("Payload Generator");
	    adb.setView(v);
		ad = adb.create();
	}

	public void dismiss() {
		ad.dismiss();
	}

	public void show() {
		ad.show();
	}
}




