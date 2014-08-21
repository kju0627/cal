package org.mrfw;

import java.util.Date;

/**
 * ������ ��Ÿ���� Ŭ����
 *
 */
public class Mail {
	/**
	 * �߽�
	 */
	private String From;
	
	/**
	 * ���� 
	 */
	private String To;

	/**
	 * ���� 
	 */
	private String Cc;
	
	/**
	 * ���� ����
	 */
	private String Bcc;
	
	/**
	 * ��¥/�ð�
	 */
	private Date Date;
	
	/**
	 * ����
	 */
	private String Subject;
	
	/** 
	 * ����
	 */
	private String Body;

	/**
	 * ������
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
	 * �߽��� ��ȯ�Ѵ�.	
	 * @return	�߽�
	 */
	public String getFrom() {
		return From;
	}

	/** 
	 * �߽��� �����Ѵ�.
	 * @param from	������ �߽�
	 */
	public void setFrom(String from) {
		From = from;
	}

	/**
	 * ������ ��ȯ�Ѵ�.
	 * @return	����
	 */
	public String getTo() {
		return To;
	}

	/**
	 * ������ �����Ѵ�.
	 * @param to	������ ����
	 */
	public void setTo(String to) {
		To = to;
	}

	/**
	 * ������ ��ȯ�Ѵ�.
	 * @return	����
	 */
	public String getCc() {
		return Cc;
	}

	/**
	 * ������ �����Ѵ�.
	 * @param cc	������ ����
	 */
	public void setCc(String cc) {
		Cc = cc;
	}

	/**
	 * ���� ������ ��ȯ�Ѵ�.
	 * @return	���� ����
	 */
	public String getBcc() {
		return Bcc;
	}

	/**
	 * ������ �����Ѵ�.
	 * @param bcc	������ ����
	 */
	public void setBcc(String bcc) {
		Bcc = bcc;
	}

	/**
	 * ��¥/�ð��� ��ȯ�Ѵ�.
	 * @return	��¥/�ð�
	 */
	public Date getDate() {
		return Date;
	}

	/**
	 * ��¥/�ð��� �����Ѵ�.
	 * @param date	������ ��¥/�ð�
	 */
	public void setDate(Date date) {
		Date = date;
	}

	/**
	 * ������ ��ȯ�Ѵ�.
	 * @return	����
	 */
	public String getSubject() {
		return Subject;
	}

	/**
	 * ������ �����Ѵ�.
	 * @param subject	������ ����
	 */
	public void setSubject(String subject) {
		Subject = subject;
	}

	/**
	 * ������ ��ȯ�Ѵ�.
	 * @return	����
	 */
	public String getBody() {
		return Body;
	}

	/**
	 * ������ �����Ѵ�.
	 * @param body	������ ����
	 */
	public void setBody(String body) {
		Body = body;
	}
}
