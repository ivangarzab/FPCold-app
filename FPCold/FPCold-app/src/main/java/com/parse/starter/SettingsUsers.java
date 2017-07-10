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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sapphire on 6/12/2017.
 */

public class SettingsUsers extends Fragment implements View.OnClickListener {

	private View VIEW;
	private char ACTION;
	private Button add, edit, confirm;
	private EditText name, passcode;
	private Spinner user, tier;
	private TextView title;

	private int user_id;
	private List<ParseUser> users_list;

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

		users_list = new ArrayList<>();

		add.setOnClickListener(this);
		edit.setOnClickListener(this);
		confirm.setOnClickListener(this);

		setAction(ACTION);

		ArrayAdapter<CharSequence> tiers_adapter = ArrayAdapter.createFromResource(VIEW.getContext(),
				R.array.tiers_array, R.layout.custom_spinner_item);
		tiers_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tier.setAdapter(tiers_adapter);

		user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				user_id = i;

				if (i == 0) {
					name.setText("");
					passcode.setText("");
					tier.setSelection(0);
				}
				else {
					ParseUser u = users_list.get(i - 1);
					name.setText(u.get("name").toString());
					passcode.setText(u.getUsername());
					int t = Integer.valueOf(u.get("tier").toString());
					if (t == 0 || t == 1) {
						tier.setSelection(1);
					} else if (t == 2) {
						tier.setSelection(2);
					} else Log.i("TRASH", "WTF?");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) { }
		});
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

			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(VIEW.getContext(),
					R.array.users_add, R.layout.custom_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			user.setAdapter(adapter);

			user.setClickable(false);

			name.setText("");
			passcode.setText("");
			tier.setSelection(0);
			title.setText("Add User:");
			confirm.setText("ADD");
		}
		else if (c == 'e') {
			ACTION = c;
			edit.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
			add.setBackgroundColor(getResources().getColor(R.color.colorBackgroundLight));

			QueryUsers query = new QueryUsers();
			query.execute();
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
			//ServerUpdate update = new ServerUpdate();
			//update.execute();
			Toast.makeText(VIEW.getContext(), "EDIT USER", Toast.LENGTH_LONG).show();
		}
	}


	private class ServerSignup extends AsyncTask<Void, Void, Void> {

		private String n, p;
		private int t;
		// ProgressDialog to show while performing
		private ProgressDialog PD;

		// Constructor method
		ServerSignup() {
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


	private class QueryUsers extends AsyncTask<Void, Void, Void> {

		// ProgressDialog to show while performing
		private ProgressDialog PD;

		// Constructor method
		QueryUsers() { }

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Showing progress dialog while accessing server
			PD = ProgressDialog.show(getActivity(), "Fetching Users", "Please wait...",
					false, false);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			ArrayList<String> users_names = new ArrayList<>();
			users_names.add("Select User");
			for (ParseUser user : users_list) {
				users_names.add(user.get("name").toString());
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<>(VIEW.getContext(),
					R.layout.custom_spinner_item, users_names);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			user.setAdapter(adapter);
			//Dismissing the progress dialog
			PD.dismiss();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ParseQuery<ParseUser> query = ParseUser.getQuery();
			try {
				users_list = query.find();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return null;
		}
	}


	private class ServerUpdate extends AsyncTask<Void, Void, Void> {

		private ParseUser current_user;
		private String name, pass;
		private int tier;

		// ProgressDialog to show while performing
		private ProgressDialog PD;

		// Constructor method
		ServerUpdate() {
			current_user = HomeActivity.CURRENT_USER;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			ParseUser.logOut();
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
			/*
			try {
				ParseUser.logIn(edit_user.getUsername(), edit_user.getUsername());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			ParseUser current = ParseUser.getCurrentUser();
*/
			return null;
		}
	}
}
