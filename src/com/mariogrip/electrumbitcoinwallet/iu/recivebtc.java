package com.mariogrip.electrumbitcoinwallet.iu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mariogrip.electrumbitcoinwallet.MainActivity;
import com.mariogrip.electrumbitcoinwallet.R;

/**
 * Created by mariogrip on 01.08.14.
 */
public class recivebtc extends Fragment{

    public recivebtc(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.recivebtc, container, false);

        TextView BalanceRev = (TextView) rootView.findViewById(R.id.BalanceRev);
        BalanceRev.setText(MainActivity.Bal.toString());
        TextView SetAdd = (TextView) rootView.findViewById(R.id.BtcAdd);
        SetAdd.setText(MainActivity.Addr.toString());

        return rootView;
    }

}
