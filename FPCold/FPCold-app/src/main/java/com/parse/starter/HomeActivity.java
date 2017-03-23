package com.parse.starter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sapphire.DownloadInventory;
import sapphire.Product;
import sapphire.UploadInventory;

public class HomeActivity extends AppCompatActivity {

	// Activity's Context
	final private Context context = this;

	// Current User loged in for static use
	public static ParseUser CURRENT_USER;
	// Current User's Tier priviledges for static use
	public static int TIER;
	// Today's date
	public static String DATE;
	// Virtual Storage
	public static ArrayList<Product> V_STORAGE;

	// TextViews for greeting the user, date and announcements
	private TextView greet, date, announcement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Set title
		setTitle("Dashboard");

		// Get boolean from calling activity
		/// if true, download the virtual storage
		Intent i = getIntent();
		if (i.getBooleanExtra("download", false)) {
			getVirtualStorage();
		}
		else {
			int count = 0;
			for (int v = 0; v < HomeActivity.V_STORAGE.size(); v++) {
				//Log.i("TRASH", HomeActivity.V_STORAGE.get(v).toString());
				count++;
			}
			Log.i("TRASH", "# of contents in V_STORAGE: " + count);
		}

		// Initialize variablers
		greet = (TextView) findViewById(R.id.greetingTextView);
		date = (TextView)findViewById(R.id.homeDateTextView);
		announcement = (TextView) findViewById(R.id.announcementTextView);

		// Set the greeting EditText
		greet.setText("Welcome " + getCurrentUser());

		// Set the date for the app and home screen
		SimpleDateFormat datestamp = new SimpleDateFormat("MM-dd-yy");
		DATE = datestamp.format(new Date());
		date.setText(DATE);
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
	 * Get the name and tier of the current user for future ose
	 * @return the name of the current user for greeting
	 */
	private String getCurrentUser() {
		CURRENT_USER = ParseUser.getCurrentUser();
		TIER = CURRENT_USER.getInt("tier");
		return CURRENT_USER.getString("name");
	}

	private void getVirtualStorage() {
		V_STORAGE = new ArrayList<>();
		// Prepare the virtual storage for offline use
		DownloadInventory DI = new DownloadInventory(context);
		DI.execute();

		V_STORAGE = DI.getVirtualStorage();
	}

	/**
	 * Decide which button was pressed and perform the corresponding action
	 * @param view : Pressed button's View
	 */
	public void homeButtonAction(View view) {
		Intent i = null;
		boolean flag = false;
		if (view.getId() == R.id.inHomeButton) {
			i = new Intent(context, InOutActivity.class);
			i.putExtra("type", 'i');
			flag = true;
			//Toast.makeText(getApplicationContext(), "INBOUND!", Toast.LENGTH_LONG).show();
		}
		else if (view.getId() == R.id.outHomeButton) {
			i = new Intent(context, InOutActivity.class);
			i.putExtra("type", 'o');
			flag = true;
			//Toast.makeText(getApplicationContext(), "OUTBOUND!", Toast.LENGTH_LONG).show();
		}
		else if (view.getId() == R.id.transferHomeButton) {
			i = new Intent(context, TransferActivity.class);
			flag = true;
			//Toast.makeText(getApplicationContext(), "TRANSFER!", Toast.LENGTH_LONG).show();
		}
		else if (view.getId() == R.id.inventoryHomeButton) {
			i = new Intent(context, InventoryActivity.class);
			flag = true;
			//Toast.makeText(getApplicationContext(), "INVENTORY!", Toast.LENGTH_LONG).show();
		}
		else if (view.getId() == R.id.settingsHomeButton) {
			if (TIER == 1) {
				//i = new Intent(context, SettingsActivity.class);
				//flag = true;
				Toast.makeText(getApplicationContext(), "SETTINGS!", Toast.LENGTH_LONG).show();
			}
			else denyAccess(context);
		} /*
		else if (view.getId() == R.id.syncHomeButton) {
			if (MainActivity.SYNCH == true) {
				UploadInventory UI = new UploadInventory(context);
				UI.execute();

				MainActivity.SYNCH = false;
				//getVirtualStorage();
			}
			else
				Toast.makeText(context,
						"There are no changes to be synchronized!", Toast.LENGTH_LONG).show();
		}
		*/

		if (flag) startActivity(i);
	}

	/**
	 * Do nothing when back button is pressed for this activity
	 */
	public void onBackPressed() { }

	/**
	 * Static method for the use all around the app
	 * if a lower-tier user tries accessing upper-tier areas, deny access through pop-up message
	 * @param context : Activitie's Context
	 */
	public static void denyAccess(Context context) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle("Oops!");
		adb.setMessage("You do not have the priviledge to access this!")
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
}
