package com.example.gridviewaddbitmap;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.gridviewaddbitmap.DragGridView.OnChanageListener;
import com.example.gridviewaddbitmap.DraggableGridViewPager.OnPageChangeListener;
import com.example.gridviewaddbitmap.DraggableGridViewPager.OnRearrangeListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
/**
 * 
* @ClassName: MainActivity
* @Description: 
* @author yangbofeng
* @date 2017-3-22
* @see MainActivity
* 
* item 拖拽排序 的实现, 重写gridview,直接可以用.
 */

public class MainActivity extends Activity {

	private List<String> fileArray = new ArrayList<String>();
	private ImageAdapter mImageAdapter;

	private Map<String, Bitmap> mMapImgs = new HashMap<String, Bitmap>();
	private List<String> mListPaths = new ArrayList<String>();

	

	DragGridView gridview;
	int mCounter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AsyncLoadedImage mAsyncLoadedImage = new AsyncLoadedImage();
		mAsyncLoadedImage.execute();
		Log.i("Async", " success");
		
		mImageAdapter = new ImageAdapter(MainActivity.this);
		gridview = (DragGridView) findViewById(R.id.gr_image);
		gridview.setAdapter(mImageAdapter);
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Log.e("TAG", "position="+position);
			}
		});
		
		gridview.setOnChangeListener(new OnChanageListener() {
			
			@Override
			public void onChange(int from, int to) {
				// TODO Auto-generated method stub
					

					if(from < to){
						for(int i=from; i<to; i++){
							Collections.swap(mListPaths, i, i+1);
						}
					}else if(from > to){
						for(int i=from; i>to; i--){
							Collections.swap(mListPaths, i, i-1);
						}
					}
					
					//mListPaths.set(to,from );
					mListPaths.set(to,mListPaths.get(to));
					
					mImageAdapter.notifyDataSetChanged();
					
					
				
			}
		});
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	Handler mAsyncHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				mImageAdapter.refreshDatas();
			}
		}
	};

	class AsyncLoadedImage extends AsyncTask<Object, Bitmap, Object> {

		private void loadLogos(String root) {
			File rootDir = new File(root);
			if (rootDir.exists()) {
				File[] files = rootDir.listFiles();
				int flagIdx = 0;
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						String path = files[i].getPath();
						Bitmap cover = getBitmap(path);
						if (cover != null) {
							mMapImgs.put(path, getBitmap(path));
							mListPaths.add(path);
						}
					}
					flagIdx++;

					// Refresh
					if (mListPaths.size() % 3 == 0 || flagIdx == (files.length - 1)) {
						Message mMessage = mAsyncHandler.obtainMessage();
						mMessage.what = 1;
						mAsyncHandler.sendMessage(mMessage);
					}
				}
			}
			mAsyncHandler.sendEmptyMessage(1);
		}

		@Override
		protected Object doInBackground(Object... params) {
			Log.i("Async", "fileArray new success");
			
			String path="/storage/sdcard1/videocatch";			
				loadLogos(path);
			return null;
		}

		@Override
		public void onProgressUpdate(Bitmap... value) {
			Log.e("Async", "onProgressUpdate:wxp addImage");
		}

		@Override
		protected void onPostExecute(Object result) {
		}
	}
	
	
	
	
	
	
	

	private Bitmap getBitmap(String path) {
		Bitmap cover = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			cover = ThumbnailUtils.extractThumbnail(bitmap, 450, 300);
			bitmap.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cover;
	}

	
	
	
	
	
	
	
	
	
	
	class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;
		

		public ImageAdapter (Context c) {
			mContext = c;
			mInflater = LayoutInflater.from(c);
		}

		public void remove(String item) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() {
			return mListPaths.size();
		}

		public void refreshDatas() {
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.griditem, parent, false);
				holder = new ViewHolder();
				holder.imageview = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			loadSquare(mListPaths.get(position), holder.imageview);
			return convertView;
		}

		private final class ViewHolder {
			public ImageView imageview;
		}
	
		
	}
	
	
	
	
	
	
	

	private void loadSquare(String path, ImageView iv) {
		iv.setImageBitmap(mMapImgs.get(path));
	}
}
