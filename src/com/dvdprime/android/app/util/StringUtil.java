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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Class Name : StringUtil.java
 * @Description : String Utility
 * @Modification Information
 *
 *  @since 2009.10.21
 *  @version 1.0
 *  @see
 *  
 */
public class StringUtil {

	public static final String EMPTY = ""; //$NON-NLS-1$
	public static final String NULL = "null"; //$NON-NLS-1$
	public static final char[] WORD_SEPARATORS = {'_', '-', '@', '$', '#', ' '};
	public static final int INDEX_NOT_FOUND = -1;

	
	private StringUtil() {
	}
	
	/**
	 * <p>문자(char)가 단어 구분자('_', '-', '@', '$', '#', ' ')인지 판단한다.</p>
	 * 
	 * @param c 문자(char)
	 * @return 단어 구분자이면 true, 아니면 false를 반환한다.
	 */
	public static boolean isWordSeparator(char c) {
		for (int i = 0; i < WORD_SEPARATORS.length; i++) {
			if (WORD_SEPARATORS[i] == c) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <p>문자(char)가 단어 구분자('_', '-', '@', '$', '#', ' ')인지 판단한다.</p>
	 * 
	 * @param c 문자(char)
	 * @return 단어 구분자이면 true, 아니면 false를 반환한다.
	 */
	public static boolean isWordSeparator(char c, char[] wordSeparators) {
		if (wordSeparators == null) {
			return false;
		}
		for (int i = 0; i < wordSeparators.length; i++) {
			if (wordSeparators[i] == c) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * <p>문자열(String)을 카멜표기법으로 표현한다.</p>
	 * 
	 * <pre>
	 * StringUtil.camelString("ITEM_CODE", true)  = "ItemCode"
	 * StringUtil.camelString("ITEM_CODE", false) = "itemCode"
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param firstCharacterUppercase 첫문자열을 대문자로 할지 여부
	 * @return 카멜표기법으로 표현환 문자열
	 */
	public static String camelString(String str, boolean firstCharacterUppercase) {
		if (str == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		
		boolean nextUpperCase = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			
			if (isWordSeparator(c)) {
				if (sb.length() > 0) {
					nextUpperCase = true;
				}
			} else {
				if (nextUpperCase) {
					sb.append(Character.toUpperCase(c));
					nextUpperCase = false;
				} else {
					sb.append(Character.toLowerCase(c));
				}
			}
		}
		
		if (firstCharacterUppercase) {
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		}
		return sb.toString();
	}
	
	/**
	 * <p>입력 받은 문자를 반복숫자만큼 붙여서 만든다.</p>
	 * 
	 * <pre>
	 * StringUtil.repeat(null, *)   = null
	 * StringUtil.repeat("", -1)    = ""
	 * StringUtil.repeat("", 2)     = ""
	 * StringUtil.repeat("han", -1) = ""
	 * StringUtil.repeat("han", 0)  = ""
	 * StringUtil.repeat("han", 2)  = "hanhan"
	 * </pre>
	 * 
	 * @param str
	 * @param repeat 반복숫자
	 * @return
	 */
	public static String repeat(String str, int repeat) {
		if (str == null) {
			return null;
		}
		if (repeat < 1) {
			return EMPTY;
		}
		int inputLen = str.length();
		if (inputLen == 0 || repeat == 1) {
			return str;
		}
		int outputLen = inputLen * repeat;
		if (inputLen == 1) {
			char ch = str.charAt(0);
			char[] output = new char[outputLen];
			for (int i = 0; i < outputLen; i++) {
				output[i] = ch;
			}
			return new String(output);
		} else {
			StringBuilder output = new StringBuilder((int) Math.min((outputLen*110L)/100, Integer.MAX_VALUE));
			for (int i = 0; i < repeat; i++) {
				output.append(str);
			}
			return output.toString();
		}
	}
	
	// ----------------------------------------------------------------------
	// 공백/여백문자  검사, 제거, 치환
	// ----------------------------------------------------------------------
	
	/**
	 * <p>문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이거나 <code>null</code>인 검사한다.</p>
	 * 
	 * <pre>
	 * StringUtil.isBlank(null)    = true
	 * StringUtil.isBlank("")      = true
	 * StringUtil.isBlank("   ")   = true
	 * StringUtil.isBlank("han")   = false
	 * StringUtil.isBlank(" han ") = false
	 * </pre> 
	 * 
	 * @param str 문자열
	 * @return
	 */
	public static boolean isBlank(String str) {
		if (str == null) {
			return true;
		}
		int strLen = str.length();
		if (strLen > 0) {
			for (int i = 0; i < strLen; i++) {
				if (Character.isWhitespace(str.charAt(i)) == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * <p>문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이 아니거나 <code>null</code>이 아닌지 검사한다.</p>
	 * 
	 * <pre>
	 * StringUtil.isNotBlank(null)    = false
	 * StringUtil.isNotBlank("")      = false
	 * StringUtil.isNotBlank("   ")   = false
	 * StringUtil.isNotBlank("han")   = true
	 * StringUtil.isNotBlank(" han ") = true
	 * </pre> 
	 * 
	 * @param str 문자열
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	/**
	 * <p>문자열(String)이 공백("")이거나 <code>null</code>인 검사한다.</p>
	 * 
	 * <pre>
	 * StringUtil.isEmpty(null)    = true
	 * StringUtil.isEmpty("")      = true
	 * StringUtil.isEmpty("   ")   = false
	 * StringUtil.isEmpty("han")   = false
	 * StringUtil.isEmpty(" han ") = false
	 * </pre>
	 * 
	 * 
	 * @param str 검사할 문자열
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	/**
	 * <p>문자열(String)이 공백("")이 아니거나 <code>null</code>이 아닌지 검사한다.</p>
	 * 
	 * <pre>
	 * StringUtil.isNotEmpty(null)    = false
	 * StringUtil.isNotEmpty("")      = false
	 * StringUtil.isNotEmpty("   ")   = true
	 * StringUtil.isNotEmpty("han")   = true
	 * StringUtil.isNotEmpty(" han ") = true
	 * </pre>
	 * 
	 * @param str 검사할 문자열
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	/**
	 * <p>문자열이 숫자로만 구성되어 있는지 검사한다.</p>
	 * 
	 * <pre>
	 * StringUtil.isNumber(null)    = false;
	 * StringUtil.isNumber("")      = false;
	 * StringUtil.isNumber("1234")  = true;
	 * StringUtil.isNumber("abc123")= false;
	 * </pre>
	 * 
	 * @param str 검사할 문자열
	 * @return
	 */
	public static boolean isNumber(String str) {
		try {
			Integer.valueOf(str);
		}
		catch (Exception e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * <p>
	 * 문자열이 이메일인지 검사한다.
	 * </p>
	 * 
	 * <pre>
	 * StringUtil.isValidEmail(null)          = false;
	 * StringUtil.isValidEmail("abc.abc")     = false;
	 * StringUtil.isValidEmail("abc@abc.com") = true;
	 * 
	 * @param inputStr
	 * @return
	 */
	public static boolean isValidEmail(String inputStr) {
		boolean rtn = false;

		if (inputStr == null) {
			return rtn;
		}

		String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(inputStr);

		if (m.matches()) {
			rtn = true;
		}

		return rtn;
	}

	/**
	 * <p>
	 * 문자열 중에서 문자형을 제외한 숫자형만 반환합니다.
	 * </p>
	 * 
	 * <pre>
	 * StringUtil.removeString(null) = 0;
	 * StringUtil.removeString(&quot;&quot;) = 0;
	 * StringUtil.removeString(&quot;1234&quot;) = 1234;
	 * StringUtil.removeString(&quot;abc123&quot;) = 123;
	 * </pre>
	 * 
	 * @param str
	 *            변환할 문자열
	 * @return
	 */
	public static String removeString(String str) {
		str = defaultIfBlank(str, "0");

		if (isNumber(str))
			return str;

		char[] c = str.toCharArray();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < c.length; i++) {
			if (isNumber(String.valueOf(c[i]))) {
				sb.append(c[i]);
			}
		}
		return sb.toString();
	}

	/**
	 * <p>문자열을 숫자형으로 변경하여 반환합니다.</p>
	 * 
	 * <pre>
	 * StringUtil.toNumber(null)     = 0;
	 * StringUtil.toNumber("")       = 0;
	 * StringUtil.toNumber("1234")   = 1234;
	 * StringUtil.toNumber("abc123") = 0;
	 * </pre>
	 * 
	 * @param str 변환할 문자열
	 * @return
	 */
	public static int toNumber(String str) {
		str = defaultIfBlank(str, "0");
		
		if (isNumber(str))
			return Integer.valueOf(str);
		else
			return 0;
	}
	
	/**
	 * <p>문자열(String)의 좌우 여백문자(white space)를 제거한다.</p>
	 * 
	 * <pre>
	 * StringUtil.trim(null)    = null
	 * StringUtil.trim("")      = ""
	 * StringUtil.trim("   ")   = ""
	 * StringUtil.trim("han")   = "han"
	 * StringUtil.trim(" han ") = "han"
	 * </pre>
	 * 
	 * @param str 문자열
	 * @return
	 */
	public static String trim(String str) {
		return str == null ? null : str.trim();
	}
	
	/**
	 * <p>문자열(String)의 좌우 여백문자(white space)를 제거한 후 공백("")이거나 <code>null</code>이면 <code>null</code>을 반환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.trimToNull(null)    = null
	 * StringUtil.trimToNull("")      = null
	 * StringUtil.trimToNull("   ")   = null
	 * StringUtil.trimToNull("han")   = "han"
	 * StringUtil.trimToNull(" han ") = "han"
	 * </pre>
	 *
	 * @param str 문자열
	 * @return
	 */
	public static String trimToNull(String str) {
		return isBlank(str) ? null : trim(str);
	}
	
	/**
	 * <p>문자열(String)의 좌우 여백문자(white space)를 제거한 후 공백("")이거나 <code>null</code>이면 공백("")을 반환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.trimToEmpty(null)    = ""
	 * StringUtil.trimToEmpty("")      = ""
	 * StringUtil.trimToEmpty("   ")   = ""
	 * StringUtil.trimToEmpty("han")   = "han"
	 * StringUtil.trimToEmpty(" han ") = "han"
	 * </pre>
	 *
	 * @param str 문자열
	 * @return
	 */
	public static String trimToEmpty(String str) {
		return isBlank(str) ? EMPTY : trim(str);
	}
	
	/**
	 * <p>문자열에 섞여 있는 인코딩된 문자를 변환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.decode(null)       = null;
	 * StringUtil.decode("")         = "";
	 * StringUtil.decode("abc")      = "abc";
	 * StringUtil.decode("abc&#39;") = "abc'";
	 * </pre>
	 * 
	 * @param str 문자열
	 * @return
	 */
	public static String decode(String str) {
		str = replace(str, "&#39;", "'");
		str = replace(str, "&#59;", ";");
		str = replace(str, "&#45", "-");
		str = replace(str, "&#62;", ">");
		str = replace(str, "&gt;", ">");
		str = replace(str, "&lt;", "<");

		str = replace(str, "&#46124;", "됬");
		str = replace(str, "&#47750;", "몆");
		str = replace(str, "&#50776;", "왘");
		str = replace(str, "&#50860;", "욬");
		str = replace(str, "&#54973;", "횽");
		str = replace(str, "&#44248;", "곘");
		str = replace(str, "&#46468;", "떄");
		str = replace(str, "&#50522;", "앚");
		str = replace(str, "&#54855;", "홇");
		str = replace(str, "&#47791;", "몯");
		str = replace(str, "&#54631;", "핧");
		str = replace(str, "&#53384;", "킈");
		str = replace(str, "&#50043;", "썻");
		str = replace(str, "&#50735;", "옯");
		str = replace(str, "&#50524;", "앜");
		str = replace(str, "&#51902;", "쪾");
		str = replace(str, "&#52651;", "춫");
		str = replace(str, "&#52573;", "쵝");
		str = replace(str, "&#50351;", "쒯");
		str = replace(str, "&#51053;", "읭");
		str = replace(str, "&#48437;", "봵");
		str = replace(str, "&#54643;", "핳");
		str = replace(str, "&#46095;", "됏");
		str = replace(str, "&#49968;", "쌰");
		str = replace(str, "&#50785;", "왡");
		str = replace(str, "&#52924;", "캼");
		str = replace(str, "&#47368;", "뤈");
		str = replace(str, "&#51115;", "잫");
		str = replace(str, "&#47973;", "뭥");
		str = replace(str, "&#53240;", "쿸");
		str = replace(str, "&#49856;", "싀");
		str = replace(str, "&#52658;", "췍");
		str = replace(str, "&#48962;", "뽂");
		str = replace(str, "&#51781;", "쩅");
		str = replace(str, "&#49817;", "슙");
		str = replace(str, "&#53380;", "큭");
		str = replace(str, "&#53383;", "킇");
		str = replace(str, "&#55144;", "흨");
		str = replace(str, "&#52935;", "컇");
		str = replace(str, "&#54696;", "햨");
		str = replace(str, "&#54640;", "핰");
		str = replace(str, "&#44704;", "꺠");
		str = replace(str, "&#48577;", "뷁");
		str = replace(str, "&#48505;", "뵹");
		str = replace(str, "&#54676;", "햔");
		
		return str;
	}
	
	/**
	 * <p>문자열(String)이 <code>null</code>이면 기본문자열을 반환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.defaultIfNull(null, "")    = ""
	 * StringUtil.defaultIfNull("", "")      = ""
	 * StringUtil.defaultIfNull("   ", "")   = "   "
	 * StringUtil.defaultIfNull("han", "")   = "han"
	 * StringUtil.defaultIfNull(" han ", "") = " han "
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param defaultStr 기본문자열
	 * @return
	 */
	public static String defaultIfNull(String str, String defaultStr) {
		return str == null ? defaultStr : str;
	}
	
	/**
	 * <p>문자열(String)이 <code>null</code>이면 공백문자열을 반환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.defaultIfNull(null)    = ""
	 * StringUtil.defaultIfNull("")      = ""
	 * StringUtil.defaultIfNull("   ")   = "   "
	 * StringUtil.defaultIfNull("han")   = "han"
	 * StringUtil.defaultIfNull(" han ") = " han "
	 * </pre>
	 * 
	 * @param str 문자열
	 * @return
	 */
	public static String defaultIfNull(String str) {
		return defaultIfNull(str, EMPTY);
	}
	
	/**
	 * <p>문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이거나 <code>null</code>이면, 기본문자열을 반환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.defaultIfBlank(null, "")    = ""
	 * StringUtil.defaultIfBlank("", "")      = ""
	 * StringUtil.defaultIfBlank("   ", "")   = ""
	 * StringUtil.defaultIfBlank("han", "")   = "han"
	 * StringUtil.defaultIfBlank(" han ", "") = " han "
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param defaultStr 기본문자열
	 * @return
	 */
	public static String defaultIfBlank(String str, String defaultStr) {
		return isBlank(str) ? defaultStr : str;
	}
	
	/**
	 * <p>문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이거나 <code>null</code>이면, 공백문자열을 반환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.defaultIfBlank(null)    = ""
	 * StringUtil.defaultIfBlank("")      = ""
	 * StringUtil.defaultIfBlank("   ")   = ""
	 * StringUtil.defaultIfBlank("han")   = "han"
	 * StringUtil.defaultIfBlank(" han ") = " han "
	 * </pre>
	 * 
	 * @param str 문자열
	 * @return
	 */
	public static String defaultIfBlank(String str) {
		return defaultIfBlank(str, EMPTY);
	}
	
	
	// ----------------------------------------------------------------------
	// 문자열 비교
	// ----------------------------------------------------------------------
	/**
	 * <p>두 문자열(String)이 일치하면 <code>true</code>을 반환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.equals(null, null)   = true
	 * StringUtil.equals(null, "")     = false
	 * StringUtil.equals("", null)     = false
	 * StringUtil.equals(null, "han")  = false
	 * StringUtil.equals("han", null)  = false
	 * StringUtil.equals("han", "han") = true
	 * StringUtil.equals("han", "HAN") = false
	 * </pre>
	 * 
	 * @see java.lang.String#equals(Object)
	 * @param str1 첫번째 문자열
	 * @param str2 두번째 문자열
	 * @return 문자열(String)이 일치하면 <code>true</code>
	 */
	public static boolean equals(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equals(str2);
	}
	
	/**
	 * <p>대소문자를 무시한, 두 문자열(String)이 일치하면 <code>true</code>을 반환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.equalsIgnoreCase(null, null)   = true
	 * StringUtil.equalsIgnoreCase(null, "")     = false
	 * StringUtil.equalsIgnoreCase("", null)     = false
	 * StringUtil.equalsIgnoreCase(null, "han")  = false
	 * StringUtil.equalsIgnoreCase("han", null)  = false
	 * StringUtil.equalsIgnoreCase("han", "han") = true
	 * StringUtil.equalsIgnoreCase("han", "HAN") = true
	 * </pre>
	 * 
	 * @see java.lang.String#equalsIgnoreCase(String)
	 * @param str1 첫번째 문자열
	 * @param str2 두번째 문자열
	 * @return 대소문자를 무시한 문자열(String)이 일치하면 <code>true</code>
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}
	
	/**
	 * <p>문자열이 접두사로 시작하는지를 판단한다.</p>
	 * 
	 * <pre>
	 * StringUtil.startsWith(null, *)    = false
	 * StringUtil.startsWith(*, null)    = false
	 * StringUtil.startsWith("han", "h") = true
	 * StringUtil.startsWith("han", "a") = false
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param prefix 접두사
	 * @return
	 */
	public static boolean startsWith(String str, String prefix) {
		if (str == null || prefix == null) {
			return false;
		}
		return str.startsWith(prefix);
	}
	
	/**
	 * <p>문자열 offset 위치부터 접두사로 시작하는지를 판단한다.</p>
	 * 
	 * <pre>
	 * StringUtil.startsWith(null, *, 0)    = false
	 * StringUtil.startsWith(*, null, 0)    = false
	 * StringUtil.startsWith("han", "h", 0) = true
	 * StringUtil.startsWith("han", "a", 0) = false
	 * StringUtil.startsWith("han", "a", 1) = true
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param prefix 접두사
	 * @param offset 비교 시작 위치
	 * @return
	 */
	public static boolean startsWith(String str, String prefix, int offset) {
		if (str == null || prefix == null) {
			return false;
		}
		return str.startsWith(prefix, offset);
	}
	
	/**
	 * <p>문자열이 접미사로 끝나는지를 판단한다.</p>
	 * 
	 * <pre>
	 * StringUtil.endsWith(null, *)    = false
	 * StringUtil.endsWith(*, null)    = false
	 * StringUtil.endsWith("han", "h") = false
	 * StringUtil.endsWith("han", "n") = true
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param prefix 접두사
	 * @return
	 */
	public static boolean endsWith(String str, String suffix) {
		if (str == null || suffix == null) {
			return false;
		}
		return str.endsWith(suffix);
	}

	/**
	 * <p>문자열(String)에 검색문자열(String)이 몇번 포함되어 있는지 검사한다.</p>
	 * 
	 * <pre>
	 * StringUtil.containCount("haaaan", "a") = 4
	 * </pre>
	 * 
	 * @see java.lang.String#indexOf(String)
	 * @param str 문자열
	 * @param searchStr 검색문자열
	 * @return 문자열(String)에 검색 문자열이 포함되어 있을때 <code>count</code>,
	 *  문자열(String)에 검색 문자열이 포함되어 있지 않을때나, 문자열 또는 검색문자열이 <code>null</code>일때 <code>0</code>
	 */
	public static int containCount(String str, String searchStr) {
		int i = 0;
		int idx = 0;
		
		if (str == null || searchStr == null) {
			return 0;
		}
		while (true) {
			if ((idx=str.indexOf(searchStr)) > INDEX_NOT_FOUND) {
				str = substring(str, (idx+searchStr.length()), str.length());
				i++;
			} else {
				break;
			}
		}
		
		return i;
	}

	/**
	 * <p>문자열(String)에 검색문자열(String)이 포함되어 있는지 검사한다.</p>
	 * 
	 * <pre>
	 * StringUtil.contains(null, *)    = false
	 * StringUtil.contains(*, null)    = false
	 * StringUtil.contains("han", "")  = true
	 * StringUtil.contains("han", "h") = true
	 * StringUtil.contains("han", "H") = false
	 * </pre>
	 * 
	 * @see java.lang.String#indexOf(String)
	 * @param str 문자열
	 * @param searchStr 검색문자열
	 * @return 문자열(String)에 검색 문자열이 포함되어 있을때 <code>true</code>,
	 *  문자열(String)에 검색 문자열이 포함되어 있지 않을때나, 문자열 또는 검색문자열이 <code>null</code>일때 <code>false</code>
	 */
	public static boolean contains(String str, String searchStr) {
		if (str == null || searchStr == null) {
			return false;
		}
		return str.indexOf(searchStr) > INDEX_NOT_FOUND;
	}
	
	/**
	 * <p>문자열(String) 배열에 검색문자열(String)이 포함되어 있는지 검사한다.</p>
	 * 
	 * @see java.lang.String#indexOf(String)
	 * @param str array 문자열 배열
	 * @param searchStr 검색문자열
	 * @return 문자열(String)에 검색 문자열이 포함되어 있을때 <code>true</code>,
	 *  문자열(String)에 검색 문자열이 포함되어 있지 않을때나, 문자열 또는 검색문자열이 <code>null</code>일때 <code>false</code>
	 */
	public static boolean contains(String[] str, String searchStr) {
		boolean val = false;
		if (str == null || searchStr == null) {
			return val;
		}
		for (String s : str) {
			if (StringUtil.equals(s, searchStr)) {
				val = true;
				break;
			}
		}
		return val;
	}
	
	
	
	// ----------------------------------------------------------------------
	// 대/소문자 변환
	// ----------------------------------------------------------------------
	/**
	 * <p>문자열(String)을 대문자로 변환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.toUpperCase(null)  = null
	 * StringUtil.toUpperCase("han") = "HAN"
	 * StringUtil.toUpperCase("hAn") = "HAN"
	 * </pre>
	 * 
	 * @param str 문자열
	 * @return 대문자로 변환한 문자열
	 */
	public static String toUpperCase(String str) {
		if (str == null) {
			return null;
		}
		return str.toUpperCase();
	}
	
	/**
	 * <p>시작 인덱스부터 종료 인덱스까지 대문자로 변환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.toUpperCase(null, *, *)  = null
	 * StringUtil.toUpperCase("han", 0, 1) = "Han"
	 * StringUtil.toUpperCase("han", 0, 2) = "HAn"
	 * StringUtil.toUpperCase("han", 0, 3) = "HAN"
	 * </pre>
	 * 
	 * @param str
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public static String toUpperCase(String str, int beginIndex, int endIndex) {
		if (str == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if (beginIndex < 0) {
			beginIndex = 0;
		}
		if (endIndex > str.length()) {
			endIndex = str.length();
		}
		if (beginIndex > 0) {
			sb.append(str.substring(0, beginIndex));
		}
		sb.append(str.substring(beginIndex, endIndex).toUpperCase());
		if (endIndex < str.length()) {
			sb.append(str.substring(endIndex));	
		}
		return sb.toString();
	}
	
	/**
	 * <p>문자열(String)을 소문자로 변환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.toLowerCase(null)  = null
	 * StringUtil.toLowerCase("han") = "han"
	 * StringUtil.toLowerCase("hAn") = "han"
	 * </pre>
	 * 
	 * @param str 문자열
	 * @return 소문자로 변환한 문자열
	 */
	public static String toLowerCase(String str) {
		if (str == null) {
			return null;
		}
		return str.toLowerCase();
	}
	
	/**
	 * <p>시작 인덱스부터 종료 인덱스까지 소문자로 변환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.toLowerCase(null, *, *)  = null
	 * StringUtil.toLowerCase("HAN", 0, 1) = "hAN"
	 * StringUtil.toLowerCase("HAN", 0, 2) = "haN"
	 * StringUtil.toLowerCase("HAN", 0, 3) = "han"
	 * </pre>
	 * 
	 * @param str
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public static String toLowerCase(String str, int beginIndex, int endIndex) {
		if (str == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if (beginIndex < 0) {
			beginIndex = 0;
		}
		if (endIndex > str.length()) {
			endIndex = str.length();
		}
		if (beginIndex > 0) {
			sb.append(str.substring(0, beginIndex));
		}
		sb.append(str.substring(beginIndex, endIndex).toLowerCase());
		if (endIndex < str.length()) {
			sb.append(str.substring(endIndex));	
		}
		return sb.toString();
	}
	
	/**
	 * <p>대문자는 소문자로 변환하고 소문자는 대문자로 변환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.swapCase(null)  = null
	 * StringUtil.swapCase("Han") = "hAN"
	 * StringUtil.swapCase("hAn") = "HaN"
	 * </pre>
	 * 
	 * @param str 문자열
	 * @return
	 */
	public static String swapCase(String str) {
		if (str == null) {
			return null;
		}
		char[] charArray = str.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if (Character.isLowerCase(charArray[i])) {
				charArray[i] = Character.toUpperCase(charArray[i]);
			} else {
				charArray[i] = Character.toLowerCase(charArray[i]);
			}
		}
		
		return new String(charArray);
	}

	/**
	 * 문자열(String)의 첫번째 문자를 대문자로 변환한다.
	 * 
	 * <pre>
	 * StringUtil.capitalize(null)  = null
	 * StringUtil.capitalize("Han") = "Han"
	 * StringUtil.capitalize("han") = "Han"
	 * </pre>
	 * 
	 * @param str 문자열
	 * @return 첫번째 문자를 대문자로 변환한 문자열
	 */
	public static String capitalize(String str) {
		if (str == null) {
			return null;
		}
		char[] charArray = str.toCharArray();
		if (charArray.length > 0) {
			charArray[0] = Character.toUpperCase(charArray[0]);
		}
		return new String(charArray);
	}
	
	/**
	 * 문자열(String)의 첫번째 문자를 소문자로 변환한다.
	 * 
	 * <pre>
	 * StringUtil.uncapitalize(null)  = null
	 * StringUtil.uncapitalize("han") = "han"
	 * StringUtil.uncapitalize("HAN") = "hAN"
	 * </pre>
	 * 
	 * @param str 문자열
	 * @return 첫번째 문자를 대문자로 변환한 문자열
	 */
	public static String uncapitalize(String str) {
		if (str == null) {
			return null;
		}
		char[] charArray = str.toCharArray();
		if (charArray.length > 0) {
			charArray[0] = Character.toLowerCase(charArray[0]);
		}
		return new String(charArray);
	}
	
	// ----------------------------------------------------------------------
	// 문자열 배열 결합/분리
	// ----------------------------------------------------------------------
	/**
	 * <p>문자열 배열을 하나의 문자열로 결합시킨다.</p>
	 * <p>배열의 문자열 중에 <code>null</code>과 공백("")은 무시한다.</p>
	 * 
	 * <pre>
	 * StringUtil.compose(null, *)               = ""
	 * StringUtil.compose(["h", "a", "n"], ".")  = "h.a.n"
	 * StringUtil.compose([null, "a", "n"], ".") = "a.n"
	 * StringUtil.compose(["", "a", "n"], ".")   = "a.n"
	 * StringUtil.compose(["h", "", "n"], ".")   = "h.n"
	 * StringUtil.compose(["  ", "a", "n"], ".") = "  .a.n"
	 * </pre>
	 * 
	 * @param strArray 문자열 배열
	 * @param separator 구분자
	 * @return 구분자로 결합한 문자열
	 */
	public static String compose(String[] strArray, char separator) {
		StringBuilder sb = new StringBuilder();
		if (strArray != null) {
			for (int i = 0; i < strArray.length; i++) {
				if (StringUtil.isEmpty(strArray[i])) {
					sb.append(EMPTY);
				} else {
					if (sb.length() > 0) {
						sb.append(separator);
					}
					sb.append(strArray[i]);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * <p>문자열 배열을 하나의 문자열로 결합시킨다.</p>
	 * <p>배열의 문자열 중에 <code>null</code>과 공백("")은 무시한다.</p>
	 * 
	 * <pre>
	 * StringUtil.compose(null, *)               = ""
	 * StringUtil.compose(["h", "a", "n"], ".")  = "h.a.n"
	 * StringUtil.compose([null, "a", "n"], ".") = "a.n"
	 * StringUtil.compose(["", "a", "n"], ".")   = "a.n"
	 * StringUtil.compose(["h", "", "n"], ".")   = "h.n"
	 * StringUtil.compose(["  ", "a", "n"], ".") = "  .a.n"
	 * </pre>
	 * 
	 * @param strArray 문자열 배열
	 * @param separator 구분자
	 * @return 구분자로 결합한 문자열
	 */
	public static String compose(String[] strArray, String separator) {
		StringBuilder sb = new StringBuilder();
		if (strArray != null) {
			for (int i = 0; i < strArray.length; i++) {
				if (StringUtil.isEmpty(strArray[i])) {
					sb.append(EMPTY);
				} else {
					if (sb.length() > 0) {
						sb.append(separator);
					}
					sb.append(strArray[i]);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * <p>문자열 배열을 하나의 문자열로 결합시킨다.</p>
	 * <p>배열의 문자열 중에 <code>null</code>과 공백("")도 포함한다.</p>
	 * 
	 * <pre>
	 * StringUtil.join(null, *)               = ""
	 * StringUtil.join(["h", "a", "n"], '-')  = "h-a-n"
	 * StringUtil.join([null, "a", "n"], '-') = "-a-n"
	 * StringUtil.join(["", "a", "n"], '-')   = "-a-n"
	 * StringUtil.join(["h", "", "n"], '-')   = "h--n"
	 * StringUtil.join(["  ", "a", "n"], '-') = "  -a-n"
	 * </pre>
	 * 
	 * @param strArray 문자열 배열
	 * @param separator 구분자
	 * @return 구분자로 결합한 문자열
	 */
	public static String join(String[] strArray, char separator) {
		StringBuilder sb = new StringBuilder();
		if (strArray != null) {
			boolean isFirst = true;
			for (int i = 0; i < strArray.length; i++) {
				if (isFirst) {
					isFirst = false;
				} else {
					sb.append(separator);
				}
				if (StringUtil.isEmpty(strArray[i])) {
					sb.append(EMPTY);
				} else {
					sb.append(strArray[i]);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * <p>문자열 배열을 하나의 문자열로 결합시킨다.</p>
	 * <p>배열의 문자열 중에 <code>null</code>과 공백("")도 포함한다.</p>
	 * 
	 * <pre>
	 * StringUtil.join(null, *)               = ""
	 * StringUtil.join(["h", "a", "n"], "-")  = "h-a-n"
	 * StringUtil.join([null, "a", "n"], "-") = "-a-n"
	 * StringUtil.join(["", "a", "n"], "-")   = "-a-n"
	 * StringUtil.join(["h", "", "n"], "-")   = "h--n"
	 * StringUtil.join(["  ", "a", "n"], "-") = "  -a-n"
	 * </pre>
	 * 
	 * @param strArray 문자열 배열
	 * @param separator 구분자
	 * @return 구분자로 결합한 문자열
	 */
	public static String join(String[] strArray, String separator) {
		StringBuilder sb = new StringBuilder();
		if (strArray != null) {
			boolean isFirst = true;
			for (int i = 0; i < strArray.length; i++) {
				if (isFirst) {
					isFirst = false;
				} else {
					sb.append(separator);
				}
				if (StringUtil.isEmpty(strArray[i])) {
					sb.append(EMPTY);
				} else {
					sb.append(strArray[i]);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * @param strArray 문자열 배열
	 * @param searchStr 찾는 문자
	 * @return 찾는 문자를 제거한 문자열 배열
	 */
	public static String[] remove(String[] strArray, String searchStr) {
		if (!contains(strArray, searchStr))
			return strArray;
		
		int idx = 0;
		String[] newStr = new String[strArray.length-1];
		for (int i = 0; i < strArray.length; i++) {
			if (!equals(strArray[i], searchStr)) {
				newStr[idx] = strArray[i];
				idx++;
			}
		}
		
		return newStr;
	}
	
	/**
	 * <p>문자열을 구분자로 나누어서, 문자열 배열로 만든다.</p>
	 * <p>배열의 문자열 중에 <code>null</code>과 공백("")도 포함한다.</p>
	 * 
	 * <pre>
	 * StringUtil.split("h-a-n", '-') = ["h", "a", "n"]
	 * StringUtil.split("h--n", '-')  = ["h", "", "n"]
	 * StringUtil.split(null, *)      = null
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param separator 구분자
	 * @return 구분자로 나누어진 문자열 배열
	 */
	public static String[] split(String str, char separator) {
		return split(str, new String(new char[]{separator}));
	}
	
	/**
	 * <p>문자열을 구분자로 나누어서, 문자열 배열로 만든다.</p>
	 * <p>배열의 문자열 중에 <code>null</code>과 공백("")도 포함한다.</p>
	 * 
	 * <pre>
	 * StringUtil.split("h-a-n", "-") = ["h", "a", "n"]
	 * StringUtil.split("h--n", "-")  = ["h", "", "n"]
	 * StringUtil.split(null, *)      = null
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param separator 구분자
	 * @return 구분자로 나누어진 문자열 배열
	 */
	public static String[] split(String str, String separator) {
		if (str == null) {
			return null;
		}
		String[] result;
		int i = 0;     // index into the next empty array element
		
		//--- Declare and create a StringTokenizer
		StringTokenizer st = new StringTokenizer(str, separator);
		//--- Create an array which will hold all the tokens.
		result = new String[st.countTokens()];
		
		//--- Loop, getting each of the tokens
		while (st.hasMoreTokens()) {
			result[i++] = st.nextToken();
		}
		
		return result;
	}
	
	// ----------------------------------------------------------------------
	// 문자열 자르기
	// ----------------------------------------------------------------------
	/**
	 * <p>문자열(String)을 해당 길이(<code>length</code>) 만큼, 왼쪽부터 자른다.</p>
	 * 
	 * <pre>
	 * StringUtil.left(null, *)    = null
	 * StringUtil.left(*, -length) = ""
	 * StringUtil.left("", *)      = *
	 * StringUtil.left("han", 0)   = ""
	 * StringUtil.left("han", 1)   = "h"
	 * StringUtil.left("han", 11)  = "han"
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param length 길이
	 * @return
	 */
	public static String left(String str, int length) {
		if (str == null) {
			return null;
		}
		if (length < 0) {
			return EMPTY;
		}
		if (str.length() <= length) {
			return str;
		}
		return str.substring(0, length);
	}
	
	/**
	 * <p>문자열(String)을 해당 길이(<code>length</code>) 만큼, 오른쪽부터 자른다.</p>
	 * 
	 * <pre>
	 * StringUtil.right(null, *)    = null
	 * StringUtil.right(*, -length) = ""
	 * StringUtil.right("", *)      = *
	 * StringUtil.right("han", 0)   = ""
	 * StringUtil.right("han", 1)   = "n"
	 * StringUtil.right("han", 11)  = "han"
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param length 길이
	 * @return
	 */
	public static String right(String str, int length) {
		if (str == null) {
			return null;
		}
		if (length < 0) {
			return EMPTY;
		}
		if (str.length() <= length) {
			return str;
		}
		return str.substring(str.length() - length);
	}
	
	/**
	 * <p>문자열(String)을 시작 위치(<code>beginIndex</code>)부터 길이( <code>length</code>) 만큼 자른다.</p>
	 * 
	 * <p>시작 위치(<code>beginIndex</code>)가 음수일 경우는 0으로 자동 변환된다.</p>
	 * 
	 * <pre>
	 * StringUtil.mid(null, *, *)    = null
	 * StringUtil.mid(*, *, -length) = ""
	 * StringUtil.mid("han", 0, 1)   = "h"
	 * StringUtil.mid("han", 0, 11)  = "han"
	 * StringUtil.mid("han", 2, 3)   = "n"
	 * StringUtil.mid("han", -2, 3)  = "han"
	 * </pre> 
	 * 
	 * @param str 문자열
	 * @param beginIndex 위치(음수일 경우는 0으로 자동 변환된다.)
	 * @param length 길이
	 * @return
	 */
	public static String mid(String str, int beginIndex, int length) {
		if (str == null) {
			return null;
		}
		if (length < 0 || beginIndex > str.length()) {
			return EMPTY;
		}
		if (beginIndex < 0) {
			beginIndex = 0;
		}
		if (str.length() <= (beginIndex + length)) {
			return str.substring(beginIndex);
		}
		return str.substring(beginIndex, beginIndex + length);
	}
	
	/**
	 * <p>시작 인덱스부터 문자열을 자는다.</p>
	 * <p>시작 인덱스가 0보다 작거나, 문자열의 총길이보다 크면 공백("")을 반환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.substring(null, *)    = null
	 * StringUtil.substring("", *)      = ""
	 * StringUtil.substring("han", 1)   = "an"
	 * StringUtil.substring("han", 615) = ""
	 * StringUtil.substring("han", -1)  = ""
	 * </pre> 
	 * 
	 * @param str
	 * @param beginIndex 시작 인덱스(0부터 시작)
	 * @return
	 */
	public static String substring(String str, int beginIndex) {
		if (str == null){
			return null;
		}
		
		if (beginIndex < 0) {
			return EMPTY;
		}
		
		if (beginIndex > str.length()) {
			return EMPTY;
		}
		
		return str.substring(beginIndex);
	}


	/**
	 * <p>시작 인덱스부터 끝 인덱스까지 문자열을 자는다.</p>
	 * <p>시작 인덱스또는 끝 인덱스가  0보다 작으면 공백("")을 반환한다.</p>
	 * 
	 * <pre>
	 * StringUtil.substring(null, *, *)    = null
	 * StringUtil.substring("", *, *)      = ""
	 * StringUtil.substring("han", 1, 2)   = "a"
	 * StringUtil.substring("han", 1, 3)   = "an"
	 * StringUtil.substring("han", 1, 615) = "an"
	 * StringUtil.substring("han", -1, *)  = ""
	 * StringUtil.substring("han", *, -1)  = ""
	 * </pre> 
	 * 
	 * @param str
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public static String substring(String str, int beginIndex, int endIndex) {
		if (str == null){
			return null;
		}
		
		if (beginIndex < 0 || endIndex < 0) {
			return EMPTY;
		}
		
		if (endIndex > str.length()) {
			endIndex = str.length();
		}
		
		if (beginIndex > endIndex || beginIndex > str.length()) {
			return EMPTY;
		}
		
		return str.substring(beginIndex, endIndex);
	}
	
	
	/**
	 * <p>처음 발견한 구분자의 위치까지 문자열을 자른다.</p>
	 * 
	 * <pre>
	 * StringUtil.substringBefore(null, *)       = null
	 * StringUtil.substringBefore("", *)         = ""
	 * StringUtil.substringBefore("han", null)   = "han"
	 * StringUtil.substringBefore("han", "")     = ""
	 * StringUtil.substringBefore("hanhan", "a") = "h"
	 * StringUtil.substringBefore("hanhan", "g") = "hanhan"
	 * </pre> 
	 * 
	 * @param str 문자열
	 * @param separator 구분자
	 * @return
	 */
	public static String substringBefore(String str, String separator) {
		if (isEmpty(str) || separator == null) {
			return str;
		}
        if (separator.length() == 0) {
            return EMPTY;
        }
		int endIndex = str.indexOf(separator);
		if (endIndex == INDEX_NOT_FOUND) {
			return str;
		}
		return str.substring(0, endIndex);
	}
	
	
	/**
	 * <p>마지막으로 발견한 구분자의 위치까지 문자열을 자른다.</p>
	 * 
	 * <pre>
	 * StringUtil.substringBeforeLast(null, *)       = null
	 * StringUtil.substringBeforeLast("", *)         = ""
	 * StringUtil.substringBeforeLast("han", null)   = "han"
	 * StringUtil.substringBeforeLast("han", "")     = "han"
	 * StringUtil.substringBeforeLast("hanhan", "a") = "hanh"
	 * StringUtil.substringBeforeLast("hanhan", "g") = "hanhan"
	 * </pre> 
	 * 
	 * @param str 문자열
	 * @param separator 구분자
	 * @return
	 */
	public static String substringBeforeLast(String str, String separator) {
		if (isEmpty(str) || isEmpty(separator)) {
			return str;
		}
		int endIndex = str.lastIndexOf(separator);
		if (endIndex == INDEX_NOT_FOUND) {
			return str;
		}
		return str.substring(0, endIndex);
	}

	
	/**
	 * <p>처음 발견한 구분자의 위치 다음부터 문자열을 자른다.</p>
	 * 
	 * <pre>
	 * StringUtil.substringAfter(null, *)       = null
	 * StringUtil.substringAfter("", *)         = ""
	 * StringUtil.substringAfter("han", null)   = ""
	 * StringUtil.substringAfter("han", "")     = "han"
	 * StringUtil.substringAfter("hanhan", "a") = "nhan"
	 * StringUtil.substringAfter("hanhan", "g") = ""
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param separator 구분자
	 * @return
	 */
	public static String substringAfter(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return EMPTY;
		}
		int beginIndex = str.indexOf(separator);
		if (beginIndex == INDEX_NOT_FOUND) {
			return EMPTY;
		}
		beginIndex = beginIndex + separator.length();
		if (beginIndex == str.length()) {
			return EMPTY;
		}
		return str.substring(beginIndex);
	}
	
	/**
	 * <p>마지막으로 발견한 구분자의 위치 다음부터 문자열을 자른다.</p>
	 * 
	 * <pre>
	 * StringUtil.substringAfterLast(null, *)       = null
	 * StringUtil.substringAfterLast("", *)         = ""
	 * StringUtil.substringAfterLast("han", null)   = ""
	 * StringUtil.substringAfterLast("han",     "") = ""
	 * StringUtil.substringAfterLast("hanhan", "a") = "n"
	 * StringUtil.substringAfterLast("hanhan", "g") = ""
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param separator 구분자
	 * @return
	 */
	public static String substringAfterLast(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (isEmpty(separator)) {
			return EMPTY;
		}
		int beginIndex = str.lastIndexOf(separator);
		if (beginIndex == INDEX_NOT_FOUND) {
			return EMPTY;
		}
		beginIndex = beginIndex + separator.length();
		if (beginIndex == str.length()) {
			return EMPTY;
		}
		return str.substring(beginIndex);
	}
	
	/**
	 * <p>시작 문자부터 끝 문자열까지 자른다.</p>
	 * 
	 * <pre>
	 * StringUtil.substringBetween(null, *, *)       = null
	 * StringUtil.substringBetween(*, null, *)       = null
	 * StringUtil.substringBetween(*, *, null)       = null
	 * StringUtil.substringBetween("h<a>n", "<", ">") = "a"
	 * StringUtil.substringBetween("h<a><b>n", "<", ">") = "a"
	 * </pre>
	 * 
	 * @param str 문자열
	 * @param separator 구분자
	 * @return
	 * @since 1.1
	 */
    public static String substringBetween(String str, String open, String close) {
		if (str == null || open == null || close == null) {
			return null;
		}
		
		int start = str.indexOf(open);
		if (start != INDEX_NOT_FOUND) {
			int end = str.indexOf(close, start + open.length());
			if (end != INDEX_NOT_FOUND) {
				return str.substring(start + open.length(), end);
			} else {
				// 끝이 없으면 null이 좋을까, 끝까지가 좋을까...
			}
		}
		return null;
	}
    
    /**
     * <p>입력한 문자열을 한줄의 최대 길이만큼, 여러 줄로 나누어 반환한다.</p>
     * <p>공백(" ")을 기준으로 줄 바꿈을 시도한다.</p>
     * 
     * @param str
     * @param maxLineLength 한줄의 최대 길이
     * @return
     */
    public static List<String> wrap(String str, int maxLineLength) {
    	if (str == null) {
    		return null;
    	}
    	List<String> lines = new ArrayList<String>();
        if (str.length() <= maxLineLength || str.indexOf(' ') == INDEX_NOT_FOUND) {
        	// 전체 길이가 최대 길이보다 짧거나, 구분할수 있는 조건이 안되면 그대로 반환한다.
        	lines.add(str);
            return lines;
        }
        
        StringBuilder sb = new StringBuilder();
        StringTokenizer tokenzier = new StringTokenizer(str, " ");
        sb.append(tokenzier.nextToken());
        while (tokenzier.hasMoreTokens()) {
            String token = tokenzier.nextToken();
            if ( (sb.length() + token.length() + 1) > maxLineLength) {
                lines.add(sb.toString());
                sb.setLength(0);
                sb.append(token);
            } else {
                sb.append(" ");
                sb.append(token);
            }
        }

        if (sb.toString().trim().length() > 0) {
            lines.add(sb.toString());
        }
        return lines;
    }
	
	/**
	 * <p>문자열이 해당 길이보다 크면, 자른 후 줄임말을 붙여준다.</p>
	 * <p>길이는 기본문자들(영어/숫자등)이 1으로, 다국어(한글등)이면 2로 계산한다.</p>
	 * 
	 * <pre>
	 * StringUtil.curtail(null, *, *) = null
	 * StringUtil.curtail("abcdefghijklmnopqr", 10, null) = "abcdefghij"
	 * StringUtil.curtail("abcdefghijklmnopqr", 10, "..") = "abcdefgh.."
	 * StringUtil.curtail("한글을 사랑합시다.", 10, null)   = "한글을 사랑"
	 * StringUtil.curtail("한글을 사랑합시다.", 10, "..")   = "한글을 사.."
	 * </pre>
	 * 
	 * 
	 * @param str 문자열
	 * @param size 길이(byte 길이)
	 * @param tail 줄임말
	 * @return
	 */
	public static String curtail(String str, int size, String tail) {
		if (str == null) {
			return null;
		}
		int strLen = str.length();
		int tailLen = (tail != null) ? tail.length() : 0;
		int maxLen = size - tailLen;
		int curLen = 0;
		int index = 0;
		for (; index < strLen && curLen < maxLen; index++) {
			if (Character.getType(str.charAt(index)) == Character.OTHER_LETTER) {
				curLen++;
			}
			curLen++;
		}
		
		if (index == strLen) {
			return str;
		} else {
			StringBuilder result = new StringBuilder();
			result.append(str.substring(0, index));
			if (tail != null) {
				result.append(tail);	
			}
			return result.toString();
		}
	}
	
	// ----------------------------------------------------------------------
	// 패딩
	// ----------------------------------------------------------------------
	/**
	 * <p>왼쪽부터 크기만큼 패딩문자로 채운다.</p>
	 * 
	 * <pre>
	 * StringUtil.leftPad("han", 5, " ")    = "  han"
	 * StringUtil.leftPad("han", 5, "123")  = "12han"
	 * StringUtil.leftPad("han", 10, "123") = "1231231han"
	 * StringUtil.leftPad("han", -1, " ")   = "han"
	 * </pre>
	 * 
	 * @param str
	 * @param size 크기
	 * @param padStr 패딩문자
	 * @return
	 */
	public static String leftPad(String str, int size, String padStr) {
		if (str == null) {
			return null;
		}
		if (isEmpty(padStr)) {
			padStr = " "; //$NON-NLS-1$
		}
		int strLen = str.length();
		int padStrLen = padStr.length();
		int padLen = size - strLen;
		if (padLen <= 0) {
			// 패딩할 필요가 없음
			return str;
		}
		
		StringBuilder result = new StringBuilder();
		if (padLen == padStrLen) {
			result.append(padStr);
			result.append(str);
		} else if (padLen < padStrLen) {
			result.append(padStr.substring(0, padLen));
			result.append(str);
		} else {
			char[] padding = padStr.toCharArray();
			for (int i = 0; i < padLen; i++) {
				result.append(padding[ i % padStrLen]);
			}
			result.append(str);
		}
		return result.toString();
	}
	
	/**
	 * <p>오른쪽부터 크기만큼 패딩문자로 채운다.</p>
	 * 
	 * <pre>
	 * StringUtil.rightPad("han", 5, " ")    = "han  "
	 * StringUtil.rightPad("han", 5, "123")  = "han12"
	 * StringUtil.rightPad("han", 10, "123") = "han1231231"
	 * StringUtil.rightPad("han", -1, " ")   = "han"
	 * </pre>
	 * 
	 * @param str
	 * @param size 크기
	 * @param padStr 패딩문자
	 * @return
	 */
	public static String rightPad(String str, int size, String padStr) {
		if (str == null) {
			return null;
		}
		if (isEmpty(padStr)) {
			padStr = " "; //$NON-NLS-1$
		}
		int strLen = str.length();
		int padStrLen = padStr.length();
		int padLen = size - strLen;
		if (padLen <= 0) {
			// 패딩할 필요가 없음
			return str;
		}
		
		StringBuilder result = new StringBuilder();
		if (padLen == padStrLen) {
			result.append(str);
			result.append(padStr);
		} else if (padLen < padStrLen) {
			result.append(str);
			result.append(padStr.substring(0, padLen));
		} else {
			result.append(str);
			char[] padding = padStr.toCharArray();
			for (int i = 0; i < padLen; i++) {
				result.append(padding[ i % padStrLen]);
			}
		}
		return result.toString();
	}

	/**
	 * <p>숫자를 알바벳으로 변경한다.</p>
	 * 
	 * <pre>
	 * StringUtil.changeAlpabet(0)  = "A"
	 * StringUtil.changeAlpabet(1)  = "B"
	 * StringUtil.changeAlpabet(5)  = "F"
	 * StringUtil.changeAlpabet(100)= ""
	 * </pre>
	 * 
	 * @param int
	 * @return
	 */
	public static String changeAlpabet(int num) {
		String[] alpabet = {"A", "B", "C", "D", "E",
				            "F", "G", "H", "I", "J", "K",
				            "L", "M", "N", "O", "P", "Q",
				            "R", "S", "T", "U", "V", "W",
				            "X", "Y", "Z"};
		
		if (num > 25)
			return "";
		else 
			return alpabet[num];
	}

    /**
    * HTML 포맷으로 특수 문자 변환
    * @param    s       원본 문자열
    * @return           치환된 문자열
    */
	public static String toHTMLString(String s){
		StringBuffer stringbuffer = new StringBuffer();
		for(int i = 0; s != null && i < s.length(); i++){
			char c = s.charAt(i);
			if(c == '\'')
				//stringbuffer.append("&#39;");
				stringbuffer.append(c);
			else
			if(c == '"')
				//stringbuffer.append("&#34;");
				stringbuffer.append(c);
			else
			if(c == '\n')
				stringbuffer.append("<BR>\n");
			else
			if(c == '\t')
				stringbuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			else
			if(c == '<')
				//stringbuffer.append("&lt;");
				stringbuffer.append(c);
			else
			if(c == '>')
				//stringbuffer.append("&gt;");
				stringbuffer.append(c);
			else
			if(c == '&')
				stringbuffer.append("&amp;");
			else
				stringbuffer.append(c);
		}

		return stringbuffer.toString();
	}

    /**
    * XML 포맷으로 특수 문자 변환
    * @param    s       원본 문자열
    * @return           치환된 문자열
    */
	public static String toXMLString(String s){
		StringBuffer stringbuffer = new StringBuffer();
		for(int i = 0; s != null && i < s.length(); i++){
			char c = s.charAt(i);
			if(c == '\'')
				stringbuffer.append("&#39;");
				//stringbuffer.append(c);
			else
			if(c == '"')
				stringbuffer.append("&#34;");
				//stringbuffer.append(c);
			else
			if(c == '\n')
				stringbuffer.append("<BR>\n");
			else
			if(c == '\t')
				stringbuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			else
			if(c == '<')
				stringbuffer.append("&lt;");
				//stringbuffer.append(c);
			else
			if(c == '>')
				stringbuffer.append("&gt;");
				//stringbuffer.append(c);
			else
			if(c == '&')
				stringbuffer.append("&amp;");
			else
				stringbuffer.append(c);
		}

		return stringbuffer.toString();
	}

    /**
    문자열에서 이전문자를 새 문자로 대체해서 반환
    @param   s        문자열
    @param   oldSub   이전문자
    @param   newSub   새 문자
    @return  String   결과 문자열
    */
    public static String replace(String s, char oldSub, char newSub) {
        return replace(s, oldSub, new Character(newSub).toString());
    }

    /**
     문자열에서 이전문자를 새 문자로 대체해서 반환
     @param   s        문자열
     @param   oldSub   이전문자
     @param   newSub   새 문자
     @return  String   결과 문자열
     */
    public static String replace(String s, char oldSub, String newSub) {
      if ((s == null) || (newSub == null)) {
          return null;
      }

      char[] c = s.toCharArray();

      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < c.length; i++) {
          if (c[i] == oldSub) {
              sb.append(newSub);
          }
          else {
              sb.append(c[i]);
          }
      }

      return sb.toString();
    }

  /**
    문자열에서 이전문자를 새 문자로 대체해서 반환
    @param   s        문자열
    @param   oldSub   이전문자
    @param   newSub   새 문자
    @return  String   결과 문자열
  */
  public static String replace(String s, String oldSub, String newSub) {
      if ((s == null) || (oldSub == null) || (newSub == null)) {
          return null;
      }

      int y = s.indexOf(oldSub);

      if (y >= 0) {
          StringBuffer sb = new StringBuffer();
          int length = oldSub.length();
          int x = 0;

          while (x <= y) {
              sb.append(s.substring(x, y));
              sb.append(newSub);
              x = y + length;
              y = s.indexOf(oldSub, x);
          }

          sb.append(s.substring(x));

          return sb.toString();
      }
      else {
          return s;
      }
  }

  /**
  문자열에서 이전문자를 새 문자로 대체해서 반환
  @param   s        문자열
  @param   oldSubs   이전문자배열
  @param   newSubs   새 문자배열
  @return  String   결과 문자열
*/
public static String replace(String s, String[] oldSubs, String[] newSubs) {
    if ((s == null) || (oldSubs == null) || (newSubs == null)) {
        return null;
    }

    if (oldSubs.length != newSubs.length) {
        return s;
    }

    for (int i = 0; i < oldSubs.length; i++) {
        s = replace(s, oldSubs[i], newSubs[i]);
    }

    return s;
}

/**
문자열에서 이전문자를 새 문자로 대체해서 반환
@param   s        문자열
@param   oldSubs   이전문자배열
@param   newSubs   새 문자배열
@return  String   결과 문자열
*/
public static String replace(String s, String[] oldSubs, char[] newSubs) {
  if ((s == null) || (oldSubs == null) || (newSubs == null)) {
      return null;
  }

  if (oldSubs.length != newSubs.length) {
      return s;
  }

  for (int i = 0; i < oldSubs.length; i++) {
      s = replace(s, oldSubs[i],String.valueOf(newSubs[i]));
  }
  return s;
}
  /**
   *  String에서 substring을 찾아 바꾸고자하는 다른 string으로대치해서
   *  넘겨주는메소드. 놀랍게도 java.lang.String에 이러한 메소드가 없답니다.
   *  물론 차후에 생기게 될지도..^^
   *  @param search 찾으려고하는 단어
   *  @param replace 바꾸고자하는 단어
   *  @param source 전체 String
   *  @return The source with all instances of <code>search</code>
   *      replaced by <code>replace</code>
   */
  public static String sReplace(String search, String replace, String source) {
      int spot;
      String returnString;
      String origSource = new String(source);

      spot = source.indexOf(search);
      if (spot > -1)
          returnString = "" ;
      else
          returnString = source;
      while (spot > -1) {
          if (spot == source.length() + 1) {
              returnString = returnString.concat(source.substring(0, source.length() - 1).concat(replace));
              source = "";
          }
          else if (spot > 0) {
              returnString = returnString.concat(source.substring(0, spot).concat(replace));
              source = source.substring(spot + search.length(), source.length());
          }
          else {
              returnString = returnString.concat(replace);
              source = source.substring(spot + search.length(), source.length());
          }
          spot = source.indexOf(search);
      }
      if (! source.equals(origSource)) {
          return returnString.concat(source);
      }
      else {
          return returnString;
      }
  }

  	/**
	  문자열을 날짜 형식으로 변환
	  @param   d    문자열
	  @param   delim   구분 분자
	  @return  String  결과 값
	*/  
	public static String toDateFormat(String d, String delim){
	    StringBuffer sb = new StringBuffer();
	    if(d == null || d.equals("") || d.length() != 8){
	        return d;
	    }
	    sb.append(d.substring(0,4));
	    sb.append(delim);
	    sb.append(d.substring(4,6));
	    sb.append(delim);
	    sb.append(d.substring(6));
	    
	    return sb.toString();
	}

	public static boolean assigned(String personID) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 스트링 parameter의 모든 html 태그를 제거한다.
	 * @param body
	 */
	public static String removeHtmlTags(String s) {
//		String[] startTags = {"<HTML>", "<meta", "<body>", "<div", "<img", "<a", "<embed"};
//		String[] endTags = {"</HTML>", "</meta>", "</body>", "</div>", "</img>", "</a>", "</embed>"};
		String[] oldStr = {"<br>", "</br>", "<BR>", "</BR>", "&nbsp;"};
		String[] newStr = {"\n", "\n", "\n", "\n", " "};
		
		if (s == null) { 
			return null; 
		} 
		
		for (int i = 0; i < oldStr.length; i++)
			s = replace(s, oldStr, newStr);

		Matcher m; 

		m = Patterns.SCRIPTS.matcher(s); 
		s = m.replaceAll(""); 
		m = Patterns.STYLE.matcher(s); 
		s = m.replaceAll(""); 
		m = Patterns.TAGS.matcher(s); 
		s = m.replaceAll(""); 

		return s; 
	}

    /**
     * \n -> <br>로 치환
     */
    public static String newLineToBr(String s) {
    	if (s == null)
    		return null;
    	
    	return replace(s, "\n", "<br>");
    }
    
	/**
	 * 검색어중 특수문자를 제외시킨다.
	 * @param originText
	 * @return
	 */
    public static String getSearchText(String originText) {
    	
    	String searchText = null;
    	originText = trimToEmpty(originText);

    	if(originText.length() > 0){					
	    	StringBuffer checkSpecialCharacter = new StringBuffer();
	    	for(int i=0; i<originText.length(); i++) {
	    		char characterAtI = originText.charAt(i);
	    		if((characterAtI == '[') || (characterAtI == '_') || (characterAtI == '%')) {
	    			continue;
	    			//checkSpecialCharacter.append("\\"+characterAtI);
	    		} else if((characterAtI == '\'')) {
	    			continue;
	    		} else if((characterAtI == '\n') || (characterAtI == '\r')) {
	    			continue;
	    		} else {
	    			checkSpecialCharacter.append(characterAtI);
	    		}
	    	}
    		searchText = checkSpecialCharacter.toString();
    	} else {
    		searchText = null;	
    	}   	
    	
    	return searchText;
    }

    /**
	 * 스트링 parameter의 모든 html 태그를 제거한다.
	 * @param body
	 */
	public static String removeAllTags(String s) {
		if (s == null) { 
			return null; 
		} 
		Matcher m; 

		m = Patterns.TAGS.matcher(s); 
		s = m.replaceAll(""); 
		m = Patterns.ENTITY_REFS.matcher(s); 
		s = m.replaceAll(""); 
		m = Patterns.WHITESPACE.matcher(s); 
		s = m.replaceAll(" "); 

		return s; 
	}

	private static interface Patterns { 
		// javascript tags and everything in between 
		public static final Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL); 
		public static final Pattern STYLE = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL); 
		// HTML/XML tags 
		public static final Pattern TAGS = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>"); 
		@SuppressWarnings("unused")
		public static final Pattern nTAGS = Pattern.compile("<\\w+\\s+[^<]*\\s*>"); 
		// entity references 
		public static final Pattern ENTITY_REFS = Pattern.compile("&[^;]+;"); 
		// repeated whitespace 
		public static final Pattern WHITESPACE = Pattern.compile("\\s\\s+"); 
	} 
}