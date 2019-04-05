package com.sora.crawler.crawler.common;

public enum RestCode {
	READY(0,""),
	RUNNING(1,"waiting..."),
	DONE(2,"finished"),
	ERROR(3,"error");
	
	public final int code;
	public final String msg;
	private RestCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
