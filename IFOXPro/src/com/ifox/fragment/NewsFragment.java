package com.ifox.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.ifox.constant.XmlConstant;
import com.ifox.layout.SlideImageLayout;
import com.ifox.main.R;
import com.ifox.main.StartActivity;
import com.ifox.parser.NewsXmlParser;
import com.ifox.parser.RemoteNewsImageLoader;
import com.ifox.util.NetworkConnected;

public class NewsFragment extends ListFragment {
	private com.ifox.util.ScrollViewExtend scroll; 
	private ImageView imageView1;
	private ListView news_listview;
	private ArrayList<HashMap<String, Object>> listItems ;    //������֡�ͼƬ��Ϣ  
    private SimpleAdapter listItemAdapter;                  //������     
	// ����ͼƬ�ļ���
	private ArrayList<View> imagePageViews = null;
	private ViewGroup main = null;
	private ViewPager viewPager = null;
	// ��ǰViewPager����
	private int pageIndex = 0; 
	// ����Բ��ͼƬ��View
	private ViewGroup imageCircleView = null;
	private ImageView[] imageCircleViews = null; 
	// ��������
	private TextView tvSlideTitle = null;
	// ����������
	private SlideImageLayout slideLayout = null;
	// ���ݽ�����
	private NewsXmlParser parser = null; 
	LayoutInflater inflater;
	private SharedPreferences sp ;
	private String imagetitle[] = null; 
	private String title[] = null ;
	private String date[] = null ;
	private String info[] = null ;
	
	private PullToRefreshScrollView mPullRefreshScrollView;
	private ScrollView mScrollView;
	
	File cache  ;
	private int length ;
	private SlideImageAdapter slideImageAdapter  ;
	private ImagePageChangeListener imagePageChangeListener  ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.inflater = inflater ;
		init(inflater); 
		//��ʼ���Ϸ���ͼƬ��������
		initeViews() ;
		tvSlideTitle.setText(imagetitle[0]);
        viewPager.setAdapter(slideImageAdapter);  
        viewPager.setCurrentItem(pageIndex) ;
        viewPager.setOnPageChangeListener(imagePageChangeListener) ;
        System.out.println("********* onCreateView() **********") ;
		//��ʼ���·�����������
  		initListview();
  		//������������Item�Ͷ�̬�����Ӧ��Ԫ��     
        listItemAdapter = new SimpleAdapter(getActivity(),listItems,    
				                R.layout.news_list_item, 
				                new String[] {"ItemTitle","ItemContent"},      
				                new int[] {R.id.newsTitle,R.id.newsContent}     
        					); 
        this.setListAdapter(listItemAdapter);
		return inflater.inflate(R.layout.suibian, main);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	private void init(LayoutInflater inflater) {
		
		listItems = new ArrayList<HashMap<String, Object>>();
		// ����ͼƬ����
		imagePageViews = new ArrayList<View>();
		
		//LayoutInflater inflater = getLayoutInflater();  
		main = (ViewGroup)inflater.inflate(R.layout.news_fragment, null);
		viewPager = (ViewPager) main.findViewById(R.id.image_slide_page);  //�õ�ViewPager
		mPullRefreshScrollView=(com.handmark.pulltorefresh.library.PullToRefreshScrollView)main.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			NetworkConnected nc = new NetworkConnected(getActivity()) ;
			boolean isConnected = nc.isNetworkConnected() ;
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if(isConnected){
					new GetDataTask().execute();
				}else{
					Toast.makeText(getActivity(), "��������!!", Toast.LENGTH_SHORT).show() ;
					mPullRefreshScrollView.onRefreshComplete();
				}
			}
		});
		mScrollView = mPullRefreshScrollView.getRefreshableView();
		
		//scroll.setOnTouchListener(new ImageOnTouchListener()) ;
		// Բ��ͼƬ����
		parser = new NewsXmlParser(); 
		length = parser.getSlideImages().length;
