package com.fsck.k9.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.mrfw.Appointment;
import org.mrfw.Constants;
import org.mrfw.Mail;
import org.mrfw.MailToAppointment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
//import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fsck.k9.Account;
import com.fsck.k9.K9;
import com.fsck.k9.Preferences;
import com.fsck.k9.R;
import com.fsck.k9.controller.MessagingController;
import com.fsck.k9.controller.MessagingListener;
import com.fsck.k9.crypto.PgpData;
import com.fsck.k9.helper.Contacts;
import com.fsck.k9.helper.HtmlConverter;
import com.fsck.k9.mail.Address;
import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.MessagingException;
import com.fsck.k9.mail.store.LocalStore;
import com.fsck.k9.view.SingleMessageView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.kaist.swrc.helper.GPSHelper;
import edu.kaist.swrc.helper.JSONHelper;
import edu.kaist.swrc.model.MeetingLocation;

//import android.location.Address;
/**
 * <p>Title: Extractor.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: KAIST., Ltd</p>
 * 
 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
 * @version v 1.0 2011. 4. 24.
 */

public class Extractor extends MapActivity{

	private static final String EXTRA_MESSAGE_REFERENCE = "com.fsck.k9.MessageView_messageReference";
	private static final String EXTRA_MESSAGE_REFERENCES = "com.fsck.k9.MessageView_messageReferences";
	private static final String EXTRA_NEXT = "com.fsck.k9.MessageView_next";
	private static final String STATE_PGP_DATA = "pgpData";

	private SingleMessageView mMessageView;
	
	private Contacts mContacts;

	private PgpData mPgpData;

	private View mNext;
	private Account mAccount;
	private MessageReference mMessageReference;
	private ArrayList<MessageReference> mMessageReferences;
	private Message mMessage;
	private MessagingController mController = MessagingController.getInstance(getApplication());
	private Listener mListener = new Listener();
	private ExtractorHandler mHandler = new ExtractorHandler();

	// Email text
	private String uid;
	private String subject;
	private String body;
	private static String sender;
	
	// account info
	private String account_name;
	private String account_password;
	
	//scrolliew
	private ScrollView mScroll;
	
	// Loading Dialog
	private ProgressDialog loadingDialog; 
	public void createLoadingDialog() {
		/* ProgressDialog */
		loadingDialog = ProgressDialog.show(this, "Meeting Information Extractor", "Extracting schedule information..", true, false);
	}

	public Context mContext;

	// Map
	public MapView mapView;
	public List<Overlay> overlayList;
	public String bestProvider;
	public LocationManager locM;
	public LocationListener locL;
	public Location currentLocation;
	public MapController mapController;
	
	Drawable markerIHA;
	Drawable markerLLM;
	Drawable markerGL;	
	
	public MyLocationOverlay overlayIHA; // isHeldAt
	public MyLocationOverlay overlayLLM;// locationLandmark
	
	public final static int ZOOM_LEVEL_NORESULT = 10;
	public final static int ZOOM_LEVEL_DETAIL = 18;
	
