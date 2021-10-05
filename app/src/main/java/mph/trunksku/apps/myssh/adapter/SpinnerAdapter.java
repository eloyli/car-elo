package mph.trunksku.apps.myssh.adapter;
import android.content.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import com.masfha.id.*;

public class SpinnerAdapter extends BaseAdapter {
	// Declare Variables
	Context c;
	LayoutInflater inflater;
	ArrayList<HashMap<String, String>> data;
	HashMap<String, String> resultp = new HashMap<String, String>();

	public SpinnerAdapter(Context context,
						   ArrayList<HashMap<String, String>> arraylist) {
		c = context;
		data = arraylist;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) c
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.layout_spinner_row, parent, false);
		resultp = data.get(position);
		TextView country = (TextView) itemView.findViewById(R.id.imageNameSpinner);
		ImageView flag = (ImageView) itemView.findViewById(R.id.imageIconSpinner);

		country.setText(resultp.get("COUNTRY"));
		try {
			InputStream open = c.getAssets().open(new StringBuffer().append("flag/").append(resultp.get("FLAG")).append(".png").toString());
			flag.setImageDrawable(Drawable.createFromStream(open, (String) null));
			if (open != null) {
				open.close();
			}
			//flag.setImageResource(c.getResources().getIdentifier(new StringBuffer().append("flag_").append(resultp.get("FLAG").toLowerCase()).toString(), "drawable", c.getPackageName()));
		} catch(Exception e) {
			Toast.makeText(c, e.getMessage(), 1).show();
		}
		return itemView;
	}
}
