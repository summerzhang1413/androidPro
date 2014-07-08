package com.ifox.main;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ifox.fragment.DownloadFragment;
import com.ifox.fragment.NewsFragment;
import com.ifox.fragment.OtherFragment;
import com.ifox.other.UpdateManager;
import com.ifox.util.MyFragmentPagerAdapter;
import com.ifox.util.NetworkConnected;

public class MainActivity extends FragmentActivity {
	private TextView news_textview,download_textview,other_textview ;
	private ImageView iv_bottom_line;
	private ViewPager vpager; 
	private ArrayList<Fragment> arraylist;
	private int currIndex   =0;
	private  Resources resources;
	private int position_one;
	private int position_two;
	private ImageView ivBottomLine ;
    private int bottomLineWidth;
	private int offset  = 0;
	
	private UpdateManager up ;	//���¼��
	
	@Override
	protected void onCreate(Bundle saveInatanceState) {
		super.onCreate(saveInatanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE) ;
		super.setContentView(R.layout.main) ;
		
		//�����������״̬
		NetworkConnected nc = new NetworkConnected(MainActivity.this) ;
		boolean isCon = nc.isNetworkConnected() ;
		if(isCon){
			up = new UpdateManager(MainActivity.this) ;
			up.sendM() ; //�汾�����±��浽�����ļ�
			
			
		}else{
			Toast.makeText(MainActivity.this, R.string.unconnected, Toast.LENGTH_LONG).show() ;
		}
		
		
		
		resources = getResources();
        initWindowWidth();
		initTextView();
		initFragment();
	}

	public void initTextView() {
		news_textview = (TextView)findViewById(R.id.news_textview);
		download_textview =(TextView)findViewById(R.id.download_textview);
		other_textview = (TextView)findViewById(R.id.other_textview);
		vpager = (ViewPager)findViewById(R.id.vPager);
		
		news_textview.setOnClickListener(new TextViewOnClickListener(0));
		download_textview.setOnClickListener(new TextViewOnClickListener(1));
		other_textview.setOnClickListener(new TextViewOnClickListener(2));
	}

	public void initFragment() {
		arraylist = new ArrayList<Fragment>();
		Fragment newsfragment = new NewsFragment();
		Fragment downloadfragment = new DownloadFragment();
		Fragment otherfragment = new OtherFragment();

		arraylist.add(newsfragment );
		arraylist.add(downloadfragment);
		arraylist.add(otherfragment);
		vpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), arraylist));
		vpager.setCurrentItem(0);
		vpager.setOffscreenPageLimit(2) ;//��������fragmentҳ��
		vpager.setOnPageChangeListener(new MyChangeListener());
		
	}

	public void initWindowWidth() {
		ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);//��ð׸�
        bottomLineWidth = ivBottomLine.getLayoutParams().width;
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (int) ((screenW / 3.0 - bottomLineWidth) / 2);

        position_one = (int) (screenW / 3.0);
        position_two = position_one * 2;
	}
	
	public class TextViewOnClickListener implements View.OnClickListener{
		private int index = 0;
		
		@Override
		public void onClick(View v) {
			vpager.setCurrentItem(index);
		}
		
		public TextViewOnClickListener(int i){
			this.index = i;
		}
	}
	
	public class MyChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			 Animation animation = null;
	            switch (arg0) {
	            case 0:
	                if (currIndex == 1) {
	                    animation = new TranslateAnimation(position_one, 0, 0, 0);
	                    download_textview.setTextColor(resources.getColor(R.color.lightwhite));
	                } else if (currIndex == 2) {
	                    animation = new TranslateAnimation(position_two, 0, 0, 0);
	                  other_textview.setTextColor(resources.getColor(R.color.lightwhite));
	                } 
	                news_textview.setTextColor(resources.getColor(R.color.white));
	                break;
	            case 1:
	                if (currIndex == 0) {
	                    animation = new TranslateAnimation(0, position_one, 0, 0);
	                   news_textview.setTextColor(resources.getColor(R.color.lightwhite));
	                } else if (currIndex == 2) {
	                    animation = new TranslateAnimation(position_two, position_one, 0, 0);
	                    other_textview.setTextColor(resources.getColor(R.color.lightwhite));
	                }
	                download_textview.setTextColor(resources.getColor(R.color.white));
	                break;
	            case 2:
	                if (currIndex == 0) {
	                    animation = new TranslateAnimation(0, position_two, 0, 0);
	                    news_textview.setTextColor(resources.getColor(R.color.lightwhite));
	                } else if (currIndex == 1) {
	                    animation = new TranslateAnimation(position_one, position_two, 0, 0);
	                    download_textview.setTextColor(resources.getColor(R.color.lightwhite));
	                } 
	                other_textview.setTextColor(resources.getColor(R.color.white));
	                break;
			
	            }

	            currIndex = arg0;
	            animation.setFillAfter(true);
	            animation.setDuration(300);
	            ivBottomLine.startAnimation(animation);
		
		}
	}
	
	//���÷��ذ����ļ����¼�
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {	// ���ؼ�
			this.exitDialog() ;
		}
		return false ;
	}
	
	private void exitDialog(){
		Dialog dialog = new AlertDialog.Builder(MainActivity.this)
			.setTitle("�˳���")		// ��������
			.setMessage("��ȷ��Ҫ�˳���������") // ��ʾ�Ի����е�����
			.setIcon(R.drawable.exit) // ����LOGO
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.finish() ;	// ��������
				}
			}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			}).create(); // ������һ���Ի���
		dialog.show() ;	// ��ʾ�Ի���
	}
	
}
