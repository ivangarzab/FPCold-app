package com.parse.starter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import apache.CSVWriter;

/**
 * Created by Sapphire on 6/12/2017.
 */

public class SettingsReports extends Fragment implements View.OnClickListener {

	private Button day, week, month, all;
	private Button delete;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_settings_reports, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		day = (Button)view.findViewById(R.id.reportsDayButton);
		week = (Button)view.findViewById(R.id.reportsWeekButton);
		month = (Button)view.findViewById(R.id.reportsMonthButton);
		all = (Button)view.findViewById(R.id.reportsAllButton);
		delete = (Button)view.findViewById(R.id.reportsDeleteButton);

		day.setOnClickListener(this);
		week.setOnClickListener(this);
		month.setOnClickListener(this);
		all.setOnClickListener(this);
		delete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.reportsDayButton:
				shareReport('d');
				break;
			case R.id.reportsWeekButton:
				shareReport('w');
				break;
			case R.id.reportsMonthButton:
				shareReport('m');
				break;
			case R.id.reportsAllButton:
				shareReport('a');
				break;
			case R.id.reportsDeleteButton:
				deleteHistory();
				break;
		}
	}

	private void shareReport(char type) {
		Reporter fetch = new Reporter(type);
		fetch.execute();
	}

	private void deleteHistory() {
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
		adb.setTitle("One Second...");
		adb.setMessage("Are you sure you want to delete the transaction history?")
				.setCancelable(false)
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ServerDeleter deleter = new ServerDeleter();
						deleter.execute();
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
	}




	public class Reporter extends AsyncTask<Void, Void, Void> {

		private char type;
		private String fileName;
		private CSVWriter writer;
		// ProgressDialog to show while performing
		private ProgressDialog PD;

		// Constructor method
		public Reporter(char type) {
			this.type = type;

			SimpleDateFormat datestamp = new SimpleDateFormat("MM-dd-yy HH:mm");
			String date = datestamp.format(new Date());
			fileName = "report." + date + ".csv";
			File cacheDir = getActivity().getCacheDir();
			File f = new File(cacheDir, fileName);
			// Initiate CSVWriter
			if(f.exists() && !f.isDirectory()){
				FileWriter mFileWriter = null;
				try {
					mFileWriter = new FileWriter(f.getPath() , true);
				} catch (IOException e) {
					e.printStackTrace();
				}
				writer = new CSVWriter(mFileWriter);
			}
			else {
				try {
					writer = new CSVWriter(new FileWriter(f.getPath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Showing progress dialog while accessing server
			PD = ProgressDialog.show(getActivity(), "Preparing report", "Please wait...",
					false, false);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			//Dismissing the progress dialog
			PD.dismiss();

			Uri fileUri = Uri.parse("content://" + "sapphire" + "/" + fileName);
			//Log.i("TRASH", "sending "+fileUri.toString()+" ...");
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
			shareIntent.setType("application/octet-stream");
			getActivity().startActivity(Intent.createChooser(shareIntent, "Send to")
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<String[]> data = new ArrayList<>();
			// Insert report title
			switch (type) {
				case 'd': //day
					data.add(new String[]{"Transaction(s) of the day:"});
					break;
				case 'w': //week
					data.add(new String[]{"Transactions of the week:"});
					break;
				case 'm': //month
					data.add(new String[]{"Transactions of the month:"});
					break;
				case 'a': //all
					data.add(new String[]{"All transactions on record:"});
					break;
				default: return null;
			}
			data.add(new String[] {"Author", "Type", "Description", "Time Started", "Time Ended"});

			// Query the server and add to report only the relevant transactions
			ParseQuery<ParseObject> query = new ParseQuery<>("Transaction");
			query.addDescendingOrder("createdAt");
			try {
				List<ParseObject> objects = query.find();
				// Transaction sorting
				for (ParseObject object : objects) {
					switch (type) {
						case 'd': //day
							String day = object.getString("endTime").substring(0, 8);
							Log.i("TRASH", day);
							if (day.equals(HomeActivity.DATE)) {
								data.add(new String[] {object.getString("user"),
										object.getString("type"), object.getString("description"),
										object.getString("startTime"), object.getString("endTime")});
							}
							break;
						case 'w': //week
							// // TODO: 6/12/2017 Implement week report sorting algorithm 
							break;
						case 'm': //month
							// // TODO: 6/12/2017 Implement month report sorting algorithm
							break;
						case 'a': //all
							data.add(new String[] {object.getString("user"),
									object.getString("type"), object.getString("description"),
									object.getString("startTime"), object.getString("endTime")});
							break;
						default: return null;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

			try {
				writer.writeAll(data);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public class ServerDeleter extends AsyncTask<Void, Void, Void> {

		// ProgressDialog to show while performing
		private ProgressDialog PD;

		// Constructor method
		public ServerDeleter() { }

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Showing progress dialog while accessing server
			PD = ProgressDialog.show(getActivity(), "Deleting transaction hisotry", "Please wait...",
					false, false);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			//Dismissing the progress dialog
			PD.dismiss();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ParseQuery<ParseObject> query = new ParseQuery<>("Transaction");
			//boolean flag = false;
			try {
				List<ParseObject> objects = query.find();
				for (ParseObject object : objects) {
					object.deleteEventually();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
