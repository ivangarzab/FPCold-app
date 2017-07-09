package com.parse.starter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Created by Sapphire on 6/12/2017.
 */

public class SettingsAnnouncement extends Fragment {

	public EditText announcement;
	private Button but;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_settings_announcement, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		announcement = (EditText)view.findViewById(R.id.announcementEditText);
		but = (Button)view.findViewById(R.id.announcementButton);
		but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ServerAccess access = new ServerAccess();
				access.execute();
			}
		});
	}



	public class ServerAccess extends AsyncTask<Void, Void, Void> {

		String msg;
		// ProgressDialog to show while performing
		private ProgressDialog PD;

		// Constructor method
		public ServerAccess() {
			this.msg = announcement.getText().toString();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			announcement.setText("");
			//Showing progress dialog while accessing server
			PD = ProgressDialog.show(getActivity(), "Accessing Server", "Please wait...",
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
			ParseObject announce = new ParseObject("Announcement");
			announce.put("announcement", msg);
			try {
				announce.save();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return null;
		}
	}
}
