package com.mariogrip.electrumbitcoinwallet.iu;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mariogrip.electrumbitcoinwallet.MainActivity;
import com.mariogrip.electrumbitcoinwallet.PythonWrapper;
import com.mariogrip.electrumbitcoinwallet.R;

/**
 * Created by mariogrip on 01.08.14.
 */
public class sendbtc extends Fragment{

    TextView BalanceSend;
    String Balance;
    public sendbtc(){}




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.sendbtc, container, false);

        TextView BalanceSend = (TextView) rootView.findViewById(R.id.BalanceSend);
        BalanceSend.setText(MainActivity.Bal.toString());

        Button SendBtc = (Button) rootView.findViewById(R.id.SendBtcButton);
        SendBtc.setOnClickListener(new View.OnClickListener() {

        	
            @Override
            public void onClick(View v) {
                TextView SendAddr = (TextView) rootView.findViewById(R.id.SendBtcAddr);
                TextView SendAmount = (TextView) rootView.findViewById(R.id.SendBtcAmout);
                String Addr = (String) SendAddr.getText().toString();
                Double Amount = Double.parseDouble(SendAmount.getText().toString());
                Double AmountHave = Double.parseDouble(PythonWrapper.quarry("import boot \nimport api \nfrom api import * \napi = api() \nbackto = api.getbalance() \n"));
                if (Amount == 0) {
                    Toast.makeText(getActivity(), "You cannot send 0 btc", Toast.LENGTH_SHORT).show();
                }else{
                    if (Amount > AmountHave) {
                        Toast.makeText(getActivity(), "You do not have enough bitcoins to send", Toast.LENGTH_SHORT).show();
                    } else {
                    	Toast.makeText(getActivity(), "Okey", Toast.LENGTH_SHORT);
                        //PythonWrapper.quarry("try:\n    import boot \n    import api \n    from api import * \n    api = api() \n    backto = api.payto(" + Addr + ", " + Amount.toString() + ") \nexcept Exception,e: print str(e)\n");

                    }
                }
            }
        });
        
        return rootView;
    }

}
