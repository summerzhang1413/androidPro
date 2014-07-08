package com.ifox.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ifox.constant.XmlConstant;
import com.ifox.parser.RemoteNewsImageLoader;
import com.ifox.util.NetworkConnected;

public class GuideActivity extends Activity {
	private ViewPager pager;  // ���һ������
//	private LinearLayout points;  // ���ڴ�Ž���ͼ��Ĳ���
	private List<View> list;  // ���ڱ���4��ͼƬ��������Դ 
    private ImageView[] views;  // ���ڴ�ŵ���СԲ��
    // 4�������ļ�,�������
    private int[] ids = {R.layout.firstitem01, R.layout.firstitem02, R.layout.firstitem03, R.layout.firstitem04,R.layout.firstitem05};
    private SharedPreferences sp ;
    private SharedPreferences.Editor edit ;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.firstactivity_main);
		sp = getSharedPreferences(XmlConstant.CHECKFIRSTBUILD_FILENAME, Activity.MODE_PRIVATE) ; //����Ƿ��ǵ�һ�ΰ�װ
		edit = sp.edit() ;
		boolean firstload = sp.getBoolean("firstload", true);
		NetworkConnected nc = new NetworkConnected(GuideActivity.this) ;
		boolean isConnected = nc.isNetworkConnected() ;
		if(firstload){
			if(isConnected){
				new Thread(new Runnable(){
		
						@Override
						public void run() {
							// TODO Auto-generated method stub
							RemoteNewsImageLoader newsImageLoader = new RemoteNewsImageLoader(GuideActivity.this) ;
							newsImageLoader.saveNewsDetails() ;
							newsImageLoader.saveRemoteImage() ;
						}
				
					}
				).start() ;
			}
			
			init();
		}else{
			Intent it = new Intent(GuideActivity.this, StartActivity.class) ;
			startActivity(it) ;
			finish() ;
		}
		
		
	}

	private void init() {
		edit.putBoolean("firstload", false) ;
		edit.commit() ;
		pager = (ViewPager)this.findViewById(R.id.imagePages);
//		points = (LinearLayout)this.findViewById(R.id.pointGroup);
		list = new ArrayList<View>();
		// ����ͼƬ�ļ��ϣ��������ó��˹̶����أ���ȻҲ�ɶ�̬���ء�
		int[] slideImages = {
				R.drawable.firstp1,
				R.drawable.firstp2,
				R.drawable.firstp3,
				R.drawable.firstp4,
				R.drawable.firstp5
		};
		for(int i = 0; i<5; i++){
			FrameLayout layout = (FrameLayout)getLayoutInflater().inflate(ids[i], null);
			ImageView image = (ImageView)layout.findViewById(R.id.imageView1);
			// ����Ҫ
			image.setImageResource(slideImages[i]);
			list.add(layout);
		}
		// ����list�ĳ��Ⱦ��������ĵ���ͼƬ����
		views = new ImageView[list.size()];

		pager.setAdapter(new MyPagerAdapter());
		pager.setOnPageChangeListener(new OnPageChangeListenerImpl() );
	}
	
    private class OnPageChangeListenerImpl implements OnPageChangeListener{
    	@Override
		public void onPageSelected(int arg0) {
			//�������һҳ�Զ���ת
			if(arg0 == views.length-1){
				new Handler().postDelayed(
						new Runnable(){
							@Override
							public void run() {
								// T ODO Auto-generated method stub
								Intent it = new Intent(GuideActivity.this, MainActivity.class) ;
								startActivity(it) ;
								finish() ;
							}
						}
				, 1000) ;
			}
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}
    }

    private class MyPagerAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		@Override
		public Object instantiateItem(View container, int position) {
			( (ViewPager)container ).addView(list.get(position));
			return list.get(position);
		}
		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			( (ViewPager)container ).removeView(list.get(position));
		}
		
	}
    
}
