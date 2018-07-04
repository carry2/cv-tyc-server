package cn.com.chinaventure.cv.entity.vo;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class JsonResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@JSONField(ordinal = 1)
	private String resCode;

	@JSONField(ordinal = 2)
	public String resMsg;

	@JSONField(ordinal = 3)
	private T data;

	public JsonResult() {
	}

	public JsonResult(T data, String resCode) {
		this.data = data;
		this.resCode = resCode;
	}

	public JsonResult(T data, String resCode, String resMsg) {
		this.data = data;
		this.resCode = resCode;
		this.resMsg = resMsg;
	}

	public JsonResult(String resCode, String resMsg) {
		this.resCode = resCode;
		this.resMsg = resMsg;
	}
	
	public JsonResult(T data) {
		this.resCode = "0";
		this.resMsg = "ok";
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getResMsg() {
		return resMsg;
	}

	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}
	
}
