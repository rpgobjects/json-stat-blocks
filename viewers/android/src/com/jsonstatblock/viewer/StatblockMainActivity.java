package com.jsonstatblock.viewer;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.view.Menu;
import android.widget.Toast;

public class StatblockMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statblock_main);
		if(getFragmentManager().findFragmentById(R.id.home)==null) {
			StatBlockFragment f = new StatBlockFragment();
			f.parseJSON(openStatBlockFile("nocktrink.json"));
			getFragmentManager().beginTransaction().add(R.id.home,f).commit();
        }
	}
	
	public String openStatBlockFile(String file_name) {
		InputStream input;
		AssetManager assetManager = getAssets();
        try {
            input = assetManager.open(file_name);
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            String s = new String(buffer);
            return s;
        } catch (IOException e) {
        	Toast.makeText(this, "IO Error: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return "";
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statblock_main, menu);
		return true;
	}

}
