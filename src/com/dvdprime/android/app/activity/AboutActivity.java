/**
 * Copyright (C) 2010 inHim. All rights reserved.
 *
 * This software and its documentation are confidential and proprietary
 * information of inHim.  No part of the software and
 * documents may be copied, reproduced, transmitted, translated, or reduced to
 * any electronic medium or machine-readable form without the prior written
 * consent of inHim.
 *
 * inHim makes no representations with respect to the contents,
 * and assumes no responsibility for any errors that might appear in the
 * software and documents. This publication and the contents here of are subject
 * to change without notice.
 */
package com.dvdprime.android.app.activity;

import java.util.Locale;

import com.dvdprime.android.app.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;

public class AboutActivity extends Activity {

	private WebView mHelpContentView;
	private static Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about_layout);
//		setTitle(getString(R.string.about));

		mHandler.postDelayed(new Runnable() {
			public void run() {
				loadPage();
			}
		}, 300);
	}

	private void loadPage() {
		Locale locale = Locale.getDefault();
		String language = locale.getLanguage();

		mHelpContentView = (WebView) findViewById(R.id.help_content);
		mHelpContentView.setVerticalScrollbarOverlay(true);
		mHelpContentView.setBackgroundColor(Color.BLACK);
		if (mHelpContentView != null) {
			if ("ko".equals(language)) {
				mHelpContentView.loadUrl("file:///android_asset/about.html");
			} else {
				mHelpContentView.loadUrl("file:///android_asset/about.html");
			}
		}
	}
}
