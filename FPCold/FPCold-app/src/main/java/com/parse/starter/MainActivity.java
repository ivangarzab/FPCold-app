/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import sapphire.DownloadInventory;
import sapphire.Product;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

	// Flag for the Sync button
	public static boolean SYNCH = false;
	// Variables for Virtual Storage updates
	public static ArrayList<Product> TAKE_IN;
	public static ArrayList<Product> TAKE_OUT;
	public static ArrayList<Product> TAKE_TRANSFER;

	// Activity's UI
	private RelativeLayout RL;
	private LinearLayout logo;
	private EditText pinNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ParseAnalytics.trackAppOpenedInBackground(getIntent());
		setTitle("");

		// Initiate the UI for this activity
		RL = (RelativeLayout) findViewById(R.id.loginRelativeLayout);
		logo = (LinearLayout) findViewById(R.id.logoLinearLayout);
		pinNumber = (EditText) findViewById(R.id.loginEditText);

		// Set up native OnClickListeners
		RL.setOnClickListener(this);
		logo.setOnClickListener(this);

		pinNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
				boolean handled = false;
				if (i == EditorInfo.IME_ACTION_GO) {
					loginAction(null);
					handled = true;
				}

				return handled;
			}
		});

		TAKE_IN = new ArrayList<>();
		TAKE_OUT = new ArrayList<>();
		TAKE_TRANSFER = new ArrayList<>();
	}

	/**
	 * Access server to log in user based on the information provided
	 * @param view : Pressed button's View
	 */
	public void loginAction(View view) {
		// Hide SoftInputKeyboard
		InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

		// User PIN #
		String pin = pinNumber.getText().toString();

		// Attempt to login with Parse
		ParseUser.logInInBackground(pin, pin, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					// Start HomeActivity
					Intent i = new Intent(getApplicationContext(), HomeActivity.class);
					i.putExtra("download", true);
					startActivity(i);
				}
				else
					Toast.makeText(getApplicationContext(),
							"Unable to log in.  Please try again!", Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * Hides the SoftInputKeyboard when clicking on the layout
	 * @param v : View of the component pressed
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.loginRelativeLayout || v.getId() == R.id.logoLinearLayout) {
			InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	/**
	 * Do nothing when back button is pressed for this activity
	 */
	public void onBackPressed() { }
}
