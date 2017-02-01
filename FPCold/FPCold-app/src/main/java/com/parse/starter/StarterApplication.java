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

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


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
		for (int i = 000001; i<003001; i++) {
			ParseObject product = new ParseObject("Product");
			product.put("tag", String.valueOf(i));
			product.put("location", "012649");
			product.put("dateIn", "date");
			product.saveEventually();
		}
		*/
	}
}
