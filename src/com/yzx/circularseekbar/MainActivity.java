package com.yzx.circularseekbar;



import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;

import com.yzx.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener;

public class MainActivity extends Activity{
	private SharedPreferences sp;
	
	private AlertDialog dialog;
	private MyAlertDialog myDialog;
	
	private Timer mTimer;
    private TimerTask mTimerTask;
    
    private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				dialog.dismiss();
				break;
			case 2:
				myDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void click(View v){
		//Toast.makeText(this, "haha", Toast.LENGTH_LONG).show();
		switch (v.getId()) {
		case R.id.btn1:
			sp = getSharedPreferences("brightness", MODE_PRIVATE);
			showBrightnessDialog();
			break;
		
		case R.id.btn2:
			sp = getSharedPreferences("brightness", MODE_PRIVATE);
			showMyBrightnessDialog();
			break;

		default:
			break;
		}
	}

	private void showMyBrightnessDialog() {
		myDialog = new MyAlertDialog(MainActivity.this);
		//设置为没有title
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.show();
		
		//使用Timer来实现3秒自动关闭dialog
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
            @Override
            public void run() {
            	Message message = new Message();      
            	message.what = 2;      
            	handler.sendMessage(message);
            }
        };
        //开始一个定时任务
        mTimer.schedule(mTimerTask, 3000);
	}
	
	//重写Dialog，实现每次dismiss时终止定时器
	class MyAlertDialog extends Dialog{
		
		protected MyAlertDialog(Context context) {
			super(context);
		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.brightness_dialog);
			CircularSeekBar seekbar = (CircularSeekBar) findViewById(R.id.circularSeekBar1);
			int brightnessValue = sp.getInt("customBrightnessValue", 45);
			seekbar.setProgress(brightnessValue);
			//注册监听事件
			seekbar.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {
				
				@Override
				public void onProgressChanged(CircularSeekBar circularSeekBar,
						int progress, boolean fromUser) {
					//在此处修改亮度
					System.out.println("circularSeekBar's progress is :"+progress);
				}
				
				@Override
				public void onStopTrackingTouch(CircularSeekBar seekBar) {
					mTimer = new Timer();
					mTimerTask = new TimerTask() {
			            @Override
			            public void run() {
			            	Message message = new Message();      
			            	message.what = 2;      
			            	handler.sendMessage(message);
			            }
			        };
			        //开始一个定时任务
			        mTimer.schedule(mTimerTask, 3000);
			        
			        Editor editor = sp.edit();
					editor.putInt("customBrightnessValue", seekBar.getProgress());
					editor.commit();
				}
				
				@Override
				public void onStartTrackingTouch(CircularSeekBar seekBar) {
					mTimer.cancel();
				}
				
			});
		}
		
		@Override
		public void dismiss() {
			// 终止所有的定时器
			mTimer.cancel();
			System.out.println("circularSeekBar is auto close.");
			super.dismiss();
		}
	}

	private void showBrightnessDialog() {
		
		//使用Timer来实现3秒自动关闭dialog
		mTimer = new Timer();
		mTimerTask = new TimerTask() {
            @Override
            public void run() {
            	Message message = new Message();      
            	message.what = 1;      
            	handler.sendMessage(message);
            }
        };
        //开始一个定时任务
        mTimer.schedule(mTimerTask, 3000);
		
		AlertDialog.Builder builder = new Builder(this);
		View contentView = View.inflate(this, R.layout.brightness_dialog, null);
		CircularSeekBar seekbar = (CircularSeekBar) contentView.findViewById(R.id.circularSeekBar1);
		int brightnessValue = sp.getInt("brightnessValue", 45);
		seekbar.setProgress(brightnessValue);
		//注册监听事件
		seekbar.setOnSeekBarChangeListener(new OnCircularSeekBarChangeListener() {
			
			@Override
			public void onProgressChanged(CircularSeekBar circularSeekBar,
					int progress, boolean fromUser) {
				//在此处修改亮度
				System.out.println("circularSeekBar:"+progress);
			}
			
			@Override
			public void onStopTrackingTouch(CircularSeekBar seekBar) {
				// TODO Auto-generated method stub
				mTimer = new Timer();
				mTimerTask = new TimerTask() {
		            @Override
		            public void run() {
		            	Message message = new Message();      
		            	message.what = 1;      
		            	handler.sendMessage(message);
		            }
		        };
		        //开始一个定时任务
		        mTimer.schedule(mTimerTask, 3000);
		        
		        Editor editor = sp.edit();
				editor.putInt("brightnessValue", seekBar.getProgress());
				editor.commit();
			}
			
			@Override
			public void onStartTrackingTouch(CircularSeekBar seekBar) {
				// TODO Auto-generated method stub
				mTimer.cancel();
			}
		});
		dialog = builder.create();
		dialog.setView(contentView,0,0,0,0);
		dialog.show();
		
		//Handle实现过3秒自动关闭dialog。自动关闭对话框的功能主要使用Handler对象来实现，该对象的postDelayed方法用来实现延时多少秒去执行某个任务。
		//有Bug，无论是否操作，Dialog过三秒都会关闭
		/*Handler handler = new Handler();  
        handler.postDelayed(new Runnable() {
 
            public void run() {  
            	dialog.dismiss();  
            }  
        }, 3000);*/
		
	}
}
