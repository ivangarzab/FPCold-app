package sapphire;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.HomeActivity;
import com.parse.starter.InOutActivity;
import com.parse.starter.MainActivity;
import com.parse.starter.TransferActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sapphire on 1/31/2017.
 */

public class UploadInventory extends AsyncTask<Void, Void, Void> {

	// Environment variables
	private Context context;

	// ProgressDialog to show while sending email
	private ProgressDialog progressDialog;

	// Local Storage variable
	private ArrayList<Product> LS = HomeActivity.V_STORAGE;

	public UploadInventory(Context context) { this.context = context; }

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//Showing progress dialog while sending email
		progressDialog = ProgressDialog.show(context,"Synchronizing virtual storage",
				"Please wait...", false, false);
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		//Dismissing the progress dialog
		if (progressDialog.isShowing())
			progressDialog.dismiss();

		//Showing a success message
		Toast.makeText(context, "Virtual storage has been updated", Toast.LENGTH_LONG).show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		// IN
		if (MainActivity.TAKE_IN.size() > 0) {
			for (final Product i : MainActivity.TAKE_IN) {
				ParseObject product = new ParseObject("Product");
				product.put("tag", i.getTag());
				product.put("location", i.getLocation());
				product.put("dateIn", i.getDate());
				product.saveInBackground();

				MainActivity.TAKE_IN = new ArrayList<>();
			}
		}

		// OUT
		//// if Product doesn't have an ObjectID, do something!
		if (MainActivity.TAKE_OUT.size() > 0) {
			for (final Product o : MainActivity.TAKE_OUT) {
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
				query.getInBackground(o.getObjectID(), new GetCallback<ParseObject>() {
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							object.deleteInBackground();
						}
					}
				});
				MainActivity.TAKE_OUT = new ArrayList<>();
			}
		}

		// TRANSFER
		if (MainActivity.TAKE_TRANSFER.size() > 0) {
			for (final Product t : MainActivity.TAKE_TRANSFER) {
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
				query.getInBackground(t.getObjectID(), new GetCallback<ParseObject>() {
					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							if (!(object.getString("location").equals(t.getLocation()))) {
								object.put("location", t.getLocation());
								object.saveInBackground();

							}
						}
					}
				});
				MainActivity.TAKE_TRANSFER = new ArrayList<>();
			}
		}

		return null;
	}

	/*
	@Override
	protected Void doInBackground(Void... params) {
		// Cloud Storage
		final ArrayList<Product> CS = new ArrayList<>();
		// Lists to divide the Local Storage(LS) into two
		final ArrayList<Product> id_list = new ArrayList<>();
		final ArrayList<Product> tag_list = new ArrayList<>();

		ParseQuery<ParseObject> query = new ParseQuery<>("Product");
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
						CS.add(p);
					}
					ArrayList<Product> removables = new ArrayList<>();
					for (Product p : CS) {
						for (Product l : id_list) {
							if (l.getTag().equals(p.getTag())) {
								removables.add(p);
							}
						}
					}
					for (Product r : removables) {
						CS.remove(r);
					}
				}
				updateOUTs(CS);
			}
		});

		for (Product p : LS) {
			if (!(p.getObjectID().equals("")))
				id_list.add(p);
			else tag_list.add(p);
		}

		updateTransfers(id_list);
		updateINs(tag_list);

		return null;
	}	*/

	private void updateINs(ArrayList<Product> prod) {
		for (final Product p : prod) {
			ParseObject product = new ParseObject("Product");
			product.put("tag", p.getTag());
			product.put("location", p.getLocation());
			product.put("dateIn", p.getDate());
			product.saveEventually();
		}
		//updateProducts(prod);
	}

	private void updateOUTs(ArrayList<Product> prod) {
		for (final Product p : prod) {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
			query.getInBackground(p.getObjectID(), new GetCallback<ParseObject>() {
				public void done(ParseObject object, ParseException e) {
					if (e == null) {
						object.deleteEventually();
					}
				}
			});
		}
	}

	private void updateTransfers(ArrayList<Product> prod) {
		for (final Product p : prod) {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
			query.getInBackground(p.getObjectID(), new GetCallback<ParseObject>() {
				public void done(ParseObject object, ParseException e) {
					if (e == null) {
						if (!(object.getString("location").equals(p.getLocation()))) {
							object.put("location", p.getLocation());
							object.saveInBackground();

						}
					}
				}
			});
		}
	}

	private void updateProducts(final ArrayList<Product> prod) {
		ParseQuery<ParseObject> query = new ParseQuery<>("Product");
		query.addAscendingOrder("dateIn");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject obj : objects) {
						String tag = obj.getString("tag");
						if (prod.contains(tag)) {
							HomeActivity.V_STORAGE.get(prod.indexOf(tag)).setObjectID(obj.getObjectId());
						}
					}
				}
			}
		});
	}
}
