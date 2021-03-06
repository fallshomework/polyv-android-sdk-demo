package com.easefun.polyvsdk.demo;



import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tv.danmaku.ijk.media.player.IMediaPlayer;

import com.easefun.polyvsdk.ijk.IjkVideoView;
import com.easefun.polyvsdk.ijk.OnPreparedListener;
import com.easefun.polyvsdk.ijk.PolyvOnPreparedListener;


import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.SDKUtil;
import com.easefun.polyvsdk.Video;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class IjkVideoActicity extends Activity {
   private  static final String TAG = "IjkVideoActicity";
   IjkVideoView videoview;
   MediaController mediaController;
   ProgressBar progressBar;
   private WindowManager wm;
   float ratio;
   int w,h,adjusted_h;
   RelativeLayout rl,botlayout;
   private boolean isLandscape=false;
   private int stopPosition =0;
   private View view =null;
   private String path;
   private String vid;
   //boolean encrypt=false;
   private DBservice service;
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.video_small2);
 //------------------------------------------------------
    	Bundle e = getIntent().getExtras();
    	if (e != null) {
			path = e.getString("path");
			vid = e.getString("vid");
		}
		
		service=new DBservice(this);
    	wm = this.getWindowManager();
		w = wm.getDefaultDisplay().getWidth();
		h = wm.getDefaultDisplay().getHeight();
		//小窗口的比例
		ratio=(float)4/3;
		adjusted_h= (int)Math.ceil((float)w/ratio);
		rl = (RelativeLayout) findViewById(R.id.rl);
		botlayout=(RelativeLayout)findViewById(R.id.botlayout);
		rl.setLayoutParams(new RelativeLayout.LayoutParams(w, adjusted_h));
		videoview=(IjkVideoView)findViewById(R.id.videoview);
		progressBar =(ProgressBar)findViewById(R.id.loadingprogress);
		videoview.setMediaBufferingIndicator(progressBar); //在缓冲时出现的loading
		videoview.setVideoLayout(IjkVideoView.VIDEO_LAYOUT_SCALE);

		mediaController = new MediaController(this,false);//
		mediaController.setAnchorView(videoview);
		videoview.setMediaController(mediaController);
		if(path != null && path.length() > 0){
			progressBar.setVisibility(View.GONE);
			videoview.setVideoPath(path);
	        
		}else{
			//播放流畅，参数1
			videoview.setVid(vid,1);
		}
		
		
		
		videoview.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(IMediaPlayer mp) {
				// TODO Auto-generated method stub
				//do nothing
				//Log.i(TAG,"video prepared..");
				//videoview.setVideoLayout(IjkVideoView.VIDEO_LAYOUT_SCALE);

				
			}
		});
		videoview.setOnVideoStatusListener(new IjkVideoView.OnVideoStatusListener() {
			
			@Override
			public void onStatus(int status) {
				// TODO Auto-generated method stub
				//Log.i(TAG, " video status ->"+status);
			}
		});
		//设置切屏事件
		mediaController.setOnBoardChangeListener(new MediaController.OnBoardChangeListener() {
			
			@Override
			public void onPortrait() {
				// TODO Auto-generated method stub
				changeToLandscape();
			}
			
			@Override
			public void onLandscape() {
				// TODO Auto-generated method stub
				changeToPortrait();
			}
		});
		// 设置视频尺寸 ，在横屏下效果较明显
	   mediaController.setOnVideoChangeListener(new MediaController.OnVideoChangeListener() {
		
		@Override
		public void onVideoChange(int layout) {
			// TODO Auto-generated method stub
			videoview.setVideoLayout(layout);
			switch (layout) {
			case IjkVideoView.VIDEO_LAYOUT_ORIGIN:
				Toast.makeText(IjkVideoActicity.this, "VIDEO_LAYOUT_ORIGIN", 1).show();
				break;
            case IjkVideoView.VIDEO_LAYOUT_SCALE:
            	Toast.makeText(IjkVideoActicity.this, "VIDEO_LAYOUT_SCALE", 1).show();
				break;
            case IjkVideoView.VIDEO_LAYOUT_STRETCH:
            	Toast.makeText(IjkVideoActicity.this, "VIDEO_LAYOUT_STRETCH", 1).show();
	            break;
             case IjkVideoView.VIDEO_LAYOUT_ZOOM:
            	 Toast.makeText(IjkVideoActicity.this, "VIDEO_LAYOUT_ZOOM", 1).show();
	            break;
	            }
		}
	});
		//播放视频
		findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			    videoview.start();	
			}
		});
		findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			    videoview.pause();
			}
		});
        findViewById(R.id.swtichlevel).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("IjkVideoActivity","码率数----->"+videoview.getLevel());
				int df_num = videoview.getLevel();
				String[] items = null;
				if(df_num==1){
					items = new String[]{ "流畅" };
				}
				if(df_num==2){
					items = new String[]{ "流畅","高清" };
				}
				if(df_num==3){
					items = new String[]{ "流畅","高清","超清" };
				}
				
				final Builder selectDialog = new AlertDialog.Builder(
						IjkVideoActicity.this).setTitle("选择下载码率")
				// 数字2代表的是数组的下标
						.setSingleChoiceItems(items, 0,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {
										int bitrate = which + 1;
										videoview.switchLevel(bitrate);
										dialog.dismiss();
									}
								});
				selectDialog.show().setCanceledOnTouchOutside(true);
				
			}
		});
        
        /*findViewById(R.id.seekTo).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				videoview.seekTo(10*1000);
			}
		});*/
    }
	
	/*class VideoInfo extends AsyncTask<String,String,String>{
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			DownloadInfo downloadInfo=null;
			JSONArray jsonArray = SDKUtil.loadVideoInfo(params[0]);
			String vid = null;
			String duration = null;
			int filesize = 0;
			try {
				JSONObject jsonObject =jsonArray.getJSONObject(0);
				vid = jsonObject.getString("vid");
				duration = jsonObject.getString("duration");
				filesize = jsonObject.getInt("filesize1");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 downloadInfo = new DownloadInfo(vid, duration, filesize,1);
			 if(service!=null&&!service.isAdd(downloadInfo)){
				 service.addDownloadFile(downloadInfo);
			 }else{
				 runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						 Toast.makeText(IjkVideoActicity.this, "this video has been added !!", 1).show();
					}
				});
				
			 }
			return null;
		}
	}*/
	
	 class MyListener extends PolyvOnPreparedListener{

	  		public MyListener(String path, String videoId) {
	  			super(path, videoId);
	  		
	  		}
	      	 @Override
	      	public void onPrepared(IMediaPlayer mp) {
	      		// TODO Auto-generated method stub
	      		videoview.setVideoLayout(IjkVideoView.VIDEO_LAYOUT_SCALE);
	      	}
	  }

