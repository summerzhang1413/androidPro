package com.ifox.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.xmlpull.v1.XmlPullParser;

import android.graphics.drawable.Drawable;
import android.util.Xml;

import com.ifox.entity.News;
import com.ifox.main.R;
import com.ifox.util.FileAccess;


/**
 * �������������б�
 * @Description: �������������б�����ֻ�Ǹ�ʾ��������ز���ʵ�֡�

 * @File: NewsXmlParser.java

 * @Package com.image.indicator.parser

 * @Author Hanyonglu

 * @Date 2012-6-18 ����02:31:26

 * @Version V1.0
 */
public class NewsXmlParser {
	// ͼƬ�ĵ�ַ��������Դӷ�������ȡ
    private String[] urls = new String[]{
             
            "http://www.baidu.com/img/baidu_sylogo1.gif",
            "http://www.baidu.com/img/baidu_sylogo1.gif",
            "http://www.baidu.com/img/baidu_sylogo1.gif",
            "http://www.baidu.com/img/baidu_sylogo1.gif",
            "http://www.baidu.com/img/baidu_sylogo1.gif"
     
    };
	private Drawable imageDrawable[] = new Drawable[5] ;
	 
	
	// �����б�
	private List<HashMap<String, News>> newsList = null;
	
	// ����ͼƬ�ļ��ϣ��������ó��˹̶����أ���ȻҲ�ɶ�̬���ء�
	private int[] slideImages = {
			R.drawable.p3,
			R.drawable.p5,
			R.drawable.p0,
			R.drawable.p6,
			R.drawable.p3
	};
	
	// ��������ļ���
	private int[] slideTitles = {
			R.string.title1,
			R.string.title2,
			R.string.title3,
			R.string.title4,
			R.string.title5,
	};
	
	// �������ӵļ���
	private String[] slideUrls = {
			"http://mobile.csdn.net/a/20120616/2806676.html",
			"http://cloud.csdn.net/a/20120614/2806646.html",
			"http://mobile.csdn.net/a/20120613/2806603.html",
			"http://news.csdn.net/a/20120612/2806565.html",
			"http://mobile.csdn.net/a/20120615/2806659.html",
	};
	
	public int[] getSlideImages(){
		return slideImages;
	}
	
	public int[] getSlideTitles(){
		return slideTitles;
	}
	
	public String[] getSlideUrls(){
		return slideUrls;
	}
	public NewsXmlParser(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i = 0; i<5; i++){
					imageDrawable[i] = loadImageFromUrl( urls[i] ) ;
				}
			}
			
			}).start() ;
		
	                
	}
	/**
     * ����ͼƬ  ��ע��HttpClient ��httpUrlConnection������
     */
    public Drawable loadImageFromUrl(String url) {

        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000*15);
            HttpGet get = new HttpGet(url);
            HttpResponse response;

            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();

                Drawable d = Drawable.createFromStream(entity.getContent(),
                        "src");

                return d;
            } else {
                return null;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
	/**
	 * ��ȡXmlPullParser����
	 * @param result
	 * @return
	 */
	private XmlPullParser getXmlPullParser(String result){
		XmlPullParser parser = Xml.newPullParser();
		InputStream inputStream = FileAccess.String2InputStream(result);
		
		try {
			parser.setInput(inputStream, "UTF-8");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return parser;
	}
	
	public int getNewsListCount(String result){
		int count = -1;
		
		try {
			XmlPullParser parser = getXmlPullParser(result);
	        int event = parser.getEventType();//������һ���¼�
	        
	        while(event != XmlPullParser.END_DOCUMENT){
	        	switch(event){
	        	case XmlPullParser.START_DOCUMENT:
	        		break;
	        	case XmlPullParser.START_TAG://�жϵ�ǰ�¼��Ƿ��Ǳ�ǩԪ�ؿ�ʼ�¼�
	        		if("count".equals(parser.getName())){//�жϿ�ʼ��ǩԪ���Ƿ���count
	        			count = Integer.parseInt(parser.nextText());
	                }
	        		
	        		break;
	        	case XmlPullParser.END_TAG://�жϵ�ǰ�¼��Ƿ��Ǳ�ǩԪ�ؽ����¼�
//	        		if("count".equals(parser.getName())){//�жϿ�ʼ��ǩԪ���Ƿ���count
//	        			count = Integer.parseInt(parser.nextText());
//	                }
	        		
	        		break;
	        	}
            
	        	event = parser.next();//������һ��Ԫ�ز�������Ӧ�¼�
	        }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		// �޷���ֵ���򷵻�-1
		return count;
	}
}
