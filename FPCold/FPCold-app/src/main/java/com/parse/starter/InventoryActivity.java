package com.parse.starter;

import android.IntentIntegrator;
import android.IntentResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import sapphire.Product;

import static android.R.id.list;

public class InventoryActivity extends AppCompatActivity implements View.OnClickListener {

	// Environment variables
	final private Context context = this;
	final private Activity activity = this;

	// Variables for the UI
	private RelativeLayout RL;
	private TextView top_instructions, bot_instructions;
	private EditText pallet_tag, location_number;
	private ImageButton pallet_camera, location_camera;

	// Virutal storage variable for error support
	ArrayList<String> virtual_locations;

	// Supporting flag for calling the external Barcode Scanner app
	boolean flag;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setTitle("INVENTORY");

		RL = (RelativeLayout)findViewById(R.id.inventoryRelativeLayout);
		top_instructions = (TextView)findViewById(R.id.inventoryTopInstructionsTextView);
		bot_instructions = (TextView)findViewById(R.id.inventoryBotInstructionsTextView);

		pallet_tag = (EditText)findViewById(R.id.inventoryTopEditText);
		pallet_camera = (ImageButton)findViewById(R.id.inventoryTopImageButton);
		location_number = (EditText)findViewById(R.id.inventoryBotEditText);
		location_camera = (ImageButton)findViewById(R.id.inventoryBotImageButton);

		virtual_locations = new ArrayList<>();
		for (Product p : HomeActivity.V_STORAGE) {
			String l = p.getLocation();
			if (!(virtual_locations.contains(l))) {
				virtual_locations.add(l);
			}
		}

		RL.setOnClickListener(this);
		top_instructions.setOnClickListener(this);
		bot_instructions.setOnClickListener(this);

		pallet_tag.setInputType(InputType.TYPE_NULL);
		location_number.setInputType(InputType.TYPE_NULL);

		pallet_tag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int i, KeyEvent event) {
				boolean handled = false;
				if (i == EditorInfo.IME_ACTION_DONE) {
					pallet_tag.setInputType(InputType.TYPE_NULL);
					hideKeyboard();
					handled = true;
				}

				return handled;
			}
		});

		location_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int i, KeyEvent event) {
				boolean handled = false;
				if (i == EditorInfo.IME_ACTION_DONE) {
					location_number.setInputType(InputType.TYPE_NULL);
					hideKeyboard();
					handled = true;
				}

				return handled;
			}
		});

		// Set the extra OnClickListeners to call external app Barcode Scanner
		pallet_camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = false;
				IntentIntegrator scanIntegrator = new IntentIntegrator(activity);
				scanIntegrator.initiateScan();
			}
		});

		location_camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				flag = true;
				IntentIntegrator scanIntegrator = new IntentIntegrator(activity);
				scanIntegrator.initiateScan();
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
				pallet_tag.setText(scanningResult.getContents());
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
	 * Let the respective EditText input text through the soft keyboard
	 * @param view : View which calls the method
	 */
	public void keyboardAction(View view) {
		int id = view.getId();

		if (id == R.id.inventoryTopKeyboardImageButton) {
			pallet_tag.requestFocus();
			pallet_tag.setInputType(InputType.TYPE_CLASS_TEXT);
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.showSoftInput(pallet_tag, InputMethodManager.SHOW_FORCED);
		}
		else if (id == R.id.inventoryBotKeyboardImageButton) {
			location_number.requestFocus();
			location_number.setInputType(InputType.TYPE_CLASS_TEXT);
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.showSoftInput(location_number, InputMethodManager.SHOW_FORCED);
		}
	}

	public void searchPallet(View view) {
		String pallet = pallet_tag.getText().toString();
		// if the pallet field is empty, throw an error
		if (pallet.equals("")) {
			searchError('p');
			return;
		}
		// Travese the Virutal Storage to find pallet
		/// if it exists, show information and clear EditText field
		for (Product p : HomeActivity.V_STORAGE) {
			if (p.getTag().equals(pallet)) {
				// Set up Strings for the formatting of the AlertDialog
				String title = "Pallet #" + pallet;

				String location = "Location: " + p.getLocation() + "\n";
				String date_in = "\nDate in: " + p.getDate() + "\n";
				String msg = location + date_in;

				// Call the AlertDialog
				searchAction(title, msg);
				// Reset location field
				pallet_tag.setText("");
				return;
			}
		}
		// if the pallet does not exist, throw an error
		searchError('p');
	}

	public void searchLocation(View view) {
		String location = location_number.getText().toString();
		// if the location field is empty, throw an error
		if (location.equals("")) {
			searchError('l');
			return;
		}
		// if the location does not exist, throw an error
		if (!(virtual_locations.contains(location))) {
			searchError('l');
			return;
		}

		// Set up Strings for the formatting of the AlertDialog
		String title = "Location #" +location;
		String msg = String.format("%1$-8s %2$32s\n", "Date in", "Pallet #");
		//Traverse the Vritual Storage to find all products in given location
		for (Product p : HomeActivity.V_STORAGE) {
			if (p.getLocation().equals(location)) {
				msg += String.format("%1$-30s %2$-10s%n", p.getDate(), p.getTag());
			}
		}
		// Call the AlertDialog
		searchAction(title, msg);
		// Reset location field
		location_number.setText("");
	}

	public void searchAction(String title, String message) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle(title);
		adb.setMessage(message)
				.setCancelable(false)
				.setNegativeButton("CONFIRM", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog ad = adb.create();
		ad.show();
	}

	public void searchError(char c) {
		String message;
		if (c == 'l') {
			message = "There was an error with the location number!\nPlease, try again.";
			location_number.setText("");
		}
		else if (c == 'p') {
			message = "There was an error with the pallet tag!\nPlease, try again.";
			pallet_tag.setText("");
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

		if (view.getId() == R.id.inventoryTopAllButton) {
			//i = new Intent(context, SearchBatchActivity.class);
			//startActivity(i);
			Toast.makeText(getApplicationContext(), "Batch Search", Toast.LENGTH_LONG).show();
		}
		else if (view.getId() == R.id.inventoryBotAllButton) {
			i = new Intent(context, InventoryList.class);
			i.putExtra("type", 'l');
			startActivity(i);
			//Toast.makeText(getApplicationContext(), "All Inventory", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * * Handle the native onClick Action
	 * @param v : View of the component calling the method
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.inventoryRelativeLayout ||
				v.getId() == R.id.inventoryTopInstructionsTextView ||
				v.getId() == R.id.inventoryBotInstructionsTextView ||
				v.getId() == R.id.inventoryTopLinearLayout ||
				v.getId() == R.id.inventoryBotLinearLayout) {
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