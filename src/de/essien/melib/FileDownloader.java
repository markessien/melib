package de.essien.melib;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;

public class FileDownloader extends AsyncTask<URL, Integer, Long> {
    
	public Context 			 mContext;
	public String 			 fileNameOverride;
	public PlistPage 		 callback;
	public ArrayList<String> params;
	public String 			 baseURL;
	public boolean			 saveFiles = true;
	public boolean 			 usePost = false;
	List<NameValuePair>		 nameValuePairs = new ArrayList<NameValuePair>();
	
	@Override
	protected Long doInBackground(URL... urls) {
    	
         int count = urls.length;
         if (params != null)
        	 count = params.size();
         
         for (int i = 0; i < count; i++) {
        	 URL url = urls[params == null ? i : 0];
        	 
        	 if (params != null) {
        		 try {
        			 url = new URL(baseURL + params.get(i));
        		 }
        		 catch(Exception e) {
        			 
        		 }
        	 }
        	 
        	 try {
        		
        		 
        		 if (usePost == true) {
        			 
        			    // Create a new HttpClient and Post Header
        			    HttpClient httpclient = new DefaultHttpClient();
        			    HttpPost httppost = new HttpPost(url.toString());

        			    try {
        			        // Add your data
        			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        			        // Execute HTTP Post Request
        			        ResponseHandler<String> responseHandler=new BasicResponseHandler();
        			        String responseBody = httpclient.execute(httppost, responseHandler);
        			        
        			        System.out.print(responseBody);
        			        
        			    } catch (ClientProtocolException e) {
        			        // TODO Auto-generated catch block
        			    } catch (IOException e) {
        			        // TODO Auto-generated catch block
        			    }
        			 
        		 
        		 /*
        			 c.setRequestMethod("POST");
        			 c.setDoInput(true);
        			 c.setDoOutput(true);
        			// c.connect();
        			 
        	            OutputStreamWriter wr = new OutputStreamWriter(c.getOutputStream());
        	            wr.write(this.postParams);
        	            wr.flush();
        	            
        	            /*
        	            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
        	            String line = null;
        	            String response = "";
        	            while ((line = rd.readLine()) != null) {
        	               response += line;
        	            }*/
        	            
        		 /*
        	             InputStream in = c.getInputStream();
	        			 byte[] buffer = new byte[1024];
	        			 in.read(buffer);
	        			 String s = new String(buffer);
	        			 System.out.print(s);
	        			 */
        		 }
        		 else {
        			 HttpURLConnection c = (HttpURLConnection) url.openConnection();
        			 c.setRequestMethod("GET");
        		 
        			 c.setDoOutput(true);
        			 c.connect();
        	 
        			 if (saveFiles == true) {
        			 
            		 String s = url.getFile();
            		 if (fileNameOverride != null) {
            			 s = fileNameOverride;
            		 }
            		 else {
            			 if (s.indexOf('=') != -1)
            				 s = s.substring(s.lastIndexOf('=') + 1);
            		 }
            		 
        			 FileOutputStream fos = mContext.openFileOutput(s, Context.MODE_PRIVATE);
        			 InputStream in = c.getInputStream();

        			 byte[] buffer = new byte[1024];
        			 int len1 = 0;
        			 while ( (len1 = in.read(buffer)) > 0 ) {
        				 fos.write(buffer,0, len1);
        			 }
        	    
        			 fos.close();
        			
        			 
        			 publishProgress(0);
        			 
        			 c.disconnect();
        		 }
        		 }
        		 
        	   
     		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
         
         if (fileNameOverride == null)
        	 fileNameOverride = "icons";
         
         return 0L;
     }
	
	@Override
	protected void onPostExecute (Long result) {
		// this will run on UI thread
		
		if (callback != null)
			callback.onPostExecute(fileNameOverride);
	}
	
	@Override
	protected void onProgressUpdate (Integer... values) {
		
		if (callback != null)
			callback.onDownloadProgress(fileNameOverride);
	}

 }