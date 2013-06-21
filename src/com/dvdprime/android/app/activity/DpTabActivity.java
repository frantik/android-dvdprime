package com.dvdprime.android.app.activity;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.util.DpUtil;
import com.dvdprime.android.app.util.PrefUtil;

import android.app.Activity;
import android.os.Bundle;

/**
 * 게시판 목록 보기 탭
 * 
 * @author Kwang-myung,Choi (frantik@gmail.com)
 */
public class DpTabActivity extends Activity {
	
	public DpTabActivity() {
		super();
	}
	
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        int activeTab = PrefUtil.getInstance().getInt("activetab", R.id.communitytab);
        
        if (activeTab != R.id.communitytab
                && activeTab != R.id.hardwaretab
                && activeTab != R.id.softwaretab
                && activeTab != R.id.bluraytab
                && activeTab != R.id.smartphonetab) {
            activeTab = R.id.communitytab;
        }
        DpUtil.activateTab(this, activeTab);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
