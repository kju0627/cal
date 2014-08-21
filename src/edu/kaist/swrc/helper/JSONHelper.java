/**
 * 
 */
package edu.kaist.swrc.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import edu.kaist.swrc.model.MeetingLocation;

/**
 * <p>Title: JSONHelper.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: KAIST., Ltd</p>
 * 
 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
 * @version v 1.0 2011. 4. 26.
 */

public class JSONHelper {
	
	/**
	 * @param serverUrl
	 * @param postPara
	 * @param flagEncoding
	 * @return
	 * @throws Exception
	 */
	public static String getJSON(String serverUrl, String postPara, boolean flagEncoding) 
	throws Exception {
		System.out.println("SERVER: " + serverUrl);
		System.out.println("postPara:" + postPara);
		URL url = null;
		HttpURLConnection conn = null;
		PrintWriter postReq = null;
		BufferedReader postRes = null;
		StringBuilder json = null;
		String line = null;

		json = new StringBuilder();
		try {
			
			if (flagEncoding) {
				postPara = URLEncoder.encode(postPara);
			}
			
			url = new URL(serverUrl);
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			//conn.setRequestProperty("Content-Type", "text/plain");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//conn.setRequestProperty("Content-Length", Integer.toString(postPara.length()));
			conn.setDoInput(true);
			
			postReq = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "EUC-KR"));
			postReq.write(postPara);
			postReq.flush();
			
			postRes = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			while ((line = postRes.readLine()) != null){
				json.append(line);
			}
			conn.disconnect();
			
		} catch (MalformedURLException ex) {
			throw new Exception(ex.getMessage());
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
		return json.toString();    
	}
	
	public static String getTypeFromJSON(String type, String in){
		System.out.println(in);
		String ret = "";
		try {
			JSONObject json = new JSONObject(in);
			ret = json.getString(type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static LinkedList<MeetingLocation> getMeetingLocationFromJSON(String result){
		LinkedList<MeetingLocation> locationList = new LinkedList<MeetingLocation>();
		try {
			JSONArray jarray = new JSONArray(result);
			if(jarray.length()>0){
				JSONObject json = jarray.getJSONObject(0);
				/*[{
				 * "distance":"-1",
				 * "title":"��2�������ջ�ȸ������",
				 * "address":"����Ư���� ���ϱ� ��2�� 241",
				 * "score":1,
				 * "in_boundary":"false",
				 * "lng":127.03967151533618,
				 * "lat":37.62973659226249
				 * ,"matched_query":""},{"distance":"-1","title":"��2���� ���ջ�ȸ������","address":"����Ư���� ���ϱ� ��3�� 241 �ְ����Ʈ 202��","score":0,"in_boundary":"false","lng":127.0464445193661,"lat":37.62633146196977,"matched_query":""}]*/
				
				MeetingLocation location = new MeetingLocation();
				location.setAddressTitle(json.getString("title"));
				location.setAddress(json.getString("address"));
				location.setGeopoint(GPSHelper.getGeoPoint(json.getDouble("lat"), json.getDouble("lng")));
				location.score	= json.getDouble("score");
				locationList.add(location);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return locationList;
	}
}
