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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.StringWriter;

public class InventoryActivity extends AppCompatActivity implements View.OnClickListener {

	// Environment variables
	final private Context context = this;
	final private Activity activity = this;

	// Variables for the UI
	private RelativeLayout RL;
	private TextView top_instructions, bot_instructions;
	private EditText pallet_tag, location_number;
	private Button pallet_button, location_button;
	private ImageButton pallet_camera, location_camera;
	private ImageButton pallet_search, location_search;

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
		pallet_button = (Button)findViewById(R.id.inventoryTopAllButton);
		pallet_camera = (ImageButton)findViewById(R.id.inventoryTopImageButton);
		pallet_search = (ImageButton)findViewById(R.id.inventoryTopSearchButton);

		location_number = (EditText)findViewById(R.id.inventoryBotEditText);
		location_button = (Button)findViewById(R.id.inventoryBotAllButton);
		location_camera = (ImageButton)findViewById(R.id.inventoryBotImageButton);
		location_search = (ImageButton)findViewById(R.id.inventoryBotSearchButton);

		RL.setOnClickListener(this);
		top_instructions.setOnClickListener(this);
		bot_instructions.setOnClickListener(this);

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
		switch (item.getItemId()) {
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

	public void searchPallet(View view) {
		// Perform pallet search here!

		String title = "Pallet #" + pallet_tag.getText().toString();

		String location = "Location: " +"\n";
		String date_in = "\nDate in: ";
		String msg = location + date_in;

		searchAction(title, msg);
	}

	public void searchLocation(View view) {
		//Perform location search here!

		String title = "Location #" +location_number.getText().toString() + " contents";

		String msg = "\tPallet #\t\t\t\t\tDate in" + "\n";
		String[] contents = {"123", "234", "345", "456", "567", "678"};
		String[] dates = {"01012017", "01012017", "01012017", "01062017", "01062017", "01322017"};
		for (int i = 0; i < contents.length; i++) {
			msg += "\t" + contents[i] + "\t\t\t\t\t\t" + dates[i] + "\n";
		}

		searchAction(title, msg);
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
			//i = new Intent(context, InventoryList.class);
			//startActivity(i);
			Toast.makeText(getApplicationContext(), "All Inventory", Toast.LENGTH_LONG).show();
		}
		else return;
	}

	/**
	 * Hides the SoftInputKeyboard
	 * @param v : View of the component calling the method
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.inventoryRelativeLayout ||
				v.getId() == R.id.inventoryTopInstructionsTextView ||
				v.getId() == R.id.inventoryBotInstructionsTextView ||
				v.getId() == R.id.inventoryTopLinearLayout ||
				v.getId() == R.id.inventoryBotLinearLayout) {
			InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

}