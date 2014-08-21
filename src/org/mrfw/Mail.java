package org.mrfw;

import java.util.Date;

/**
 * 메일을 나타내는 클래스
 *
 */
public class Mail {
	/**
	 * 발신
	 */
	private String From;
	
	/**
	 * 수신 
	 */
	private String To;

	/**
	 * 참조 
	 */
	private String Cc;
	
	/**
	 * 숨은 참조
	 */
	private String Bcc;
	
	/**
	 * 날짜/시간
	 */
	private Date Date;
	
	/**
	 * 제목
	 */
	private String Subject;
	
	/** 
	 * 본문
	 */
	private String Body;

	/**
	 * 생성자
	 */
	public Mail() {
		super();
		
		From = "";
		To = "";
		Cc = "";
		Bcc = "";
		Date = new Date(0);
		Subject = "";
		Body = "";
	}
	
	/**
	 * 발신을 반환한다.	
	 * @return	발신
	 */
	public String getFrom() {
		return From;
	}

	/** 
	 * 발신을 설정한다.
	 * @param from	설정할 발신
	 */
	public void setFrom(String from) {
		From = from;
	}

	/**
	 * 수신을 반환한다.
	 * @return	수신
	 */
	public String getTo() {
		return To;
	}

	/**
	 * 수신을 설정한다.
	 * @param to	설정할 수신
	 */
	public void setTo(String to) {
		To = to;
	}

	/**
	 * 참조를 반환한다.
	 * @return	참조
	 */
	public String getCc() {
		return Cc;
	}

	/**
	 * 참조를 설정한다.
	 * @param cc	설정할 참조
	 */
	public void setCc(String cc) {
		Cc = cc;
	}

	/**
	 * 숨은 참조를 반환한다.
	 * @return	숨은 참조
	 */
	public String getBcc() {
		return Bcc;
	}

	/**
	 * 참조를 설정한다.
	 * @param bcc	설정할 참조
	 */
	public void setBcc(String bcc) {
		Bcc = bcc;
	}

	/**
	 * 날짜/시간을 반환한다.
	 * @return	날짜/시간
	 */
	public Date getDate() {
		return Date;
	}

	/**
	 * 날짜/시간을 설정한다.
	 * @param date	설정할 날짜/시간
	 */
	public void setDate(Date date) {
		Date = date;
	}

	/**
	 * 제목을 반환한다.
	 * @return	제목
	 */
	public String getSubject() {
		return Subject;
	}

	/**
	 * 제목을 설정한다.
	 * @param subject	설정할 제목
	 */
	public void setSubject(String subject) {
		Subject = subject;
	}

	/**
	 * 본문을 반환한다.
	 * @return	본문
	 */
	public String getBody() {
		return Body;
	}

	/**
	 * 본문을 설정한다.
	 * @param body	설정할 본문
	 */
	public void setBody(String body) {
		Body = body;
	}
}
