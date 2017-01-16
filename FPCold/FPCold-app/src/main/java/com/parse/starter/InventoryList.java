package com.parse.starter;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import sapphire.InventoryListView;

public class InventoryList extends AppCompatActivity {

	// Environment variables
	final private Context context = this;
	final private Activity activity = this;

	// States the type of the list to be shown by app
	private char TYPE;

	// Title variables
	private TextView title1;
	private TextView title2;
	private TextView title3;

	// Variables for ListView setup
	private ListView lv;
	private InventoryListView IA;
	private ArrayList<String> pallets;
	private ArrayList<String> locations;
	private ArrayList<String> dates;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory_list);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the type of transaction to be performed by app
		Intent i = getIntent();
		TYPE = i.getCharExtra("type", 'c');

		// Initiate titles
		title1 = (TextView)findViewById(R.id.inventoryListTitle1TextView);
		title2 = (TextView)findViewById(R.id.inventoryListTitle2TextView);
		title3 = (TextView)findViewById(R.id.inventoryListTitle3TextView);

		///// Prepare ListView and components
		lv = (ListView)findViewById(R.id.inventoryListView);
		pallets = new ArrayList<>();
		locations = new ArrayList<>();
		dates = new ArrayList<>();
		IA = new InventoryListView(activity, TYPE, pallets, locations, dates);

		// Set up the activity depending on the active filter (TYPE)
		activitySetup(TYPE);

		// Unrack product when a row is long-clicked
		/*
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
										   final int i, long l) {
				AlertDialog.Builder adb = new AlertDialog.Builder(context);
				adb.setTitle("Warning!");
				adb.setMessage("Would you like to unrack this product")
						.setCancelable(false)
						.setPositiveButton("YES", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent i = new Intent(context, InOutActivity.class);
								i.putExtra("type", 'o');
								i.putExtra("inventory", true);
								i.putExtra("pallet", pallets.get(i));
								i.putExtra("location", locations.get(i));
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
		});		*/

		// Retrieve all of the rack numbers, product tags and their dates
		ParseQuery<ParseObject> query = new ParseQuery<>("Product");
		if (TYPE == 'l')
			query.addDescendingOrder("location");
		if (TYPE == 'd')
			query.addDescendingOrder("dateIn");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					if (objects.size() > 0) {
						for (ParseObject obj : objects) {
							pallets.add(obj.getString("tag"));
							locations.add(obj.getString("location"));
							dates.add(obj.getString("dateIn"));
						}
						lv.setAdapter(IA);
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Set up activity's title and column headings depending on TYPE
	 * @param t : type of filter to be applied to the list
	 */
	private void activitySetup(char t) {
		if (t == 'p') {
			setTitle("Inventory List: by pallet");
			title1.setText("PALLET");
			title2.setText("LOCATION");
			title3.setText("DATE IN");
		}
		else if (t == 'l') {
			setTitle("Inventory List: by location");
			title1.setText("LOCATION");
			title2.setText("PALLET");
			title3.setText("DATE IN");
		}
		else if (t == 'd') {
			setTitle("Inventory List: by date");
			title1.setText("DATE IN");
			title2.setText("PALLET");
			title3.setText("LOCATION");
		}
	}

}
