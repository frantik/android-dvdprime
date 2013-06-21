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
package com.dvdprime.android.app.util;

import static android.content.Context.CONNECTIVITY_SERVICE;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.activity.TabBrowserActivity;
import com.dvdprime.android.app.constants.IntentKeys;
import com.dvdprime.android.app.constants.PreferenceKeys;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TabWidget;

/**
 * This class is the utility class.
 */
/**
 * @author Frantik
 *
 */
/**
 * @author Frantik
 *
 */
public final class DpUtil {

	private static final String HEX_CHARS = "0123456789abcdef";
	private static String[] mSytemDateFormat = { "MM-dd-yyyy", "dd-MM-yyyy", "yyyy-MM-dd" };
	private static String[] mDateFormat = { "MM/dd/yyyy", "dd/MM/yyyy", "yyyy/MM/dd" };
    static int sActiveTabIndex = -1;
	
//  private static int MINUTE = 60*1000;// 1분
//	private static int HOUR = 60*60*1000;// 1시간

    public static void activateTab(Activity a, int id) {
        Intent intent = new Intent(a, TabBrowserActivity.class);
        switch (id) {
            case R.id.communitytab:
                intent.putExtra(IntentKeys.TAB_ID, R.id.communitytab);
                break;
            case R.id.hardwaretab:
                intent.putExtra(IntentKeys.TAB_ID, R.id.hardwaretab);
                break;
            case R.id.softwaretab:
                intent.putExtra(IntentKeys.TAB_ID, R.id.softwaretab);
                break;
            case R.id.bluraytab:
                intent.putExtra(IntentKeys.TAB_ID, R.id.bluraytab);
                break;
            case R.id.smartphonetab:
                intent.putExtra(IntentKeys.TAB_ID, R.id.smartphonetab);
                break;
                // fall through and return
            default:
                return;
        }
        intent.putExtra("withtabs", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        a.startActivity(intent);
        a.finish();
        a.overridePendingTransition(0, 0);
    }

    public static boolean updateButtonBar(Activity a, int highlight) {
        final TabWidget ll = (TabWidget) a.findViewById(R.id.dptabbar);
        boolean withtabs = false;
        Intent intent = a.getIntent();
        if (intent != null) {
            withtabs = intent.getBooleanExtra("withtabs", false);
        }
        
        if (highlight == 0 || !withtabs) {
            ll.setVisibility(View.GONE);
            return withtabs;
        } else if (withtabs) {
            ll.setVisibility(View.VISIBLE);
        }
        for (int i = ll.getChildCount() - 1; i >= 0; i--) {
            
            View v = ll.getChildAt(i);
            boolean isActive = (v.getId() == highlight);
            if (isActive) {
                ll.setCurrentTab(i);
                sActiveTabIndex = i;
            }
            v.setTag(i);
            v.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        for (int i = 0; i < ll.getTabCount(); i++) {
                            if (ll.getChildTabViewAt(i) == v) {
                                ll.setCurrentTab(i);
                                processTabClick((Activity)ll.getContext(), v, ll.getChildAt(sActiveTabIndex).getId());
                                break;
                            }
                        }
                    }
                }});
            
            v.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    processTabClick((Activity)ll.getContext(), v, ll.getChildAt(sActiveTabIndex).getId());
                }});
        }
        return withtabs;
    }

    static void processTabClick(Activity a, View v, int current) {
        int id = v.getId();
        if (id == current) {
            return;
        }

        final TabWidget ll = (TabWidget) a.findViewById(R.id.dptabbar);
        ll.setCurrentTab((Integer) v.getTag());

        activateTab(a, id);
        PrefUtil.getInstance().setInt("activetab", id);
    }
    
    public static Cursor query(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder, int limit) {
        try {
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            if (limit > 0) {
                uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
         } catch (UnsupportedOperationException ex) {
            return null;
        }
        
    }

    public static Cursor query(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
    }


	/**
	 * @param dateType
	 * @param use24Hour
	 * @param time
	 * @return well-formed date with time, ex) 05/12/2010 14:10, 05/12/2010 9:10
	 *         PM
	 */
	public static String getTimeDate(String dateType, String use24Hour, long time)
	{
		// when factory reset, use24Hour seems null, so we init the value.
		if(dateType == null)
		{
			dateType = "dd-MM-yyyy";
		}
		
		if(use24Hour == null)
		{
			use24Hour = "24";
		}
		
		SimpleDateFormat formatter = getSettingDateFormat(dateType, use24Hour);
		return formatter.format(time);
	}

	/**
	 * @param dateType
	 * @param use24Hour
	 * @param time
	 * @return well-formed date, ex) 05/12/2010 12/05/2010
	 */
	public static String getDate(String dateType, long time)
	{
		SimpleDateFormat formatter = getSettingDateFormat(dateType, null);
		return formatter.format(time);
	}

	/**
	 * @param dateType
	 * @param use24Hour
	 * @param time
	 * @return SimpleDateFormate
	 */
	public static SimpleDateFormat getSettingDateFormat(String dateType, String use24Hour)
	{
		//Log.d(TAG, "[getSettingDateFormat] date type : " + dateType + ", use24Hour : " + use24Hour);

		String dateFormat = null;

		if (dateType == null)
		{
			dateFormat = mDateFormat[1];
		}
		else
		{
			if (dateType.compareTo(mSytemDateFormat[0]) == 0)
			{
				dateFormat = mDateFormat[0];
			}
			else if (dateType.compareTo(mSytemDateFormat[1]) == 0)
			{
				dateFormat = mDateFormat[1];
			}
			else if (dateType.compareTo(mSytemDateFormat[2]) == 0)
			{
				dateFormat = mDateFormat[2];
			}
			else
			{
				dateFormat = mDateFormat[1];
			}
		}

		if (use24Hour != null)
		{
			if (use24Hour.compareTo("12") == 0)
			{
				dateFormat += " " + "h:mm a";
			}
			else
			{
				dateFormat += " " + "HH:mm";
			}
		}

		//Log.d(TAG, "[getSettingDateFormat] parsed format : " + dateFormat);

		return new SimpleDateFormat(dateFormat);

	}

	private static void appendByteToHexString(StringBuffer outString, byte inByte) {
		// Append the first nibble and then append the second nibble sequentially
		outString.append(HEX_CHARS.charAt((inByte>>4)&0x0F)).append(HEX_CHARS.charAt(inByte&0x0F));
	}

	private static byte[] hexStringToBytes(String hexString) {

		if (hexString == null || hexString.length() <= 0)
			return null;

		int length = hexString.length() / 2;
		byte[] outBytes = new byte[length];

		for (int i = 0; i< length; i++) {
			outBytes[i] = Integer.valueOf(hexString.substring(i*2, i*2+2), 16).byteValue();
		}
		return outBytes;
	}

	private static String bytesToHexString(byte[] inBytes) {
		if (inBytes == null || inBytes.length <= 0)
			return null;

		StringBuffer outStringBuf = new StringBuffer(inBytes.length * 2);
		for (int i = 0; i < inBytes.length; i++) {
			appendByteToHexString(outStringBuf, inBytes[i]);
		}

		return outStringBuf.toString();
	}

	/**
	 * Gets SHA1 digest hexa string
	 * @param srcString 
	 * 		srcString
	 * @return SHA1 digest hexa string
	 */
	public static String getSHA1DigestHexString(String srcString) {

		MessageDigest digestSHA1;
		byte[] outBytes;
		try {
			digestSHA1 = MessageDigest.getInstance("SHA-1");
			digestSHA1.update(srcString.getBytes());
			outBytes = digestSHA1.digest();
		} catch  (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

		return bytesToHexString(outBytes);
	}

	/**
	 * Encrypts AES
	 * @param seed 
	 * 		seed
	 * @param plainText 
	 * 		plainText
	 * @return Encrypted AES
	 */
	public static String encryptAES(String seed, String plainText) {
		if (seed == null || seed.length() <= 0)
			return null;

		if (plainText == null || plainText.length() <= 0)
			return null;

		byte[] secretKeyBytes = getAESSecretKey(seed.getBytes());
		byte[] cipherTextBytes = encryptBytesByAES(secretKeyBytes, plainText.getBytes());
		return bytesToHexString(cipherTextBytes);
	}

	/**
	 * Decrypts AES
	 * @param seed 
	 * 		seed
	 * @param cipherText 
	 * 		cipherText
	 * @return Decrypted AES
	 */
	public static String decryptAES(String seed, String cipherText) {
		if (seed == null || seed.length() <= 0) {
			return null;
		}

		if (cipherText == null || cipherText.length() <= 0) {
			return null;
		}

		byte[] secretKeyBytes = getAESSecretKey(seed.getBytes());
		byte[] cipherTextBytes = hexStringToBytes(cipherText);
		byte[] plainTextBytes = decryptBytesByAES(secretKeyBytes, cipherTextBytes);

		if(plainTextBytes != null && plainTextBytes.length > 0) {
			return new String(plainTextBytes);
		} else {
			return null;
		}
	}

	private static byte[] getAESSecretKey(byte[] seed) {

		try {

			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			SecureRandom secureRamdom = SecureRandom.getInstance("SHA1PRNG");

			secureRamdom.setSeed(seed);
			keyGen.init(128, secureRamdom);
			SecretKey secretKey = keyGen.generateKey();

			byte[] secretKeyBytes = secretKey.getEncoded(); 
			return secretKeyBytes;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;		
	}

	private static byte[] encryptBytesByAES(byte[] secretKeyBytes, byte[] plainTextBytes) {

		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");
		Cipher cipher;
		byte[] outBytes = null;

		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			outBytes = cipher.doFinal(plainTextBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return outBytes;		
	}

	private static byte[] decryptBytesByAES(byte[] secretKeyBytes, byte[] cipherTextBytes) {
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "AES");
		Cipher cipher;
		byte[] outBytes = null;

		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			outBytes = cipher.doFinal(cipherTextBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

		return outBytes;		
	}

	/**
	 * Extracts port number.
	 * @param url 
	 * 		URL
	 * @return Port number
	 */
	public static int extractPortNumber(String url) {
		int portNum = 0;
		int contentPos = 0;

		// Get contentStart Pos.
		if ( (contentPos = url.indexOf("://")) != -1 ) {
			contentPos += "://".length();
		}

		int portStartPos = -1;
		if ( (portStartPos = url.indexOf(':', contentPos)) != -1 ) {
			portStartPos++;

			int portEndPos = -1;
			if ((portEndPos = url.indexOf('/', portStartPos)) != -1) {
				portNum = Integer.parseInt(url.substring(portStartPos, portEndPos));
			} else {
				portNum = Integer.parseInt(url.substring(portStartPos));
			}
		}

		return portNum;
	}

	/**
	 * Gets device information
	 * @param context 
	 * 		Context
	 * @return Device information
	 */
	public static String getDeviceInfo(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String MCCMNC = null;

		if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
			MCCMNC = tm.getSimOperator();	
		} else {
			Log.w("SNS", "DpUtil : getDeviceInfo() - SIM state is not ready!!" );
		}

		return MCCMNC;
	}

	public static String getICCID(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String ICCID = null;
		
		if (tm != null)
			ICCID = tm.getSimSerialNumber();

		return ICCID;
	}

	public static void invokeAssertion(String msg) {		
		// invoke RuntimeException intentionally
		throw new RuntimeException ("INTENTIONALLY OCCURRED EXCEPTION !!! : " + msg);	
	}
	
	/*
	 * Compute the sample size as a function of the image size and the target.
	 * Scale the image down so that both the width and height are just above
	 * the target.  If this means that one of the dimension goes from above
	 * the target to below the target (e.g. given a width of 480 and an image
	 * width of 600 but sample size of 2 -- i.e. new width 300 -- bump the
	 * sample size down by 1.
	 */
	public static int computeSampleSize(BitmapFactory.Options options, int target)
	{
		int w = options.outWidth;
		int h = options.outHeight;

		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);

		if (candidate == 0)
		{
			return 1;
		}

		if (candidate > 1)
		{
			if ((w > target) && (w / candidate) < target)
				candidate -= 1;
		}

		if (candidate > 1)
		{
			if ((h > target) && (h / candidate) < target)
				candidate -= 1;
		}

		//Log.d(TAG, "[computeSampleSize] candidate : " + candidate);

		return candidate;
	}

	public static void clearBitamp(Bitmap bm)
	{
		if(bm == null)
		{
			//Log.d(TAG, "[clearBitmap] Bitmap is null, no bitmap to clear");
			return;
		}
		
		//Log.d(TAG, "[clearBitmap] Bitmap cleared");
		bm.recycle();
	}

	public static void clearBitmaps(HashMap<String, Bitmap> hashmap)
	{
		if(hashmap == null)
		{
			//Log.d(TAG, "[clearBitmaps] hashMap is null, no bitmap to clear");
			return;
		}
		
		//Log.d(TAG, "[clearBitmaps] start, size = " + hashmap.size());
		
		Collection<Bitmap> bitmaps = hashmap.values();
		Iterator<Bitmap> i = bitmaps.iterator();
		while(i.hasNext())
		{
			Bitmap bm = i.next();
			bm.recycle();
		}		
		hashmap.clear();		

		//Log.d(TAG, "[clearBitmaps] end, size = " + hashmap.size());	
	}	

	
    public static Bitmap cropBitmap(Bitmap photo) {
        int sourceWidth = photo.getWidth();
        int sourceHeight = photo.getHeight();
        int sourceX = 0;
        int sourceY = 0;
        int destLength = 0;


        if(sourceHeight > sourceWidth)
        {
        	destLength = sourceWidth;
        	sourceY = 0;
        }
        else if(sourceWidth > sourceHeight)
        {
        	destLength = sourceHeight;
        	sourceX = (int)((sourceWidth - sourceHeight) / 2);
        }
        else 
        	destLength = sourceWidth;

        Bitmap cropedBitmap = Bitmap.createBitmap(photo, sourceX, sourceY, destLength, destLength);
//        Log.i(TAG, "called : cropBitmap(Bitmap bitmap) - " + photo.getClass().toString());

        return cropedBitmap;
    }

    /* 
     * Network
     */
    public static boolean isNetWorkConnected(Context context) {
        final ConnectivityManager connManager = (ConnectivityManager) 
                context.getSystemService(CONNECTIVITY_SERVICE);
        
        final NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    
	public static boolean checkNetwork(Context context){
		boolean isActiveNetwork;
        ConnectivityManager connectivityMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivityMgr.getActiveNetworkInfo();
        if(networkinfo != null){
        	isActiveNetwork = true;
        }
        else{
        	isActiveNetwork = false;
        }
        return isActiveNetwork;
	}
	
    /**
     * export URL information
     * @param msg
     * @return body exception <a> tag
     * 
     * exportURL("<a href='http://m.google.com'>http://m.google.com</a>") ==> http://m.google.com
     */
    
    /*
     * delete tag A
     */
    public static String deleteTagA(String msg) {
    	String tmpStr = null;
    	
    	if (StringUtil.contains(msg, "<a")) {
    		while (true) {
    			if (StringUtil.contains(msg, "<a")) {
    	    		tmpStr = StringUtil.substringBefore(msg, "<a");
    				msg = tmpStr + StringUtil.substringAfter(StringUtil.substringAfter(msg, "<a"), ">");
    				tmpStr = null;
    			} else {
    				if (StringUtil.contains(msg, "</a>"))
    					msg = StringUtil.replace(msg, "</a>", "");
    				
					break;
    			}
    		}
    	}
		return msg;
    }

    /*
     * delete tag B
     */
    public static String deleteTagB(String msg) {
    	
    	if (msg == null)
    		return msg;
    	
    	msg = StringUtil.replace(msg, "<b>", "");
    	msg = StringUtil.replace(msg, "</b>", "");

		return msg;
    }

    /*
     * delete tag BR
     */
    public static String deleteTagBR(String msg) {
    	
    	if (msg == null)
    		return msg;
    	
    	msg = StringUtil.replace(msg, "<br>", "");
    	msg = StringUtil.replace(msg, "<BR>", "");

		return msg;
    }

    /*
     * delete tag Img
     */
    public static String[] valueTagImg(String msg) {
    	String returnStr = "";
    	
    	if (StringUtil.contains(msg, "<img") || StringUtil.contains(msg, "<IMG")) {
    		do {
    			msg = StringUtil.trimToEmpty(StringUtil.substringAfter(msg, "src="));
	    		if (msg.length() > 0 && msg.charAt(0) == '\"')
	    			returnStr += StringUtil.substring(msg, 1, msg.indexOf("\"", 2)) + "|";
	    		else if (msg.length() > 0 && msg.charAt(0) == '\'')
	    			returnStr += StringUtil.substring(msg, 1, msg.indexOf("\'", 2)) + "|";
	    		else {
	    			if (msg.indexOf(".jpg") > 0)
	    				returnStr += StringUtil.substringBefore(msg, ".jpg") + ".jpg" + "|";
	    			else if (msg.indexOf(".JPG") > 0)
	    				returnStr += StringUtil.substringBefore(msg, ".JPG") + ".JPG" + "|";
	    			else if (msg.indexOf(".gif") > 0)
	    				returnStr += StringUtil.substringBefore(msg, ".gif") + ".gif" + "|";
	    			else if (msg.indexOf(".GIF") > 0)
	    				returnStr += StringUtil.substringBefore(msg, ".GIF") + ".GIF" + "|";
	    			else
	    				returnStr += StringUtil.substring(msg, 0, msg.indexOf(">", 2)) + "|";
	    		}
    		} while(StringUtil.contains(msg, "<img") || StringUtil.contains(msg, "<IMG"));
    	}
    	if (StringUtil.isNotEmpty(returnStr))
    		return StringUtil.split(StringUtil.substringBeforeLast(returnStr, "|"), '|');
    	else
    		return null;
    }
    
    /*
     * replace tag img
     */
    public static String replaceImg(String body) {
    	if (body == null)
    		return null;
    	
    	String[] images = valueTagImg(body);
    	if (images == null)
    		return body;
    	else {
        	String[] oldImg = new String[images.length];
        	
    		for (int i = 0; i < images.length; i++) {
    			String img = images[i];
    			int startIdx = body.lastIndexOf("<", body.indexOf(img));
    			int endIdx = body.indexOf(">", body.indexOf(img))+1;
    			oldImg[i] = StringUtil.substring(body, startIdx, endIdx);
    			images[i] = "<img src='"+img+"' width='100%'>";
    		}
    		body = StringUtil.replace(body, oldImg, images);
    	}
    	return body;
    }

    /*
     * replace URL information
     */
    public static String replaceURL(String body, String replaceURL) {
    	String tmpStr = null;
    	String replaceStr = null;
    	int fstIdx = 0;
    	int lstIdx = 0;
    	int count = 0;
    	
    	if (StringUtil.contains(body, "src=")) {
    		count = StringUtil.containCount(body, "src=");
    		for (int i = 0; i < count; i++) {
    			tmpStr = StringUtil.substringBefore(StringUtil.substringAfter(body, "http://"), " ");
    			tmpStr = "http://" + tmpStr;
    			fstIdx = body.indexOf("http://");
    			lstIdx = fstIdx + tmpStr.lastIndexOf('/');
    			tmpStr = StringUtil.substring(tmpStr, 0, lstIdx);
    			replaceStr = replaceURL + StringUtil.substringAfterLast(tmpStr, "/");
    			body   = StringUtil.replace(body, tmpStr, replaceStr);
    		}
    		Log.d("DP", "replaceURL : " + body);
    		return body;
    	} else {
    		return null;
    	}
    }
    
    /**
     * 자동 로그인 활성화 여부
     */
    public static boolean isAutoLoginEnabled() {
    	
    	PrefUtil prefs = PrefUtil.getInstance();
    	
    	if (StringUtil.isNotBlank(prefs.getString(PreferenceKeys.ACCOUNT_ID, null))
    			&& prefs.getBoolean(PreferenceKeys.AUTO_LOGIN_ENABLED, false))
    		return true;
    	else
    		return false;
    }
    
    /**
     * 최소 로그인 유지 시간으로 체크함.
     * @return
     */
    public static boolean isLogined() {
    	long durationTime = 1000 * 60 * 5; // 5분 간격으로 체크 함.
    	
    	long elapsedTime =  System.currentTimeMillis() - PrefUtil.getInstance().getLong(PreferenceKeys.ACCOUNT_TIME, 0);
    	
    	return (elapsedTime < durationTime);
    }

    /*
     * delete temporary files
     */
    public static void deleteReceivedFiles(Context mContext) {
		// delete received photo files
		String filesDir = mContext.getFilesDir().getAbsolutePath() + File.separator + "ReceivedFiles";
		File directory = new File(filesDir); 
		if (directory.exists() && directory.isDirectory()) { 
    		Log.d("DP", "DpUtil.deleteReceivedFiles : " + filesDir);
			deleteDir(directory);
		} 
    }

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
					return false;
			}
		} // The directory is now empty so delete it return dir.delete();
		return dir.delete();
	}
}	

