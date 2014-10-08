package com.mariogrip.electrumbitcoinwallet.iu;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mariogrip.electrumbitcoinwallet.MainActivity;
import com.mariogrip.electrumbitcoinwallet.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

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

      //  File file = QRCode.from("Hello World").file();
       // BitmapFactory.Options options = new BitmapFactory.Options();
      //  options.inPreferredConfig = Bitmap.Config.ARGB_8888;
      //  Bitmap bitmap = BitmapFactory.decodeFile(file.getPath()+file.getName(), options);
      //  ImageView mImageView;
      //  mImageView = (ImageView) rootView.findViewById(R.id.QR);
      //  mImageView.setImageBitmap(bitmap);
        if (MainActivity.debug){
        	MainActivity.debug = false;
          Toast.makeText(getActivity(), "Cannot make QR code, Please report: error 0xQR", Toast.LENGTH_SHORT).show();
        }
        

        return rootView;
    }

}
