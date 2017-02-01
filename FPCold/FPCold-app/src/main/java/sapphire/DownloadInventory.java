package sapphire;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.HomeActivity;
import com.parse.starter.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sapphire on 1/31/2017.
 */

public class DownloadInventory extends AsyncTask<Void, Void, Void> {

	// Environment variables
	private Context context;

	//ProgressDialog to show while sending email
	private ProgressDialog progressDialog;

	// Virtual Storage to use for download
	private ArrayList<Product> virtual_storage = new ArrayList<>();

	public DownloadInventory(Context context) { this.context = context; }

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//Showing progress dialog while sending email
		progressDialog = ProgressDialog.show(context,"Retrieving Virutal Storage",
				"Please wait...", false, false);
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		//Dismissing the progress dialog
		if (progressDialog.isShowing())
			progressDialog.dismiss();

		//Showing a success message
		Toast.makeText(context, "Virtual storage is ready to use", Toast.LENGTH_LONG).show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		ParseQuery<ParseObject> query = new ParseQuery<>("Product");
		query.addAscendingOrder("dateIn");
		query.addAscendingOrder("location");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject object : objects) {
						String id = object.getObjectId();
						String tag = object.getString("tag");
						String loc = object.getString("location");
						String date = object.getString("dateIn");

						Product p = new Product(id, tag, loc, date);
						virtual_storage.add(p);
					}
					HomeActivity.V_STORAGE = virtual_storage;
					Log.i("TRASH", "Contents of V_STORAGE");
					for (int i = 0; i < HomeActivity.V_STORAGE.size(); i++) {
						Log.i("TRASH", HomeActivity.V_STORAGE.get(i).toString());
					}
				}
			}
		});

		return null;
	}

	public ArrayList<Product> getVirtualStorage() {
		return virtual_storage;
	}
}
