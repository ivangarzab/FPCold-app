package com.parse.starter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
			ACTION = 'e';
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
			title.setText("Add User");
			confirm.setText("ADD");
		}
		else if (c == 'e') {
			ACTION = 'a';
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
			title.setText("Edit User");
			confirm.setText("EDIT");
		}
	}

	private void actionButton() {
		if (ACTION == 'a') {

		}
		else if (ACTION == 'e') {

		}
	}
}
