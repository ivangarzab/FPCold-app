package sapphire;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.starter.R;

import java.util.ArrayList;

/**
 * Created by Sapphire on 12/20/2016.
 */
public class StorageListView extends BaseAdapter {

	private LayoutInflater inflater;

	private char type;
	private ArrayList<String> pal;
	private ArrayList<String> loc;

	public StorageListView(Activity context, char t, ArrayList<String> p, ArrayList<String> l) {
		super();

		this.type = t;
		this.pal = p;
		this.loc= l;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (pal.size() == loc.size())
			return pal.size();
		else return -1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null)
			vi = inflater.inflate(R.layout.custom_storage_row, null);

		TextView left_title = (TextView)vi.findViewById(R.id.leftTitleTextView);
		TextView left_sub = (TextView)vi.findViewById(R.id.leftSubTextView);
		TextView right_title = (TextView)vi.findViewById(R.id.rightTitleTextView);
		TextView right_sub = (TextView)vi.findViewById(R.id.rightSubTextView);

		if (type == 'i') {
			left_title.setText(pal.get(position));
			right_title.setText(loc.get(position));
			left_sub.setText("Pallet Tag");
			right_sub.setText("Location Number");
		}
		else if (type == 'o') {
			left_title.setText(loc.get(position));
			right_title.setText(pal.get(position));
			left_sub.setText("Location Number");
			right_sub.setText("Pallet Tag");
		}

		return vi;
	}
}
