package com.mariogrip.electrumbitcoinwallet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mariogrip.electrumbitcoinwallet.lib.MakeWallet;
import com.mariogrip.electrumbitcoinwallet.lib.wallet;

import static com.mariogrip.electrumbitcoinwallet.R.id.devbutt;

public class MainActivity extends Activity {


	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("Electrum-A", "OnCreate");
        Log.d("Electrum-A", "Get file");
        wallet.canWR();
        MakeWallet.MakeWallet();

        Button clickButton = (Button) findViewById(devbutt);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
	}
	public void Settext(String text){
		TextView newtext = (TextView) findViewById(R.id.textView1);
		newtext.setText(text);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
