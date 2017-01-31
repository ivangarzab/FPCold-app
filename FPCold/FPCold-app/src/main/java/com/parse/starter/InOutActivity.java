package com.parse.starter;

import android.IntentIntegrator;
import android.IntentResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import sapphire.StorageListView;
public class InOutActivity extends AppCompatActivity implements View.OnClickListener{

	// Environment variables
	final private Context context = this;
	final private Activity activity = this;

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
	private ArrayList<String> rack_numbers;

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
		rack_numbers = new ArrayList<>();

		// Set up the activity based on the type of transaction to be performed
		activitySetup(TYPE);

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

		// Populate the rack_numbers list for the outbound to check the virtual storage quicker
		if (TYPE == 'o') {
			// Retrieve all of the racks and their IDs from the server
			ParseQuery<ParseObject> query = new ParseQuery<>("Product");
			query.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> objects, ParseException e) {
					if (e == null) {
						if (objects.size() > 0) {
							for (ParseObject obj : objects) {
								rack_numbers.add(obj.getString("location"));
							}
						}
					}
				}
			});
		}
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
			else {//(flag)
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
	 * @param c : defines the type of transaction to be performed:
	 *          c = 'i' : inbound
	 *          c = 'o' : outbound
	 */
	public void activitySetup(char c) {
		// String for the instructions TextView
		String inst = "";
		// Hints to be placed on the correct EditTexts
		String pallet_hint = "Pallet Tag";
		String location_hint = "Location Number";

		// We are performing an inbound
		if (c == 'i') {
			setTitle("IN");
			getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.colorInbound)));
			inst = "First scan pallet tag to be stored " +
					"followed by the location number where it will be stored to.";
			first_number.setHint(pallet_hint);
			second_number.setHint(location_hint);
		}
		// We are performing an outbound
		else if (c == 'o') {
			setTitle("OUT");
			getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(getResources().getColor(R.color.colorOutbound)));
			inst = "First scan the location number to be unload " +
					"followed by the pallet tag that will be unloaded.";
			first_number.setHint(location_hint);
			second_number.setHint(pallet_hint);
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
		// If the pallet is already on the pallet list attached to ListView
		/// then Toast the user and return
		if (pallets.contains(palletNo) && update) {
			Toast.makeText(context,
					"Pallet has already been stored in this order! Please, try again.",
					Toast.LENGTH_LONG).show();
			first_number.setText("");
			return;
		}

		// Save the new product into the virtual storage
		// and reset EditText fields and update list if desired
		ParseObject product = new ParseObject("Product");
		product.put("tag", palletNo);
		product.put("location", locationNo);
		product.put("dateIn", HomeActivity.DATE);
		product.saveEventually();
		/*
		product.saveEventually(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (update) updateList(palletNo, locationNo);
				first_number.setText("");
				second_number.setText("");
			}
		});
		*/
		if (update) updateList(palletNo, locationNo);
		first_number.setText("");
		second_number.setText("");
	}

	/**
	 * Unloads the desired palletNo from the locationNo @ the server
	 * @param palletNo : pallet Number
	 * @param locationNo : location Number
	 * @param update : decides whether to call updateList at the end or not
	 */
	public void outStorageAction(final String palletNo, final String locationNo,
								 final boolean update) {
		// position of location Number on rack_numbers to get server object ID
		int storage_position = rack_numbers.indexOf(locationNo);
		Log.i("TRASH", palletNo +" " +locationNo);

		// if the rack location does not exist in server
		/// then Toast the user and return
		if (storage_position ==-1) {
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
					"Pallet has already been stored in this order! Please, try again.",
					Toast.LENGTH_LONG).show();
			second_number.setText("");
			return;
		}

		ParseObject obj = new ParseObject("Product");
		try {
			obj.fetch();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		ParseQuery<ParseObject> query = new ParseQuery<>("Product");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject object : objects) {
						if (object.get("location").equals(locationNo)
								&& object.get("tag").equals(palletNo)) {
							object.deleteEventually(new DeleteCallback() {
								@Override
								public void done(ParseException e) {
									if (update) updateList(palletNo, locationNo);
									first_number.setText("");
									second_number.setText("");
								}
							});
							return;
						}
					}
					Toast.makeText(context,
							"Pallet tag is not stored on this location! Please, try again.",
							Toast.LENGTH_LONG).show();

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
}
