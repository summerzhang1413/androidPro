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
	private ArrayList<HashMap<String, Object>> listItems ;    //存放文字、图片信息  
    private SimpleAdapter listItemAdapter;                  //适配器     
	// 滑动图片的集合
	private ArrayList<View> imagePageViews = null;
	private ViewGroup main = null;
	private ViewPager viewPager = null;
	// 当前ViewPager索引
	private int pageIndex = 0; 
	// 包含圆点图片的View
	private ViewGroup imageCircleView = null;
	private ImageView[] imageCircleViews = null; 
	// 滑动标题
	private TextView tvSlideTitle = null;
	// 布局设置类
	private SlideImageLayout slideLayout = null;
	// 数据解析类
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
		//初始化上方的图片滑动区域
		initeViews() ;
		tvSlideTitle.setText(imagetitle[0]);
        viewPager.setAdapter(slideImageAdapter);  
        viewPager.setCurrentItem(pageIndex) ;
        viewPager.setOnPageChangeListener(imagePageChangeListener) ;
        System.out.println("********* onCreateView() **********") ;
		//初始化下方的新闻区域
  		initListview();
  		//生成适配器的Item和动态数组对应的元素     
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
		// 滑动图片区域
		imagePageViews = new ArrayList<View>();
		
		//LayoutInflater inflater = getLayoutInflater();  
		main = (ViewGroup)inflater.inflate(R.layout.news_fragment, null);
		viewPager = (ViewPager) main.findViewById(R.id.image_slide_page);  //得到ViewPager
		mPullRefreshScrollView=(com.handmark.pulltorefresh.library.PullToRefreshScrollView)main.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			NetworkConnected nc = new NetworkConnected(getActivity()) ;
			boolean isConnected = nc.isNetworkConnected() ;
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if(isConnected){
					new GetDataTask().execute();
				}else{
					Toast.makeText(getActivity(), "请检查网络!!", Toast.LENGTH_SHORT).show() ;
					mPullRefreshScrollView.onRefreshComplete();
				}
			}
		});
		mScrollView = mPullRefreshScrollView.getRefreshableView();
		
		//scroll.setOnTouchListener(new ImageOnTouchListener()) ;
		// 圆点图片区域
		parser = new NewsXmlParser(); 
		length = parser.getSlideImages().length;
//		int length = ids.length ;
		imageCircleViews = new ImageView[length];//实例化圆点图片 
		imageCircleView = (ViewGroup) main.findViewById(R.id.layout_circle_images);
		slideLayout = new SlideImageLayout(getActivity());
		slideLayout.setCircleImageLayout(length);
		// 不需要加载网络图片,直接在内部缓存读就可以了
		cache = new File(getActivity().getFilesDir(),"image_cache");
		// 设置默认的滑动标题
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
			imagePageViews.add(slideLayout.getSlideImageLayout(map));//得到滚动图片
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

	//加载新闻列表内容
	private void initListview(){
		for(int i=0;i<6;i++){     
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle" , title[i]);
			map.put("ItemContent" , info[i]);
			listItems.add(map);     
		}     
        
	}
	
	
	// 滑动图片数据适配器
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
   
    
    // 滑动页面更改事件监听器
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
				imagePageViews.add(slideLayout.getSlideImageLayout(map));//得到滚动图片
			}
			viewPager.getAdapter().notifyDataSetChanged() ;
			
			Toast.makeText(getActivity(), "更新成功...", Toast.LENGTH_SHORT).show() ;
			mPullRefreshScrollView.onRefreshComplete();
//			super.onPostExecute(result);
		}
	}
	
}
