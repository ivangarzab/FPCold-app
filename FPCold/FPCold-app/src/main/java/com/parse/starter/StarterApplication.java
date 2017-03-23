/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.Application;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

import sapphire.Product;


public class StarterApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

    	// Enable Local Datastore.
    	Parse.enableLocalDatastore(this);

    	// Add your initialization code here
    	Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
						.applicationId("oXBUzU2b8IcKRVx5r2GS")
						.clientKey(null)
						.server("http://fpcold-app.herokuapp.com/parse/")
						.build()
		);

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
    	// Optionally enable public read access.
    	// defaultACL.setPublicReadAccess(true);
    	// ParseACL.setDefaultACL(defaultACL, true);
/*
		int counter = 0;
		int location = 112401;
		for (int i = 14494; i<14506; i++) {
			Log.i("TRASH", String.valueOf(i) + " " + String.valueOf(location));
			counter++;

			ParseObject product = new ParseObject("Product");
			product.put("tag", String.valueOf(i));
			product.put("dateIn", "02-09-17");
			product.put("location", String.valueOf(location));
			product.saveInBackground();

			if (counter == 3 && location ==112401) {
				location++;
				counter = 0;
			}
			else if (counter %4 == 0)
				location++;
			//if (counter ==3)
			//	location = 113904;
		}
		Log.i("TRASH", String.valueOf("COUNTER " +counter));
*/
/*
		ParseQuery<ParseObject> query = new ParseQuery<>("Product");
		query.setLimit(3000);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject obj : objects) {
						String str = "1000" + obj.getString("tag");
						obj.put("tag", str);
						obj.saveInBackground();
					}
				}
			}
		});
	*/
	}
}
