package sapphire;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Sapphire on 5/10/2017.
 */

public class AccessServer extends AsyncTask<Void, Void, Void> {

	// Declaring variables
	private Context context;

	// Functional variables
	private char type;

	// ProgressDialog to show while performing
	private ProgressDialog PD;

	public AccessServer (Context context, char t) {
		this.context = context;
		this.type = t;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//Showing progress dialog while sending email
		PD = ProgressDialog.show(context, "Access Server", "Please wait...", false, false);
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		//Dismissing the progress dialog
		PD.dismiss();
/////////////////////////////////////////////////////////////////////////
		// What to do once the process is done?
	}

	@Override
	protected Void doInBackground(Void... params) {
		switch (type) {
			case 'l':

				break;
			default:

		}


		return null;
	}
}