//	   切换到横屏
	public void changeToLandscape(){
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(h,w);
		rl.setLayoutParams(p);
		stopPosition = videoview.getCurrentPosition();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		botlayout.setVisibility(View.GONE);
		isLandscape = !isLandscape;
		//Log.i("vv","lanscape");
		//videoview.setVideoLayout(IjkVideoView.VIDEO_LAYOUT_SCALE);
		

	}
	
//	切换到竖屏
	public void changeToPortrait(){
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(w,adjusted_h);
		rl.setLayoutParams(p);
		stopPosition = videoview.getCurrentPosition();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		botlayout.setVisibility(View.VISIBLE);
		isLandscape = !isLandscape;
		//Log.i("vv","portrait");

	}
	
	
	// 配置文件设置congfigchange 切屏调用一次该方法，hide()之后再次show才会出现在正确位置
	@Override
		public void onConfigurationChanged(Configuration arg0) {
			// TODO Auto-generated method stub
			super.onConfigurationChanged(arg0);
			videoview.setVideoLayout(IjkVideoView.VIDEO_LAYOUT_SCALE);
			mediaController.hide();
		}
	
	   @Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			super.onBackPressed();
			if(videoview.getMediaPlayer()!=null) videoview.getMediaPlayer().release();
		}
	   
	   @Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
		}
}
