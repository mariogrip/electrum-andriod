package com.mariogrip.electrumbitcoinwallet.iu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mariogrip.electrumbitcoinwallet.R;

/**
 * Created by mariogrip on 01.08.14.
 */
public class recivebtc extends Fragment{

    public recivebtc(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recivebtc, container, false);

        return rootView;
    }

}
