/**
 * 
 */
package com.fsck.k9.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.fsck.k9.R;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * <p>Title: CreateEvent.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: KAIST., Ltd</p>
 * 
 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
 * @version v 1.0 2011. 8. 26.
 */

@SuppressLint("NewApi") public class CreateEvent extends Activity implements OnClickListener{

	String title;
	String location;
	String stime;
	String etime;
	Date date_stime;
	Date date_etime;
	
	//2012.12.05  by kyungtae lim for handling cancelation
	String sender;
	String type;
	String cc;
	String to;
	String subject;
	
	String mail_id;
	String mail_pwd;
	String uuid;

	Uri calendarUri;
	Uri eventUri;
	Uri reminderUri;

	public boolean isTablet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_event);
		
		isTablet = isTablet();
		
		if(isTablet == true){
			setContentView(R.layout.create_event);
		}else{
			setContentView(R.layout.create_event_phone);
		}

		// get parameters from Intent
		Intent intent = getIntent();
		title = intent.getExtras().getString("title");		
		location = intent.getExtras().getString("location");		
		stime = intent.getExtras().getString("stime");
		etime = intent.getExtras().getString("etime");
		sender = intent.getExtras().getString("sender");
		type = intent.getExtras().getString("type");
		
		mail_id = intent.getExtras().getString("mail_id");
		mail_pwd = intent.getExtras().getString("mail_pwd");
		
		cc = intent.getExtras().getString("cc");
		to = intent.getExtras().getString("to");
		subject = intent.getExtras().getString("subject");
		
		Log.e("cc", cc);
		Log.e("to", to);
		Log.e("subject", subject);
		
		
		// set values from the parameters
		((TextView)findViewById(R.id.editTextTitle)).setText(title);
		((TextView)findViewById(R.id.editTextLocation)).setText(location);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			// 10-13 11:40:49.162: WARN/System.err(5051): java.text.ParseException: Unparseable date: "Thu Oct 13 10:00:00 KST 2011" (at offset 0)
			
			date_stime = parseDate(stime);
			date_etime = parseDate(etime);
			if(date_stime.equals(date_etime)){
				// ����/���� �ð��� ������ ���, ����ð����� +1 Hour 
				Calendar cal_etime = Calendar.getInstance();
				cal_etime.setTime(date_etime);
				cal_etime.add(Calendar.HOUR, 1);
				date_etime = cal_etime.getTime();
			}
			
			int stime_year = date_stime.getYear() + 1900;
			int stime_month = date_stime.getMonth();
			int stime_day = date_stime.getDate();
			int stime_hour = date_stime.getHours();
			int stime_min = date_stime.getMinutes();
			
			int etime_year = date_etime.getYear() + 1900;
			int etime_month = date_etime.getMonth();
			int etime_day = date_etime.getDate();
			int etime_hour = date_etime.getHours();
			int etime_min = date_etime.getMinutes();
			
			
			// datePicker, timePicker ��Ʈ�ѿ� �ð� �ݿ�
			((DatePicker)findViewById(R.id.datePicker1)).updateDate(stime_year, stime_month, stime_day);
			((TimePicker)findViewById(R.id.timePicker1)).setCurrentHour(stime_hour);
			((TimePicker)findViewById(R.id.timePicker1)).setCurrentMinute(stime_min);
			((DatePicker)findViewById(R.id.datePicker2)).updateDate(etime_year, etime_month, etime_day);
			((TimePicker)findViewById(R.id.timePicker2)).setCurrentHour(etime_hour);
			((TimePicker)findViewById(R.id.timePicker2)).setCurrentMinute(etime_min);
			
			
			//Spinner init			
			int maxContacts = 100;
			int maxContactsPos = 5;
			Spinner anniversary = null;
			ArrayAdapter<CharSequence> maxContactsAdapter= null;

			anniversary = (Spinner)findViewById(R.id.selAnybox);
			maxContactsAdapter = ArrayAdapter.createFromResource(this, 
			    R.array.anniversary, 
			    android.R.layout.simple_spinner_item);
			maxContactsAdapter.setDropDownViewResource(
			    android.R.layout.simple_spinner_dropdown_item);
			anniversary.setAdapter(maxContactsAdapter);
			//--- 선택값을 지정할 때마다 onItemSelected 함수가 호출
			anniversary.setSelection(maxContactsPos);      
			
			//Spinner init			
