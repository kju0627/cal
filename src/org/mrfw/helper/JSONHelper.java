/**
 * 
 */
package org.mrfw.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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
}
