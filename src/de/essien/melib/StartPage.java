package de.essien.melib;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
// import tv.puffbirds.bedtime.R;

public class StartPage extends PlistPage {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.app_id = app_id;
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        super.Load(R.raw.startpage);
	}
}   