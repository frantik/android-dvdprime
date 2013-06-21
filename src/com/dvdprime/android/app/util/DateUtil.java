package com.dvdprime.android.app.util;

/**
 * @class DateUtils ver 1.0.0
 * @brief ?좎쭨???쒓컙???쒖뼱??
 *
 * registered date 20081217
 * programmed by Seok Kyun. Choi. (理쒖꽍洹?
 * http://syaku.tistory.com
 */

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class DateUtil {

	  private static SimpleDateFormat formatter;
	  private static DecimalFormat df;

	  /**
	   * @method : getToday()
	   * @brief : 오늘 날짜를 구한다.
	   */
	  public static String getToday() {
		  SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd" );
		  Date currentTime = new Date();
		  String dTime = formatter.format ( currentTime );	  
		  
		  return dTime;
	  }

	  /**
	   * @method : getYesterday()
	   * @brief : 어제 날짜를 구한다.
	   */
	  public static String getYesterday ()
	  {
		  SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd" );
		  Date yesterday = new Date ();
		  yesterday.setTime ( yesterday.getTime() - ( (long) 1000 * 60 * 60 * 24 ) );
		  String dTime = formatter.format ( yesterday );	  
		
		  return dTime;
	  }
	  
	  /**
	  * @method : getDate(parameter,parameter2)
	  * @brief : 정규표현식을 이용하여, 날짜를 제어한다. 각 시분초는 구분자가 있어야 한다.
	  * @parameters {
	        parameter : (String) $1 = 년 , $2 = 월 , $3 = 일 , $4 = 시 , $5 = 분 , $6 = 초
	        parameter2 : (String) 구분자(-_:./\s) 를 가진 날짜
	      }
	  * @return : (String) 정규표현식 시간 || 00:00:00
	  */
	  public static String getDate(String patten,String date) {
	    if (StringUtil.isEmpty(date)) return date;
	    String sysdate_patten = "(^[0-9]*)[-_:.\\/\\s]?([0-9]*)[-_:.\\/\\s]?([0-9]*)[-_:.\\/\\s]?([0-9]*)[-_:.\\/\\s]?([0-9]*)[-_:.\\/\\s]?([0-9]*)(.*)$";
	    Pattern date_comp = Pattern.compile(sysdate_patten);
	    if (date_comp.matcher(date).find()) return date.replaceAll(sysdate_patten,patten);
	    else return getDate(patten,"00:00:00");
	  }

	  /**
	  * @method : setDate(parameter)
	  * @brief : 날짜를 설정하고 Date 객체로 반환한다.
	  * @parameters {
	        parameter : (String) 날짜
	      }
	  * @return : (Date)
	  */
	  public static Date setDate(String date) throws Exception {
	    date = date("yyyy-MM-dd HH:mm:ss",date);
	    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return (Date) formatter.parse(date);
	  }

	  /**
	   * @method : getTime(long timestamp)
	   * @brief : 타임스탬프를 이용하여 날짜, 시간을 구한다.
	   * @parameters {
	   * 	parmeter : (long) System.currentTimeMillis();
	   * }
	   * @return : (String) 정규표현식 날짜,시간
	   */
	  public static String getTime(long timestamp) {
		  if (timestamp < 0)
			  return "";
		  
		  SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  String strNow = sdfNow.format(new Date(timestamp));
		  
		  return strNow;
	  }
	  /**
	  * @method : getTime(parameter,parameter2)
	  * @brief : 정규표현식을 이용하여, 시간을 제어한다. 각 시분초는 구분자가 있어야 한다.
	  * @parameters {
	        parameter : (String) $1 = 시 , $2 = 분 , $3 = 초
	        parameter2 : (String) 구분자(-_:./\s) 를 가진 시간
	      }
	  * @return : (String) 정규표현식 시간 || 00:00:00
	  */
	  public static String getTime(String patten,String time) {
	    if (StringUtil.isEmpty(time)) return time;
	    String time_patten = "(^[0-9]*)[-_:.\\/\\s]?([0-9]*)[-_:.\\/\\s]?([0-9]*)(.*)$";
	    Pattern time_comp = Pattern.compile(time_patten);
	    if (time_comp.matcher(time).find()) return time.replaceAll(time_patten,patten);
	    else return getTime(patten,"00:00:00");
	  }
	  
	  /**
	  * @method : setTime(parameter)
	  * @brief : 시간을 설정하고 Date 객체로 반환한다.
	  * @parameters {
	        parameter : (String) 시간
	      }
	  * @return : (Date)
	  */
	  public static Date setTime(String time) throws Exception {
	    time = time("HH:mm:ss",time);
	    formatter = new SimpleDateFormat("HH:mm:ss");
	    return (Date) formatter.parse(time);
	  }

	  /**
	  * @method : date(parameter, parameter2)
	  * @brief : SimpleDateFormat 이용하여 날짜를 반환한다.
	  * @parameters {
	       parameter : (String) SimpleDateFormat 클래스의 포맷
	       parameter2 : (String || Date) SimpleDateFormat 포맷 시간
	     }
	  * @return : (String) 날짜
	  */
	  public static String date() throws Exception {
	    return date("yyyy-MM-dd HH:mm:ss");
	  }
	  public static String date(String format) throws Exception {
	    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String date = formatter.format(new Date());
	    return date(format,date);
	  }
	  public static String date(String format, Date date) throws Exception {
	    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return date(format,formatter.format(date));
	  }
	  public static String date(String format, String date) throws Exception {
	    if (StringUtil.isEmpty(format)) format = "yyyy-MM-dd HH:mm:ss";
	    if (StringUtil.isEmpty(date)) return null;

	    date = date.replaceAll("[^0-9]+","");
	    date = StringUtil.rightPad(date,14,"0");
	    date = date.replaceAll("(^[0-9]{4})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})","$1-$2-$3 $4:$5:$6");
	    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date redate = formatter.parse(date);
	    formatter = new SimpleDateFormat(format);
	    return formatter.format(redate);
	  }


	  /**
	  * @method : time(parameter, parameter2)
	  * @brief SimpleDateFormat 이용하여 날짜를 반환한다.
	  * @parameters {
	        parameter : (String) SimpleDateFormat 클래스의 포맷
	        parameter2 : (String || Date) SimpleDateFormat 포맷 시간
	    }
	  * @return : (String) 시간
	  */
	  public static String time() throws Exception {
	    return time("HH:mm:ss");
	  }
	  public static String time(String format) throws Exception {
	    formatter = new SimpleDateFormat("HH:mm:ss");
	    String time = formatter.format(new Date());
	    return time(format,time);
	  }
	  public static String time(String format, String time) throws Exception {
	    time = time.replaceAll("[^0-9]+","");
	    time = StringUtil.rightPad(time,6,"0");
	    time = StringUtil.leftPad(time,14,"0");
	    return date(format,time);
	  }
	  /**
	  * @method : timespace(parameter, parameter2,parameter3)
	  * @brief 시작시간과 종료시간의 간격을 구합니다.
	  * @parameters {
	        parameter : (String) 시작시간
	        parameter2 : (String) 종료시간
	        parameter3 : (String) SimpleDateFormat 클래스의 포맷
	    }
	  * @return : (String) SimpleDateFormat 포맷 시간
	  */
	  public static String timespace(String stime, String etime) throws Exception {
	    return timespace(stime,etime,"HH:mm:ss");
	  }
	  public static String timespace(String stime, String etime, String format) throws Exception {
	    try {
	      if (StringUtil.isEmpty(stime) || StringUtil.isEmpty(etime)) throw new Exception("parameter null");

	      stime = stime.replaceAll("[^0-9]+","");
	      etime = etime.replaceAll("[^0-9]+","");

//	      int s = Integer.parseInt(stime);
//	      int e = Integer.parseInt(etime);
	      stime = StringUtil.rightPad(stime,6,"0");
	      etime = StringUtil.rightPad(etime,6,"0");
	      stime = date("yyyy-MM-dd HH:mm:ss","19700101" + stime);
	      etime = date("yyyy-MM-dd HH:mm:ss","19700101" + etime);

	      formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      Date sdate = formatter.parse(stime);
	      Date edate = formatter.parse(etime);
	      // 9시간 (540*60*1000 = 32400000) 오차 제거하기
	      Long ret = (edate.getTime() - sdate.getTime()) - 32400000;
	      String ext = formatter.format(ret);

	      return date(format,ext);
	    } catch (Exception e) {
	      return time(format,"000000");
	    }
	  }
	  /**
	  * @method : timeAdd(parameter, parameter2,parameter3)
	  * @brief 구분자를 포함한 두 시간을 합산합니다. 분 초 중에 60 이상인 경우 시간 혹은 분을 반올림합니다.
	  * @parameters {
	        parameter : (String) 구분자(-_:./\s)를 가진 사간
	        parameter2 : (String) 구분자(-_:./\s)를 가진 시간
	        parameter3 : (String)  $1 = 시 , $2 = 분 , $3 = 초
	    }
	  * @return : (String) 정규표현식 시간 : 00:00:00
	  */
	  public static String timeAdd(String time,String time2,String patten) throws Exception {
	    String ret = "00:00:00";
	    if (StringUtil.isEmpty(patten)) patten = "$1:$2:$3";

	    int sh = Integer.parseInt(getTime("$1",time));
	    int sm = Integer.parseInt(getTime("$2",time));
	    int ss = Integer.parseInt(getTime("$3",time));
	    int eh = Integer.parseInt(getTime("$1",time2));
	    int em = Integer.parseInt(getTime("$2",time2));
	    int es = Integer.parseInt(getTime("$3",time2));

	    try {
	      int h = sh + eh;
	      int s = ss + es;
	      int m = 0;
	      if (s > 60) { 
	        Double mm = Math.floor(s/60);
	        df = new DecimalFormat("0"); 
	        int mmm = Integer.parseInt(df.format(mm));

	        s = s - (mmm * 60); 
	        m = m + mmm;
	      }

	      m = m + sm + em;
	      if (m > 60) { 
	        Double hh = Math.floor(m/60);
	        df = new DecimalFormat("0"); 
	        int hhh = Integer.parseInt(df.format(hh));

	        m = m - (hhh * 60); 
	        h = h + hhh;
	      }

	      ret = StringUtil.leftPad(Integer.toString(h),2,"0") + ":" + StringUtil.leftPad(Integer.toString(m),2,"0") + ":" + StringUtil.leftPad(Integer.toString(s),2,"0");
	    } catch (Exception e) {
	      ret = "00:00:00";
	    }

	    return getTime(patten,ret);
	  }

}