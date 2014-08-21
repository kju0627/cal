/**
 * 
 */
package edu.kaist.swrc.model;

import com.google.android.maps.GeoPoint;

/**
 * <p>Title: MeetingLocation.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: KAIST., Ltd</p>
 * 
 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
 * @version v 1.0 2011. 4. 27.
 */

public class MeetingLocation {
	
	// text
	String text;
	String[] text_chunks;
	String partialText;
	
	public double score;
	
	// isHeldAt, locationLandmark, generalLocation
	String relationType;
	
	// 3 types of location info.
	GeoPoint geopoint;
	String address; String addressFixed; String matchedAddress; String addressTitle;
	String semantics;
	
	public String getAddressFixed() {
		return addressFixed;
	}
	public void setAddressFixed(String addressFixed) {
		this.addressFixed = addressFixed;
	}
	public String getMatchedAddress() {
		return matchedAddress;
	}
	public void setMatchedAddress(String matchedAddress) {
		this.matchedAddress = matchedAddress;
	}
	public String getRelationType() {
		return relationType;
	}
	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}	
	public String getPartialText() {
		return partialText;
	}
	public void setPartialText(String partialText) {
		this.partialText = partialText;
	}
	public String[] getText_chunks() {
		return text_chunks;
	}
	public void setText_chunks(String[] text_chunks) {
		this.text_chunks = text_chunks;
	}
	public GeoPoint getGeopoint() {
		return geopoint;
	}
	public void setGeopoint(GeoPoint geopoint) {
		this.geopoint = geopoint;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}	
	public String getAddressTitle() {
		return addressTitle;
	}
	public void setAddressTitle(String addressTitle) {
		this.addressTitle = addressTitle;
	}
	public String getSemantics() {
		return semantics;
	}
	public void setSemantics(String semantics) {
		this.semantics = semantics;
	}
	
	
}
