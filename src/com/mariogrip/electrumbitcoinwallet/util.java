package com.mariogrip.electrumbitcoinwallet;

import java.text.DecimalFormat;

/**
 * Created by mariogrip on 10.08.14.
 */
public class util {

    public static boolean SendBtc(String toAddr, String Amount){
        MainActivity.Paying = true;
        //TODO add error return to python string...
        String Check = PythonWrapper.quarry("try:\n" +
                                            "    import boot \n" +
                                            "    import api \n" +
                                            "    from api import * \n" +
                                            "    api = api() \n" +
                                            "    backto = api.payto(\"" + toAddr + "\",\"" + Amount + "\") \n" +
                                            "except Exception,e: print str(e)\n");
        if (Check.startsWith("false")){
            MainActivity.Paying = false;
            return false;
        }else{
            MainActivity.Paying = false;
            return true;
        }
    }
    protected static String GetBalance(){
        String Balance = PythonWrapper.quarry("try:\n    import boot \n    import api \n    from api import * \n    api = api() \n    backto = api.getbalance() \nexcept Exception,e: print str(e)\n");
        DecimalFormat df = new DecimalFormat("0.00000000");
        String out = (String) df.format(Double.parseDouble(Balance));
        return out;

    }
    protected static String GetUBalance(){
        String Balance = PythonWrapper.quarry("try:\n    import boot \n    import api \n    from api import * \n    api = api() \n    backto = api.getUnbalance() \nexcept Exception,e: print str(e)\n");
        DecimalFormat df = new DecimalFormat("0.00000000");
        String out = (String) df.format(Double.parseDouble(Balance));
        return out;
    }

}
