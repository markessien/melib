package de.essien.melib;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Share extends Activity {
	
	public String send_text;
	public String app_id;
	public String panel_id;
	public String file_name;
	public String item_text;
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);

        String lpanel_id = getIntent().getStringExtra("panel_id");
        if (lpanel_id != null)
        	this.panel_id = lpanel_id;
        
        String lapp_id = getIntent().getStringExtra("app_id");
        if (lapp_id != null)
        	this.app_id = lapp_id;
      
        String lsend_text = getIntent().getStringExtra("send_text");
        if (lsend_text != null)
        	this.send_text = lsend_text;

        String lfilename = getIntent().getStringExtra("file_name");
        if (lfilename != null)
        	this.file_name = lfilename;

        String litem_text = getIntent().getStringExtra("item_text");
        if (litem_text != null)
        	this.item_text = litem_text;
        
        final TextView sendLabel = (TextView)findViewById(R.id.send_text);
        sendLabel.setText("sent you " + this.send_text);
        
        Button btnSend = (Button)findViewById(R.id.btn_send_pic);
        btnSend.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
            	
            	EditText personsName = (EditText)findViewById(R.id.persons_name);
            	EditText personsEmail = (EditText)findViewById(R.id.person_email);
            	EditText yourName = (EditText)findViewById(R.id.your_name);
            	EditText yourEmail = (EditText)findViewById(R.id.your_email);
            	
            	/*
            	personsName.setText("Puff");
            	personsEmail.setText("puff@puffbirds.tv");
            	yourName.setText("Mark Android");
            	yourEmail.setText("markessien@gmail.com");
            	*/
            	
            	String persons_name = personsName.getText().toString();
            	if (persons_name.length() == 0) {
            		msg("Enter the persons name");
            		return;
            	} 
            	
            	String persons_email = personsEmail.getText().toString();
            	if (persons_email.length() == 0) {
            		msg("Enter the persons email address");
            		return;
            	}
            	
            	String your_name = yourName.getText().toString();
            	if (your_name.length() == 0) {
            		msg("Enter your name");
            		return;
            	}
            	  
            	String your_email = yourEmail.getText().toString();
            	if (your_email.length() == 0) {
            		msg("Enter your email address");
            		return;
            	}
            	
            	sendLabel.setText("Sending....");
            	sendData(persons_email, persons_name, your_email, your_name);
            	
            	sendLabel.requestFocus();
            	sendLabel.setText("Sent!");
            	msg("Sent email!");
             }
            
            public void msg(String msg) {
            	
              	Toast toast = Toast.makeText(Share.this, msg, Toast.LENGTH_LONG);
            	toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 20);
                toast.show();
            }
       });
        
        
    }
    
    
    public void sendData(String to_email, String to_name, String my_email, String my_name) {
    	// to=%@&file=%@&app_id=%@&text=%@&his_name=
    	// &my_name=&my_email=
    	
    	FileDownloader f = new FileDownloader();
    	
    	String shareURL = "http://control.puffbirds.tv/external/share.php?";
    
    	f.mContext = this;
    	f.saveFiles = false;
    	f.usePost = true;
    	
        f.nameValuePairs.add(new BasicNameValuePair("app_id", panel_id));
        f.nameValuePairs.add(new BasicNameValuePair("to", to_email));
        f.nameValuePairs.add(new BasicNameValuePair("my_email", my_email));
        f.nameValuePairs.add(new BasicNameValuePair("his_name", to_name));
        f.nameValuePairs.add(new BasicNameValuePair("my_name", my_name));
        f.nameValuePairs.add(new BasicNameValuePair("file", file_name + ".jpg"));
        
        try {
			f.nameValuePairs.add(new BasicNameValuePair("text",  URLDecoder.decode(item_text, "UTF8")));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	URL url = null;
    	try {
    		url = new URL(shareURL);
    	} catch (MalformedURLException e) {
    		e.printStackTrace();
    	}
	
    	f.execute(url);
    }
} 