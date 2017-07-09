package com.parse.starter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Sapphire on 6/12/2017.
 */

public class SettingsUsers extends Fragment implements View.OnClickListener {

	private View VIEW;
	private char ACTION;
	private Button add, edit, confirm;
	public EditText name, passcode;
	private Spinner user, tier;
	private TextView title;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_settings_users, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		VIEW = view;
		ACTION = 'a';

		add = (Button)view.findViewById(R.id.usersAddButton);
		edit = (Button)view.findViewById(R.id.usersEditButton);
		confirm = (Button)view.findViewById(R.id.usersActionButton);
		name = (EditText)view.findViewById(R.id.usersNameTextView);
		passcode = (EditText)view.findViewById(R.id.usersPasscodeTextView);
		user = (Spinner)view.findViewById(R.id.usersTopSpinner);
		tier = (Spinner)view.findViewById(R.id.usersTierSpinner);
		title = (TextView)view.findViewById(R.id.usersInfoTitleTextView);

		add.setOnClickListener(this);
		edit.setOnClickListener(this);
		confirm.setOnClickListener(this);

		setAction(ACTION);

		ArrayAdapter<CharSequence> users_adapter = ArrayAdapter.createFromResource(VIEW.getContext(),
				R.array.users_add, R.layout.custom_spinner_item);
		users_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		user.setAdapter(users_adapter);

		ArrayAdapter<CharSequence> tiers_adapter = ArrayAdapter.createFromResource(VIEW.getContext(),
				R.array.tiers_array, R.layout.custom_spinner_item);
		tiers_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tier.setAdapter(tiers_adapter);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.usersAddButton:
				setAction('a');
				break;
			case R.id.usersEditButton:
				setAction('e');
				break;
			case R.id.usersActionButton:
				actionButton();
				break;
		}
	}

	private void setAction(char c) {
		if (c == 'a') {
			ACTION = c;
			add.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
			edit.setBackgroundColor(getResources().getColor(R.color.colorBackgroundLight));
/*
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(VIEW.getContext(),
					R.array.users_add, R.layout.custom_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			user.setAdapter(adapter);
			*/
			user.setClickable(false);

			name.setText("");
			passcode.setText("");
			tier.setSelection(0);
			title.setText("Add User:");
			confirm.setText("ADD");
		}
		else if (c == 'e') {
			edit.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
			add.setBackgroundColor(getResources().getColor(R.color.colorBackgroundLight));
/*
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(VIEW.getContext(),
					R.array.users_add, R.layout.custom_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			user.setAdapter(adapter);
			*/
			user.setClickable(true);

			name.setText("");
			passcode.setText("");
			tier.setSelection(0);
			title.setText("Edit User:");
			confirm.setText("EDIT");
		}
	}

	private void actionButton() {
		if (name.getText().toString().equals("") || passcode.getText().toString().equals("") ||
				tier.getSelectedItemPosition() == 0) {
			Toast.makeText(VIEW.getContext(),
					"Whoops! One or more fields is missing. Please try again",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (ACTION == 'a') {
			ServerSignup signup = new ServerSignup();
			signup.execute();
		}
		else if (ACTION == 'e') {
			Toast.makeText(VIEW.getContext(), "EDIT USER", Toast.LENGTH_LONG).show();
		}
	}


	public class ServerSignup extends AsyncTask<Void, Void, Void> {

		private String n, p;
		private int t;
		// ProgressDialog to show while performing
		private ProgressDialog PD;

		// Constructor method
		public ServerSignup() {
			this.n = name.getText().toString();
			this.p = passcode.getText().toString();
			this.t = Integer.valueOf(tier.getSelectedItem().toString().substring(0, 1));
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Showing progress dialog while accessing server
			PD = ProgressDialog.show(getActivity(), "Signing up", "Please wait...",
					false, false);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			name.setText("");
			passcode.setText("");
			tier.setSelection(0);
			Toast.makeText(VIEW.getContext(), "Sign up successful!",
					Toast.LENGTH_LONG).show();
			//Dismissing the progress dialog
			PD.dismiss();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ParseUser user = new ParseUser();
			user.setUsername(p);
			user.setPassword(p);
			user.put("name", n);
			user.put("tier", t);
			try {
				user.signUp();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	public class ServerUser extends AsyncTask<Void, Void, Void> {

		// ProgressDialog to show while performing
		private ProgressDialog PD;

		// Constructor method
		public ServerUser() { }

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Showing progress dialog while accessing server
			PD = ProgressDialog.show(getActivity(), "Updating User", "Please wait...",
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

			return null;
		}
	}
}
