package de.essien.melib;


import java.io.InputStream;
import java.lang.reflect.Field;

// import com.google.ads.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import android.widget.TextView;
import android.util.TypedValue;

public class LRView extends Activity {
	   Button btnSent = null;  
       Button btnPrev = null;  
       Button btnRand = null;
       Button btnNext = null;
       Button btnMore = null;
	   public Integer resid;
	   public int randimg;
	   PlistInformation dataHandler;
	   public Integer[] mImageIds= null;
	   public int position; 
	   public ImageView i;
       private static final String TAG = "fmLibMainActivity";
       private Gallery gallery;
       public String app_id;
       public String panel_id;
       public String send_text;
       public String ad_id;
       public boolean showing_flip = false;
       
       static final int MORE_PAGE_REQUEST = 0;
       
       protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          
    	   if (requestCode == LRView.MORE_PAGE_REQUEST && resultCode == LRView.RESULT_FIRST_USER + 10) {
    		   
    		   SaveFilesTask task = new SaveFilesTask(this);
    		   task.position = position;
    		   task.execute();
    	   }
       }
       
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        
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
        
        btnSent = (Button)findViewById(R.id.btnSent);
     //   btnPrev = (Button)findViewById(R.id.btnPrev);
        btnRand = (Button)findViewById(R.id.btnRand);
    //    btnNext = (Button)findViewById(R.id.btnNext);
        btnMore = (Button)findViewById(R.id.btnMore);
        
        btnRand.setBackgroundResource(R.drawable.btn_flipper);

        Log.v(TAG, "Starting fmlib");
        
        try {
        	SAXParserFactory spf = SAXParserFactory.newInstance();
        	SAXParser sp = spf.newSAXParser();
        
        	XMLReader xr = sp.getXMLReader();
        	
        	dataHandler = new PlistInformation();
        	xr.setContentHandler(dataHandler);
        	
        	Resources myResources = getResources();
        	InputStream istream = null;
        	
        	@SuppressWarnings("rawtypes")
			Class res = R.raw.class;
            Field field = res.getField("main");
            int drawableId = field.getInt(null);
            
        	istream = (InputStream)myResources.openRawResource(drawableId);
        	// istream = (InputStream)myResources.openRawResource(R.raw.main);
        	
        	xr.parse(new InputSource(istream));
        }
        catch (Exception e) {
        	System.out.println("Error: " + e.getMessage());
        }
        
        gallery = (Gallery) findViewById(R.id.gallery2);
        final ImageAdapter img = new ImageAdapter(this, dataHandler, this.app_id);
        gallery.setAdapter(img);      
        
        String index = getIntent().getStringExtra("index");
        if (index != null) {
        	
        	int i = Integer.parseInt(index);
        	if (i != -1) {
        		gallery.setSelection(i, false);
        		position = i;
        	}
        	else {
        		// we have a file but not index. We gotta find it. Happens
        		// when clicked from favorites
        		String sFile = getIntent().getStringExtra("ItemFile");
        		i = dataHandler.findItem(sFile);
        		if (i >= 0) {
        			gallery.setSelection(i, false);
        			position = i;
        		}
        	}
        }
        
       
        // Create the adView
        AdView adView = new AdView(this, AdSize.BANNER, this.ad_id);
         
        // Lookup your LinearLayout assuming it’s been given
        // the attribute android:id="@+id/mainLayout"
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.lrLayout);
        // Add the adView to it
        layout.addView(adView);
        // Initiate a generic request to load it with an ad
        
        AdRequest request = new AdRequest();
        request.setTesting(true);
        adView.loadAd(request);        
   
        /*
        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {            	
            	Toast toast = Toast.makeText(fmLib.this, ""+ dataHandler.items.get(position).Title, Toast.LENGTH_LONG);
            	toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 20);
                toast.show();                
             } 
            });*/
        
        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

        	 //  @Override
        	   public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        		   position = pos;
        	  }

        	  // @Override
        	   public void onNothingSelected(AdapterView<?> parent) {
        	   }
       });
        
        btnSent.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), Share.class);
                myIntent.putExtra("send_text", send_text);
                myIntent.putExtra("app_id", app_id);
                myIntent.putExtra("panel_id", panel_id);
                
                int pos = position;
                myIntent.putExtra("file_name", dataHandler.items.get(pos).File);
                
                AssetManager assetManager = getAssets();
                String s = dataHandler.getItemText(pos, assetManager);
                myIntent.putExtra("item_text", s);
                
                myIntent.putExtra("cur_index", new Integer(position).toString());
                startActivityForResult(myIntent, 0);
             }
       });
        

        btnMore.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
    			Intent myIntent = new Intent(v.getContext(), PlistPage.class);
    			myIntent.putExtra("File", "more");
    			myIntent.putExtra("app_id", app_id);
    			myIntent.putExtra("panel_id", panel_id);
    			myIntent.putExtra("cur_index", new Integer(position).toString());
    			myIntent.putExtra("Title", dataHandler.items.get(position).Title);
    			myIntent.putExtra("Icon", dataHandler.items.get(position).Icon);
    			myIntent.putExtra("ItemFile", dataHandler.items.get(position).File);
    			startActivityForResult(myIntent, MORE_PAGE_REQUEST);
    			
             }
       });  
        
        /*
        btnPrev.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
					if (position > 0){
						position = position-1;
		 				gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, new KeyEvent(0, 0));
						//gallery.setSelection(position, false);
					} 
             }  
       });  
        
 
        btnNext.setOnClickListener(new OnClickListener(){
              public void onClick(View v) {
                    if (position < dataHandler.items.size() -1){
                    	position = position+1;
						gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, new KeyEvent(0, 0));
                     }
                }
          });
          
          */
        
        btnRand.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
            	ViewFlipper flip=(ViewFlipper)findViewById(R.id.flip);
            	
            	if (showing_flip == false) {
            		flip.showNext();
            		showing_flip = true;
            		
            		Item i = dataHandler.items.get(position);
            		String fileName = "file:///android_asset/" + i.File + "_back.html";
            		WebView mWebView = null;
            		mWebView = (WebView) findViewById(R.id.webview);
            		mWebView.getSettings().setJavaScriptEnabled(true);
            		mWebView.loadUrl(fileName); //this will be your file name
            		
            		btnRand.setBackgroundResource(R.drawable.btn_coverpage);
            	}
            	else {
            		btnRand.setBackgroundResource(R.drawable.btn_flipper);
            		flip.showPrevious();
            		showing_flip = false;
            	}

            	/*
            			int i = 0;
						Random rand = new Random();
						randimg = rand.nextInt(dataHandler.items.size());
						while (randimg == position) {
							randimg = rand.nextInt(dataHandler.items.size());
							if (i++ == 30)
								break;
						}
						
						gallery.setSelection(randimg, false);
						position = randimg;
						*/
						// i.setImageResource(mImageIds[randimg]);
             }
       });

	}

    public class ImageAdapter extends BaseAdapter {
    	private Context mContext;
    	int mGalleryItemBackground;
    	private String app_id;
    	
        public ImageAdapter(Context c, PlistInformation dataH, String app_id) 
        {       	
        	mContext = c;        	
        	dataHandler = dataH; 
        	Resources x = mContext.getResources();
        	mImageIds = new Integer[dataHandler.items.size()];
        	this.app_id = app_id;
        	
    		for (int i=0;i<dataHandler.items.size();i++) {
    			String fileName = dataHandler.items.get(i).File;
    			System.out.println("File=" + fileName);
    			int resid = x.getIdentifier(fileName, "drawable", this.app_id);
    			mImageIds[i] = resid;
    		}
    		
            TypedArray a = obtainStyledAttributes(R.styleable.HelloGallery);
            mGalleryItemBackground = a.getResourceId(
            R.styleable.HelloGallery_android_galleryItemBackground, 0);
            a.recycle();
        }

        public int getCount() 
        {        
			return mImageIds.length;
        }

        public Object getItem(int position) 
        {
            return position;
        }

        public long getItemId(int position) 
        {
            return position;
        }

        public void configFrontText(ItemFrontsideText tf, TextView label) {
  			
        	System.out.print("Label:" + label);
        	System.out.print("tf:" + tf);
        	
        	try {
			label.setText(tf.Text);
    		}
			catch (Exception ex) {
			
			}
        	
			Rect rc = new Rect();
			rc.left = tf.Left;
			rc.top = tf.Top;
			rc.right = tf.Left + tf.Width;
			rc.bottom = tf.Top + tf.Height;
			
			Rect rc2 = convertRectToPaddingOffset(rc);
			label.setPadding(rc2.left, rc2.top, rc2.right, rc2.bottom); 
			
			// 
			try {
			if (tf.FontSize != 0)
				label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tf.FontSize);
			}
			catch (Exception ex) {
			
			}
			
			try {
				int c = Color.parseColor(tf.TextColor);
				label.setTextColor(c);
			}
			catch (Exception ex) {
			
			}
			
			try {
				if (tf.Align != null) {
					// Gravity.CENTER_VERTICAL|
					 if (tf.Align.equals("center"))
						 label.setGravity(Gravity.CENTER_HORIZONTAL);
					 
				}	
			}
			catch (Exception ex) {
			
			}
        }
        
        public View getView(final int position, View convertView, ViewGroup parent) 
        {   

        	 
        	LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.gallery_item, parent, false);
        	
         	Item it = dataHandler.items.get(position);
        	
        	if (it.frontsidetext.size() > 0) {
        		ItemFrontsideText tf = it.frontsidetext.get(0);
        		tf.Height += 15;
        		tf.Top -= 15;
        		TextView label=(TextView)row.findViewById(R.id.gal_txt_1);
        		configFrontText(tf, label);
        	}
        	if (it.frontsidetext.size() > 1) {
        		ItemFrontsideText tf = it.frontsidetext.get(1);
        		// tf.Top = tf.Top + 20;
        		TextView label=(TextView)row.findViewById(R.id.gal_txt_2);
        		configFrontText(tf, label);
        	}
        	
			// Set icon
			ImageView i =(ImageView)row.findViewById(R.id.gal_img);
			
			Resources x = mContext.getResources();
			int resid = x.getIdentifier("flipperbackground", "drawable", this.app_id);
            i.setImageResource(resid);
           
            i.setBackgroundResource(mImageIds[position]);
            i.setScaleType(ImageView.ScaleType.FIT_XY);
			
			return row;
        } 
        
        
	
	
	public Rect convertRectToPaddingOffset(Rect rc) {
		
		// The bottom and right items are PADDING!
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth(); 
		int height = display.getHeight(); 
		
		double leftRatio = (double)rc.left /  (double)320;
		double rightRatio =  (double)rc.right /  (double)320;
		double topRatio =  (double)rc.top /  (double)460;
		double botRatio =  (double)rc.bottom /  (double)460;
		
		Rect rcProjected = new Rect();
		rcProjected.left = (int)(width * leftRatio);
		rcProjected.right = (int)(width - (width * rightRatio));
		rcProjected.top = (int)(height * topRatio);
		rcProjected.bottom = (int)(height - (height * botRatio));
		
		
	    //final float scale = getResources().getDisplayMetrics().density;
	    // float x = scale;
	    //rcProjected.left= (int) (rcProjected.left * scale + 0.5f);
	    //rcProjected.right= (int) (rcProjected.right * scale + 0.5f);
	    //rcProjected.top= (int) (rcProjected.top * scale + 0.5f);
	    //rcProjected.bottom= (int) (rcProjected.bottom * scale + 0.5f);
	    
		return rcProjected;
	}
    }

	}