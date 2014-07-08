package com.ifox.other;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ifox.constant.XmlConstant ;
import com.ifox.main.R;



public class UpdateManager { 
	/* ������ */ 
    private static final int DOWNLOAD = 1; 
    /* ���ؽ��� */ 
    private static final int DOWNLOAD_FINISH = 2;
    /* ���������XML��Ϣ */ 
    private HashMap<String, String> mHashMap = new HashMap<String, String>(); 
    /* ���ر���·�� */ 
    private String mSavePath; 
    /* ��¼���������� */ 
    private int progress; 
    /* �Ƿ�ȡ������ */ 
    private boolean cancelUpdate = false; 
 
    private Context mContext; 
    /* ���½����� */ 
    private ProgressBar mProgress; 
    private Dialog mDownloadDialog; 
    //��ַ�Ƿ�������version.xml���ӵ�ַ 
    private String path = "http://" + XmlConstant.IP + "/wygl/xml/version.xml";  
    //��ǰ�汾
    private int versionCode = 0 ;
    //���°汾
    private int serviceCode = 0 ;
    //�Ƿ���Ҫ���£�Ĭ��Ϊ����Ҫ
    private boolean target = false ;
    private static  final String FILENAME = "version" ;	//����汾��Ϣ
    
    
    private Handler mHandler = new Handler() { 
        public void handleMessage(Message msg) { 
            switch (msg.what) { 
            // �������� 
            case DOWNLOAD: 
	            // ���ý�����λ�� 
	            mProgress.setProgress(progress); 
	            break; 
            case DOWNLOAD_FINISH: 
	            // ��װ�ļ� 
	            installApk(); 
	            break; 
            default: 
                break; 
            } 
        }; 
    }; 
 
    public UpdateManager(Context context) { 
        this.mContext = context; 
    } 

