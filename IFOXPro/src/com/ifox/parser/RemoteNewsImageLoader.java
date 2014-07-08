package com.ifox.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ifox.constant.XmlConstant;
import com.ifox.other.ParseXmlService;

public class RemoteNewsImageLoader {
	Context context ;
	
	public RemoteNewsImageLoader(Context con){
		context = con;
	}
	
	public void saveNewsDetails(){
		String imageNewsInfoPath = "http://" + XmlConstant.IP + "/wygl/xml/imagetitleinfo.xml" ;
		try {
			URL url  = new URL(imageNewsInfoPath) ;
			HttpURLConnection conn = (HttpURLConnection)url.openConnection() ;
			conn.setConnectTimeout(5000) ;
			conn.connect() ;
			InputStream input = conn.getInputStream() ;
			ParseXmlService parse = new ParseXmlService() ;//使用DOM解析
			HashMap<String, String> hashmap = new HashMap<String, String>() ;
			hashmap = parse.parseXmlNewsInfo(input) ;
			
			saveData(hashmap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveData(HashMap<String, String> hashmap) {
		SharedPreferences sp = context.getSharedPreferences(XmlConstant.NEWSINFO_FILENAME, context.MODE_PRIVATE) ;
		SharedPreferences.Editor edit = sp.edit() ;//保存信息到本地
		String imageTitle[] = new String[5] ;
		String newsTitle[] = new String[6] ;
		String newsDate[] = new String[6] ;
		String newsDetail[] = new String[6] ;
		for(int newsInfoNumber = 1; newsInfoNumber <= 6; newsInfoNumber ++){
			String num = String.valueOf(newsInfoNumber) ;
			if(newsInfoNumber <= 5){
				imageTitle[newsInfoNumber-1] = hashmap.get("imagetitle" + num) ;
				edit.putString("imagetitle" + num, imageTitle[newsInfoNumber-1]) ;
				saveNewsDetailsData(hashmap, edit, newsTitle, newsDate,
						newsDetail, newsInfoNumber, num);
			}else{
				saveNewsDetailsData(hashmap, edit, newsTitle, newsDate,
						newsDetail, newsInfoNumber, num);
			}
			
		}
		edit.commit() ;
	}

	private void saveNewsDetailsData(HashMap<String, String> hashmap,
			SharedPreferences.Editor edit, String[] newsTitle,
			String[] newsDate, String[] newsDetail, int newsInfoNumber,
			String num) {
		newsTitle[newsInfoNumber-1] = hashmap.get("title" + num) ;
		newsDate[newsInfoNumber-1] = hashmap.get("date" + num) ;
		newsDetail[newsInfoNumber-1] = hashmap.get("info" + num) ;
		edit.putString("title" + num, newsTitle[newsInfoNumber-1]) ;
		edit.putString("date" + num, newsDate[newsInfoNumber-1]) ;
		edit.putString("info" + num, newsDetail[newsInfoNumber-1]) ;
	}
	
	public void saveRemoteImage() {
		File cacheDir = new File(context.getFilesDir(), "image_cache");
		if(!cacheDir.exists()){
			cacheDir.mkdirs();
		}
		for(int i=0;i<5;i++){
			Bitmap bitmap = getHttpBitmap("http://" + XmlConstant.IP + "/wygl/images/image" + (i+1) + ".jpg");
			if(bitmap != null){
				File cacheFile = new File(cacheDir, "item" + (i+1) + ".png");
				try {
					cacheFile.createNewFile();
					OutputStream output = new FileOutputStream(cacheFile);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);
					output.flush();
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("******** 保存图片成功！ *********");
			}
		}
	}
	
	public Bitmap getHttpBitmap(String url) {
		URL myFileURL;
        Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
			conn.setConnectTimeout(6000);
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
