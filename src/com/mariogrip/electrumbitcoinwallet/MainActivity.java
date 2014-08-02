package com.mariogrip.electrumbitcoinwallet;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.ListView;

import com.mariogrip.electrumbitcoinwallet.iu.Status;
import com.mariogrip.electrumbitcoinwallet.iu.recivebtc;
import com.mariogrip.electrumbitcoinwallet.iu.sendbtc;
import com.mariogrip.electrumbitcoinwallet.lib.MakeWallet;
import com.mariogrip.electrumbitcoinwallet.lib.wallet;

import java.util.Locale;

public class MainActivity extends Activity {

    private String[] nawTitle;
    private DrawerLayout nawlay;
    private ListView nawList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nawdraw);
		Log.d("Electrum-A", "OnCreate");
        Log.d("Electrum-A", "Get file");

        PythonWrapper.start();
        Log.d("Electrum-A", "Starting PythonWrapper");
        PythonWrapper.end();
        wallet.canWR();
        MakeWallet.MakeWallet();

        mTitle = mDrawerTitle = getTitle();
        nawTitle = getResources().getStringArray(R.array.nawbars);
        nawlay = (DrawerLayout) findViewById(R.id.drawer_layout);
        nawList = (ListView) findViewById(R.id.left_drawer);


        // set a custom shadow that overlays the main content when the drawer opens

        // set up the drawer's list view with items and click listener
        nawList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.nawlist, nawTitle));
        nawList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                nawlay,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        nawlay.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        //sendBTC



        //RecBTC



        //Status

        final Button button = (Button) findViewById(R.id.getHi);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });

/*
        Button clickButton = (Button) findViewById(devbutt);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        */

	}


    /*
	public void Settext(String text){
		TextView newtext = (TextView) findViewById(R.id.);
		newtext.setText(text);

	}
	*/

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

    private void selectItem(int position) {
        // update the main content by replacing fragments
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

            getActivity().setTitle(naws);
            return rootView;

        }
    }

}
