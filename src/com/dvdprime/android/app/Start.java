package com.dvdprime.android.app;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.activity.BaseActivity;
import com.dvdprime.android.app.activity.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class Start extends BaseActivity {
	
	Handler handler;
 
	public Start() {
		super(R.layout.start_layout);
	}
	
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
	protected final void onResume() {
		super.onResume();

		handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                startActivity(new Intent(Start.this, MainActivity.class));
                //overridePendingTransition(R.anim.fade, R.anim.hold);
                finish();
            }
        };
        
        handler.sendEmptyMessageDelayed(0, 1500);
	}
    
    @Override
    public void onBackPressed() {
    	if (handler != null) 
    		handler.removeMessages(0);
    	
    	super.onBackPressed();
    }
    
}