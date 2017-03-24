package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.ParseUser;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

	// Environment variables
	final private Context context = this;
	final private Activity activity = this;

	private EditText name, pass, announce;
	private Button u_add, u_all, t_first, t_second, t_third, t_delete;
	private Spinner spinner;

	private int tier;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setTitle("SETTINGS");

		name = (EditText)findViewById(R.id.settingsUsersTopEditText);
		pass = (EditText)findViewById(R.id.settingsUsersMidEditText);
		spinner = (Spinner)findViewById(R.id.settingsUsersSpinner);
		u_add = (Button)findViewById(R.id.settingsAddUsersButton);
		u_all = (Button)findViewById(R.id.settingsAllUsersButton);

		t_first = (Button)findViewById(R.id.settingsFirstTransactionsButton);
		t_second = (Button)findViewById(R.id.settingsSecondTransactionsButton);
		t_third = (Button)findViewById(R.id.settingsThirdTransactionsButton);
		t_delete = (Button)findViewById(R.id.settingsDeleteTransactionsButton);

		announce = (EditText)findViewById(R.id.settingsAnnEditText);

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> AA = ArrayAdapter.createFromResource(this,
				R.array.tiers_array, R.layout.custom_spinner_item);
		// Specify the layout to use when the list of choices appears
		AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(AA);
		spinner.setOnItemSelectedListener(this);

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



	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		parent.getItemAtPosition(position);

		if (position == 2) {

		}
		else if (position == 1) {

		}
		else { // position == 0

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// THROW AN ERROR MESSAGE
		/// Do the same as else statement in previous method
	}
}
