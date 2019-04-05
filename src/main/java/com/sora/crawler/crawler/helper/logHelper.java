package com.sora.crawler.crawler.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class logHelper {
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static Logger getLogger() {
		return LOGGER;
	}
	
	public static void trace (String msg) {
		LOGGER.trace(msg);
	}
	
	public static void debug (String msg) {
		LOGGER.debug(msg);
	}
	
	public static void info (String msg) {
		LOGGER.info(msg);
	}
	
	public static void warn (String msg) {
		LOGGER.warn(msg);
	}
	
	public static void error(String msg) {
		LOGGER.error(msg);
	}
}
