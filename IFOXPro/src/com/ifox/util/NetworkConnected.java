package com.ifox.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnected {
	private Context cont ;
	
	public NetworkConnected(Context ct){
		this.cont = ct ;
	}
	
	//����Ƿ�����������
	public boolean isNetworkConnected() { 
		if (cont != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) cont 
					.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
			if (mNetworkInfo != null) { 
				return mNetworkInfo.isAvailable(); 
			} 
		} 
		
		return false; 
	} 
}
