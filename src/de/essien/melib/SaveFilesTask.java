package de.essien.melib;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

/**
 * this class performs all the work, shows dialog before the work and dismiss it after
 */
public class SaveFilesTask extends AsyncTask<String, Void, Boolean> {

    public SaveFilesTask(Activity activity) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
    }

    /** progress dialog to show user that the backup is processing. */
    private ProgressDialog dialog;
    public int position = 0;
    public int error_msg = 0;
    
    /** application context. */
    private Activity activity;

    protected void onPreExecute() {
    	dialog = ProgressDialog.show(this.activity, "", "Saving to photo library...", true);
    }

        @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        
        if (error_msg == 1)
        	Toast.makeText(this.activity, "SD-Card is not available", Toast.LENGTH_SHORT).show();
        
        this.activity.getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
    }

    protected Boolean doInBackground(final String... args) {
       
    	try {
    		
    		View v1 = this.activity.getWindow().getDecorView().findViewById(android.R.id.content);
    	
    		v1.setDrawingCacheEnabled(true);
    		v1.buildDrawingCache(true);
    		Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
    		v1.setDrawingCacheEnabled(false); // clear drawing cache
        
    	
    		String state = Environment.getExternalStorageState();
    		if (Environment.MEDIA_MOUNTED.equals(state)) {

	        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        	error_msg = 1;
	        } else {
	        	error_msg = 1;
	        }

    		File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/puffbirds/" );
    		// have the object build the directory structure, if needed.
    		wallpaperDirectory.mkdirs();
    	
    		// image naming and path  to include sd card  appending name you choose for file
    		String mPath = Environment.getExternalStorageDirectory().toString() + "/puffbirds/item" + (position+1) + ".png";   

    		OutputStream fout = null;
    		File imageFile = new File(mPath);
        
	        try {
	            fout = new FileOutputStream(imageFile);
	            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout);
	            fout.flush();
	            fout.close();
	
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    	
	    
	   }
	   catch (Exception e) {
		   
		   e.printStackTrace();
		   error_msg = 1;
	   }
    	
    	return true;
    }
}
