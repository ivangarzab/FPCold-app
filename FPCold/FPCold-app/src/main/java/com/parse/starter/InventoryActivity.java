package com.parse.starter;

import android.IntentIntegrator;
import android.IntentResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import sapphire.Product;
import sapphire.StorageListView;

public class InventoryActivity extends AppCompatActivity implements View.OnClickListener {

	// Environment variables
	final private Context context = this;
	final private Activity activity = this;

	// Variables for tab management
	/// Link derivative, which will keep every
	//// loaded fragment in memory. If this becomes too memory intensive, it
	//// may be best to switch to a
	private SectionsPagerAdapter mSectionsPagerAdapter;
	/// The link that will host the section contents.
	private ViewPager mViewPager;
	// Indicates which tab the user is currently viewing
	private int TAB;

	// Variables for the UI
	private RelativeLayout RL;
	private TextView instructions, result;
	private EditText textField;
	private Button button;
	private ImageButton imageButton;

	// Support variables for error checking
	ArrayList<String> location_numbers;
	ArrayList<String> pallet_numbers;
	ArrayList<String> dates;

	// Variables for ListView which will show the individual search results
	private ListView lv;
	private ArrayAdapter AA;
	private ArrayList<String> results;

	// Defines which filter to apply on the InventoryList view
	private char list_type = 'n';

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		// Set up the TabLayout for the parent View
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(mViewPager);

		setTitle("SEARCH INVENTORY");

		// Initiate UI elements
		RL = (RelativeLayout)findViewById(R.id.inventoryRelativeLayout);
		instructions = (TextView)findViewById(R.id.inventoryInstructionsTextView);
		result = (TextView)findViewById(R.id.inventoryResultsTextView);
		textField = (EditText)findViewById(R.id.inventoryEditText);
		button = (Button)findViewById(R.id.inventoryButton);
		imageButton = (ImageButton)findViewById(R.id.inventoryImageButton);

		// Initiate arrayLists for error checking
		location_numbers = new ArrayList<>();
		pallet_numbers = new ArrayList<>();
		dates = new ArrayList<>();

		// Initiate variables for ListView which will show the results for
		/// individual searches
		lv = (ListView)findViewById(R.id.inventoryListView);
		results = new ArrayList<String>();
		AA = new ArrayAdapter(context, android.R.layout.simple_list_item_1, results);
		lv.setAdapter(AA);

		// Set native OnClickListeners to hide the softkeyboard when in use
		RL.setOnClickListener(this);
		instructions.setOnClickListener(this);

