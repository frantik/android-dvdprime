package com.dvdprime.android.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.dvdprime.android.app.R;
import com.dvdprime.android.app.constants.Const;

/**
 * @author Frantik
 * 
 */
public class SystemUtil {

	private static int currentApi = 0;

	public static int getApiLevel() {

		if (currentApi > 0) {
			return currentApi;
		}

		if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.CUPCAKE) {
			currentApi = 3;
		} else {
			try {
				Field f = android.os.Build.VERSION.class
						.getDeclaredField("SDK_INT");
				currentApi = (Integer) f.get(null);
			} catch (Exception e) {
				return 0;
			}
		}

		return currentApi;
	}

	public static boolean isCompatible(int apiLevel) {
		return getApiLevel() >= apiLevel;
	}

	/**
	 * Ư�� ������ �̵�� ��ĳ��
	 */
	public static class MediaScannerNotifier implements MediaScannerConnectionClient {
		@SuppressWarnings("unused")
		private Context mContext; 
	    private MediaScannerConnection mConnection; 
	    private String mPath; 
	    private String mMimeType; 
	    
	    public MediaScannerNotifier(Context context, String path, String 
	    		mimeType) { 
	    	mContext = context;
	    	mPath = path;
	    	mMimeType = mimeType;
	    	mConnection = new MediaScannerConnection(context, this);
	    	mConnection.connect(); 
	    }
	    
		@Override
		public void onMediaScannerConnected() {
			mConnection.scanFile(mPath, mMimeType);
		}

		@Override
		public void onScanCompleted(String path, Uri uri) {
			// OPTIONAL: scan is complete, this will cause the viewer to render it 
			try {
				/*
				if (uri != null) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(uri);
					mContext.startActivity(intent);
				}
				*/
			} finally {
				mConnection.disconnect();
				mContext = null;
			}
		}
	}

	/**
	 * ���� ��Ʈ��ũ ��� ���� ����
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) 
        	return false;
        else {
            final NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
	}

	/**
	 * ���� �޸� ���� ���� ����
	 * 
	 * @return
	 */
	public static boolean isExternalWritable() {
		File sdDir = new File(Const.SDCARD_DIRECTORY);
		if (sdDir.exists() && sdDir.canWrite())
			return true;
		else
			return false;
	}

	/**
	 * ĳ�� ��θ� ��ȯ�Ѵ�.
	 * 
	 * @param mContext
	 * @return
	 */
	public static File getCacheDir(Context mContext) {
		File cacheDir;

		if (isExternalWritable()) {
			cacheDir = Const.CACHE_DIR;
			if (!cacheDir.exists())
				cacheDir.mkdirs();
		} else
			cacheDir = mContext.getCacheDir();

		return cacheDir;
	}

	/**
	 * Clipboard�� ������ �ؽ�Ʈ ����
	 * 
	 * @param mContext
	 * @param s
	 */
	@SuppressWarnings("deprecation")
	public static void copyToClipboard(Context mContext, String s) {
		ClipboardManager clipboard = (ClipboardManager) mContext
				.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText(s);
		Toast.makeText(mContext, R.string.msg_clipboard_copy_success,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Clipboard�� ����� �ؽ�Ʈ ��������
	 * 
	 * @param mContext
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String pasteFromClipboard(Context mContext) {
		ClipboardManager clipboard = (ClipboardManager) mContext
				.getSystemService(Context.CLIPBOARD_SERVICE);

		if (clipboard.hasText())
			return clipboard.getText().toString();

		return "";
	}

	/**
	 * ���� �������� ���μ��� ��Ű�� ���
	 * 
	 * @param mContext
	 * @return
	 */
	public static List<String> getRunningActivity(Context mContext) {
		List<String> activePackage = new ArrayList<String>();

		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> proceses = am.getRunningAppProcesses();

		// ���μ��� ��ü�� �ݺ�
		for (RunningAppProcessInfo process : proceses) {
			if (process.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				activePackage.add(process.processName); // package�̸��� ������.
			}
		}
		return activePackage;
	}

	/**
	 * ���� ȭ�鿡 �������� �ֻ��� ��Ƽ��Ƽ Ŭ������
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getRunningTopActivity(Context mContext) {

		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> task = am.getRunningTasks(1);
		ComponentName topActivity = task.get(0).topActivity;

		return topActivity.getClassName();
	}

	/**
	 * .zip ���� ���� (�ҽ��� ����� ��Ʈ �������� �����Ǿ� ����)
	 * 
	 * @param unZipFile
	 * @throws IOException
	 */
	public static void unZip(File unZipFile) throws IOException {
		ZipInputStream in = new ZipInputStream(new FileInputStream(unZipFile));
		ZipEntry ze;

		if (unZipFile.exists()) {
			while ((ze = in.getNextEntry()) != null) {
				String path = unZipFile.getAbsolutePath();
				path = StringUtil.replace(path, ".zip", ".ttf");

				if (ze.getName().indexOf("/") != -1) {
					File parent = new File(path).getParentFile();
					if (!parent.exists()) {
						if (!parent.mkdirs())
							throw new IOException("Unable to create folder"
									+ parent);
					}
				}

				FileOutputStream out = new FileOutputStream(path);

				byte[] buf = new byte[1024];

				for (int nReadSize = in.read(buf); nReadSize != -1; nReadSize = in
						.read(buf)) {
					out.write(buf, 0, nReadSize);
				}
				out.close();
			}
			in.close();

			// delete zip file
			unZipFile.delete();
		}
	}

	/**
	 * �ܸ��� ����ȣ �˾ƿ���
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getPhoneNumber(Context mContext) {
		TelephonyManager tm = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		
		String phoneNumber = StringUtil.defaultIfBlank(tm.getLine1Number());
		phoneNumber = StringUtil.removeString(phoneNumber);
		if (StringUtil.startsWith(phoneNumber, "821")) { // ���ѹα� ������ȣ ���Խ�
			phoneNumber = "01" + StringUtil.substringAfter(phoneNumber, "821");
		}

		return phoneNumber;
	}

	/**
	 * �︮�� ���� ������ ���� ����
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * �ܸ��� ���� ID
	 * @return
	 */
	public static String getDeviceId() {
		// Returns the unique device ID, for example, 
		// the IMEI for GSM and the MEID for CDMA phones. 
		// Return null if device ID is not available.
		// Requires Permission: READ_PHONE_STATE
		
//		String id = TelephonyManager.getDeviceId();
		return null; 
	}
	
	public static String getMacAddress(Context mContext) {
		// Requires Permission: ACCESS_WIFI_STATE
		WifiManager wm = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wm.getConnectionInfo();
		
		return wifiInfo.getMacAddress();  
	}
}
