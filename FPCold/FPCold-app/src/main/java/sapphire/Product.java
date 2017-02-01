package sapphire;

import android.util.Log;

/**
 * Created by Sapphire on 1/31/2017.
 */

public class Product {

	private String objectID;
	private String tag;
	private String location;
	private String date;

	public Product() { }

	public Product(String id, String tag, String loc, String date) {
		this.objectID = id;
		this.tag = tag;
		this.location = loc;
		this.date = date;
	}

	public Product (String tag, String loc, String date) {
		this.tag = tag;
		this.location = loc;
		this.date = date;
		this.objectID = null;
	}

	public void setObjectID(String id) {
		this.objectID = id;
	}

	public String getObjectID() {
		if (objectID == null) {
			//Log.i("TRASH", "PRODUCT: Object is only stored on local storage");
			return "";
		}
		else
			return this.objectID;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return this.tag;
	}

	public void setLocation(String loc) {
		this.location = loc;
	}

	public String getLocation() {
		return this.location;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return this.date;
	}

	@Override
	public String toString() {
		String s = "{" +this.objectID +", " +this.tag +", "
				+this.location +", " +this.date +"}";

		return s;
	}
}