		// Block any input from the soft keyboard towards the textField
		textField.setInputType(InputType.TYPE_NULL);
		// Hide the soft keyboard for the textField when the View is opened
		textField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int i, KeyEvent event) {
				boolean handled = false;
				if (i == EditorInfo.IME_ACTION_DONE) {
					textField.setInputType(InputType.TYPE_NULL);
					hideKeyboard();
					handled = true;
				}

				return handled;
			}
		});

		// Set the extra OnClickListeners to call external app Barcode Scanner
		imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentIntegrator scanIntegrator = new IntentIntegrator(activity);
				scanIntegrator.initiateScan();
			}
		});

		// Setup Tabs PageChangeListener in order to know which tab is currently showing
		final ViewPager.OnPageChangeListener changer = new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset,
									   int positionOffsetPixels) { }

			@Override
			public void onPageScrollStateChanged(int state) { }

			@Override
			public void onPageSelected(int position) {
				switch (position) {
					case 0:
						TAB = 1;
						activitySetup();
						break;
					case 1:
						TAB = 2;
						activitySetup();
						break;
					default:
						break;
				}
			}
		};
		mViewPager.setOnPageChangeListener(changer);
		mViewPager.post(new Runnable() {
			@Override
			public void run() {
				changer.onPageSelected(mViewPager.getCurrentItem());
			}
		});

		// Retrieve all of the racks and their IDs from the server
		ParseQuery<ParseObject> query = new ParseQuery<>("Product");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					if (objects.size() > 0) {
						for (ParseObject obj : objects) {
							dates.add(obj.getString("dateIn"));
							location_numbers.add(obj.getString("location"));
							pallet_numbers.add(obj.getString("tag"));
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
		switch (id) {
			case R.id.logout:
				ParseUser.logOut();
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Method receiving the results from the Barcode Scanner app
	 *   if there is a result, paste it on the textField
	 *   else if no scan is done, Toast the user
	 * @param requestCode : Necessary for the receiving of information from BarcodeScanner
	 * @param resultCode : Necessary for the receiving of information from BarcodeScanner
	 * @param intent : Necessary for the receiving of information from BarcodeScanner
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode,
				resultCode, intent);
		if (scanningResult != null) {
			textField.setText(scanningResult.getContents());
		}
		else {
			Toast t = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			t.show();
		}
	}

	/**
	 * Sets up the Activity depending on the current tab:
	 *   if TAB == 1 then we show the Inventory actions
	 *   else if TAB == 2 then we show the Pallet actions
	 */
	public void activitySetup() {
		result.setText("Individual Search Results:");
		String inst = "";
		String hint = "";
		String butt = "";
		textField.setText("");

		if (TAB == 1) {
			inst = "Scan a Location Number to find all of its contents:";
			hint = "Location Number";
			butt = "View All Inventory";
		}
		else if (TAB == 2) {
			inst = "Scan a Pallet Tag to find it on the virtual storage:";
			hint = "Pallet Tag";
			butt = "Search Pallet Tags by Batch";
		}

		instructions.setText(inst);
		textField.setHint(hint);
		button.setText(butt);
	}

	/**
	 * Let the respective EditText input text through the soft keyboard
	 * @param view : View which calls the method
	 */
	public void keyboardAction(View view) {
		textField.requestFocus();
		textField.setInputType(InputType.TYPE_CLASS_TEXT);
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(textField, InputMethodManager.SHOW_FORCED);
	}

	public void searchAction (View view) {
		results.clear();
		// if the textField is empty, throw an error
		String search = textField.getText().toString();
		if (search.equals("")) {
			searchError();
			return;
		}

		if (TAB == 1) {
			// if the location does not exist, throw an error
			if (!(location_numbers.contains(search))) {
				searchError();
				return;
			}
			for (int i = 0; i < location_numbers.size(); i++) {
				if (location_numbers.get(i).equals(search)) {
					results.add(pallet_numbers.get(i));
				}
			}
			result.setText("Location Number " + search + " contains:");
			AA.notifyDataSetChanged();
		}
		else if (TAB == 2) {
			// if the pallet is not on the inventory, throw an error
			// else, get its position on the support ArrayList
			int position;
			if (!(pallet_numbers.contains(search))) {
				searchError();
				return;
			}
			else {
				position = pallet_numbers.indexOf(search);
			}

			result.setText("Pallet tag " + search + " location: ");
			results.add(location_numbers.get(position));
			// Notify the Adapter to update the view of the listView
			AA.notifyDataSetChanged();
		}
	}

	public void searchError() {
		result.setText("Individual Search Results:");
		results.clear();

		String message;
		if (TAB == 1) {
			message = "There was an error with the location number!\nPlease, try again.";
			textField.setText("");
		}
		else if (TAB == 2) {
			message = "There was an error with the pallet tag!\nPlease, try again.";
			textField.setText("");
		}
		else return;

		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle("Oops...");
		adb.setMessage(message)
				.setCancelable(false)
				.setNegativeButton("GO BACK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog ad = adb.create();
		ad.show();
	}

	/**
	 * Take the user to the next Activity depending on which of the buttons is pressed
	 * @param view : View of the component calling the method
	 */
	public void buttonsAction(View view) {
		Intent i = null;

		if (TAB == 1) {
			viewInventory();
		}
		else if (TAB == 2) {
			//i = new Intent(context, SearchBatchActivity.class);
			//startActivity(i);
			Toast.makeText(getApplicationContext(), "Batch Search", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Invokes the AlertDialog to prompt the user to select a filter for the InventoryList
	 * Filters:
	 *   Location
	 *   Pallet Tag
	 *   Date
	 * Starts the InventoryList Activity once a filter has been chosen
	 */
	private void viewInventory() {
		// Set selection choices
		String[] choices_list = getResources().getStringArray(R.array.filters_array);
		int choice_check = -1;

		// Invoke the AlertDialog and functionality
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle("Select a Filter for Inventory");
		adb.setSingleChoiceItems(choices_list, choice_check, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					list_type = 'l';
				}
				else if (which == 1) {
					list_type = 'p';
				}
				else if (which == 2) {
					list_type = 'd';
				}
			}
		});
		adb.setCancelable(false)
				.setPositiveButton("GO", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//// If no filter was selected, prompt the user through Toast
						if (list_type == 'n') {
							Toast.makeText(context, "Cannot proceed without a filter for inventory.",
									Toast.LENGTH_LONG).show();
						}
						//// If a filter was selected, start InventoryList activity
						// which desired filter
						else {
							Intent i = new Intent(context, InventoryList.class);
							i.putExtra("type", list_type);
							startActivity(i);
						}
					}
				})
				.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						list_type = 'n';
						dialog.cancel();
					}
				});
		AlertDialog ad = adb.create();
		ad.show();
	}

	/**
	 * * Handle the native onClick Action
	 * @param v : View of the component calling the method
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.inventoryRelativeLayout ||
				v.getId() == R.id.inventoryInstructionsTextView) {
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



	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		public PlaceholderFragment() {
		}

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.content_inventory, container, false);
			return rootView;
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {	return 2; }

		@Override
		public CharSequence getPageTitle(int position) {
			// Show tab titles
			switch (position) {
				case 0:
					return "by LOCATION";
				case 1:
					return "by PALLET";
			}
			return null;
		}
	}
}