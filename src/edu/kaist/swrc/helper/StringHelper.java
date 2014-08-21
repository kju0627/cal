package edu.kaist.swrc.helper;

/**
 * <p>Title: StringHelper.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: KAIST., Ltd</p>
 * 
 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
 * @version v 1.0 2011. 4. 25.
 */
public class StringHelper {
	
	/**
	 * @param in
	 * @return
	 */
	public static String removeHtmlTag( String in){
		return in.replaceAll("\\<.*?>","");
	}
	
	/**
	 * @param in
	 * @return
	 */
	public static String convertNbspToSpace(String in){
		return in.replaceAll("&nbsp;", " ");
	}
	
	/**
	 * @param in
	 * @return
	 */
	public static String convertBrToNewline(String in){
		return in.replaceAll("(<br>|<br/>|<BR>|<BR/>|</div>|</DIV>)", "\n");
	}
	
	/**
	 * @param in
	 * @return
	 */
	public static String convertHtmlToText(String in){
		String ret = convertNbspToSpace(in);
		ret = convertBrToNewline(ret);
		ret = removeHtmlTag(ret);
		return ret;
	}
	
}