    /**
     * ����������
     */ 
    public void checkUpdate(boolean tg){
        if (tg) { 
            // ��ʾ��ʾ�Ի��� 
            showNoticeDialog(); 
        } else { 
        	
            Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG).show(); 
        } 
    } 
 
    /**
     * �������Ƿ��и��°汾
     * 
     * @return
     * @throws InterruptedException 
     */ 
    
    public void sendM() {
    	// ��ȡ��ǰ����汾 
    	versionCode = getVersionCode(mContext); 
    	System.out.println("**** ��ǰ�汾versionCode ****:" + versionCode)  ;
    	
        new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try{  
		            URL url = new URL(path);  
		            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
		            conn.setReadTimeout(5*1000);  
		            conn.setRequestMethod("GET");  
		            InputStream inStream = conn.getInputStream();  
		            //ʹ��DOM����
		            ParseXmlService service = new ParseXmlService(); 
		            mHashMap = service.parseXml(inStream);  
		            serviceCode = Integer.parseInt(mHashMap.get("version")) ;
		            String name = mHashMap.get("name") ;
		            String urll = mHashMap.get("url") ;
		            System.out.println("**** ���汾serviceCode ****:" + serviceCode)  ;
	            	if(serviceCode > versionCode){
	            		//���°汾���������ñ�־λ
	            		target = true ;
	            		
	            	}
	            	//����汾�ŵ������ļ�
            		SharedPreferences share = mContext.getSharedPreferences(FILENAME,
            				Activity.MODE_PRIVATE);	// ָ���������ļ�����
            		SharedPreferences.Editor edit = share.edit(); 	// �༭�ļ�
            		edit.putBoolean("version", target);			// ��������
            		edit.putString("name", name) ;
            		edit.putString("url", urll) ;
            		edit.commit() ;			// �ύ����
		        } catch (Exception e)  {  
		            e.printStackTrace();  
		        }
			}
        	
        }).start() ;
        System.out.println("*** UpdateManager�����target ****:" + target) ;
    }
	/**
     * ��ȡ����汾��
     * 
     * @param context
     * @return
     */ 
    private int getVersionCode(Context context) 
    { 
        int versionCode = 0; 
        try 
        { 
            // ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode 
            versionCode = context.getPackageManager()
            		.getPackageInfo("com.ifox.main", 0).versionCode; 
        } catch (NameNotFoundException e) 
        { 
            e.printStackTrace(); 
        } 
        return versionCode; 
    } 
 
    /**
     * ��ʾ������¶Ի���
     */ 
    private void showNoticeDialog() 
    { 
        // ����Ի��� 
        AlertDialog.Builder builder = new Builder(mContext); 
        builder.setTitle(R.string.soft_update_title); 
        builder.setMessage(R.string.soft_update_info); 
        // ���� 
        builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() 
        { 
            @Override 
            public void onClick(DialogInterface dialog, int which) 
            { 
                dialog.dismiss(); 
                // ��ʾ���ضԻ��� 
                showDownloadDialog(); 
            } 
        }); 
        // �Ժ���� 
        builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() 
        { 
            @Override 
            public void onClick(DialogInterface dialog, int which) 
            { 
                dialog.dismiss(); 
            } 
        }); 
        Dialog noticeDialog = builder.create(); 
        noticeDialog.show(); 
    } 
 
    /**
     * ��ʾ������ضԻ���
     */ 
    private void showDownloadDialog(){ 
        // ����������ضԻ��� 
        AlertDialog.Builder builder = new Builder(mContext); 
        builder.setTitle(R.string.soft_updating); 
        // �����ضԻ������ӽ����� 
        final LayoutInflater inflater = LayoutInflater.from(mContext); 
        View v = inflater.inflate(R.layout.softupdate_progress, null); 
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress); 
        builder.setView(v); 
        // ȡ������ 
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() 
        { 
            @Override 
            public void onClick(DialogInterface dialog, int which) 
            { 
                dialog.dismiss(); 
                // ����ȡ��״̬ 
                cancelUpdate = true; 
            } 
        }); 
        mDownloadDialog = builder.create(); 
        mDownloadDialog.show(); 
        // �����ļ� 
        downloadApk(); 
    } 
 
    /**
     * ����apk�ļ�
     */ 
    private void downloadApk() 
    { 
        // �������߳�������� 
        new downloadApkThread().start(); 
    } 
 
    /**
     * �����ļ��߳�
     */ 
    private class downloadApkThread extends Thread { 
        @Override 
        public void run() { 
            try { 
                // �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ�� 
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { 
                    // ��ô洢����·�� 
                    String sdpath = Environment.getExternalStorageDirectory().toString() + File.separator;  //ȡ����չ�Ĵ洢Ŀ¼
                    mSavePath = sdpath + "iFOXUpdate"; 
                    System.out.println("******mHashMap.get(url)***:" +  mHashMap.get("url")) ;
                    
                    //��ȡ���ذ汾�����ļ�
					SharedPreferences share = mContext.getSharedPreferences(FILENAME,
							Activity.MODE_PRIVATE);	// ָ���������ļ�����
					String urll = share.getString("url", null) ;
					System.out.println("**** share.getString(url) ****" + urll) ;
                    
                    URL url = new URL(urll); 
                    // �������� 
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
                    conn.connect(); 
                    if(HttpURLConnection.HTTP_OK==conn.getResponseCode()){
                    	System.out.println("******* �������ӳɹ�!!!  *********" ) ;
                    }
                    // ��ȡ�ļ���С 
                    int length = conn.getContentLength(); 
                    // ���������� 
                    InputStream inputs = conn.getInputStream(); 
                    File file = new File(mSavePath); 
                    // �жϸ��ļ�Ŀ¼�Ƿ���� 
                    if (!file.exists()){ 
                        file.mkdirs(); 
                    } 
                    System.out.println("******* File�ļ�·��  *********" + file) ;
                    //��ȡ���ذ汾�����ļ�
					String name = share.getString("name", null) ;
                    File apkName = new File(mSavePath + File.separator + name); 
                    System.out.println("******* apkFile·��  *********" + apkName) ;
                    
                    FileOutputStream foutputs = new FileOutputStream(apkName); 
                    int count = 0; 
                    // ���� 
                    byte buf[] = new byte[length]; 
                    // д�뵽�ļ��� 
                    do{ 
                        int numread = inputs.read(buf); 
                        
                        System.out.println("******* numread  *********" + numread) ;
                        
                        count += numread; 
                        // ���������λ�� 
                        progress = (int) (((float) count / length) * 100); 
                        // ���½��� 
                        mHandler.sendEmptyMessage(DOWNLOAD); 
                        if (numread <= 0) 
                        { 
                            // ������� 
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH); 
                            break; 
                        } 
                        // д���ļ� 
                        foutputs.write(buf, 0, numread); 
                    } while (!cancelUpdate);// ���ȡ����ֹͣ����. 
                    foutputs.close(); 
                    inputs.close(); 
                } 
            }catch (MalformedURLException e) { 
            	System.out.println("******* MalformedURLException�쳣 *********") ;
                e.printStackTrace(); 
            }catch (IOException e) { 
            	System.out.println("******* IOException�쳣 *********") ;
                e.printStackTrace(); 
            } 
            // ȡ�����ضԻ�����ʾ 
            mDownloadDialog.dismiss(); 
        } 
    }; 
 
    /**
     * ��װAPK�ļ�
     */ 
    private void installApk() {
    	//��ȡ���ذ汾�����ļ�
		SharedPreferences share = mContext.getSharedPreferences(FILENAME,
				Activity.MODE_PRIVATE);	// ָ���������ļ�����
		String name = share.getString("name", null) ;
        File apkName = new File(mSavePath + File.separator + name); 
        if (!apkName.exists()) 
        { 
            return; 
        } 
        // ͨ��Intent��װAPK�ļ� 
        Intent it = new Intent(Intent.ACTION_VIEW); 
        it.setDataAndType(Uri.parse("file://" + apkName.toString()), "application/vnd.android.package-archive"); 
        mContext.startActivity(it); 
    } 
}

