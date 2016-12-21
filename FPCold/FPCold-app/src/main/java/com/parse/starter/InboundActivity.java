package com.parse.starter;

import android.IntentIntegrator;
import android.IntentResult;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import sapphire.StorageListView;

public class InboundActivity extends AppCompatActivity implements View.OnClickListener {

	// Environment variables
	final private Context context = this;
	final private Activity activity = this;

	// Variables for the UI
	private RelativeLayout RL;
	private TextView instructions;
	private EditText pallet_number, location_number;
	private ImageButton pallet_image, location_image;

	// Variables for ListView setup
	private ListView lv;
	private StorageListView SA;
	private ArrayList<String> pallets;
	private ArrayList<String> locations;

	// Variables to store database information temporarily
	private ArrayList<String> rack_numbers;
	private ArrayList<String> rack_ids;

	// Supporting flag for calling the external Barcode Scanner app
	boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbound);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setTitle("IN");

		// Initialization of all variables
		RL = (RelativeLayout)findViewById(R.id.inboundRelativeLayout);
		lv = (ListView)findViewById(R.id.checkedInboundListView);

		///// UI variables
		instructions = (TextView)findViewById(R.id.inboundInstructionsTextView);
		pallet_number = (EditText)findViewById(R.id.inboundPalletEditText);
		location_number = (EditText)findViewById(R.id.inboundLocationEditText);

		///// Variables for Barcode Scanner app
		pallet_image = (ImageButton)findViewById(R.id.inboundPalletImageButton);
		location_image = (ImageButton)findViewById(R.id.inboundLocationImageButton);

		///// Arrays for temporal storage
		rack_numbers = new ArrayList<String>();
		rack_ids = new ArrayList<String>();

		///// Prepare ListView
		lv = (ListView)findViewById(R.id.checkedInboundListView);
		pallets = new ArrayList<String>();
		locations = new ArrayList<String>();
		SA = new StorageListView(activity, 'i', pallets, locations);
		lv.setAdapter(SA);

		// Assigning the implementaiton of OnClickListener
		RL.setOnClickListener(this);
		instructions.setOnClickListener(this);

		// Set the extra OnClickListeners to call external app Barcode Scanner
		pallet_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = false;
				IntentIntegrator scanIntegrator = new IntentIntegrator(activity);
				scanIntegrator.initiateScan();
			}
		});

		location_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = true;
				IntentIntegrator scanIntegrator = new IntentIntegrator(activity);
				scanIntegrator.initiateScan();
			}
		});

		// Retrieve all of the racks and their IDs from the server
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Storage");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					if (objects.size() > 0) {
						for (ParseObject obj : objects) {
							rack_numbers.add(obj.getString("rackNumber"));
							rack_ids.add(obj.getObjectId());
						}
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement

		if (id == R.id.logout) {
			ParseUser.logOut();
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Method receiving the results from the Barcode Scanner app
	 *   if flag is false:
	 *      assign results to first EditText (pallet)
	 *   else if flag is true:
	 *      assign results to second EditText (location)
	 *   else if no scan is done, Toast the user
	 * @param requestCode : Necessary for the receiving of information from BarcodeScanner
	 * @param resultCode : Necessary for the receiving of information from BarcodeScanner
	 * @param intent : Necessary for the receiving of information from BarcodeScanner
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode,
				resultCode, intent);
		if (scanningResult != null) {
			if (!flag) {
				pallet_number.setText(scanningResult.getContents());
			}
			else {//(flag)
				location_number.setText(scanningResult.getContents());
			}
		}
		else {
			Toast t = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			t.show();
		}

	}

	/**
	 * Takes care of both of the buttons' action:
	 *   Checks for errors on the input
	 *   if the button = 'Save & Next'
	 *    then call the corresponding method and clear EditText fields
	 *   else if button = 'Finish'
	 *    then call the corresponding method and go back to HomeActivity
	 * @param view : View of the Button which calls on the method
	 */
	public void storageButtonAction(View view) {
		// Get pallet tag and location number from Activity
		String pal = pallet_number.getText().toString();
		String loc = location_number.getText().toString();
		// Get the location position in the server
		int storage_position = rack_numbers.indexOf(loc);

		// if one of the fields is missing, Toast the user and return
		if (pal.equals("") || loc.equals("")) {
			Toast.makeText(context,
					"One ore more fields are missing! Please, try again.",
					Toast.LENGTH_LONG).show();
			return;
		}

		// if the rack location does not exist in server, Toast the user and return
		if (storage_position ==-1) {
			Toast.makeText(context,
					"Rack location number is invalid! Please, try again.",
					Toast.LENGTH_LONG).show();
			location_number.setText("");
			return;
		}

		// Next & Save button clicked
		if (view.getId() == R.id.inboundNextButton) {
			inStorageAction(pal, loc, storage_position);
		}
		// Finish button clicked
		else if (view.getId() == R.id.inboundFinishButton) {
			inStorageAction(pal, loc, storage_position);
			///// Go back to HomeActivity
			Intent i = new Intent(context, HomeActivity.class);
			startActivity(i);
		}
	}

	/**
	 * Stores the desired palletNo and locationNo in the server
	 *   Check for errors on the pallet EditText
	 *   Call updateList when done
	 * @param palletNo : pallet Number
	 * @param locationNo : location Number
	 * @param position : position of location Number on rack_numbers to get server object ID
	 */
	public void inStorageAction(final String palletNo, final String locationNo, int position) {
		for (String p : pallets) {
			if (p.contains(palletNo)) {
				Toast.makeText(context,
						"Pallet has already been stored in this order! Please, try again.",
						Toast.LENGTH_LONG).show();
				pallet_number.setText("");
				return;
			}
		}

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Storage");
		query.getInBackground(rack_ids.get(position), new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					List<String> list = object.getList("content");
					list.add(palletNo);
					object.put("content", list);
					object.saveEventually(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							updateList(palletNo, locationNo);
							pallet_number.setText("");
							location_number.setText("");
						}
					});
				}
			}
		});
	}

	/**
	 * Updates list to show the new information and scrolls to the bottom of it
	 * @param pallet : pallet tag to be added to listView
	 * @param location : location number to be added to the listView
	 */
	public void updateList(String pallet, String location) {
		// Add the new pallet tag and location number to the respective ArrayLists
		pallets.add(pallet);
		locations.add(location);
		// Notify the Adapter to update the view of the listView
		SA.notifyDataSetChanged();

		// Scrolling to bottom of listView
		lv.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				lv.setSelection(SA.getCount() - 1);
			}
		});
	}

	/**
	 * Hides the SoftInputKeyboard
	 * @param v : View of the component pressed
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.inboundRelativeLayout ||
				v.getId() == R.id.inboundInstructionsTextView) {
			InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}
}
