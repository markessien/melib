package de.essien.melib;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.res.AssetManager;
import android.graphics.Rect;
import android.util.Log;

public class PlistInformation extends DefaultHandler {

	private static final String TAG = "fmLibPlistInformation";
	private boolean in_key = false;
	private boolean in_string=false;
	private int num_tag=0;
	private int flag_File=0;
	private int flag_Title=0;
	private int flag_bg_img = 0;
	private int flag_icon = 0;
	private int flag_action = 0;
	
	private int flag_top = 0;
	private int flag_left = 0;
	private int flag_width = 0;
	private int flag_height = 0;
	private boolean flag_in_frontsidetext = false;
	private int flag_text = 0;
	private int flag_fontsize = 0;
	private int flag_fontcolor = 0;
	private int flag_align = 0;
	private int flag_url = 0;
	private int flag_update_url = 0;
	private int flag_in_item = 0;
	private int flag_type = 0;
	
	public int device_width = 0;
	public int device_height = 0;
	
	public ArrayList<Item> items;
	
	private Item it = new Item();
	private ItemFrontsideText ft = new ItemFrontsideText();
	
	boolean isNewElement = false;
	String bg_image;
	public Rect rc_main_table;
	private int rc_main_table_control = 0;
	String updateURL;
	String tableTextColor = new String();
	
	@Override
	public void startDocument() throws SAXException {
		bg_image = "";
		items = new ArrayList<Item>();
		rc_main_table = new Rect();
	}
	  
	@Override
	public void endDocument() throws SAXException {
				
		if (rc_main_table.right == 0) {
			rc_main_table.right = device_width;
			rc_main_table.bottom = device_height;
		}
	}
	
