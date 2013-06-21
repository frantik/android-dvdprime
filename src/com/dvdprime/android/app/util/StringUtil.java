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
	 * <p>����(char)�� �ܾ� ������('_', '-', '@', '$', '#', ' ')���� �Ǵ��Ѵ�.</p>
	 * 
	 * @param c ����(char)
	 * @return �ܾ� �������̸� true, �ƴϸ� false�� ��ȯ�Ѵ�.
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
	 * <p>����(char)�� �ܾ� ������('_', '-', '@', '$', '#', ' ')���� �Ǵ��Ѵ�.</p>
	 * 
	 * @param c ����(char)
	 * @return �ܾ� �������̸� true, �ƴϸ� false�� ��ȯ�Ѵ�.
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
	 * <p>���ڿ�(String)�� ī��ǥ������� ǥ���Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.camelString("ITEM_CODE", true)  = "ItemCode"
	 * StringUtil.camelString("ITEM_CODE", false) = "itemCode"
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @param firstCharacterUppercase ù���ڿ��� �빮�ڷ� ���� ����
	 * @return ī��ǥ������� ǥ��ȯ ���ڿ�
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
	 * <p>�Է� ���� ���ڸ� �ݺ����ڸ�ŭ �ٿ��� �����.</p>
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
	 * @param repeat �ݺ�����
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
	// ����/���鹮��  �˻�, ����, ġȯ
	// ----------------------------------------------------------------------
	
	/**
	 * <p>���ڿ�(String)�� �¿� ���鹮��(white space)�� ��������, ����("")�̰ų� <code>null</code>�� �˻��Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.isBlank(null)    = true
	 * StringUtil.isBlank("")      = true
	 * StringUtil.isBlank("   ")   = true
	 * StringUtil.isBlank("han")   = false
	 * StringUtil.isBlank(" han ") = false
	 * </pre> 
	 * 
	 * @param str ���ڿ�
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
	 * <p>���ڿ�(String)�� �¿� ���鹮��(white space)�� ��������, ����("")�� �ƴϰų� <code>null</code>�� �ƴ��� �˻��Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.isNotBlank(null)    = false
	 * StringUtil.isNotBlank("")      = false
	 * StringUtil.isNotBlank("   ")   = false
	 * StringUtil.isNotBlank("han")   = true
	 * StringUtil.isNotBlank(" han ") = true
	 * </pre> 
	 * 
	 * @param str ���ڿ�
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	/**
	 * <p>���ڿ�(String)�� ����("")�̰ų� <code>null</code>�� �˻��Ѵ�.</p>
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
	 * @param str �˻��� ���ڿ�
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	/**
	 * <p>���ڿ�(String)�� ����("")�� �ƴϰų� <code>null</code>�� �ƴ��� �˻��Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.isNotEmpty(null)    = false
	 * StringUtil.isNotEmpty("")      = false
	 * StringUtil.isNotEmpty("   ")   = true
	 * StringUtil.isNotEmpty("han")   = true
	 * StringUtil.isNotEmpty(" han ") = true
	 * </pre>
	 * 
	 * @param str �˻��� ���ڿ�
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	/**
	 * <p>���ڿ��� ���ڷθ� �����Ǿ� �ִ��� �˻��Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.isNumber(null)    = false;
	 * StringUtil.isNumber("")      = false;
	 * StringUtil.isNumber("1234")  = true;
	 * StringUtil.isNumber("abc123")= false;
	 * </pre>
	 * 
	 * @param str �˻��� ���ڿ�
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
	 * ���ڿ��� �̸������� �˻��Ѵ�.
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
	 * ���ڿ� �߿��� �������� ������ �������� ��ȯ�մϴ�.
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
	 *            ��ȯ�� ���ڿ�
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
	 * <p>���ڿ��� ���������� �����Ͽ� ��ȯ�մϴ�.</p>
	 * 
	 * <pre>
	 * StringUtil.toNumber(null)     = 0;
	 * StringUtil.toNumber("")       = 0;
	 * StringUtil.toNumber("1234")   = 1234;
	 * StringUtil.toNumber("abc123") = 0;
	 * </pre>
	 * 
	 * @param str ��ȯ�� ���ڿ�
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
	 * <p>���ڿ�(String)�� �¿� ���鹮��(white space)�� �����Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.trim(null)    = null
	 * StringUtil.trim("")      = ""
	 * StringUtil.trim("   ")   = ""
	 * StringUtil.trim("han")   = "han"
	 * StringUtil.trim(" han ") = "han"
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @return
	 */
	public static String trim(String str) {
		return str == null ? null : str.trim();
	}
	
	/**
	 * <p>���ڿ�(String)�� �¿� ���鹮��(white space)�� ������ �� ����("")�̰ų� <code>null</code>�̸� <code>null</code>�� ��ȯ�Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.trimToNull(null)    = null
	 * StringUtil.trimToNull("")      = null
	 * StringUtil.trimToNull("   ")   = null
	 * StringUtil.trimToNull("han")   = "han"
	 * StringUtil.trimToNull(" han ") = "han"
	 * </pre>
	 *
	 * @param str ���ڿ�
	 * @return
	 */
	public static String trimToNull(String str) {
		return isBlank(str) ? null : trim(str);
	}
	
	/**
	 * <p>���ڿ�(String)�� �¿� ���鹮��(white space)�� ������ �� ����("")�̰ų� <code>null</code>�̸� ����("")�� ��ȯ�Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.trimToEmpty(null)    = ""
	 * StringUtil.trimToEmpty("")      = ""
	 * StringUtil.trimToEmpty("   ")   = ""
	 * StringUtil.trimToEmpty("han")   = "han"
	 * StringUtil.trimToEmpty(" han ") = "han"
	 * </pre>
	 *
	 * @param str ���ڿ�
	 * @return
	 */
	public static String trimToEmpty(String str) {
		return isBlank(str) ? EMPTY : trim(str);
	}
	
	/**
	 * <p>���ڿ��� ���� �ִ� ���ڵ��� ���ڸ� ��ȯ�Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.decode(null)       = null;
	 * StringUtil.decode("")         = "";
	 * StringUtil.decode("abc")      = "abc";
	 * StringUtil.decode("abc&#39;") = "abc'";
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @return
	 */
	public static String decode(String str) {
		str = replace(str, "&#39;", "'");
		str = replace(str, "&#59;", ";");
		str = replace(str, "&#45", "-");
		str = replace(str, "&#62;", ">");
		str = replace(str, "&gt;", ">");
		str = replace(str, "&lt;", "<");

		str = replace(str, "&#46124;", "��");
		str = replace(str, "&#47750;", "�p");
		str = replace(str, "&#50776;", "��");
		str = replace(str, "&#50860;", "�D");
		str = replace(str, "&#54973;", "�O");
		str = replace(str, "&#44248;", "��");
		str = replace(str, "&#46468;", "��");
		str = replace(str, "&#50522;", "��");
		str = replace(str, "&#54855;", "�I");
		str = replace(str, "&#47791;", "��");
		str = replace(str, "&#54631;", "�C");
		str = replace(str, "&#53384;", "��");
		str = replace(str, "&#50043;", "��");
		str = replace(str, "&#50735;", "��");
		str = replace(str, "&#50524;", "��");
		str = replace(str, "&#51902;", "�U");
		str = replace(str, "&#52651;", "��");
		str = replace(str, "&#52573;", "��");
		str = replace(str, "&#50351;", "�F");
		str = replace(str, "&#51053;", "��");
		str = replace(str, "&#48437;", "�v");
		str = replace(str, "&#54643;", "�K");
		str = replace(str, "&#46095;", "��");
		str = replace(str, "&#49968;", "�X");
		str = replace(str, "&#50785;", "��");
		str = replace(str, "&#52924;", "�b");
		str = replace(str, "&#47368;", "��");
		str = replace(str, "&#51115;", "��");
		str = replace(str, "&#47973;", "�o");
		str = replace(str, "&#53240;", "�k");
		str = replace(str, "&#49856;", "��");
		str = replace(str, "&#52658;", "�G");
		str = replace(str, "&#48962;", "��");
		str = replace(str, "&#51781;", "��");
		str = replace(str, "&#49817;", "��");
		str = replace(str, "&#53380;", "Ū");
		str = replace(str, "&#53383;", "��");
		str = replace(str, "&#55144;", "ņ");
		str = replace(str, "&#52935;", "�l");
		str = replace(str, "&#54696;", "��");
		str = replace(str, "&#54640;", "�H");
		str = replace(str, "&#44704;", "��");
		str = replace(str, "&#48577;", "��");
		str = replace(str, "&#48505;", "��");
		str = replace(str, "&#54676;", "�h");
		
		return str;
	}
	
	/**
	 * <p>���ڿ�(String)�� <code>null</code>�̸� �⺻���ڿ��� ��ȯ�Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.defaultIfNull(null, "")    = ""
	 * StringUtil.defaultIfNull("", "")      = ""
	 * StringUtil.defaultIfNull("   ", "")   = "   "
	 * StringUtil.defaultIfNull("han", "")   = "han"
	 * StringUtil.defaultIfNull(" han ", "") = " han "
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @param defaultStr �⺻���ڿ�
	 * @return
	 */
	public static String defaultIfNull(String str, String defaultStr) {
		return str == null ? defaultStr : str;
	}
	
	/**
	 * <p>���ڿ�(String)�� <code>null</code>�̸� ���鹮�ڿ��� ��ȯ�Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.defaultIfNull(null)    = ""
	 * StringUtil.defaultIfNull("")      = ""
	 * StringUtil.defaultIfNull("   ")   = "   "
	 * StringUtil.defaultIfNull("han")   = "han"
	 * StringUtil.defaultIfNull(" han ") = " han "
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @return
	 */
	public static String defaultIfNull(String str) {
		return defaultIfNull(str, EMPTY);
	}
	
	/**
	 * <p>���ڿ�(String)�� �¿� ���鹮��(white space)�� ��������, ����("")�̰ų� <code>null</code>�̸�, �⺻���ڿ��� ��ȯ�Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.defaultIfBlank(null, "")    = ""
	 * StringUtil.defaultIfBlank("", "")      = ""
	 * StringUtil.defaultIfBlank("   ", "")   = ""
	 * StringUtil.defaultIfBlank("han", "")   = "han"
	 * StringUtil.defaultIfBlank(" han ", "") = " han "
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @param defaultStr �⺻���ڿ�
	 * @return
	 */
	public static String defaultIfBlank(String str, String defaultStr) {
		return isBlank(str) ? defaultStr : str;
	}
	
	/**
	 * <p>���ڿ�(String)�� �¿� ���鹮��(white space)�� ��������, ����("")�̰ų� <code>null</code>�̸�, ���鹮�ڿ��� ��ȯ�Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.defaultIfBlank(null)    = ""
	 * StringUtil.defaultIfBlank("")      = ""
	 * StringUtil.defaultIfBlank("   ")   = ""
	 * StringUtil.defaultIfBlank("han")   = "han"
	 * StringUtil.defaultIfBlank(" han ") = " han "
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @return
	 */
	public static String defaultIfBlank(String str) {
		return defaultIfBlank(str, EMPTY);
	}
	
	
	// ----------------------------------------------------------------------
	// ���ڿ� ��
	// ----------------------------------------------------------------------
	/**
	 * <p>�� ���ڿ�(String)�� ��ġ�ϸ� <code>true</code>�� ��ȯ�Ѵ�.</p>
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
	 * @param str1 ù��° ���ڿ�
	 * @param str2 �ι�° ���ڿ�
	 * @return ���ڿ�(String)�� ��ġ�ϸ� <code>true</code>
	 */
	public static boolean equals(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equals(str2);
	}
	
	/**
	 * <p>��ҹ��ڸ� ������, �� ���ڿ�(String)�� ��ġ�ϸ� <code>true</code>�� ��ȯ�Ѵ�.</p>
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
	 * @param str1 ù��° ���ڿ�
	 * @param str2 �ι�° ���ڿ�
	 * @return ��ҹ��ڸ� ������ ���ڿ�(String)�� ��ġ�ϸ� <code>true</code>
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
	}
	
	/**
	 * <p>���ڿ��� ���λ�� �����ϴ����� �Ǵ��Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.startsWith(null, *)    = false
	 * StringUtil.startsWith(*, null)    = false
	 * StringUtil.startsWith("han", "h") = true
	 * StringUtil.startsWith("han", "a") = false
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @param prefix ���λ�
	 * @return
	 */
	public static boolean startsWith(String str, String prefix) {
		if (str == null || prefix == null) {
			return false;
		}
		return str.startsWith(prefix);
	}
	
	/**
	 * <p>���ڿ� offset ��ġ���� ���λ�� �����ϴ����� �Ǵ��Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.startsWith(null, *, 0)    = false
	 * StringUtil.startsWith(*, null, 0)    = false
	 * StringUtil.startsWith("han", "h", 0) = true
	 * StringUtil.startsWith("han", "a", 0) = false
	 * StringUtil.startsWith("han", "a", 1) = true
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @param prefix ���λ�
	 * @param offset �� ���� ��ġ
	 * @return
	 */
	public static boolean startsWith(String str, String prefix, int offset) {
		if (str == null || prefix == null) {
			return false;
		}
		return str.startsWith(prefix, offset);
	}
	
	/**
	 * <p>���ڿ��� ���̻�� ���������� �Ǵ��Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.endsWith(null, *)    = false
	 * StringUtil.endsWith(*, null)    = false
	 * StringUtil.endsWith("han", "h") = false
	 * StringUtil.endsWith("han", "n") = true
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @param prefix ���λ�
	 * @return
	 */
	public static boolean endsWith(String str, String suffix) {
		if (str == null || suffix == null) {
			return false;
		}
		return str.endsWith(suffix);
	}

	/**
	 * <p>���ڿ�(String)�� �˻����ڿ�(String)�� ��� ���ԵǾ� �ִ��� �˻��Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.containCount("haaaan", "a") = 4
	 * </pre>
	 * 
	 * @see java.lang.String#indexOf(String)
	 * @param str ���ڿ�
	 * @param searchStr �˻����ڿ�
	 * @return ���ڿ�(String)�� �˻� ���ڿ��� ���ԵǾ� ������ <code>count</code>,
	 *  ���ڿ�(String)�� �˻� ���ڿ��� ���ԵǾ� ���� ��������, ���ڿ� �Ǵ� �˻����ڿ��� <code>null</code>�϶� <code>0</code>
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
	 * <p>���ڿ�(String)�� �˻����ڿ�(String)�� ���ԵǾ� �ִ��� �˻��Ѵ�.</p>
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
	 * @param str ���ڿ�
	 * @param searchStr �˻����ڿ�
	 * @return ���ڿ�(String)�� �˻� ���ڿ��� ���ԵǾ� ������ <code>true</code>,
	 *  ���ڿ�(String)�� �˻� ���ڿ��� ���ԵǾ� ���� ��������, ���ڿ� �Ǵ� �˻����ڿ��� <code>null</code>�϶� <code>false</code>
	 */
	public static boolean contains(String str, String searchStr) {
		if (str == null || searchStr == null) {
			return false;
		}
		return str.indexOf(searchStr) > INDEX_NOT_FOUND;
	}
	
	/**
	 * <p>���ڿ�(String) �迭�� �˻����ڿ�(String)�� ���ԵǾ� �ִ��� �˻��Ѵ�.</p>
	 * 
	 * @see java.lang.String#indexOf(String)
	 * @param str array ���ڿ� �迭
	 * @param searchStr �˻����ڿ�
	 * @return ���ڿ�(String)�� �˻� ���ڿ��� ���ԵǾ� ������ <code>true</code>,
	 *  ���ڿ�(String)�� �˻� ���ڿ��� ���ԵǾ� ���� ��������, ���ڿ� �Ǵ� �˻����ڿ��� <code>null</code>�϶� <code>false</code>
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
	// ��/�ҹ��� ��ȯ
	// ----------------------------------------------------------------------
	/**
	 * <p>���ڿ�(String)�� �빮�ڷ� ��ȯ�Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.toUpperCase(null)  = null
	 * StringUtil.toUpperCase("han") = "HAN"
	 * StringUtil.toUpperCase("hAn") = "HAN"
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @return �빮�ڷ� ��ȯ�� ���ڿ�
	 */
	public static String toUpperCase(String str) {
		if (str == null) {
			return null;
		}
		return str.toUpperCase();
	}
	
	/**
	 * <p>���� �ε������� ���� �ε������� �빮�ڷ� ��ȯ�Ѵ�.</p>
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
	 * <p>���ڿ�(String)�� �ҹ��ڷ� ��ȯ�Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.toLowerCase(null)  = null
	 * StringUtil.toLowerCase("han") = "han"
	 * StringUtil.toLowerCase("hAn") = "han"
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @return �ҹ��ڷ� ��ȯ�� ���ڿ�
	 */
	public static String toLowerCase(String str) {
		if (str == null) {
			return null;
		}
		return str.toLowerCase();
	}
	
	/**
	 * <p>���� �ε������� ���� �ε������� �ҹ��ڷ� ��ȯ�Ѵ�.</p>
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
	 * <p>�빮�ڴ� �ҹ��ڷ� ��ȯ�ϰ� �ҹ��ڴ� �빮�ڷ� ��ȯ�Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.swapCase(null)  = null
	 * StringUtil.swapCase("Han") = "hAN"
	 * StringUtil.swapCase("hAn") = "HaN"
	 * </pre>
	 * 
	 * @param str ���ڿ�
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
	 * ���ڿ�(String)�� ù��° ���ڸ� �빮�ڷ� ��ȯ�Ѵ�.
	 * 
	 * <pre>
	 * StringUtil.capitalize(null)  = null
	 * StringUtil.capitalize("Han") = "Han"
	 * StringUtil.capitalize("han") = "Han"
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @return ù��° ���ڸ� �빮�ڷ� ��ȯ�� ���ڿ�
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
	 * ���ڿ�(String)�� ù��° ���ڸ� �ҹ��ڷ� ��ȯ�Ѵ�.
	 * 
	 * <pre>
	 * StringUtil.uncapitalize(null)  = null
	 * StringUtil.uncapitalize("han") = "han"
	 * StringUtil.uncapitalize("HAN") = "hAN"
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @return ù��° ���ڸ� �빮�ڷ� ��ȯ�� ���ڿ�
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
	// ���ڿ� �迭 ����/�и�
	// ----------------------------------------------------------------------
	/**
	 * <p>���ڿ� �迭�� �ϳ��� ���ڿ��� ���ս�Ų��.</p>
	 * <p>�迭�� ���ڿ� �߿� <code>null</code>�� ����("")�� �����Ѵ�.</p>
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
	 * @param strArray ���ڿ� �迭
	 * @param separator ������
	 * @return �����ڷ� ������ ���ڿ�
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
	 * <p>���ڿ� �迭�� �ϳ��� ���ڿ��� ���ս�Ų��.</p>
	 * <p>�迭�� ���ڿ� �߿� <code>null</code>�� ����("")�� �����Ѵ�.</p>
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
	 * @param strArray ���ڿ� �迭
	 * @param separator ������
	 * @return �����ڷ� ������ ���ڿ�
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
	 * <p>���ڿ� �迭�� �ϳ��� ���ڿ��� ���ս�Ų��.</p>
	 * <p>�迭�� ���ڿ� �߿� <code>null</code>�� ����("")�� �����Ѵ�.</p>
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
	 * @param strArray ���ڿ� �迭
	 * @param separator ������
	 * @return �����ڷ� ������ ���ڿ�
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
	 * <p>���ڿ� �迭�� �ϳ��� ���ڿ��� ���ս�Ų��.</p>
	 * <p>�迭�� ���ڿ� �߿� <code>null</code>�� ����("")�� �����Ѵ�.</p>
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
	 * @param strArray ���ڿ� �迭
	 * @param separator ������
	 * @return �����ڷ� ������ ���ڿ�
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
	 * @param strArray ���ڿ� �迭
	 * @param searchStr ã�� ����
	 * @return ã�� ���ڸ� ������ ���ڿ� �迭
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
	 * <p>���ڿ��� �����ڷ� �����, ���ڿ� �迭�� �����.</p>
	 * <p>�迭�� ���ڿ� �߿� <code>null</code>�� ����("")�� �����Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.split("h-a-n", '-') = ["h", "a", "n"]
	 * StringUtil.split("h--n", '-')  = ["h", "", "n"]
	 * StringUtil.split(null, *)      = null
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @param separator ������
	 * @return �����ڷ� �������� ���ڿ� �迭
	 */
	public static String[] split(String str, char separator) {
		return split(str, new String(new char[]{separator}));
	}
	
	/**
	 * <p>���ڿ��� �����ڷ� �����, ���ڿ� �迭�� �����.</p>
	 * <p>�迭�� ���ڿ� �߿� <code>null</code>�� ����("")�� �����Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.split("h-a-n", "-") = ["h", "a", "n"]
	 * StringUtil.split("h--n", "-")  = ["h", "", "n"]
	 * StringUtil.split(null, *)      = null
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @param separator ������
	 * @return �����ڷ� �������� ���ڿ� �迭
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
	// ���ڿ� �ڸ���
	// ----------------------------------------------------------------------
	/**
	 * <p>���ڿ�(String)�� �ش� ����(<code>length</code>) ��ŭ, ���ʺ��� �ڸ���.</p>
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
	 * @param str ���ڿ�
	 * @param length ����
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
	 * <p>���ڿ�(String)�� �ش� ����(<code>length</code>) ��ŭ, �����ʺ��� �ڸ���.</p>
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
	 * @param str ���ڿ�
	 * @param length ����
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
	 * <p>���ڿ�(String)�� ���� ��ġ(<code>beginIndex</code>)���� ����( <code>length</code>) ��ŭ �ڸ���.</p>
	 * 
	 * <p>���� ��ġ(<code>beginIndex</code>)�� ������ ���� 0���� �ڵ� ��ȯ�ȴ�.</p>
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
	 * @param str ���ڿ�
	 * @param beginIndex ��ġ(������ ���� 0���� �ڵ� ��ȯ�ȴ�.)
	 * @param length ����
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
	 * <p>���� �ε������� ���ڿ��� �ڴ´�.</p>
	 * <p>���� �ε����� 0���� �۰ų�, ���ڿ��� �ѱ��̺��� ũ�� ����("")�� ��ȯ�Ѵ�.</p>
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
	 * @param beginIndex ���� �ε���(0���� ����)
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
	 * <p>���� �ε������� �� �ε������� ���ڿ��� �ڴ´�.</p>
	 * <p>���� �ε����Ǵ� �� �ε�����  0���� ������ ����("")�� ��ȯ�Ѵ�.</p>
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
	 * <p>ó�� �߰��� �������� ��ġ���� ���ڿ��� �ڸ���.</p>
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
	 * @param str ���ڿ�
	 * @param separator ������
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
	 * <p>���������� �߰��� �������� ��ġ���� ���ڿ��� �ڸ���.</p>
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
	 * @param str ���ڿ�
	 * @param separator ������
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
	 * <p>ó�� �߰��� �������� ��ġ �������� ���ڿ��� �ڸ���.</p>
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
	 * @param str ���ڿ�
	 * @param separator ������
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
	 * <p>���������� �߰��� �������� ��ġ �������� ���ڿ��� �ڸ���.</p>
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
	 * @param str ���ڿ�
	 * @param separator ������
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
	 * <p>���� ���ں��� �� ���ڿ����� �ڸ���.</p>
	 * 
	 * <pre>
	 * StringUtil.substringBetween(null, *, *)       = null
	 * StringUtil.substringBetween(*, null, *)       = null
	 * StringUtil.substringBetween(*, *, null)       = null
	 * StringUtil.substringBetween("h<a>n", "<", ">") = "a"
	 * StringUtil.substringBetween("h<a><b>n", "<", ">") = "a"
	 * </pre>
	 * 
	 * @param str ���ڿ�
	 * @param separator ������
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
				// ���� ������ null�� ������, �������� ������...
			}
		}
		return null;
	}
    
    /**
     * <p>�Է��� ���ڿ��� ������ �ִ� ���̸�ŭ, ���� �ٷ� ������ ��ȯ�Ѵ�.</p>
     * <p>����(" ")�� �������� �� �ٲ��� �õ��Ѵ�.</p>
     * 
     * @param str
     * @param maxLineLength ������ �ִ� ����
     * @return
     */
    public static List<String> wrap(String str, int maxLineLength) {
    	if (str == null) {
    		return null;
    	}
    	List<String> lines = new ArrayList<String>();
        if (str.length() <= maxLineLength || str.indexOf(' ') == INDEX_NOT_FOUND) {
        	// ��ü ���̰� �ִ� ���̺��� ª�ų�, �����Ҽ� �ִ� ������ �ȵǸ� �״�� ��ȯ�Ѵ�.
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
	 * <p>���ڿ��� �ش� ���̺��� ũ��, �ڸ� �� ���Ӹ��� �ٿ��ش�.</p>
	 * <p>���̴� �⺻���ڵ�(����/���ڵ�)�� 1����, �ٱ���(�ѱ۵�)�̸� 2�� ����Ѵ�.</p>
	 * 
	 * <pre>
	 * StringUtil.curtail(null, *, *) = null
	 * StringUtil.curtail("abcdefghijklmnopqr", 10, null) = "abcdefghij"
	 * StringUtil.curtail("abcdefghijklmnopqr", 10, "..") = "abcdefgh.."
	 * StringUtil.curtail("�ѱ��� ����սô�.", 10, null)   = "�ѱ��� ���"
	 * StringUtil.curtail("�ѱ��� ����սô�.", 10, "..")   = "�ѱ��� ��.."
	 * </pre>
	 * 
	 * 
	 * @param str ���ڿ�
	 * @param size ����(byte ����)
	 * @param tail ���Ӹ�
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
	// �е�
	// ----------------------------------------------------------------------
	/**
	 * <p>���ʺ��� ũ�⸸ŭ �е����ڷ� ä���.</p>
	 * 
	 * <pre>
	 * StringUtil.leftPad("han", 5, " ")    = "  han"
	 * StringUtil.leftPad("han", 5, "123")  = "12han"
	 * StringUtil.leftPad("han", 10, "123") = "1231231han"
	 * StringUtil.leftPad("han", -1, " ")   = "han"
	 * </pre>
	 * 
	 * @param str
	 * @param size ũ��
	 * @param padStr �е�����
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
			// �е��� �ʿ䰡 ����
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
	 * <p>�����ʺ��� ũ�⸸ŭ �е����ڷ� ä���.</p>
	 * 
	 * <pre>
	 * StringUtil.rightPad("han", 5, " ")    = "han  "
	 * StringUtil.rightPad("han", 5, "123")  = "han12"
	 * StringUtil.rightPad("han", 10, "123") = "han1231231"
	 * StringUtil.rightPad("han", -1, " ")   = "han"
	 * </pre>
	 * 
	 * @param str
	 * @param size ũ��
	 * @param padStr �е�����
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
			// �е��� �ʿ䰡 ����
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
	 * <p>���ڸ� �˹ٺ����� �����Ѵ�.</p>
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
    * HTML �������� Ư�� ���� ��ȯ
    * @param    s       ���� ���ڿ�
    * @return           ġȯ�� ���ڿ�
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
    * XML �������� Ư�� ���� ��ȯ
    * @param    s       ���� ���ڿ�
    * @return           ġȯ�� ���ڿ�
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
    ���ڿ����� �������ڸ� �� ���ڷ� ��ü�ؼ� ��ȯ
    @param   s        ���ڿ�
    @param   oldSub   ��������
    @param   newSub   �� ����
    @return  String   ��� ���ڿ�
    */
    public static String replace(String s, char oldSub, char newSub) {
        return replace(s, oldSub, new Character(newSub).toString());
    }

    /**
     ���ڿ����� �������ڸ� �� ���ڷ� ��ü�ؼ� ��ȯ
     @param   s        ���ڿ�
     @param   oldSub   ��������
     @param   newSub   �� ����
     @return  String   ��� ���ڿ�
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
    ���ڿ����� �������ڸ� �� ���ڷ� ��ü�ؼ� ��ȯ
    @param   s        ���ڿ�
    @param   oldSub   ��������
    @param   newSub   �� ����
    @return  String   ��� ���ڿ�
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
  ���ڿ����� �������ڸ� �� ���ڷ� ��ü�ؼ� ��ȯ
  @param   s        ���ڿ�
  @param   oldSubs   �������ڹ迭
  @param   newSubs   �� ���ڹ迭
  @return  String   ��� ���ڿ�
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
���ڿ����� �������ڸ� �� ���ڷ� ��ü�ؼ� ��ȯ
@param   s        ���ڿ�
@param   oldSubs   �������ڹ迭
@param   newSubs   �� ���ڹ迭
@return  String   ��� ���ڿ�
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
   *  String���� substring�� ã�� �ٲٰ����ϴ� �ٸ� string���δ�ġ�ؼ�
   *  �Ѱ��ִ¸޼ҵ�. ����Ե� java.lang.String�� �̷��� �޼ҵ尡 ����ϴ�.
   *  ���� ���Ŀ� ����� ������..^^
   *  @param search ã�������ϴ� �ܾ�
   *  @param replace �ٲٰ����ϴ� �ܾ�
   *  @param source ��ü String
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
	  ���ڿ��� ��¥ �������� ��ȯ
	  @param   d    ���ڿ�
	  @param   delim   ���� ����
	  @return  String  ��� ��
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
	 * ��Ʈ�� parameter�� ��� html �±׸� �����Ѵ�.
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
     * \n -> <br>�� ġȯ
     */
    public static String newLineToBr(String s) {
    	if (s == null)
    		return null;
    	
    	return replace(s, "\n", "<br>");
    }
    
	/**
	 * �˻����� Ư�����ڸ� ���ܽ�Ų��.
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
	 * ��Ʈ�� parameter�� ��� html �±׸� �����Ѵ�.
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