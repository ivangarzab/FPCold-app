package sapphire;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by Sapphire on 5/10/2017.
 * Transaction object that will hold the information of one Transaction
 * It also stores it on the server
 */

public class Transaction {

	public String AUTHOR;
	public String TRANSACTION_TYPE;
	public String DESCRIPTION;
	public String START_TIME;
	public String END_TIME;

	public Transaction(String author, String start, String type) {
		this.AUTHOR = author;
		this.START_TIME = start;
		this.TRANSACTION_TYPE = type;
	}

	public void setAuthor(String a) {
		this.AUTHOR = a;
	}

	public String getAuthor() {
		return AUTHOR;
	}

	public void setTransactionType(String tt) {
		this.TRANSACTION_TYPE = tt;
	}

	public String getTransactionType() {
		return TRANSACTION_TYPE;
	}

	public void setDescription(String desc) {
		this.DESCRIPTION = desc;
	}

	public String getDescription() {
		return DESCRIPTION;
	}

	public void setStartTime(String st) {
		this.START_TIME = st;
	}

	public String getStartTime() {
		return START_TIME;
	}

	public void setEndTime(String et) {
		this.END_TIME = et;
	}

	public String getEndTime() {
		return END_TIME;
	}

	public void sendTransaction() {
		ParseObject t = new ParseObject("Transaction");
		t.put("user", AUTHOR);
		t.put("type", TRANSACTION_TYPE);
		t.put("description", DESCRIPTION);
		t.put("startTime", START_TIME);
		t.put("endTime", END_TIME);
		t.saveInBackground();
	}
}