	@Override
	public void startElement(String namespaceURI, String localName,
							 String qName, Attributes atts) throws SAXException {
	
		Log.v(TAG, localName);
		num_tag++;
	
		if (localName.equals("key")) {
			this.in_key = true;
		}
		
		if (localName.equals("string")) {
			this.in_string = true;
		}
		
		if (localName.equals("dict")) {
			
			if (flag_in_frontsidetext == false)
				it=new Item();
			else
				ft = new ItemFrontsideText();
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName) 
	   throws SAXException {
		
		if (localName.equals("key")) {
			this.in_key = false;
		}
		
		if (localName.equals("string")) {
			this.in_string = false;
		}
		
		if (localName.equals("array")) {
			
			if (this.flag_in_frontsidetext == false) {
				this.flag_in_item = 0;
				it = new Item();
			}
			
			this.flag_in_frontsidetext = false;
		}
		

		if (localName.equals("dict")) {
			if (this.flag_in_item == 1 && this.flag_in_frontsidetext == false) {
				items.add(it);	
			}
		}
	}
	
	void receiveKey(String value) {
		 if(value.equals("File")) {
				flag_File=num_tag;
		 }
		 else if (value.equals("ItemsInCategory")) {
			 // if (it.Title != null)
			 //	 items.add(it);	
			 flag_in_item = 1;
		 }
		 else if (value.equals("Type")) {
			 flag_type = num_tag;
			 // items.add(it);
		 }
		 else if (value.equals("ItemsInTable")) {
			 
		 }
		 else if(value.equals("UpdateURL")) {
			 flag_update_url=num_tag;
		 }
		 else if(value.equals("Title")){			
			flag_Title=num_tag;  
		 }
		 else if(value.equals("Icon")){			
				flag_icon=num_tag;  
		 }
		 else if (value.equals("Background")) {
			 flag_bg_img = num_tag;
		 }
		 else if (value.equals("Left")) {
			 flag_left = num_tag;
		 }
		 else if (value.equals("Top")) {
			 flag_top = num_tag;
		 }
		 else if (value.equals("Width")) {
			 flag_width = num_tag;
		 }
		 else if (value.equals("Height")) {
			 flag_height = num_tag;
		 }
		 else if (value.equals("Action")) {
			 flag_action = num_tag;
		 }
		 else if (value.equals("FrontsideText")) {
			 flag_in_frontsidetext = true;
		 }
		 else if (value.equals("Text")) {
			 flag_text = num_tag;
		 }
		 else if (value.equals("FontSize")) {
			 flag_fontsize = num_tag;
		 }
		 else if (value.equals("TextColor")) {
			 flag_fontcolor = num_tag;
		 }
		 else if (value.equals("Align")) {
			 flag_align = num_tag;
		 }
		 else if (value.equals("URL")) {
			 flag_url = num_tag;
		 }
	}
	
	void receiveString(String value) {
		 
		if (num_tag==flag_update_url+1) {
			updateURL = value;
		}
		if (num_tag==flag_fontsize+1) {			
			 if (flag_in_frontsidetext == true) {
				 ft.FontSize = Integer.parseInt(value);	
			 }
		 }
		if (num_tag==flag_fontcolor+1) {			
			 if (flag_in_frontsidetext == true) {
				 ft.TextColor = value;	
			 }
			 else if (flag_in_item == 0) {
				 tableTextColor = value;
			 }
			 else
				 it.TextColor = new String(value);
		 }
		if (num_tag==flag_align+1) {			
			 if (flag_in_frontsidetext == true) {
				 ft.Align = value;	
			 }
		 }
		if (num_tag==flag_url+1) {			
			it.URL = value;	
		 }
		if (num_tag==flag_File+1) {			
			it.File = value;
		 }
		else if(num_tag==flag_Title+1){
     		it.Title = value;			
		 }
		else if(num_tag==flag_action+1){
     		it.Action = value;			
		 }
		else if(num_tag==flag_icon+1){
     		it.Icon = value;			
		 }
		else if(num_tag==flag_type+1) {
			
			if (value.equals("AppListing"))
				items.add(it);
			
			if (value.equals("Actions"))
				items.add(it);
		}
		else if(num_tag==flag_bg_img+1){
	    	 bg_image = value.toLowerCase();			
		 }
		else if(num_tag==flag_text+1){
			 if (flag_in_frontsidetext == true) {
				 ft.Text = value;
				it.frontsidetext.add(ft);
			 }		
		 }
		else if (num_tag == flag_left+1) {
				 
			 if (flag_in_frontsidetext == true) {
				 ft.Left = Integer.parseInt(value);	
			 }
			 else if (rc_main_table_control < 4) {
				 rc_main_table.left = Integer.parseInt(value);	
				 rc_main_table_control++;
			 }
		}
		 else if (num_tag == flag_top+1) {
			 
			 if (flag_in_frontsidetext == true) {
				 ft.Top = Integer.parseInt(value);	
			 }
			 else if (rc_main_table_control < 4) {
				 rc_main_table.top = Integer.parseInt(value);
				 rc_main_table_control++;
			 }
		 }
		 else if (num_tag == flag_width+1) {
			 
			 if (flag_in_frontsidetext == true) {
				 ft.Width = Integer.parseInt(value);	
			 }
			 else if (rc_main_table_control < 4) {
				 rc_main_table.right = Integer.parseInt(value) + rc_main_table.left;
				 rc_main_table_control++;
			 }
		 }
		 else if (num_tag == flag_height+1) {
			 
			 if (flag_in_frontsidetext == true) {
				 ft.Height = Integer.parseInt(value);	
			 }
			 else if (rc_main_table_control < 4) {
				 rc_main_table.bottom = Integer.parseInt(value) + rc_main_table.top;
				 rc_main_table_control++;
			 }
			 else {
				 it.Height = value;
			 }
		 }
	}
	
	@Override
	public void characters(char ch[], int start, int length) {	
		String value = new String(ch, start, length);
		if (this.in_key){
			receiveKey(value);
		}
		
		if (this.in_string) {
			 receiveString(value);
		}
    }
	
	public String getItemText(int pos, AssetManager assetManager) {
		
		Item it = items.get(pos);
		
		/*
		InputStream inputStream = null;
		
		
		try {
			inputStream = assetManager.open(it.File + "_back.html");
		} catch (IOException e) {
			return "";
		}
*/
		String s = readTextFile(it.File + "_back.html", assetManager);
		s = s.replace("</p>", "<br>");
		
		s = s.replace("&#8217;", "'");
		s = s.replace("&#8220;", "\"");
		s = s.replace("&#8221;", "\"");
		
		s = s.replace("#8217;", "'");		
		s = s.replace("#8220;", "\"");
		s = s.replace("#8221;", "\"");
		s = s.replace("&amp;", "&");
		
		String[] paragraphs = s.split("<p>");
		
		String finalText = new String();
		for (int i=1;i<paragraphs.length;i++) {
			finalText += paragraphs[i];
		}
		
		// &#8221 &#8220
		String query;
		try {
			query = URLEncoder.encode(finalText, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
		
		return query;
	}
	
	private String readTextFile(String fileName, AssetManager assetManager) {
		
		InputStream inputStream = null;
		
		
		try {
			inputStream = assetManager.open(fileName);
		} catch (IOException e) {
			return "";
		}
		
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(inputStream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(reader); 
		
		
		String str;
		String filetext = "";
		 try {
			while ((str = br.readLine()) != null) {
				 filetext +=str + "\r\n";
			    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		//The line below throws an IOException!!
		return filetext;
	}
	
	/*
	private String readTextFile(InputStream inputStream) {
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
		}
		return outputStream.toString();
	}
	*/
	int findItem(String itemName) 
	{
		for (int i=0;i<items.size();i++) {
			String file = items.get(i).File;
			if (file.equals(itemName)) {
				return i;
			}
		}
		
		return -1;
	}
}


 