//		int length = ids.length ;
		imageCircleViews = new ImageView[length];//ʵ����Բ��ͼƬ 
		imageCircleView = (ViewGroup) main.findViewById(R.id.layout_circle_images);
		slideLayout = new SlideImageLayout(getActivity());
		slideLayout.setCircleImageLayout(length);
		// ����Ҫ��������ͼƬ,ֱ�����ڲ�������Ϳ�����
		cache = new File(getActivity().getFilesDir(),"image_cache");
		// ����Ĭ�ϵĻ�������
		tvSlideTitle = (TextView) main.findViewById(R.id.tvSlideTitle);
		imagetitle = new String[5] ;
		title = new String[6] ;
		date = new String[6]; 
		info = new String[6] ;
		slideImageAdapter = new SlideImageAdapter() ;
		imagePageChangeListener = new ImagePageChangeListener() ;
		
		for(int i = 0;i < length;i++){
			File f = new File(cache,"item"+(i+1)+".png");
			Bitmap map = BitmapFactory.decodeFile(f.getPath());
			imagePageViews.add(slideLayout.getSlideImageLayout(map));//�õ�����ͼƬ
			imageCircleViews[i] = slideLayout.getCircleImageLayout(i);
			imageCircleView.addView(slideLayout.getLinearLayout(imageCircleViews[i], 10, 10));
		}
	}
	
	private void initeViews(){
		sp = getActivity().getSharedPreferences(XmlConstant.NEWSINFO_FILENAME, Activity.MODE_PRIVATE) ;
		for(int newsInfoNumber = 1; newsInfoNumber <= 6; newsInfoNumber ++){
			String num = String.valueOf(newsInfoNumber) ;
			if(newsInfoNumber <= 5){
				imagetitle[newsInfoNumber-1] = sp.getString("imagetitle" + num, null) ;
				getNewsDetailsData(newsInfoNumber, num);
			}else{
				getNewsDetailsData(newsInfoNumber, num);
			}
			
		}
	}

	private void getNewsDetailsData(int newsInfoNumber, String num) {
		title[newsInfoNumber-1] = sp.getString("title" + num, null) ;
		date[newsInfoNumber-1] = sp.getString("date" + num, null) ;
		info[newsInfoNumber-1] = sp.getString("info" + num, null) ;
	}

	//���������б�����
	private void initListview(){
		for(int i=0;i<6;i++){     
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle" , title[i]);
			map.put("ItemContent" , info[i]);
			listItems.add(map);     
		}     
        
	}
	
	
	// ����ͼƬ����������
    private class SlideImageAdapter extends PagerAdapter {  
		@Override  
        public int getCount() { 
            return imagePageViews.size();  
        }  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
        @Override  
        public int getItemPosition(Object object) {  
            // TODO Auto-generated method stub  
//            return super.getItemPosition(object);  
        	return POSITION_NONE;
        }  
        @Override  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).removeView(imagePageViews.get(arg1));  
        }  
        @Override  
        public Object instantiateItem(View arg0, int arg1) {  
            // TODO Auto-generated method stub  
        	((ViewPager) arg0).addView(imagePageViews.get(arg1));
            return imagePageViews.get(arg1);  
        }  
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
        }  
        @Override  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
        @Override  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
        }  
        @Override  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
        }  
    }
   
    
    // ����ҳ������¼�������
    private class ImagePageChangeListener implements OnPageChangeListener {
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
        }  
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
        }  
  
        @Override  
        public void onPageSelected(int index) {  
        	pageIndex = index;
        	slideLayout.setPageIndex(index);
        	tvSlideTitle.setText(imagetitle[index]);
            for (int i = 0; i < imageCircleViews.length; i++) {  
            	imageCircleViews[index].setBackgroundResource(R.drawable.dot_selected);
                
                if (index != i) {  
                	imageCircleViews[i].setBackgroundResource(R.drawable.dot_none);  
                }  
            }
        }  
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
//    	ActivityUtils.clearAll();
//    	imageCircleViews[pageIndex].setBackgroundResource(R.drawable.dot_selected);
    	System.out.println("**** onResume() *****") ;
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent();
		intent.setClass(getActivity(), NewsDetailActivity.class);
		intent.putExtra("title", title[position]);
		intent.putExtra("info", info[position]);
		intent.putExtra("date", date[position]);
		
		startActivity(intent);
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		Calendar calendar = Calendar.getInstance() ;
		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			RemoteNewsImageLoader newsImageLoader = new RemoteNewsImageLoader(getActivity()) ;
			newsImageLoader.saveNewsDetails() ;
			newsImageLoader.saveRemoteImage() ;
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here

			// Call onRefreshComplete when the list has been refreshed.
			initeViews() ;
			listItems.removeAll(listItems) ;
			initListview() ;
			listItemAdapter.notifyDataSetChanged() ;
			imagePageViews.removeAll(imagePageViews) ;
			for(int i = 0;i < length;i++){
				File f = new File(cache,"item"+(i+1)+".png");
				Bitmap map = BitmapFactory.decodeFile(f.getPath());
				imagePageViews.add(slideLayout.getSlideImageLayout(map));//�õ�����ͼƬ
			}
			viewPager.getAdapter().notifyDataSetChanged() ;
			
			Toast.makeText(getActivity(), "���³ɹ�...", Toast.LENGTH_SHORT).show() ;
			mPullRefreshScrollView.onRefreshComplete();
//			super.onPostExecute(result);
		}
	}
	
}
