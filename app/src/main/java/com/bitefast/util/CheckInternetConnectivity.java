package com.bitefast.util;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;




/**
 * Created by spandanv on 6/25/2015.
 */
public class CheckInternetConnectivity {

        private Context _context;

        public CheckInternetConnectivity(Context context){
            this._context = context;
        }

        public boolean isConnectingToInternet(){
            ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null)
            {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            return true;
                        }

            }
            return false;
        }
}
