package org.mrfw;

import java.util.*;

import com.fsck.k9.mail.Message;
import edu.kaist.swrc.canceldetector.CancelDetector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mrfw.helper.*;

import android.util.Log;

/**
 * KAIST SEMANTIC WEB RESEARCH CENTER
 * @author DongHyun Choi, KyoungRyol Kim
 * @version 1.0
 * @date 2011. 12. 10
 *
 */
public class MailToAppointment {

    /**
     * 입력으로 주어진 한국어 E-mail이 포함하는 모임의 일정 정보들을 추출한다.
     * 추출된 정보들은 Appointment 객체의 인스턴스 형태로 반환된다.
     * 하나의 E-mail은 여러 가지의 모임 일정 정보를 포함할 수 있으므로, 반환되는 형태는 Appointment 인스턴스의 LinkedList 형태가 된다.
     *
     * 이 모듈은 인터넷 억세스를 필요로 한다.
     *
     * @param m 추출 대상이 되는 한국어 E-mail.
     * @return 입력으로 들어온 한국어 E-mail로부터 얻어진 모임 일정 정보의 List.
     */
	public static LinkedList<Appointment> extractFromMail(Mail m, final Message message){
		LinkedList<Appointment> ret	= new LinkedList<Appointment>();

		System.out.println("들어왔다잉");
		String request_url = Constants.URL_MEETINGINFO_EXTRACTOR;
		try{
			String result = JSONHelper.getJSON(request_url,  "sentDate=" + m.getDate().getTime()/1000 + "&body="+m.getBody()+ "&command=ExtractEmail", false);
		//	String result = JSONHelper.getJSON(request_url,  "sentDate=" + new Date(System.currentTimeMillis()).getTime()/1000 + "&body="+m.getBody() + "&command=ExtractEmail", false);
		//	System.out.println(request_url+"sentDate=" + m.getDate().getTime()/1000 + "&body="+m.getBody() + "&command=ExtractEmail");
			JSONObject jroot	= new JSONObject(result);
			JSONArray apListArr	= jroot.getJSONArray("MTLIST");
			for(int i = 0; i < apListArr.length(); i++){
				Appointment ap	= new Appointment();
				JSONObject jAP	= apListArr.getJSONObject(i);
				String stime	= jAP.getString("STIME");
				String etime	= jAP.getString("ETIME");
				String isHeldAt	= jAP.getString("ISHELDAT");
				String landmark	= jAP.getString("LANDMARK");

				ap.setStartDate(parseDate(stime));
				ap.setEndDate(parseDate(etime));
				
				ap.setLocation(isHeldAt);
				ap.setLandmark(landmark);

// woo. 2014-08-20 아래 부분 추가를 위해 주석 처리.
//				if(ap.getLocation().equals("취소")){
//                    ap.setLocation("");
//					ap.setType("Cancel");
//				}else{
//					ap.setType("Add");
//				}

                /**
                 * woo. 2014-08-20 일정 추가, 취소, 변경 setType 을 위해 추가.
                 */
                String subject = message.getSubject();

                Log.i("LogCat", "MailToAppointment 클래스 안에서 메일 제목: " + subject);
                CancelDetector cd = new CancelDetector();
                if(cd.isCanceled(subject)) {
                    ap.setType("Cancel");
                }
                else if(cd.isUpdated(subject)) {
                    ap.setType("Update");
                }
                else {
                    ap.setType("Add");
                }
                // woo. 2014-08-20.

				ret.add(ap);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	private static Date parseDate(String time){
		Calendar c	= Calendar.getInstance();
		c.set(1, 1, 1, 1, 0, 0 );
		StringTokenizer tok	= new StringTokenizer(time, " ");
		String weekDay	= tok.nextToken();
		String month	= tok.nextToken();
		int m			= 0;
		if(month.equals("Jan")){
			m	= 1;			
		}
		if(month.equals("Feb")){
			m	= 2;
		}
		if(month.equals("Mar")){
			m = 3;
		}
		if(month.equals("Apr")){
			m = 4;
		}
		if(month.equals("May")){
			m = 5;
		}
		if(month.equals("Jun")){
			m = 6;
		}
		if(month.equals("Jul")){
			m = 7;
		}
		if(month.equals("Aug")){
			m = 8;
		}
		if(month.equals("Sep")){
			m= 9;
		}
		if(month.equals("Oct")){
			m = 10;
		}
		if(month.equals("Nov")){
			m = 11;
		}
		if(month.equals("Dec")){
			m = 12;
		}
		int day	=  Integer.parseInt(tok.nextToken());
		
		StringTokenizer tok2	= new StringTokenizer(tok.nextToken(), ":");
		int hour				= Integer.parseInt(tok2.nextToken());
		int min					= Integer.parseInt(tok2.nextToken());
		int second				= Integer.parseInt(tok2.nextToken());
		tok.nextToken();
		int year				= Integer.parseInt(tok.nextToken());
		
		c.clear();
		c.set(year, m - 1, day, hour, min, second);
		return c.getTime();
	}


	
	public static void main(String[] args){	
		Mail m	= new Mail();
		m.setSubject("미팅 일정");
		m.setBody(
				"오늘 3시 반 일정은 취소되었습니다.\n"
					);
		
		Calendar c	= Calendar.getInstance();
		c.set(0, 0, 0, 0, 0, 0);
		try{
			c.set(Calendar.YEAR, 2010);
			c.set(Calendar.MONTH, 11);
			c.set(Calendar.DATE, 27);
			c.set(Calendar.HOUR, 17);
			c.set(Calendar.MINUTE, 45);
			m.setDate(c.getTime());
		}catch(Exception ex){
		}
		
		LinkedList<Appointment> apList	= MailToAppointment.extractFromMail(m, null);
		Iterator<Appointment> apIter	= apList.iterator();
		int cnt							= 0;
		while(apIter.hasNext()){
			Appointment ap				= apIter.next();
			System.out.println("Meeting #" + (++cnt));
			System.out.println("StartTime: " + ap.getStartDate());
			System.out.println("EndTime  : " + ap.getEndDate());
			System.out.println("Location : " + ap.getLocation());
			System.out.println("Landmark : " + ap.getLandmark());
		}
	}

}
