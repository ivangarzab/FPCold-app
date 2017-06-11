package com.parse.starter;

import android.IntentIntegrator;
import android.IntentResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sapphire.Product;
import sapphire.StorageListView;
import sapphire.Transaction;

public class InOutActivity extends AppCompatActivity implements View.OnClickListener{

	// Environment variables
	final private Context context = this;
	final private Activity activity = this;

	// Transaction object for record-keeping
	private Transaction TRANSACTION;

	// States the type of transaction to be performed by app
	private char TYPE;

	// Variables for the UI
	private RelativeLayout RL;
	private TextView instructions;
	private EditText first_number, second_number;
	private ImageButton first_image, second_image;

	// Variables for ListView setup
	private ListView lv;
	private StorageListView SA;
	private ArrayList<String> pallets;
	private ArrayList<String> locations;

	// Variable to store database information temporarily
	private ArrayList<String> virtual_locations;

	// Supporting flag for calling the external Barcode Scanner app
	private boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_in_out);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the type of transaction to be performed by app
		Intent i = getIntent();
		TYPE = i.getCharExtra("type", 'c');

		// Initialization of variables
		RL = (RelativeLayout)findViewById(R.id.inoutRelativeLayout);

		///// UI variables
		instructions = (TextView)findViewById(R.id.inoutInstructionsTextView);
		first_number = (EditText)findViewById(R.id.inoutFirstEditText);
		second_number = (EditText)findViewById(R.id.inoutSecondEditText);

		///// Variables for Barcode Scanner app
		first_image = (ImageButton)findViewById(R.id.inoutFirstImageButton);
		second_image = (ImageButton)findViewById(R.id.inoutSecondImageButton);

		///// Arrays for temporal storage
		virtual_locations = new ArrayList<>();

		// Set up the activity based on the type of transaction to be performed
		activitySetup();

		///// Prepare ListView
		lv = (ListView)findViewById(R.id.checkedInoutListView);
		pallets = new ArrayList<>();
		locations = new ArrayList<>();
		SA = new StorageListView(activity, TYPE, pallets, locations);
		lv.setAdapter(SA);
		//////// Set up the longClickListener in order to delete items from the list
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
										   final int i, long l) {
				AlertDialog.Builder adb = new AlertDialog.Builder(context);
				adb.setTitle("Warning!");
				adb.setMessage("Would you like to revert this action?")
						.setCancelable(false)
						.setPositiveButton("YES", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								takeOutList(i);
							}
						})
						.setNegativeButton("NO", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});
				AlertDialog ad = adb.create();
				ad.show();
				return true;
			}
		});

		// Assigning the implementaiton of OnClickListener
		RL.setOnClickListener(this);
		instructions.setOnClickListener(this);

		// Hide the keyboard from EditTexts
		first_number.setInputType(InputType.TYPE_NULL);
		first_number.setOnClickListener(this);
		second_number.setInputType(InputType.TYPE_NULL);
		second_number.setOnClickListener(this);

		// Set IME Action behaviors
		first_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
				boolean handled = false;
				if (i == EditorInfo.IME_ACTION_NEXT) {
					keyboardAction(findViewById(R.id.inoutSecondEditText +1));
					first_number.setInputType(InputType.TYPE_NULL);
					handled = true;
				}
				return handled;
			}
		});

		second_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
				boolean handled = false;
				if (i == EditorInfo.IME_ACTION_DONE) {
					second_number.setInputType(InputType.TYPE_NULL);
					hideKeyboard();
					handled = true;
				}
				return handled;
			}
		});

		// Set the extra OnClickListeners to call external app Barcode Scanner
		first_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = false;
				IntentIntegrator scanIntegrator = new IntentIntegrator(activity);
				scanIntegrator.initiateScan();
			}
		});

		second_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = true;
				IntentIntegrator scanIntegrator = new IntentIntegrator(activity);
				scanIntegrator.initiateScan();
			}
		});

		// Populate the virutal_locations list for the outbound to check for existence of locations
		// on the server
		// Retrieve all of the racks and their IDs from the server
		ParseQuery<ParseObject> query = new ParseQuery<>("Product");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					if (objects.size() > 0) {
						for (ParseObject obj : objects) {
							virtual_locations.add(obj.getString("location"));
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
		switch (id) {
			case R.id.logout:
				ParseUser.logOut();
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
				return true;
			case android.R.id.home:
				finishAction();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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
				first_number.setText(scanningResult.getContents());
			}
			else { //(flag)
				second_number.setText(scanningResult.getContents());
			}
		}
		else {
			Toast t = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			t.show();
		}
	}

	/**
	 * Do the necessary adjustments depending on the type of transaction to be performed on this
	 *  activity
	 */
	public void activitySetup() {
		// String for the instructions TextView
		String inst = "";
		// Hints to be placed on the correct EditTexts
		String pallet_hint = "Pallet Tag";
		String location_hint = "Location Number";

		// We are performing an inbound
		if (TYPE == 'i') {
			setTitle("IN");
			getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.colorInbound)));
			inst = "First scan pallet tag to be stored " +
					"followed by the location number where it will be stored to.";
			first_number.setHint(pallet_hint);
			second_number.setHint(location_hint);

			openTransaction("INBOUND");
		}
		// We are performing an outbound
		else if (TYPE == 'o') {
			setTitle("OUT");
			getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.colorOutbound)));
			inst = "First scan the location number to be unload " +
					"followed by the pallet tag that will be unloaded.";
			first_number.setHint(location_hint);
			second_number.setHint(pallet_hint);

			openTransaction("OUTBOUND");
		}
		else Log.i("TRASH", "Something went wrong on activitySetup!");
		instructions.setText(inst);
	}

	/**
	 * Let the respective EditText input text through the soft keyboard
	 * @param view : View which calls the method
	 */
	public void keyboardAction(View view) {
		int id = view.getId();

		if (id == R.id.inoutFirstKeyboardImageButton) {
			first_number.requestFocus();
			first_number.setInputType(InputType.TYPE_CLASS_TEXT);
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.showSoftInput(first_number, InputMethodManager.SHOW_FORCED);
		}
		else if (id == R.id.inoutSecondKeyboardImageButton) {
			second_number.requestFocus();
			second_number.setInputType(InputType.TYPE_CLASS_TEXT);
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.showSoftInput(second_number, InputMethodManager.SHOW_FORCED);
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
		// Next & Save button clicked
		if (view.getId() == R.id.inoutNextButton) {
			// Get pallet tag and location number from Activity
			String first_field = first_number.getText().toString();
			String second_field = second_number.getText().toString();

			// if one of the fields is missing, Toast the user and return
			if (first_field.equals("") || second_field.equals("")) {
				Toast.makeText(context,
						"One ore more fields are missing! Please, try again.",
						Toast.LENGTH_LONG).show();
				return;
			}

			if (TYPE == 'i')
				inStorageAction(first_field, second_field, true);
			else if (TYPE == 'o')
				outStorageAction(second_field, first_field, true);
		}
		// Finish button clicked
		else if (view.getId() == R.id.inoutFinishButton) {
			// Go back to HomeActivity
			finishAction();
		}
	}

	/**
	 * Stores the desired palletNo in the  locationNo @ the server
	 *   Check for errors on the pallet EditText
	 *   Call updateList when done
	 * @param palletNo : pallet Number
	 * @param locationNo : location Number
	 * @param update : decides whether to call updateList at the end or not
	 */
	public void inStorageAction(final String palletNo, final String locationNo,
								final boolean update) {
		Log.i("TRASH", "In");
		// If the pallet is already on the pallet list attached to ListView
		/// then Toast the user and return
		if (pallets.contains(palletNo) && update) {
			Toast.makeText(context,
					"Pallet has already been stored in this order! Please, try again.",
					Toast.LENGTH_LONG).show();
			first_number.setText("");
			return;
		}

		// Create an IN AsyncTask object and execute the server access
		ServerAccess access = new ServerAccess(palletNo, locationNo, update, 'i');
		access.execute();
	}

	/**
	 * Unloads the desired palletNo from the locationNo @ the server
	 * @param palletNo : pallet Number
	 * @param locationNo : location Number
	 * @param update : decides whether to call updateList at the end or not
	 */
	public void outStorageAction(final String palletNo, final String locationNo,
								 final boolean update) {
		Log.i("TRASH", "Out");
		// if the rack location does not exist in virtual storage
		/// then Toast the user and return
		if (virtual_locations.indexOf(locationNo) == -1) {
			Toast.makeText(context,
					"Rack location number is invalid! Please, try again.",
					Toast.LENGTH_LONG).show();
			first_number.setText("");
			return;
		}

		// If the pallet is already on the pallet list attached to ListView
		/// then Toast the user and return
		if (pallets.contains(palletNo) && update) {
			Toast.makeText(context,
					"Pallet has already been used in this order! Please, try again.",
					Toast.LENGTH_LONG).show();
			second_number.setText("");
			return;
		}

		// Create an OUT AsyncTask object and execute the server access
		ServerAccess access = new ServerAccess(palletNo, locationNo, update, 'o');
		access.execute();
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
		first_number.requestFocus();
	}

	/**
	 * Reverts the action performed (either IN or OUT) and updates the list accordingly
	 * @param position : The position of the pallet+location pair to revert action to
	 */
	public void takeOutList(int position) {
		// if we're on an Inbound, take the item out of the virtual storage but update locally
		if (TYPE == 'i') {
			outStorageAction(pallets.get(position), locations.get(position), false);
		}
		// else if we're on an Outbound, put the item back into the virtual storage
		// but update locally
		else if (TYPE == 'o') {
			inStorageAction(pallets.get(position), locations.get(position), false);
		}
		else return;

		// Get the pallet nad location out from the ListVIew's list & update
		pallets.remove(position);
		locations.remove(position);
		SA.notifyDataSetChanged();
	}

	public void finishAction() {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle("One Second...");
		adb.setMessage("Are you sure you want to end this transaction?")
				.setCancelable(false)
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						closeTransaction();
						hideKeyboard();
						Intent i = new Intent(getApplicationContext(), HomeActivity.class);
						startActivity(i);
					}
				})
				.setNegativeButton("STAY", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog ad = adb.create();
		ad.show();
	}

	public void openTransaction(String type) {
		// Get current date and time
		SimpleDateFormat datestamp = new SimpleDateFormat("MM-dd-yy HH:mm");
		String date = datestamp.format(new Date());
		// Initiate Transaction object
		TRANSACTION = new Transaction(HomeActivity.CURRENT_USER.getString("name"), date, type);
	}

	public void closeTransaction() {
		// Get current date and time
		SimpleDateFormat datestamp = new SimpleDateFormat("MM-dd-yy HH:mm");
		String date = datestamp.format(new Date());
		// Generate Transaction's Description
		String desc = "";
		for (int i = 0; i < pallets.size(); i++) {
			desc += pallets.get(i) + " --> " + locations.get(i) + "\n";
		}

		if (desc.equals("")) return;
		else {
			// Set Transaction endTime and Description
			TRANSACTION.setEndTime(date);
			TRANSACTION.setDescription(desc);
			// Save Transaction in the server
			TRANSACTION.sendTransaction();
		}
	}

	/**
	 * Handle the native onClick Action
	 * @param v : View of the component calling the method
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.inoutRelativeLayout || v.getId() == R.id.inoutInstructionsTextView) {
			hideKeyboard();
		}
		else if (v.getId() == R.id.inoutFirstImageButton
				|| v.getId() == R.id.inoutSecondImageButton) {
			hideKeyboard();
		}
	}

	/**
	 * Hides the SoftInputKeyboard
	 */
	public void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}



	public class ServerAccess extends AsyncTask<Void, Void, Void> {

		// Declaring functional variables
		private String palletNo;
		private	String locationNo;
		private boolean update;
		private char type;
		// ProgressDialog to show while performing
		private ProgressDialog PD;

		// Constructor method
		public ServerAccess(String pallet, String location, boolean update, char type) {
			this.palletNo = pallet;
			this.locationNo = location;
			this.update = update;
			this.type = type;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Showing progress dialog while accessing server
			PD = ProgressDialog.show(context, "Accessing Server", "Please wait...", false, false);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			// Reset EditText fields and update list if desired
			if (update) updateList(palletNo, locationNo);
			first_number.setText("");
			second_number.setText("");
			//Dismissing the progress dialog
			PD.dismiss();
		}

		@Override
		protected Void doInBackground(Void... params) {
			switch (type) {
				case 'i':
					// Create a new product and store it on the server
					ParseObject product = new ParseObject("Product");
					product.put("tag", palletNo);
					product.put("location", locationNo);
					product.put("dateIn", HomeActivity.DATE);

					// Save the object on the current thread
					/// and proceed to the onPostExecute method
					try {
						product.save();
					} catch (ParseException in_error) {
						in_error.printStackTrace();
					}
					break;
				case 'o':
					ParseQuery<ParseObject> query = new ParseQuery<>("Product");
					query.findInBackground(new FindCallback<ParseObject>() {
						@Override
						public void done(List<ParseObject> objects, ParseException e) {
							if (e == null) {
								for (ParseObject object : objects) {
									if (object.get("location").equals(locationNo)
											&& object.get("tag").equals(palletNo)) {
										// Delete the object on the current thread
										/// and proceed to the onPostExecute method
										try {
											object.delete();
										} catch (ParseException out_error) {
											out_error.printStackTrace();
										}
										return;
									}
								}
								Toast.makeText(context,
										"Pallet tag is not stored on this location! Please, try again.",
										Toast.LENGTH_LONG).show();
							}
						}
					});
					break;
			}

			return null;
		}
	}
}
