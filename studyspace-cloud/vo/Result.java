package com.library.entity.vo;
import java.io.Serializable;

public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 成功标志
	 */
	private boolean success = true;

	/**
	 * 返回处理消息
	 */
	private String message = "操作成功！";

	/**
	 * 返回代码
	 */
	private Integer code = 0;

	/**
	 * 返回数据对象 data
	 */
	private T result;

	/**
	 * 时间戳
	 */
	private long timestamp = System.currentTimeMillis();

	public Result() {
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * 成功返回（自定义消息）
	 */
	public Result<T> success(String message) {
		this.message = message;
		this.code = 200;
		this.success = true;
		return this;
	}

	/**
	 * 成功返回（带数据）
	 */
	public Result<T> success(T data) {
		this.result = data;
		this.code = 200;
		this.success = true;
		return this;
	}

	/**
	 * 成功返回（带数据和消息）
	 */
	public Result<T> success(T data, String message) {
		this.result = data;
		this.message = message;
		this.code = 200;
		this.success = true;
		return this;
	}

	/**
	 * 默认成功返回
	 */
	public static Result<Object> ok() {
		Result<Object> r = new Result<>();
		r.setSuccess(true);
		r.setCode(200);
		r.setMessage("成功");
		return r;
	}

	/**
	 * 自定义消息的成功返回
	 */
	public static Result<Object> ok(String msg) {
		Result<Object> r = new Result<>();
		r.setSuccess(true);
		r.setCode(200);
		r.setMessage(msg);
		return r;
	}

	/**
	 * 带数据的成功返回
	 */
	public static Result<Object> ok(Object data) {
		Result<Object> r = new Result<>();
		r.setSuccess(true);
		r.setCode(200);
		r.setResult(data);
		return r;
	}

	/**
	 * 带数据和消息的成功返回
	 */
	public static Result<Object> ok(Object data, String msg) {
		Result<Object> r = new Result<>();
		r.setSuccess(true);
		r.setCode(200);
		r.setMessage(msg);
		r.setResult(data);
		return r;
	}

	/**
	 * 登录成功返回
	 */
	public static Result<Object> loginSuccess(Object userData) {
		return ok(userData, "登录成功");
	}

	/**
	 * 注册成功返回
	 */
	public static Result<Object> registerSuccess() {
		return ok("注册成功");
	}

	/**
	 * 默认错误返回
	 */
	public static Result<Object> error(String msg) {
		return error(500, msg);
	}

	/**
	 * 自定义错误码的错误返回
	 */
	public static Result<Object> error(int code, String msg) {
		Result<Object> r = new Result<>();
		r.setCode(code);
		r.setMessage(msg);
		r.setSuccess(false);
		return r;
	}

	/**
	 * 登录失败返回
	 */
	public static Result<Object> loginError(String msg) {
		return error(401, msg);
	}

	/**
	 * 注册失败返回
	 */
	public static Result<Object> registerError(String msg) {
		return error(400, msg);
	}

	/**
	 * 服务器错误返回
	 */
	public Result<T> error500(String message) {
		this.message = message;
		this.code = 500;
		this.success = false;
		return this;
	}

	/**
	 * 无权限访问返回结果
	 */
	public static Result<Object> noauth(String msg) {
		return error(403, msg);
	}

	/**
	 * 参数错误返回
	 */
	public static Result<Object> paramError(String msg) {
		return error(400, msg);
	}
}