package com.parse.starter;

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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {

	public static int TIER;
	public static ParseUser CURRENT_USER;

	final private Context context = this;

	private TextView greet, announcement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		setTitle("Dashboard");

		greet = (TextView) findViewById(R.id.greetingTextView);
		announcement = (TextView) findViewById(R.id.announcementTextView);

		greet.setText("Welcome " + getCurrentUser());
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

	private String getCurrentUser() {
		CURRENT_USER = ParseUser.getCurrentUser();
		TIER = CURRENT_USER.getInt("tier");
		return CURRENT_USER.getString("name");
	}

	public void homeButtonAction(View view) {
		Intent i = null;
		boolean flag = false;
		if (view.getId() == R.id.inHomeButton) {
			i = new Intent(context, InboundActivity.class);
			flag = true;
			//Toast.makeText(getApplicationContext(), "INBOUND!", Toast.LENGTH_LONG).show();
		}
		else if (view.getId() == R.id.outHomeButton) {
			//i = new Intent(context, OutboundActivity.class);
			//flag = true;
			Toast.makeText(getApplicationContext(), "OUTBOUND!", Toast.LENGTH_LONG).show();
		}
		else if (view.getId() == R.id.transferHomeButton) {
			//i = new Intent(context, TransferActivity.class);
			//flag = true;
			Toast.makeText(getApplicationContext(), "TRANSFER!", Toast.LENGTH_LONG).show();
		}
		else if (view.getId() == R.id.invnetoryHomeButton) {
			//i = new Intent(context, InventoryActivity.class);
			//flag = true;
			Toast.makeText(getApplicationContext(), "INVENTORY!", Toast.LENGTH_LONG).show();
		}
		else if (view.getId() == R.id.settingsHomeButton) {
			if (TIER == 1) {
				//i = new Intent(context, SettingsActivity.class);
				//flag = true;
				Toast.makeText(getApplicationContext(), "SETTINGS!", Toast.LENGTH_LONG).show();
			}
			else denyAccess(context);
		}

		if (flag == true) startActivity(i);
	}

	public void onBackPressed() {
		// DO NOTHING WHEN BACK BUTTON IS PRESSED FOR THIS ACTIVITY
	}

	public static void denyAccess(Context context) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		adb.setTitle("Oops!");
		adb.setMessage("You do not have the privilege to access this!")
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
