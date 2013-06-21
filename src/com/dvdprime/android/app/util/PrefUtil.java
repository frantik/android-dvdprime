package com.dvdprime.android.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import com.dvdprime.android.app.ContextHolder;

public class PrefUtil {

	private Context mContext;
	private static PrefUtil instance;
	private SharedPreferences prefs;

	/**
	 * 
	 * @param ctx
	 *            The application context.
	 */
	public PrefUtil() {
		mContext = ContextHolder.getInstance().getContext();
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public static synchronized PrefUtil getInstance() {
		if (instance == null) {
			instance = new PrefUtil();
		}
		return instance;
	}
	
	/**
	 * @return The underlying SharedPreferences object.
	 */
	public SharedPreferences getPreferences() {
		return prefs;
	}

	/**
	 * Set a boolean preference.
	 * 
	 * @param key
	 * @param value
	 * @return true if successful.
	 */
	public boolean setPreference(String key, long value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(key, value);
		return editor.commit();
	}

	/**
	 * Set a string preference.
	 * 
	 * @param key
	 * @param value
	 * @return true if successful.
	 */
	public boolean setPreference(String key, String value) {
		Map<String, String> prefs = new HashMap<String, String>(1);
		prefs.put(key, value);
		return setPreferences(prefs);
	}

	/**
	 * Sets a list of string preferences.
	 * 
	 * @param key
	 * @param value
	 * @return true if successful.
	 */
	public boolean setPreferences(Map<String, String> keyvals) {
		SharedPreferences.Editor editor = prefs.edit();
		for (String key : keyvals.keySet()) {
			editor.putString(key, keyvals.get(key));
		}
		return editor.commit();
	}

	/**
	 * Get a boolean preference.
	 * 
	 * @param key
	 *            The preference key.
	 * @param defValue
	 *            A default value, if there is no value for this key.
	 * @return the value for this key, or defValue if no key exists.
	 */
	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	/**
	 * Set a boolean preference.
	 * 
	 * @param key
	 * @param value
	 * @return true if successful.
	 */
	public boolean setBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}

	/**
	 * Get a string preference.
	 * 
	 * @param key
	 *            The preference key.
	 * @param defValue
	 *            A default value, if there is no value for this key.
	 * @return the value for this key, or defValue if no key exists.
	 */
	public String getString(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	/**
	 * Set a string preference.
	 * 
	 * @param key
	 * @param value
	 * @return true if successful.
	 */
	public boolean setString(String key, String value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	/**
	 * Get a long preference.
	 * 
	 * @param key
	 *            The preference key.
	 * @param defValue
	 *            A default value, if there is no value for this key.
	 * @return the value for this key, or defValue if no key exists.
	 */
	public long getLong(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}

	/**
	 * Set a long preference.
	 * 
	 * @param key
	 * @param value
	 * @return true if successful.
	 */
	public boolean setLong(String key, long value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(key, value);
		return editor.commit();
	}

	/**
	 * Get a integer preference.
	 * 
	 * @param key
	 *            The preference key.
	 * @param defValue
	 *            A default value, if there is no value for this key.
	 * @return the value for this key, or defValue if no key exists.
	 */
	public int getInt(String key, int defValue) {
		return prefs.getInt(key, defValue);
	}

	/**
	 * Set a integer preference.
	 * 
	 * @param key
	 * @param value
	 * @return true if successful.
	 */
	public boolean setInt(String key, int value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	/**
	 * Remove a preference.
	 * 
	 * @param name
	 *            The preference key.
	 */
	public void removePref(String name) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(name);
		editor.commit();
	}

	public void removeAllPreferences() {
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
	}

}
