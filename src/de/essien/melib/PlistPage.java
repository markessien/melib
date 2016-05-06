package de.essien.melib;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Display;
import android.view.WindowManager;
import java.util.ArrayList;

public class PlistPage extends Activity {

	 
	 public class PlistItemAdapter extends ArrayAdapter<Item> {
		 	
		 	public ArrayList<Item> items;
		 
			public PlistItemAdapter(Context context, int textViewResourceId, ArrayList<Item> items) {
					super(context, textViewResourceId, items);
					this.items = items;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				 
					LayoutInflater inflater=getLayoutInflater();
					
					Item item = items.get(position);
					
					boolean isHeader = false;
					
					if (item.File == null && item.Title != null)
						isHeader = true;
					
					if (item.Action != null) {
						
						if (item.Action.equals("OpenURL"))
							isHeader = false;
						
						if (item.Action.equals("SaveToPhotos"))
							isHeader = false;
						
						if (item.Action.equals("AddToFavorites")) {
							isHeader = false;
							item.Icon = "favorites_add2.png";
						}
							
					}
					
					if (isHeader == true) {
						View row=inflater.inflate(R.layout.list_item_header, parent, false);
						
						// Set text
						
						TextView label=(TextView)row.findViewById(R.id.list_header_title);
						label.setText(item.Title);
						return row;
					}
					else {
						View row=inflater.inflate(R.layout.list_item, parent, false);
						
						// Set text
						
					
						TextView label=(TextView)row.findViewById(R.id.weekofday);
						label.setText(item.Title);
						
						String textColr = item.TextColor;
						try {
							if (textColr == null) {
								
								if (dataHandler.tableTextColor != null)
									label.setTextColor(Color.parseColor(dataHandler.tableTextColor));
								else
									label.setTextColor(Color.WHITE);
							}
							else
								label.setTextColor(Color.parseColor(textColr));
						}
						catch(Exception e) {
							System.out.print("Exception when setting color");
							label.setTextColor(Color.WHITE);
						}
						
						// Set icon
						ImageView iconView =(ImageView)row.findViewById(R.id.icon);
						try {
							boolean loadedIcon = false;
							
							try {
			    				FileInputStream isr = openFileInput(item.Icon);
			    				if (isr != null) {				  
			    					Bitmap bMap = BitmapFactory.decodeStream(isr);
			    					if (bMap != null) {
			    						iconView.setImageBitmap(bMap);
			    						loadedIcon = true;
			    					}
			    				}
							}
							catch (Exception e) {
							
							}
							
		    				if (loadedIcon == false) {
		    					int resid = getResources().getIdentifier(PlistPage.stripFileExtension(item.Icon), "drawable", app_id);
		    					iconView.setImageResource(resid);
		    				}
						}
						catch (Exception ex) {
							iconView.setImageResource(0);
						}
						
						return row;
					}
			}
		}
	 
	PlistInformation dataHandler;
    public String app_id;
    public String ad_id;
    public int position;
    public String old_title;
    public boolean requestedUpdate;
    public String panel_id;
    private boolean iconsRefreshed;
    public String send_text;
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

      if (item.getItemId() == R.id.delete) {
    	  deleteItem(info.position);
        return true;
      }
      
