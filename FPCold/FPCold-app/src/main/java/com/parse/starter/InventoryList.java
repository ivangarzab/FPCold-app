package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import apache.CSVWriter;
import sapphire.InventoryListView;
import sapphire.Product;

public class InventoryList extends AppCompatActivity {

	// Environment variables
	final private Context context = this;
	final private Activity activity = this;

	// States the type of the list to be shown by app
	private char TYPE;

	// Title variables
	private TextView title1;
	private TextView title2;

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
		// Add filtering constraints to the query based on the user's choice
		if (TYPE == 'l')
			query.addAscendingOrder("location");
		if (TYPE == 'p')
			query.addAscendingOrder("tag");
		if (TYPE == 'd')
			query.addAscendingOrder("dateIn");
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
		}
		else if (t == 'l') {
			setTitle("Inventory List: by location");
			title1.setText("LOCATION");
			title2.setText("PALLET");
		}
		else if (t == 'd') {
			setTitle("Inventory List: by date");
			title1.setText("LOCATION");
			title2.setText("PALLET");
		}
	}

	/**
	 * Create a CSV file with the information from the current Activity
	 * and invoke the share() method in order to share the file
	 * @param view : View that called this method
	 * @throws IOException : Throw if there is an error writing into the file
	 */
	public void createCSVFile(View view) throws IOException {
		SimpleDateFormat datestamp = new SimpleDateFormat("MM-dd-yy HH:mm");
		String date = datestamp.format(new Date());
		String fileName = "inventory." + date + ".csv";
		File cacheDir = context.getCacheDir();
		File f = new File(cacheDir, fileName);

		// Initiate CSVWriter
		CSVWriter writer;
		if(f.exists() && !f.isDirectory()){
			FileWriter mFileWriter = new FileWriter(f.getPath() , true);
			writer = new CSVWriter(mFileWriter);
		}
		else {
			writer = new CSVWriter(new FileWriter(f.getPath()));
		}

		// Write data to CSV file and close
		List<String[]> data = new ArrayList<>();
		switch (TYPE) {
			case 'p':
				data.add(new String[] {"Inventory as of " + HomeActivity.DATE
						+ " sorted by pallet tag"});
				break;
			case 'l':
				data.add(new String[] {"Inventory as of " + HomeActivity.DATE
						+ " sorted by location"});
				break;
			case 'd':
				data.add(new String[] {"Inventory as of " + HomeActivity.DATE
						+ " sorted by date"});
				break;
		}
		data.add(new String[] {"Date", "Location", "Pallet Tag"});
		for (int i = 0; i < pallets.size(); i++) {
			data.add(new String[] {dates.get(i), locations.get(i), pallets.get(i)});
		}

		writer.writeAll(data);
		writer.close();

		share(fileName);
	}

	/**
	 * Invoke the ShareContent Intent
	 * @param fileName : Name of the file that will be shared
	 */
	public void share(String fileName) {
		Uri fileUri = Uri.parse("content://" + "sapphire" + "/"+fileName);
		Log.i("TRASH", "sending "+fileUri.toString()+" ...");
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
		shareIntent.setType("application/octet-stream");
		context.startActivity(Intent.createChooser(shareIntent, "Send to")
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
}
