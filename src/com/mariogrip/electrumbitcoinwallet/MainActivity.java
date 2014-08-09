package com.mariogrip.electrumbitcoinwallet;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mariogrip.electrumbitcoinwallet.iu.Status;
import com.mariogrip.electrumbitcoinwallet.iu.recivebtc;
import com.mariogrip.electrumbitcoinwallet.iu.sendbtc;
import com.mariogrip.electrumbitcoinwallet.lib.wallet;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

	private Timer timer = new Timer();
	private TimerTask timerTask;
    private String[] nawTitle;
    private DrawerLayout nawlay;
    private ListView nawList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Handler handler;
    private ProgressDialog progress;
    private TextView BalanceMain;
    private TextView BalanceRev;
    private TextView BalanceSend;
    private String Balance;
    private String UnBalance;
    Activity Msendbtc;
    private int pos;
    private boolean isRun;
    public static String Bal;
    public static String Addr;
    public static String UBal;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nawdraw);
		Log.d("Electrum-A", "OnCreate");
        Log.d("Electrum-A", "Get file");
        pos = 0;
        Balance = "0";
        Bal = "0";




            mTitle = mDrawerTitle = getTitle();
            nawTitle = getResources().getStringArray(R.array.nawbars);
            nawlay = (DrawerLayout) findViewById(R.id.drawer_layout);
            nawList = (ListView) findViewById(R.id.left_drawer);
            nawList.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.nawlist, nawTitle));
            nawList.setOnItemClickListener(new DrawerItemClickListener());





        getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);

            mDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    nawlay,
                    R.drawable.ic_drawer,
                    R.string.drawer_open,
                    R.string.drawer_close
            ) {
                public void onDrawerClosed(View view) {
                    getActionBar().setTitle(mTitle);
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    getActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu();
                }
            };
            nawlay.setDrawerListener(mDrawerToggle);

            if (savedInstanceState == null) {
                selectItem(0);
            }

            //sendBTC


            //RecBTC


            //Status

/*
        final Button button = (Button) findViewById(R.id.getHi);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });

        Button clickButton = (Button) findViewById(devbutt);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        */

            //SetButtons :)
            

            
        if (!wallet.hasWallet()){
            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Getting ready to create wallet....");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            new Thread() {
                public void run() {
                    Log.d("Electrum-A", "Setting up wallet");
                    new AssetExtractor(MainActivity.this).run();
                    PythonWrapper.start(getFilesDir().getAbsolutePath() + "/lib", getFilesDir().getAbsolutePath() + "/lib/python");
                    handler.sendEmptyMessage(1);
                }
            }.start();

            handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    progress.dismiss();
                    Intent myIntent = new Intent(MainActivity.this, CreateWallet.class);
                    MainActivity.this.startActivity(myIntent);
                }

                ;
            };
        

        }else {

            //---------------LOAD

            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while Electrum setting up your wallet....");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            new Thread() {
                public void run() {
                    Log.d("Electrum-A", "Setting up wallet");
                    new AssetExtractor(MainActivity.this).run();
                    PythonWrapper.start(getFilesDir().getAbsolutePath() + "/lib", getFilesDir().getAbsolutePath() + "/lib/python");
                    handler.sendEmptyMessage(1);
                }
            }.start();

            handler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    progress.dismiss();
                    create();

                }

                ;
            };
        }
	}

    public void runner(){
    	timerTask = new TimerTask() {
    	 @Override
    	 public void run() {

    		 Balance = PythonWrapper.quarry("try:\n    import boot \n    import api \n    from api import * \n    api = api() \n    backto = api.getbalance() \nexcept Exception,e: print str(e)\n");
    		 UnBalance = PythonWrapper.quarry("try:\n    import boot \n    import api \n    from api import * \n    api = api() \n    backto = api.getUnbalance() \nexcept Exception,e: print str(e)\n");
             Log.d("Electrum-A", "Balance: " + Balance.toString());
             isRun = true;
             Bal = Balance;
             UBal = UnBalance;
             
    		 MainActivity.this.runOnUiThread(new Runnable() {
                 public void run() {
                     switch (pos){
                         case 0:
                             BalanceMain = (TextView) findViewById(R.id.BalanceMain);
                             BalanceMain.setText(Balance.toString() + "(" + UnBalance.toString() + ")");
                             break;
                         case 1:
                             BalanceSend = (TextView) findViewById(R.id.BalanceSend);
                             BalanceSend.setText(Balance.toString());
                             break;
                         case 2:
                             BalanceRev = (TextView) findViewById(R.id.BalanceRev);
                             BalanceRev.setText(Balance.toString());
                             break;
                         default:
                             break;
                     }

                 }
             });



              

    	 }
    	};
    	timer.schedule(timerTask, 0, 10000);
    	

    }
    
    

    public void create(){
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Downloading chunks");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        new Thread() {
            public void run() {
                Log.d("Electrum-A" ,"Downloading chunks");
                PythonWrapper.quarry("try:\n    import boot \n    import api \n    from api import * \n    api = api() \n    backto = api.runwallet() \nexcept Exception,e: print str(e)\n");
                handler.sendEmptyMessage(1);
            }
        }.start();

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                SystemClock.sleep(1000);
                Addr = PythonWrapper.quarry("try:\n    import boot \n    import api \n    from api import * \n    api = api() \n    backto = api.Getaddr() \nexcept Exception,e: print str(e)\n");
                runner();
                progress.dismiss();
            };
        };
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = nawlay.isDrawerOpen(nawList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(final int position) {
        // update the main content by replacing fragments
    	pos = position;
        Fragment fragment = new PlanetFragment();
        switch (position) {
            case 0:
                fragment = new PlanetFragment();
                break;
            case 1:
                fragment = new sendbtc();
                break;
            case 2:
                fragment = new recivebtc();
                break;
            case 3:
                fragment = new Status();
                break;
            default:
                break;
        }
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


        // update selected item and title, then close the drawer
        nawList.setItemChecked(position, true);
        setTitle(nawTitle[position]);
        nawlay.closeDrawer(nawList);


    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);

        if (isRun) {

        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


            View rootView;
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String naws = getResources().getStringArray(R.array.nawbars)[i];
            rootView = inflater.inflate(R.layout.activity_main, container, false);

            int imageId = getResources().getIdentifier(naws.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());

            TextView BalanceSend = (TextView) rootView.findViewById(R.id.BalanceMain);
            BalanceSend.setText(MainActivity.Bal.toString());
            getActivity().setTitle(naws);
            return rootView;

        }
    }

}
