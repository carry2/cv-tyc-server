package cn.com.chinaventure.cv.common.log;

import org.slf4j.LoggerFactory;
import com.jfinal.log.Log;

public class Slf4jLogger extends Log {

	private org.slf4j.Logger log;

	Slf4jLogger(Class<?> clazz) {
		log = LoggerFactory.getLogger(clazz);
	}

	Slf4jLogger(String name) {
		log = LoggerFactory.getLogger(name);
	}

	public void info(String message) {
		log.info(message);
	}

	public void info(String message, Throwable t) {
		log.info(message, t);
	}

	public void debug(String message) {
		log.debug(message);
	}

	public void debug(String message, Throwable t) {
		log.debug(message, t);
	}

	public void warn(String message) {
		log.warn(message);
	}

	public void warn(String message, Throwable t) {
		log.warn(message, t);
	}

	public void error(String message) {
		log.error(message);
	}

	public void error(String message, Throwable t) {
		log.error(message, t);
	}

	public void fatal(String message) {
		log.error(message);
	}

	public void fatal(String message, Throwable t) {
		log.error(message, t);
	}

	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	public boolean isFatalEnabled() {
		return log.isErrorEnabled();
	}
}