	public boolean isTablet = false;
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCreate(Bundle icicle) {
		
		super.onCreate(icicle);
		
		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		isTablet = isTablet();

		if(isTablet == true){
			setContentView(R.layout.extractor);
			Log.e("isTablet", "yes");
		}else{
			setContentView(R.layout.extractor_phone);
			Log.e("isTablet", "no");
		}
		mContext = this; 
		mScroll = (ScrollView) findViewById(R.id.extract_scrollview);

		Intent intent = getIntent();
		Uri uri = intent.getData();
		
		//String s = intent.getStringExtra("param1");  
		
		if (icicle != null) {
			mMessageReference = icicle.getParcelable(EXTRA_MESSAGE_REFERENCE);
			mMessageReferences = icicle.getParcelableArrayList(EXTRA_MESSAGE_REFERENCES);
			mPgpData = (PgpData) icicle.getSerializable(STATE_PGP_DATA);
		} else {
			if (uri == null) {
				mMessageReference = intent.getParcelableExtra(EXTRA_MESSAGE_REFERENCE);
				mMessageReferences = intent.getParcelableArrayListExtra(EXTRA_MESSAGE_REFERENCES);
			} else {
				List<String> segmentList = uri.getPathSegments();
				if (segmentList.size() != 3) {
					Toast.makeText(this, "Invalid intent uri: " + uri.toString(), Toast.LENGTH_LONG).show();
					return;
				}

				String accountId = segmentList.get(0);
				Collection<Account> accounts = Preferences.getPreferences(this).getAvailableAccounts();
				boolean found = false;
				for (Account account : accounts) {
					if (String.valueOf(account.getAccountNumber()).equals(accountId)) {
						mAccount = account;
						found = true;
						break;
					}
				}
				if (!found) {
					Toast.makeText(this, "Invalid account id: " + accountId, Toast.LENGTH_LONG).show();
					return;
				}

				mMessageReference = new MessageReference();
				mMessageReference.accountUuid = mAccount.getUuid();
				mMessageReference.folderName = segmentList.get(1);
				mMessageReference.uid = segmentList.get(2);
				mMessageReferences = new ArrayList<MessageReference>();
			}
		}
		try{
			mAccount = Preferences.getPreferences(this).getAccount(mMessageReference.accountUuid);
			
			////////////////////////////////////mail id, password//////////////////////////////////////
			Uri accountStoreUri = Uri.parse(mAccount.getStoreUri());
			String userinfo = accountStoreUri.getUserInfo();
			
			account_name = userinfo.substring(0, userinfo.indexOf(":"));
			account_password = userinfo.substring(userinfo.indexOf(":")+1);		
			
			Log.e("LogCat", account_name + " / " + account_password);
			////////////////////////////////////2013. 07. 05 추가//////////////////////////////////////	

			if (K9.DEBUG)
				Log.d(K9.LOG_TAG, "MessageView got message " + mMessageReference);
			if (intent.getBooleanExtra(EXTRA_NEXT, false)) {
				mNext.requestFocus();
			}
			
			//setupButtonViews();
			displayMessage(mMessageReference);
			
			//((TextView)findViewById(R.id.tvTitle)).setText("uid : " + mMessageReference.uid);
			setUid(mMessageReference.uid);
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		

		// handle MapView

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		
		mapView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
		        switch (action) {
		           case MotionEvent.ACTION_DOWN:
		                // Disallow ScrollView to intercept touch events.
		                mScroll.requestDisallowInterceptTouchEvent(true);
		                // Disable touch on transparent view
		                return false;

		           case MotionEvent.ACTION_UP:
		                // Allow ScrollView to intercept touch events.
		        	   mScroll.requestDisallowInterceptTouchEvent(false);
		                return true;

		           case MotionEvent.ACTION_MOVE:
		        	   mScroll.requestDisallowInterceptTouchEvent(true);
		                return false;

		           default: 
		                return true;
		        }   
			}
		});

		mapController = mapView.getController();
		mapController.setZoom(ZOOM_LEVEL_NORESULT);
		
		// set Marker
		markerIHA = getResources().getDrawable(R.drawable.arrow_isheldat);
		markerIHA.setBounds(0, 0, markerIHA.getIntrinsicWidth(), markerIHA.getIntrinsicHeight());
		markerLLM = getResources().getDrawable(R.drawable.arrow_landmark);
		markerLLM.setBounds(0, 0, markerLLM.getIntrinsicWidth(), markerLLM.getIntrinsicHeight());
		markerGL = getResources().getDrawable(R.drawable.arrow_location);
		markerGL.setBounds(0, 0, markerGL.getIntrinsicWidth(), markerGL.getIntrinsicHeight());
		
		overlayList = mapView.getOverlays();
		
		// 2012.12.03 by kyungtae lim Adding sender and type for cancelation
		// Create Event Button
		Button btnEvent = (Button) findViewById(R.id.btnEvent);
		btnEvent.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplication(), CreateEvent.class);
				intent.putExtra("sender", ((TextView)findViewById(R.id.tvSender)).getText());
				intent.putExtra("title", ((TextView)findViewById(R.id.tvTitle)).getText());
				intent.putExtra("location", ((TextView)findViewById(R.id.tvIHA)).getText());
				intent.putExtra("stime", ((TextView)findViewById(R.id.tvStartTime)).getText());
				intent.putExtra("etime", ((TextView)findViewById(R.id.tvEndTime)).getText());
				intent.putExtra("type", ((TextView)findViewById(R.id.tvType)).getText());
				
				////////////////////////////////// 2013.07.12 추가  보낸사람,참조,일정내용, mail id/password
				intent.putExtra("mail_id", account_name);
				intent.putExtra("mail_pwd", account_password);

				String mail_cc = null;
				String mail_to = null;
				
				final Contacts contacts = K9.showContactName() ? mContacts : null;
				try {
					final CharSequence cc = Address.toFriendly(mMessage.getRecipients(Message.RecipientType.CC), contacts);
					final CharSequence to = Address.toFriendly(mMessage.getRecipients(Message.RecipientType.TO), contacts);
					
					mail_cc = cc.toString();
					mail_to = to.toString();
				} catch (MessagingException e1) {
					e1.printStackTrace();
				}
				intent.putExtra("cc", mail_cc);
				intent.putExtra("to", mail_to);
				intent.putExtra("subject", body);
				
				//////////////////////////////////2013.07.12 추가  보낸사람,참조,일정내용 
				
				startActivity(intent);
			}
		});
	}
	

	/**
	 * Mapping location to the map
	 */
	public void pointLocationToMap(LinkedList<Appointment> apList){
		try{
		String request_url 					= Constants.URL_GEOCODER;
		
		LinkedList<Appointment> locKnown	= new LinkedList<Appointment>();
		LinkedList<Appointment> locUnknown	= new LinkedList<Appointment>();
		
		Iterator<Appointment> apIter		= apList.iterator();
		while(apIter.hasNext()){
			Appointment ap					= apIter.next();
			if(ap.getLocation() == null || ap.getLocation().trim().equals("")){
				locUnknown.add(ap);
			}else{
				locKnown.add(ap);
			}
		}
		
		
		if(locKnown.size() > 0){
			apIter							= locKnown.iterator();
			GeoPoint geopoint_default 	= null;

			while(apIter.hasNext()){
				SitesOverlay sitesOverlayIHA = new SitesOverlay(markerIHA);
				
				Appointment ap				= apIter.next();				
				String result = JSONHelper.getJSON(request_url,  "location=" + ap.getLocation(), false);
				LinkedList<MeetingLocation> locationList = JSONHelper.getMeetingLocationFromJSON(result);
				MeetingLocation maxL	= null;
				double maxS				= 0.0;
				for(int i=0; i<locationList.size(); i++){
					MeetingLocation loc = locationList.get(i);
					if(maxL == null || maxS < loc.score){
						maxS	= loc.score;
						maxL	= loc;
					}
				}
					
				if(geopoint_default == null){
					geopoint_default = maxL.getGeopoint();
				}
					
				sitesOverlayIHA.addItem(new OverlayItem(
					maxL.getGeopoint(),
					maxL.getAddressTitle(),			
					"StartTime : " + ap.getStartDate() + "\n\n" +
					"EndTime: " + ap.getEndDate() + "\n\n" + 
					"Location : " + ap.getLocation() + "\n\n" +						
					"Geocode : " + "("+maxL.getGeopoint().getLatitudeE6() + ", " + maxL.getGeopoint().getLongitudeE6() + ")"+ "\n\n" +
					"Address : "+maxL.getAddress()
				));
				
				overlayList.add(sitesOverlayIHA);
			}
			
			if(locUnknown.size() > 0){
				String result = JSONHelper.getJSON(request_url,  "location=서울역", false);
				LinkedList<MeetingLocation> locationList = JSONHelper.getMeetingLocationFromJSON(result);
				MeetingLocation maxL	= null;
				double maxS				= 0.0;
				for(int i=0; i<locationList.size(); i++){
					MeetingLocation loc = locationList.get(i);
					if(maxL == null || maxS < loc.score){
						maxS	= loc.score;
						maxL	= loc;
					}
				}
				
				int cnt	= 0;
				String show	= "";
				Iterator<Appointment> unIter	= locUnknown.iterator();
				while(unIter.hasNext()){
					Appointment un	= unIter.next();
					show	+= "Meeting #" + (++cnt) + "\n";
					show	+= "  StartTime : " + un.getStartDate() + "\n";
					show	+= "  EndTime: " + un.getEndDate() + "\n";
				}
				
				GeoPoint gp	= new GeoPoint(geopoint_default.getLatitudeE6() - 100, geopoint_default.getLongitudeE6() - 100);
				
				SitesOverlay sitesOverlayIHA = new SitesOverlay(markerIHA);
				sitesOverlayIHA.addItem(new OverlayItem(
					gp,
					"Location_Unknown",		
					"Location_Unknown Meetings\n" + show
				));
				
				overlayList.add(sitesOverlayIHA);
				
				
			}
			
			mapController.setZoom(ZOOM_LEVEL_DETAIL);
			mapController.setCenter(geopoint_default);
		}else{
			if(locUnknown.size() > 0){
				String result = JSONHelper.getJSON(request_url,  "location=서울역", false);
				LinkedList<MeetingLocation> locationList = JSONHelper.getMeetingLocationFromJSON(result);
				MeetingLocation maxL	= null;
				double maxS				= 0.0;
				for(int i=0; i<locationList.size(); i++){
					MeetingLocation loc = locationList.get(i);
					if(maxL == null || maxS < loc.score){
						maxS	= loc.score;
						maxL	= loc;
					}
				}
				
				int cnt	= 0;
				String show	= "";
				Iterator<Appointment> unIter	= locUnknown.iterator();
				while(unIter.hasNext()){
					Appointment un	= unIter.next();
					show	+= "Meeting #" + (++cnt) + "\n";
					show	+= "  StartTime : " + un.getStartDate() + "\n";
					show	+= "  EndTime: " + un.getEndDate() + "\n";
				}
				
				
				SitesOverlay sitesOverlayIHA = new SitesOverlay(markerIHA);
				sitesOverlayIHA.addItem(new OverlayItem(
					maxL.getGeopoint(),
					"Location_Unknown",		
					"Location_Unknown Meetings\n" + show
				));
				
				overlayList.add(sitesOverlayIHA);
				
				mapController.setZoom(ZOOM_LEVEL_DETAIL);
				mapController.setCenter(maxL.getGeopoint());
			}else{
				showDialog("MIE", "No Result");
				mapController.setZoom(ZOOM_LEVEL_NORESULT);
				mapController.setCenter(GPSHelper.getGeoPoint(currentLocation));		
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	
//	/**
//	 * old function, [deprecated]
//	 */
//	public void getGeocodeForLocation(){
//		// isHeldAt
//		// locationLandmark
//		String request_url = Constants.URL_GEOCODER;
//		String result = "";
//		String parameters = "";
//		try {
//			
//			Iterator<String> itr = isHeldAt.iterator();
//			while(itr.hasNext()){
//				String str = itr.next();
//				parameters += "isheldat="+str+"&";
//			}
//			
//			itr = locationLandmark.iterator();
//			while(itr.hasNext()){
//				String str = itr.next();
//				parameters += "landmark="+str+"&";
//			}
//			
//			// update mapview by extracted location info.
//			
//			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
//			//Address addr_default = null;
//			GeoPoint geopoint_default = null;
//			
//			// set 'isHeldAt'
//			SitesOverlay sitesOverlayIHA = new SitesOverlay(markerIHA);
//			result = JSONHelper.getJSON(request_url, parameters, false);
//			LinkedList<MeetingLocation> locationList = JSONHelper.getMeetingLocationFromJSON(result);
//			for(int i=0; i<locationList.size(); i++){
//				MeetingLocation loc = locationList.get(i);
//				//List<Address> list = geocoder.getFromLocationName(loc.getAddress(), 1);
//				//if(list.size()>0){
//					//Address addr = list.get(0);
//					//addr_default = addr;
//				
//				geopoint_default = loc.getGeopoint();
//				
//					boolean isPartialmatched = !(loc.getText().equals(loc.getPartialText()));
//					sitesOverlayIHA.addItem(new OverlayItem(
//						//GPSHelper.getGeoPoint(addr.getLatitude(), addr.getLongitude()),
//						loc.getGeopoint(),
//						loc.getAddressTitle(),
//						"Title : " + loc.getAddressTitle() + "\n\n" +
//						"Original Location : " + loc.getText() + "\n\n" +
//						"Partially matched : " + isPartialmatched + (isPartialmatched ? "("+ loc.getPartialText() + ")" : "") + "\n\n" +
//						//"Geocode : " + "("+addr.getLatitude() + ", " + addr.getLongitude() + ")"+ "\n\n" +
//						"Geocode : " + "("+loc.getGeopoint().getLatitudeE6() + ", " + loc.getGeopoint().getLongitudeE6() + ")"+ "\n\n" +
//						"Address : "+loc.getAddress()
//					));
//					
//				//}
//			}
//			if(sitesOverlayIHA.size()>0){
//				mapController.setZoom(ZOOM_LEVEL_DETAIL);
//				overlayList.add(sitesOverlayIHA);
//				/*if(addr_default != null){
//					mapController.setCenter(GPSHelper.getGeoPoint(addr_default.getLatitude(), addr_default.getLongitude()));
//				}				*/
//				mapController.setCenter(geopoint_default);
//			}else{
//				// ��� ������ ��� ������ �˸�
//				showDialog("MIE", "No Result");
//				mapController.setZoom(ZOOM_LEVEL_NORESULT);
//				mapController.setCenter(GPSHelper.getGeoPoint(currentLocation));	
//			}
//			
//			mapView.postInvalidate();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public void showMsg(String msg, int time){
		Toast.makeText(this, msg, time);
	}
	public void showDialog(String title, String msg){
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext); 
		dialog.setTitle(title); 
		dialog.setMessage(msg);
		dialog.show();
	}
//	public String showToastForLocation(double lat, double lng, boolean showOption){
//		String ret = "";
//		try {
//			Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//			Address adr = geocoder.getFromLocation(lat, lng, 1).get(0);
//			if(adr.getLocality() != null)
//				ret += adr.getLocality() + " ";
//			if(adr.getThoroughfare() != null)
//				ret += adr.getThoroughfare();
//			if(!"".equals(ret))
//				ret += "\n\n";
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if(showOption){
//			Toast.makeText(getApplicationContext(), ret, Toast.LENGTH_LONG);
//		}
//		return ret;
//	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public void setHeader(String subject){
		if(subject != null){
			this.subject = subject;
			((TextView)findViewById(R.id.tvTitle)).setText(subject);
		}
	}    
	public void setBody(String body){
		if(body != null){
			this.body = body;
			((TextView)findViewById(R.id.tvBody)).setText(body);
		}
	}

	
	//여기다 여기서 셋해준다  by kyungtae lim 2012.12.03
	public void displayMessageBody(final Account account, final String folder, final String uid, final Message message) {
		runOnUiThread(new Runnable() {
			public void run() {
				Extractor.this.mMessage = message;
				
				Date sentDate	= mMessage.getSentDate();
				String text		= mPgpData.getDecryptedData();
				if (text == null) {
					try {
						text = ((LocalStore.LocalMessage) message).getTextForDisplay();
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}
				body = HtmlConverter.htmlToText(text);
				body = body.replaceAll("\n\n", "\n");
				
				showMsg("test", Toast.LENGTH_SHORT);
				// Extract Time & Location information.
				Mail m	= new Mail();
				m.setBody(body);
				m.setDate(sentDate);
				LinkedList<Appointment> apList	= MailToAppointment.extractFromMail(m, message);
	
				// Get Geocode for the extracted Location 
				//getGeocodeForLocation();
				
				loadingDialog.dismiss();
				
				setBody(body);
				
				if(apList.size() > 0){
					Appointment ap	= apList.get(0);
					String cancelModifier	= "";
					if(ap.getLocation().equals("취소")){
						cancelModifier		= "\nCanceled";
						ap.setLocation("");
					}
					((TextView)findViewById(R.id.tvStartTime)).setText(ap.getStartDate().toString() + cancelModifier);
					((TextView)findViewById(R.id.tvEndTime)).setText(ap.getEndDate().toString() + cancelModifier);
					((TextView)findViewById(R.id.tvIHA)).setText(ap.getLocation());
					((TextView)findViewById(R.id.tvType)).setText(ap.getType());
					((TextView)findViewById(R.id.tvSender)).setText(sender);
				}
				pointLocationToMap(apList);
				
				//((TextView)findViewById(R.id.tvLLM)).setText(extracted_location[1]);
				//((TextView)findViewById(R.id.tvGL)).setText(extracted_location[2]);
				
			}
		});
	}
	private void displayMessage(MessageReference ref) {
		mMessageReference = ref;
		if (K9.DEBUG)
			Log.d(K9.LOG_TAG, "MessageView displaying message " + mMessageReference);
		try{
			mAccount = Preferences.getPreferences(this).getAccount(mMessageReference.accountUuid);
			mPgpData = new PgpData();
			mController.loadMessageForView(mAccount, mMessageReference.folderName, mMessageReference.uid, mListener);
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	}
	public static void actionView(Context context, MessageReference messRef, ArrayList<MessageReference> messReferences, MessageInfoHolder message) {
		actionView(context, messRef, messReferences, message,null);
	}
	public static void actionView(Context context, MessageReference messRef, ArrayList<MessageReference> messReferences,MessageInfoHolder message ,Bundle extras) {
		Intent i = new Intent(context, Extractor.class);
		i.putExtra(EXTRA_MESSAGE_REFERENCE, messRef);
		i.putExtra("sender", message.senderAddress);
		sender = message.senderAddress;
		System.out.println("익스트렉터 풋 인텥트 하는 곳 들어ㅏ옴 ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ");
		i.putParcelableArrayListExtra(EXTRA_MESSAGE_REFERENCES, messReferences);
		if (extras != null) {
			i.putExtras(extras);
		}
		context.startActivity(i);
	}
	
	
	
	/**
	 * <p>Title: Listener </p>
	 * <p>Description: </p>
	 * <p>Copyright: Copyright (c) 2011</p>
	 * <p>Company: KAIST., Ltd</p>
	 * 
	 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
	 * @version v 1.0 2011. 4. 25.
	 */
	class Listener extends MessagingListener {

		@Override		
		public void loadMessageForViewStarted(Account account, String folder, String uid) {
			if (!mMessageReference.uid.equals(uid) || !mMessageReference.folderName.equals(folder)
					|| !mMessageReference.accountUuid.equals(account.getUuid())) {
				return;
			}
			mHandler.post(new Runnable() {
				public void run() {
					// create
					createLoadingDialog();

				}
			});
		}
		@Override
		public void loadMessageForViewFinished(Account account, String folder, String uid, final Message message) {
			if (!mMessageReference.uid.equals(uid) || !mMessageReference.folderName.equals(folder)
					|| !mMessageReference.accountUuid.equals(account.getUuid())) {
				return;
			}
			mHandler.post(new Runnable() {
				public void run() {
					loadingDialog.dismiss();
				}
			});
		}
		
		@Override
		public void loadMessageForViewHeadersAvailable(final Account account, String folder, String uid,
				final Message message) {
			if (!mMessageReference.uid.equals(uid) || !mMessageReference.folderName.equals(folder)
					|| !mMessageReference.accountUuid.equals(account.getUuid())) {
				return;
			}
			Extractor.this.mMessage = message;            
			runOnUiThread(new Runnable() {
				public void run() {
					setHeader(mMessage.getSubject());
				}
			});
		}

		@Override
		public void loadMessageForViewBodyAvailable(Account account, String folder, String uid,
				Message message) {
			if (!mMessageReference.uid.equals(uid) || !mMessageReference.folderName.equals(folder)
					|| !mMessageReference.accountUuid.equals(account.getUuid())) {
				return;
			}
			displayMessageBody(account, folder, uid, message);   

		}//loadMessageForViewBodyAvailable

	}
		

	/**
	 * <p>Title: ExtractorHandler</p>
	 * <p>Description: </p>
	 * <p>Copyright: Copyright (c) 2011</p>
	 * <p>Company: KAIST., Ltd</p>
	 * 
	 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
	 * @version v 1.0 2011. 4. 27.
	 */
	class ExtractorHandler extends Handler {

		public void progress(final boolean progress) {
			runOnUiThread(new Runnable() {
				public void run() {
					//setProgressBarIndeterminateVisibility(progress);
				}
			});
		}
		public void addAttachment(final View attachmentView) {
			runOnUiThread(new Runnable() {
				public void run() {
					//mMessageView.addAttachment(attachmentView);
				}
			});
		}
		/* A helper for a set of "show a toast" methods */
		private void showToast(final String message, final int toastLength)  {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(Extractor.this, message, toastLength).show();
				}
			});
		}
		public void networkError() {
			showToast(getString(R.string.status_network_error), Toast.LENGTH_LONG);
		}
		public void invalidIdError() {
			showToast(getString(R.string.status_invalid_id_error), Toast.LENGTH_LONG);
		}
		public void fetchingAttachment() {
			showToast(getString(R.string.message_view_fetching_attachment_toast), Toast.LENGTH_SHORT);
		}
		public void setHeaders(final Message message, final Account account) {
			runOnUiThread(new Runnable() {
				public void run() {
					mMessageView.setHeaders(message, account);
				}
			});
		}
	}
	
	/**
	 * <p>Title: SitesOverlay</p>
	 * <p>Description: </p>
	 * <p>Copyright: Copyright (c) 2011</p>
	 * <p>Company: KAIST., Ltd</p>
	 * 
	 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
	 * @version v 1.0 2011. 4. 27.
	 */
	private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
		
		private List<OverlayItem> items = new ArrayList<OverlayItem>();
		private Drawable marker = null;
		public SitesOverlay(Drawable marker) {
			super(marker);
			this.marker = marker;
		}		
		public void addItem(OverlayItem item){
			items.add(item);
			populate();
		}		
		@Override
		protected OverlayItem createItem(int i) {
			return (items.get(i));
		}
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		} 
		@Override
		protected boolean onTap(int i) { 
			OverlayItem layItem = items.get(i); 
			//showMsg(layItem.getSnippet(), Toast.LENGTH_LONG);			
			/*AlertDialog.Builder dialog = new AlertDialog.Builder(mContext); 
			dialog.setTitle(layItem.getTitle()); 
			dialog.setMessage(layItem.getSnippet());
			dialog.show(); 		*/
			showDialog(layItem.getTitle(), layItem.getSnippet());
			return true;
		}		
		@Override
		public int size() {
			return (items.size());
		}
	}//SitesOverlay
	
	
	
	/**
	 * <p>Title: MyLocationListener</p>
	 * <p>Description: </p>
	 * <p>Copyright: Copyright (c) 2011</p>
	 * <p>Company: KAIST., Ltd</p>
	 * 
	 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
	 * @version v 1.0 2011. 4. 27.
	 */
	public class MyLocationListener implements LocationListener{
		@Override
		public void onLocationChanged(Location location) {
			// do nothing
		}
		@Override
		public void onProviderDisabled(String provider) {
		}
		@Override
		public void onProviderEnabled(String provider) {
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}


	/**
	 * <p>Title: LocationItemizedOverlay </p>
	 * <p>Description: </p>
	 * <p>Copyright: Copyright (c) 2011</p>
	 * <p>Company: KAIST., Ltd</p>
	 * 
	 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
	 * @version v 1.0 2011. 4. 27.
	 */
	public class LocationItemizedOverlay extends ItemizedOverlay<OverlayItem>{
		public List<OverlayItem> overlays;
		public LocationItemizedOverlay(Drawable defaultMarker){
			super(boundCenterBottom(defaultMarker));
			overlays = new ArrayList<OverlayItem>();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return overlays.get(i);
		}

		@Override
		public int size() {
			return overlays.size();
		}

		public void addOverlay(OverlayItem overaly){
			overlays.add(overaly);
			populate();
		}
		@Override
		protected boolean onTap(int index) {
			if("here".equals(overlays.get(index).getTitle())){
				Toast.makeText(getApplicationContext(), overlays.get(index).getSnippet(), Toast.LENGTH_SHORT).show();
			}else{
				// 
			}
			return true;
		}
		public void mPopulate(){
			populate();
		}
	}
	
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
