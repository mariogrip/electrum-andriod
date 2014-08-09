package com.mariogrip.electrumbitcoinwallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CreateWallet extends Activity {

	TextView password;
	ProgressDialog progress;
    Handler handler;
    String report;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_wallet);
        Button SendBtc = (Button) findViewById(R.id.CreateWalletB);
        password = (TextView) findViewById(R.id.CreateWalletPass);
        SendBtc.setOnClickListener(new View.OnClickListener() {


        	
            @Override
            public void onClick(View v) {
                progress = new ProgressDialog(CreateWallet.this);
                progress.setTitle("Loading");
                progress.setMessage("Making your wallet...");
                progress.setCancelable(false);
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                new Thread() {
                    public void run() {
                        if (password.getText().toString().matches("")){
                            report = PythonWrapper.quarry("try:\n    import boot \n    import api \n    from api import * \n    api = create() \n    backto = api.createWallet() \nexcept Exception,e: print str(e)\n");
                        }else{
                            report = PythonWrapper.quarry("import boot \nimport api \nfrom api import * \napi = create() \nbackto = api.createWallet(\"" + password.getText().toString() + "\") \n");
                        }
                        if (report.startsWith("true")) {
                            handler.sendEmptyMessage(1);
                        }else{
                            progress.dismiss();
                            Toast.makeText(CreateWallet.this, "Error: cannot make wallet, please report to mariogrip at irc (#electrum)", Toast.LENGTH_LONG).show();
                        }


                    }
                }.start();

                handler = new Handler() {
                    public void handleMessage(android.os.Message msg) {
                        progress.dismiss();
                        Intent myIntent = new Intent(CreateWallet.this, MainActivity.class);
                        CreateWallet.this.startActivity(myIntent);
                    }

                    ;
                };


                

            }
        });
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_wallet, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