//			int maxContacts2 = 100;
			int maxContactsPos2 = 0;
			Spinner remindersars = null;
			ArrayAdapter<CharSequence> maxContactsAdapter2= null;

			remindersars = (Spinner)findViewById(R.id.selReminderbox1);
			maxContactsAdapter2 = ArrayAdapter.createFromResource(this, 
			    R.array.remindersary, 
			    android.R.layout.simple_spinner_item);
			maxContactsAdapter2.setDropDownViewResource(
			    android.R.layout.simple_spinner_dropdown_item);
			remindersars.setAdapter(maxContactsAdapter2);
			//--- 선택값을 지정할 때마다 onItemSelected 함수가 호출
			remindersars.setSelection(maxContactsPos2);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Toast.makeText(this, stime + ", " + etime, Toast.LENGTH_SHORT).show();
		//Toast.makeText(this, "" + date_stime.getYear() + "." +date_stime.getMonth() + "."+date_stime.getDay(), Toast.LENGTH_SHORT).show();

		// Create Event Button
		// ����/����ð�, ����, ��� �� ������Ʈ �ݿ��� ������ ������
		Button btnEvent = (Button) findViewById(R.id.btnCreateEvent);
		btnEvent.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				String n_title = ((TextView)findViewById(R.id.editTextTitle)).getText().toString();
				String n_location = ((TextView)findViewById(R.id.editTextLocation)).getText().toString();
				DatePicker datePicker1 = ((DatePicker)findViewById(R.id.datePicker1));
				DatePicker datePicker2 = ((DatePicker)findViewById(R.id.datePicker2));
				TimePicker timePicker1 = ((TimePicker)findViewById(R.id.timePicker1));
				TimePicker timePicker2 = ((TimePicker)findViewById(R.id.timePicker2));
		
			
				Date n_date_stime = new Date(datePicker1.getYear()-1900, datePicker1.getMonth(), datePicker1.getDayOfMonth(), timePicker1.getCurrentHour(), timePicker1.getCurrentMinute());
				Date n_date_etime = new Date(datePicker2.getYear()-1900, datePicker2.getMonth(), datePicker2.getDayOfMonth(), timePicker2.getCurrentHour(), timePicker2.getCurrentMinute());
				CalendarManager.addToCalendar(getApplicationContext(), n_title, n_date_stime, n_date_etime, n_location);
			}
		});
	}
	
	@Override
	public void onClick(View v) {

	}

	private static Date parseDate(String time){
		Calendar c	= Calendar.getInstance(); 
		c.set(1, 1, 1, 1, 0, 0 );
		StringTokenizer tok	= new StringTokenizer(time, " ");
		String weekDay	= tok.nextToken();
		String month	= tok.nextToken();
		int m			= 0;
		if(month.equals("Jan")){
			m	= 1;					}
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

	
	/**
	 * Adds the event to a calendar. It lets the user choose the calendar with AlertDialog
	 * @param ctx Context ( Please use the application context )
	 * @param title title of the event
	 * @param dtstart Start time: The value is the number of milliseconds since Jan. 1, 1970, midnight GMT.
	 * @param dtend End time: The value is the number of milliseconds since Jan. 1, 1970, midnight GMT.
	 * @param location Location : location 
	 */
/*	private void addToCalendar(Context ctx, final String title, final long dtstart, final long dtend, final String location) {
		final ContentResolver cr = ctx.getContentResolver();

		ContentResolver contentResolver = ctx.getContentResolver();

		if (android.os.Build.VERSION.SDK_INT <= 7 ){
			// the old way
			calendarUri = Uri.parse("content://calendar/calendars");
			eventUri    = Uri.parse("content://calendar/events");
			reminderUri = Uri.parse("content://calendar/reminders");
		}else{
			// the new way
			calendarUri = Uri.parse("content://com.android.calendar/calendars");
			eventUri    = Uri.parse("content://com.android.calendar/events");
			reminderUri = Uri.parse("content://com.android.calendar/reminders");
		}

		Cursor cursor = contentResolver.query(calendarUri, new String[]{ "_id", "displayname" }, null, null, null);
		
		if ( cursor.moveToFirst() ) {
			
			final String[] calNames = new String[cursor.getCount()];
			final int[] calIds = new int[cursor.getCount()];
			for (int i = 0; i < calNames.length; i++) {
				calIds[i] = cursor.getInt(0);
				calNames[i] = cursor.getString(1);
				cursor.moveToNext();
				System.out.println("Id: " + calIds[i] + " Display Name: " + calNames[i]);
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(CreateEvent.this);
			builder.setSingleChoiceItems(calNames, -1, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {					
					ContentValues cv = new ContentValues();
					
					
					System.out.println(dtstart);
					System.out.println(dtend);
					
				  try{
				      HttpClient client = new DefaultHttpClient();
//				      String postURL = "http://143.248.135.212:8888/FrontController";
                      String postURL = "http://143.248.135.210:8889/FrontController";
				      HttpPost post = new HttpPost(postURL);
				      List<NameValuePair> params = new ArrayList<NameValuePair>();
				      params.add(new BasicNameValuePair("command","CalendarAdd"));
				      params.add(new BasicNameValuePair("startTime", new String(dtstart)));
				      params.add(new BasicNameValuePair("startTime", dtend));
				      params.add(new BasicNameValuePair("subject","test"));
				      params.add(new BasicNameValuePair("contents","test"));
				      UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
				      post.setEntity(ent);
				      HttpResponse responsePOST = client.execute(post);
				      HttpEntity resEntity = responsePOST.getEntity();
				      if(resEntity !=null ){
				       Log.w("RESPONSE", EntityUtils.toString(resEntity));
				       Log.w("RESPONSE", "성공임");
				      }else{
				       Log.w("RESPONSE", "실패여");
				      }
				
//					    EntityUtils.toString(resEntity)
				      
				     }catch(Exception e){
				      
				     }
					
					
					
					
					
					
					cv.put("calendar_id", calIds[which]);
					cv.put("title", title);
					cv.put("dtstart", dtstart );
					cv.put("hasAlarm", 1);
					cv.put("dtend", dtend);	
					cv.put("eventLocation", location);
					Uri newEvent = cr.insert(eventUri, cv);					
					if (newEvent != null) {
						long id = Long.parseLong( newEvent.getLastPathSegment() );
						ContentValues values = new ContentValues();
						values.put( "event_id", id );
						values.put( "method", 1 );
						values.put( "minutes", 15 ); // 15 minuti
						cr.insert( reminderUri, values );
					}
					dialog.cancel();
					dialog.dismiss();
					Toast.makeText(CreateEvent.this, "Event added successfully!", Toast.LENGTH_SHORT).show();
				}				
			});
			
			builder.create().show();
		}
		cursor.close();		 
	}*/
	
	/*
	private void addToCalendar(Context ctx, final String title, final Date dtstart, final Date dtend, final String location) {

			String sTime = 	""+	dtstart.getTime();
			String eTime = 	""+	dtend.getTime();
			String command = "";
			
			System.out.println(sTime);
			System.out.println(eTime);
			
			if(this.type.equals("Add")){
				command = "CalendarAdd";
			}else if(this.type.equals("Cancel")){
				command = "CalendarDelete";
			}
		
		//	System.out.println(this.sender+"============================================" +this.type +"==-===================================");
			
		  try{
			  
			  //2012-11-16T12:00:00
		      HttpClient client = new DefaultHttpClient();
//		      String postURL = "http://143.248.135.212:8888/FrontController";
//		      String postURL = "http://203.253.22.176:8889/FrontController";
		      String postURL = "http://143.248.135.210:8889/FrontController";
		      HttpPost post = new HttpPost(postURL);
		      List<NameValuePair> params = new ArrayList<NameValuePair>();
		      params.add(new BasicNameValuePair("command", command));
		      params.add(new BasicNameValuePair("startTime", sTime));
		      params.add(new BasicNameValuePair("endTime", eTime));
		      params.add(new BasicNameValuePair("title",title));
		      params.add(new BasicNameValuePair("contents",location));
		      params.add(new BasicNameValuePair("sender",this.sender));
		      params.add(new BasicNameValuePair("type",this.type));
		      
		      params.add(new BasicNameValuePair("subject",subject));
		      
		      params.add(new BasicNameValuePair("cc",cc));
		      params.add(new BasicNameValuePair("to",to));
		      
//		      Log.d("eventttttttttttttttttttttttt", mail_id + " / " + mail_pwd );
		      params.add(new BasicNameValuePair("mail_id", mail_id));
		      params.add(new BasicNameValuePair("mail_pwd", mail_pwd));
		      
		      String strReminderSel = ((Spinner)findViewById(R.id.selReminderbox1)).getSelectedItem().toString();
//			  System.out.println("strReminderSel======" + strReminderSel);
			  String	strReminder =  ((TextView)findViewById(R.id.txtReminder1)).getText().toString();
//			  System.out.println("strReminder======" + strReminder);
			  String	strAnniversary =  ((TextView)findViewById(R.id.anyTextbox)).getText().toString();
//ktmin
//			  System.out.println("strAnniversary======" + strAnniversary);
			  String	strAnniver_tp =  ((Spinner)findViewById(R.id.selAnybox)).getSelectedItem().toString();
//			  System.out.println("strAnniver_tp======" + strAnniver_tp);
			
			  CheckBox checkBox = (CheckBox)findViewById(R.id.chkRemonder);
			  String	strReminder_re = "FALSE";  
			  if(checkBox.isChecked())
			  {
				strReminder_re = "TRUE";  
  			  }		      
//			  System.out.println("strReminder_re ======" + strReminder_re);

//		      params.add(new BasicNameValuePair("reminder",strReminder));
		      params.add(new BasicNameValuePair("reminder",strReminderSel));
		      params.add(new BasicNameValuePair("repeat",strReminder_re));
		      params.add(new BasicNameValuePair("anniversary",strAnniversary));
		      params.add(new BasicNameValuePair("anniver_type",strAnniver_tp));

		      System.out.println("strReminderSel ======" + strReminderSel);
			  System.out.println("strReminder_re ======" + strReminder_re);
			  System.out.println("strAnniversary ======" + strAnniversary);
			  System.out.println("strAnniver_tp ======" + strAnniver_tp);

		      
		      UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
		      post.setEntity(ent);
		      HttpResponse responsePOST = client.execute(post);
		      HttpEntity resEntity = responsePOST.getEntity();
		      
		      if(resEntity !=null ){
		       Log.w("RESPONSE", EntityUtils.toString(resEntity));
		       Toast.makeText(getApplicationContext(), "일정이 등록되었습니다", Toast.LENGTH_SHORT).show();
		       finish();


		       System.out.println("test");
			      if(EntityUtils.toString(resEntity).equals("finish for adding in google calendar")){
			    	  Log.w("RESPONSE", "성공임222222222222");
			      }else{
			    	  Log.w("RESPONSE", "실패여3333333333333");
			      }
		      }else{
		       Log.w("RESPONSE", "실패여");
		      }
		      
		      if(EntityUtils.toString(resEntity).equals("finish for adding in google calendar")){
		    	  Log.w("RESPONSE", "성공임");

		      }else{
		    	  Log.w("RESPONSE", "실패여");

		      }
//메인으로 이동 할 수 있도록 작업 		      
		      
		      
//			  if(EntityUtils.toString(resEntity).equals("finish for adding in google calendar") || EntityUtils.toString(resEntity).equals("finish for deleting in google calendar")){
//			       Log.w("RESPONSE", "성공 들어옴메ㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔ");
//				  System.out.println("성공 들어옴메ㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔ");
//				  Toast.makeText(CreateEvent.this, "Event added successfully!", Toast.LENGTH_SHORT).show();
//			  }else{
//			       Log.w("RESPONSE", "실패 들어옴메ㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔ");
//				  System.out.println("실패 들어옴메ㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔㅔ");
//				  Toast.makeText(CreateEvent.this, "Fail to add event!", Toast.LENGTH_SHORT).show();
//			  }

		      
	     }catch(Exception e){
	    	 
	     }
	}
	*/
	
	
	/**
 	 * 태블릿 여부 확인
	 * @param context
 	 * @return
 	 */
	// Tablet 인지 유무 파악
    protected boolean isTablet()
    {
        int portrait_width_pixel = Math.min(this.getResources().getDisplayMetrics().widthPixels, this.getResources().getDisplayMetrics().heightPixels);
        int dots_per_virtual_inch = this.getResources().getDisplayMetrics().densityDpi;
        float virutal_width_inch = portrait_width_pixel / dots_per_virtual_inch;
         
        return (virutal_width_inch > 2);
    }
	
}