        return super.onContextItemSelected(item);
    }
    
    public void deleteItem(int pos) {
    	
    	dataHandler.items.remove(pos);
    
    	String favData = "";
    	for (int i =0;i<dataHandler.items.size();i++) {
    		Item it = dataHandler.items.get(i);
         	
        	String s = "//" + it.Title + "//" +it.Icon + "//" + it.File + ";;;;";
        	favData += s;
    	}


    	try {
    		FileOutputStream fos = openFileOutput("favorites.txt", Context.MODE_PRIVATE);
    		fos.write(favData.getBytes());
    		fos.close();
    	} catch(Exception e) {
    		
    		
    	}
    	
    	ListView lv_main = (ListView) findViewById(R.id.plist_list);  
    	@SuppressWarnings("unchecked")
		ArrayAdapter<Item> l = (ArrayAdapter<Item>)lv_main.getAdapter();
    	l.notifyDataSetChanged();
    	
    }
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.favorites_menu, menu);
    }
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plistpage);  
      
        ListView lv_main = (ListView) findViewById(R.id.plist_list);        
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
        	
        	   public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {  
        	          listItemSelected(childView, position);
        	   }
   
        });  
        
        requestedUpdate = false;
        registerForContextMenu(lv_main);
        
        String lapp_id = getIntent().getStringExtra("app_id");
        if (lapp_id != null)
        	this.app_id = lapp_id;
        
        String lpanel_id = getIntent().getStringExtra("panel_id");
        if (lpanel_id != null)
        	this.panel_id = lpanel_id;

        String lad_id = getIntent().getStringExtra("ad_id");
        if (lad_id != null)
        	this.ad_id = lad_id;
        
        String lsend_text = getIntent().getStringExtra("send_text");
        if (lsend_text != null)
        	this.send_text = lsend_text;
        
        String fileName = getIntent().getStringExtra("File");
        if (fileName != null) {
        	// then this plist is a subplist and is being opened by an existing plist
        	// so load the plist it's supposed to be
        	
        	Resources myResources = getResources();
        	int resid = myResources.getIdentifier(fileName, "raw", this.app_id);
        	Load(resid);
        }
        
	}

	public void listItemSelected(View v, int index) {
		
		Item it = dataHandler.items.get(index);
		if (it.Action == null) return;
		
		if (it.Action.equals("AddToFavorites")) {
	        String lposition = getIntent().getStringExtra("cur_index");
	        if (lposition != null) {
	        	this.position = Integer.parseInt(lposition);
	        	
	        	old_title = it.Title;
	        	
	        	it.Title = "Adding...";
	        	ListView lv_main = (ListView) findViewById(R.id.plist_list);  
	        	@SuppressWarnings("unchecked")
				ArrayAdapter<Item> l = (ArrayAdapter<Item>)lv_main.getAdapter();
	        	l.notifyDataSetChanged();
	        	
	        	String title = getIntent().getStringExtra("Title");
	        	String icon = getIntent().getStringExtra("Icon");
	        	
	        	String s = "//" + title + "//" + icon + "//" +  getIntent().getStringExtra("ItemFile") + ";;;;";

	        	try {
	        		FileOutputStream fos = openFileOutput("favorites.txt", Context.MODE_APPEND);
	        		fos.write(s.getBytes());
	        		fos.close();
	        	} catch(Exception e) {
	        		
	        		
	        	}
	        	
	        	it.Title = old_title;
	        	l.notifyDataSetChanged();
	        	
	        	Toast.makeText(this, "Item has been added to favorites!", Toast.LENGTH_SHORT).show();
	        }
		}
		if (it.Action.equals("SaveToPhotos")) {
			setResult(RESULT_FIRST_USER + 10);
			finishActivity(LRView.MORE_PAGE_REQUEST);
			finish();
		}
		if (it.Action.equals("OpenPlistPage")) {
			Intent myIntent = new Intent(v.getContext(), PlistPage.class);
			myIntent.putExtra("File", it.File);
			myIntent.putExtra("app_id", app_id);
			myIntent.putExtra("ad_id", ad_id);
			myIntent.putExtra("panel_id", panel_id);
			myIntent.putExtra("send_text", send_text);
			startActivityForResult(myIntent, 0);
		}
		else if (it.Action.equals("OpenFlipperPage")) {
			Intent myIntent = new Intent(v.getContext(), LRView.class);
			myIntent.putExtra("File", it.File);
			myIntent.putExtra("app_id", app_id);
			myIntent.putExtra("ad_id", ad_id);
			myIntent.putExtra("panel_id", panel_id);
			myIntent.putExtra("send_text", send_text);
			myIntent.putExtra("index", new Integer(index).toString());
			
			startActivityForResult(myIntent, 0);
		}
		else if (it.Action.equals("OpenFlipperPageFromFav")) {
			Intent myIntent = new Intent(v.getContext(), LRView.class);
			myIntent.putExtra("File", it.File);
			myIntent.putExtra("app_id", app_id);
			myIntent.putExtra("ad_id", ad_id);
			myIntent.putExtra("panel_id", panel_id);
			myIntent.putExtra("send_text", send_text);
			myIntent.putExtra("ItemFile", it.File);
			
			
			// Set the index to -1 so the file will be
			/// used to search for it
			myIntent.putExtra("index", new Integer(-1).toString());
			
			startActivityForResult(myIntent, 0);
		}
		if (it.Action.equals("OpenURL")) {
			
			try {
				Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(it.URL));
				startActivity(browserIntent);
			}
			catch (Exception e) {
				msg("Could not open the URL. Please search for it in the store");
			}
		}
	}
	
    public void msg(String msg) {
    	
      	Toast toast = Toast.makeText(PlistPage.this, msg, Toast.LENGTH_LONG);
    	toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 20);
        toast.show();
    }
    
	public void Load(Integer plistFile) {
		
		if (plistFile == 0)
			return;
		
		    	
		Resources myResources = getResources();
		
        try {
        	SAXParserFactory spf = SAXParserFactory.newInstance();
        	SAXParser sp = spf.newSAXParser();
        
        	XMLReader xr = sp.getXMLReader();
        	
        	dataHandler = new PlistInformation();
        	xr.setContentHandler(dataHandler);
        	
    		// The bottom and right items are PADDING!
    		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    		int width = display.getWidth(); 
    		int height = display.getHeight(); 
    		dataHandler.device_width = width;
    		dataHandler.device_height = height;
    		
        	boolean loaded = false;
    		if (plistFile == R.raw.more) {
    			
    			try {
    				FileInputStream isr = openFileInput("more.plist");
    				if (isr != null) {
    			    				  
    					
    					xr.parse(new InputSource(isr));
    					loaded = true;
    			
    				}
    			}
		        catch (Exception e) {
		        	System.out.println("Error: " + e.getMessage());
		        }
    		}
    		
    		if (loaded == false) {
    			InputStream istream = null;
    			istream = (InputStream)myResources.openRawResource(plistFile);
        	
    			xr.parse(new InputSource(istream));
    		}

        }
        catch (Exception e) {
        	System.out.println("Error: " + e.getMessage());
        }
        
        if (plistFile == R.raw.favorites) {
        	
        	try {
	        	// Special case, insert favorite items
	        	FileInputStream isr = openFileInput("favorites.txt");
	        	
	        	byte[] inputBuffer = new byte[1024 * 10];
	            // Fill the Buffer with data from the file
	            isr.read(inputBuffer);
	            // Transform the chars to a String
	            String readString = new String(inputBuffer);
	    		isr.close();
	    		
	    		String[] items = readString.split(";;;;");
	    		for(int i =0; i < items.length ; i++) {
	    			String subInfo = items[i];
	    			if (!subInfo.contains("//")) continue;
	    			
	    			String[] infos = subInfo.split("//");
	    			
	    			String title = "";
	    			String icon = "";
	    			String file = "";
	    			for(int k =0; k < infos.length ; k++) {
	    				if (k == 1)
	    					title = infos[k];
	    				if (k == 2)
	    					icon = infos[k];
	    				
	    				if (k == 3)
	    					file = infos[k];
	    					
	    			}
	    			
	    			Item it = new Item();
	    			it.File = file;
	    			it.Title = title;
	    			it.Icon = icon;
	    			it.Action = "OpenFlipperPageFromFav";
	    			dataHandler.items.add(it);
	    			
	    		}
        	}
        	catch (Exception e) {
        		
        	}
        }
        
        
        ImageView bg = (ImageView) findViewById(R.id.plist_background);
       
        String bg_img = stripFileExtension(dataHandler.bg_image);
        if (bg_img != null && bg_img.length() > 0) {
        	int resid = myResources.getIdentifier(bg_img, "drawable", this.app_id);
        
        	bg.setImageResource(resid);
        }
        else  {
        	// sint resid = myResources.getIdentifier("background2", "drawable", this.app_id);
        
        	bg.setImageResource(R.drawable.morepagebackground);
        }
        	
        
        // For the listview, we're not going to just use the set images becaose
        // of variation in screen size. Rather, we project it based off iphone size
        ListView lv_main = (ListView) findViewById(R.id.plist_list);        
  	    ArrayAdapter<Item> l = new PlistItemAdapter(this, R.layout.list_item, dataHandler.items);
  	    lv_main.setAdapter(l); 
        l.notifyDataSetChanged();
        
        Rect rcOriginal = dataHandler.rc_main_table;
        rcOriginal.top += 20;
        // rcOriginal.bottom += 20;
        Rect rc = convertRectToPaddingOffset(rcOriginal);
        lv_main.setPadding(rc.left, rc.top, rc.right, rc.bottom);  
        
        // If there is an update url we load
        if (dataHandler.updateURL != null && requestedUpdate == false) {
        	requestedUpdate = true; // we only allow this plist update once
        	
        	/*
        	String iconName = "Inkblot_57.png";
        	String app_id = "inkblot";
        	String baseUrl = "http://puffbirds.salespanelpro.com/external/update.php";
        	String params = "?app_id=" + app_id + "&file_name=" + iconName;
        		*/
        
        	String fileName = "";
        	String params = "";
        	if (plistFile == R.raw.more) {
        		fileName = "more.plist";
        		params = "?app_id=" + panel_id + "&file_name=more.plist&style=android";
        	}
        	
    
        	FileDownloader f = new FileDownloader();
        	f.mContext = this;
        	f.callback = this;
        	f.fileNameOverride = fileName;
        	
        	URL url = null;
        	try {
        		url = new URL(dataHandler.updateURL + params);
        	} catch (MalformedURLException e) {
        	}
		
        	f.execute(url);
        }
	}
	
	protected void onDownloadProgress(String fileName) {
		Load(R.raw.more);
	}
	
	protected void onPostExecute (String fileName) {
		if (fileName == null) return;
		
		if (fileName.equals("more.plist")) {
			Load(R.raw.more);
			
			if (iconsRefreshed == false) {
				// So we have loaded a new  plist from the server.
				// Let have a look if there are any new icons
				iconsRefreshed = true;
				
				FileDownloader f = new FileDownloader();
				f.mContext = this;
				f.callback = this;
				
				ArrayList<String> params = new ArrayList<String>();
				for (int i=0;i<dataHandler.items.size();i++) {
					Item it = dataHandler.items.get(i);
					
					String param = "?app_id=" + panel_id + "&file_name=" + it.Icon;
					params.add(param);
				}
				
				f.baseURL = dataHandler.updateURL;
				f.params = params;
				
	        	URL url = null;
	        	try {
	        		url = new URL(dataHandler.updateURL);
	        	} catch (MalformedURLException e) {
	        	}
	        	
	        	f.execute(url);
			}
		}
		else {
			// Here we loaded an icon.
			Load(R.raw.more);
		}
	}
	 
	public Rect convertRectToPaddingOffset(Rect rc) {
		
		// The bottom and right items are PADDING!
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth(); 
		int height = display.getHeight(); 
		
		double leftRatio = (double)rc.left /  (double)320;
		double rightRatio =  (double)rc.right /  (double)320;
		double topRatio =  (double)rc.top /  (double)480;
		double botRatio =  (double)rc.bottom /  (double)480;
		
		Rect rcProjected = new Rect();
		rcProjected.left = (int)(width * leftRatio);
		rcProjected.right = (int)(width - (width * rightRatio));
		rcProjected.top = (int)(height * topRatio);
		rcProjected.bottom = (int)(height - (height * botRatio));
		return rcProjected;
	}
	
	
	  public static String stripFileExtension(String fileName) {
	      int dotInd = fileName.lastIndexOf('.');

	      // if dot is in the first position,
	      // we are dealing with a hidden file rather than an extension
	      return (dotInd > 0) ? fileName.substring(0, dotInd) : fileName;
	  }
}