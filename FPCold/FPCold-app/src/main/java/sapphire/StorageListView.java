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
 * Custom Array Adapter for the StorageActivty
 */
public class StorageListView extends BaseAdapter {

	// Necessary for the BaseAdapter extension
	private LayoutInflater inflater;

	// Decisive variable for the type of storing
	private char type;
	// Two array list with the information of concern:
	/// pal: pallet tags
	private ArrayList<String> pal;
	/// loc : location numbers
	private ArrayList<String> loc;

	/**
	 * Constructor
	 * @param context : context of the activity using the object
	 * @param t : type of call from activity
	 * @param p : pallet tag ArrayList
	 * @param l : location number ArrayList
	 */
	public StorageListView(Activity context, char t, ArrayList<String> p, ArrayList<String> l) {
		super();

		this.type = t;
		this.pal = p;
		this.loc= l;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * BaseAdapter extension method
	 * @return size of list(s)
	 */
	@Override
	public int getCount() {
		if (pal.size() == loc.size())
			return pal.size();
		else return -1;
	}

	/**
	 * BaseAdapter extension method : NOT IMPLEMENTED
	 * @param position : position of object(s) in array(s)
	 * @return object in desired position
	 */
	@Override
	public Object getItem(int position) {
		return null;
	}

	/**
	 * BaseAdapter extension method
	 * @param position : position of object(s) in array(s)
	 * @return ID of object in desired position
	 */
	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * BaseAdapter extension method : IMPORTANT/MAIN
	 * @param position : position of object of interest
	 * @param convertView :
	 * @param parent :
	 * @return View object with the new/updated view
	 */
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
