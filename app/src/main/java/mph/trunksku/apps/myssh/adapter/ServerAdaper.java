package mph.trunksku.apps.myssh.adapter;
import android.content.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import com.masfha.id.*;

public class ServerAdaper extends RecyclerView.Adapter<ServerAdaper.MyViewHolder> {

    ArrayList<HashMap<String, String>> data;
	HashMap<String, String> resultp = new HashMap<String, String>();

	private Context cont;
	
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView country;
		public ImageView flag;

        public MyViewHolder(View view) {
            super(view);
            country = (TextView) itemView.findViewById(R.id.imageNameSpinner);
			flag = (ImageView) itemView.findViewById(R.id.imageIconSpinner);
        }
    }


    public ServerAdaper(Context c, ArrayList<HashMap<String, String>> arraylist) {
        data = arraylist;
		cont = c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.layout_spinner_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        resultp = data.get(position);
        holder.country.setText(resultp.get("COUNTRY"));
		try {
			holder.flag.setImageResource(cont.getResources().getIdentifier(new StringBuffer().append("flag_").append(resultp.get("FLAG").toLowerCase()).toString(), "drawable", cont.getPackageName()));
		} catch(Exception e) {
			//Toast.makeText(c, e.getMessage(), 1).show();
		}
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}

