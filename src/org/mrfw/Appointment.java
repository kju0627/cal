package org.mrfw;

import java.util.*;

/**
 * 약속을 나타내는 클래스
 */
public class Appointment {
	/**
	 * 제목
	 */
	private String Title;
	
	/**
     * 주제
	 */
	private String Subject;
	
	/**
     * 시작 날짜/시간
	 */
	private Date StartDate;
	
	/**
     * 종료 날짜/시간
	 */
	private Date EndDate;
	
	/**
     * 위치
	 */
	private String Location;
	
	/**
     * 위치를 찾아가는 데 유용한 랜드마크
	 */
	private String Landmark;
	
	/**
     * 이동방법
	 */
	private String Transportation;
	
	/**
     * 발표자
	 */
	private String Speaker;
	
	/**
     * 참석자
	 */
	private ArrayList<String> Attendee;
	
	/**
     * 대상
	 */
	private String Target;
	
	/**
     * 준비물
	 */
	private String Preparation;
	
	/**
     * 문의처
	 */
	private String Reference;
	
	/**
     * 주관
	 */
	private String Supervision;
	
	/**
     * 내용
	 */
	private String Memo;

    /**
     * 2012.12.03 임경태 추가
     */
    private String Sender;


    /**
     * 2012.12.03 임경태 추가
     */
    private String Type;

	/**
     * 생성자
	 */
	public Appointment() {
		super();
		
		Title = "";
		Subject = "";
		StartDate = Calendar.getInstance().getTime();
		EndDate = Calendar.getInstance().getTime();
		Location = "";
		Transportation = "";
		Speaker = "";
		Attendee = new ArrayList<String>();
		Target = "";
		Preparation = "";
		Reference = "";
		Supervision = "";
		Memo = "";
		Landmark = "";
        Sender = "";
        Type = "";
	}
	
	/**
     * 제목을 반환한다.
     * @return 제목
	 */	
	public String getTitle() {
		return Title;
	}
	
	

	/**
     * 제목을 설정한다.
     * @param title 설정할 제목
	 */
	public void setTitle(String title) {
		Title = title;
	}

	/**
     * 랜드마크를 반환한다.
     * @return 랜드마크
	 */	
	public String getLandmark() {
		return Landmark;
	}
	/**
     * 랜드마크를 설정한다.
     * @return 랜드마크
	 */	
	public void setLandmark(String landmark) {
		Landmark	= landmark;
	}

	
	/**
     * 주제를 반환한다.
     * @return	주제
     */
	public String getSubject() {
		return Subject;
	}

	/**
     * 주제를 설정한다.
     * @param subject 설정할 주제
	 */
	public void setSubject(String subject) {
		Subject = subject;
	}

	/**
     * 시작 날짜/시간을 반환한다.
     * @return 시작 날짜/시간
	 */
	public Date getStartDate() {
		return StartDate;
	}

	/**
     * 시작 날짜/시간을 설정한다.
     * @param startDate 설정할 시작 날짜/시간
	 */
	public void setStartDate(Date startDate) {
		StartDate = startDate;
	}

	/**
     * 종료 날짜/시간을 반환한다.
     * @return 종료 날짜/시간
	 */
	public Date getEndDate() {
		return EndDate;
	}

	/**
     * 종료 날짜/시간을 설정한다.
     * @param endTime	설정할 종료 날짜/시간
	 */
	public void setEndDate(Date endTime) {
		EndDate = endTime;
	}

	/**
     * 위치를 반환한다.
     * @return 위치
	 */
	public String getLocation() {
		return Location;
	}

	/**
     * 위치를 설정한다.
     * @param location	설정할 위치
	 */
	public void setLocation(String location) {
		Location = location;
	}

	/**
     * 이동 방법을 반환한다.
     * @return	이동 방법
	 */
	public String getTransportation() {
		return Transportation;
	}

	/**
     * 이동 방법을 설정한다.
     * @param transportation	설정할 이동 방법
	 */
	public void setTransportation(String transportation) {
		Transportation = transportation;
	}

	/**
     * 발표자(연사)를 반환한다.
     * @return	발표자(연사)
	 */
	public String getSpeaker() {
		return Speaker;
	}

	/**
     * 발표자(연사)를 설정한다.
     * @param speaker	설정할 발표자(연사)
	 */
	public void setSpeaker(String speaker) {
		Speaker = speaker;
	}

	/**
     * 참석자를 반환한다.
     * @return	참석자
	 */
	public ArrayList<String> getAttendee() {
		return Attendee;
	}

	/**
     * 참석자를 설정한다.
     * @param attendee	설정할 참석자
	 */
	public void setAttendee(ArrayList<String> attendee) {
		Attendee = attendee;
	}

	/**
     * 대상을 반환한다.
     * @return	대상
	 */
	public String getTarget() {
		return Target;
	}

	/**
     * 대상을 설정한다.
     * @param target	대상
	 */
	public void setTarget(String target) {
		Target = target;
	}

	/**
     * 준비물을 반환한다.
     * @return	준비물
	 */
	public String getPreparation() {
		return Preparation;
	}

	/**
     * 준비물을 설정한다.
     * @param preparation	설정할 준비물
	 */
	public void setPreparation(String preparation) {
		Preparation = preparation;
	}

	/**
     * 문의처를 반환한다.
     * @return	문의처
	 */
	public String getReference() {
		return Reference;
	}

	/**
     * 문의처를 설정한다.
     * @param reference	설정할 문의처
	 */
	public void setReference(String reference) {
		Reference = reference;
	}

	/**
     * 주관을 반환한다.
     * @return	주관
	 */
	public String getSupervision() {
		return Supervision;
	}

	/**
     * 주관을 설정한다.
     * @param supervision	설정할 주관
	 */
	public void setSupervision(String supervision) {
		Supervision = supervision;
	}

	/**
     * 메모를 반환한다.
     * @return	메모
	 */
	public String getMemo() {
		return Memo;
	}

	/**
     * 메모를 설정한다.
     * @param memo	설정할 메모
	 */
	public void setMemo(String memo) {
		Memo = memo;
	}

    /**
     * 임경태 추가
     * @return
     */
	public String getSender() {
		return Sender;
	}

	public void setSender(String sender) {
		Sender = sender;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}
	
	
	
}
