package com.nbreds.bigdata.sms.vo;

public class SmsVo {

	private String send_mdn;
	private String recevie_mdn;
	private String content;
	private String title;
	
	public SmsVo() {}

	public String getSend_mdn() {
		return send_mdn;
	}

	public void setSend_mdn(String send_mdn) {
		this.send_mdn = send_mdn;
	}

	public String getRecevie_mdn() {
		return recevie_mdn;
	}

	public void setRecevie_mdn(String recevie_mdn) {
		this.recevie_mdn = recevie_mdn;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "SmsVo [send_mdn=" + send_mdn + ", recevie_mdn=" + recevie_mdn + ", content=" + content + ", title="
				+ title + "]";
	}
	
}
