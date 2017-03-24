package sapphire;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.parse.starter.HomeActivity;
import com.parse.starter.R;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sapphire on 3/24/2017.
 */

public class Spreadsheet extends AsyncTask<Void, Void, Void> {

	// Environment variables
	private Context context;

	// ProgressDialog to show while sending email
	private ProgressDialog progressDialog;

	// Map object to be used within the class
	private Map<String, Object[]> INFO;

	/**
	 * Constructor
	 * @param context : context of calling Activity
	 * @param map : Map object to be localized
	 */
	public Spreadsheet(Context context, Map<String, Object[]> map) {
		this.context = context;
		this.INFO = map;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//Showing progress dialog while sending email
		progressDialog = ProgressDialog.show(context,"Preparing spreadsheet to be shared",
				"Please wait...", false, false);
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		//Dismissing the progress dialog
		if (progressDialog.isShowing())
			progressDialog.dismiss();

		//Showing a success message
		//Toast.makeText(context, "Virtual storage is ready to use", Toast.LENGTH_LONG).show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		//XXX: Using blank template file as a workaround to make it work
		//Original library contained something like 80K methods and I chopped it to 60k methods
		//so, some classes are missing, and some things not working properly
		InputStream stream = context.getResources().openRawResource(R.raw.template);

		try {
			XSSFWorkbook workbook = new XSSFWorkbook(stream);
			XSSFSheet sheet = workbook.getSheetAt(0);
			//XSSFWorkbook workbook = new XSSFWorkbook();
			//XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("mysheet"));

			//Create row object
			XSSFRow row;

			//Iterate over data and write to sheet
			Set<String> keyid = INFO.keySet();
			int rowid = 0;
			for (String key : keyid)
			{
				row = sheet.createRow(rowid++);
				Object [] objectArr = INFO.get(key);
				int cellid = 0;
				for (Object obj : objectArr)
				{
					Cell cell = row.createCell(cellid++);
					cell.setCellValue((String)obj);
				}
			}

			String outFileName = "inventory_" + HomeActivity.DATE +".xlsx";
			File cacheDir = context.getCacheDir();
			File outFile = new File(cacheDir, outFileName);

			OutputStream outputStream = new FileOutputStream(outFile.getAbsolutePath());
			workbook.write(outputStream);
			outputStream.flush();
			outputStream.close();

			share(outFileName, context.getApplicationContext());
			Log.i("TRASH", "Spraedsheet is ready");
		} catch (Exception e) {
			Log.i("TRASH", "Error while creating spreadsheet!");
			Log.i("TRASH", e.toString());
		}


		return null;
	}

	/**
	 *
	 * @param fileName :
	 * @param context
	 */
	public void share(String fileName, Context context) {
		Uri fileUri = Uri.parse("content://" + "sapphire" + "/"+fileName);
		Log.i("TRASH", "sending "+fileUri.toString()+" ...");
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
		shareIntent.setType("application/octet-stream");
		context.startActivity(Intent.createChooser(shareIntent, "Send to")
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
}
