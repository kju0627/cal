package com.fsck.k9.activity;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

public class CalendarManager {

	public static void addToCalendar(Context ctx, final String title, final Date dtstart, final Date dtend, final String location) {

		String sTime = "" + dtstart.getTime();
		String eTime = "" + dtend.getTime();
		
		System.out.println("adding " + title + " to calendar...");
		  
		//	System.out.println(this.sender+"============================================" +this.type +"==-===================================");
			/*
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
	    	 
	     }*/
	}	
}
