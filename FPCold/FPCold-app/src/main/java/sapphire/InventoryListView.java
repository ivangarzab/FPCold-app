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
 * Created by Sapphire on 1/13/2017.
 * Custom ArrayAdapter for InventoryList
 */

public class InventoryListView extends BaseAdapter {

	// Necessary for the BaseAdapter extension
	private LayoutInflater inflater;

	private char type;

	private ArrayList<String> pallets;
	private ArrayList<String> locations;
	private ArrayList<String> dates;

	/**
	 * Constructor
	 * @param context : context of the activity using the object
	 * @param p : pallets ArrayList
	 * @param l : locations ArrayList
	 * @param d : dates ArrayList
	 */
	public InventoryListView(Activity context, char t, ArrayList<String> p,
							 ArrayList<String> l, ArrayList<String> d) {
		super();
		this.type = t;
		this.pallets = p;
		this. locations = l;
		this.dates = d;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * BaseAdapter extension method
	 * @return size of list(s)
	 */
	@Override
	public int getCount() {
		if (pallets.size() == locations.size() && pallets.size() == dates.size())
			return pallets.size();
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
			vi = inflater.inflate(R.layout.custom_inventory_row, null);

		TextView first = (TextView)vi.findViewById(R.id.firstFieldTextView);
		TextView second = (TextView)vi.findViewById(R.id.secondFieldTextView);
		TextView third = (TextView)vi.findViewById(R.id.thirdFieldTextView);

		if (type == 'l') {
			first.setText(locations.get(position));
			second.setText(pallets.get(position));
			third.setText(dates.get(position));
		}
		else if (type == 'd') {
			first.setText(dates.get(position));
			second.setText(pallets.get(position));
			third.setText(locations.get(position));
		}
		else if (type == 'p') {
			first.setText(pallets.get(position));
			second.setText(locations.get(position));
			third.setText(dates.get(position));
		}

		return vi;
	}
}
