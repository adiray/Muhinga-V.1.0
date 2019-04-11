package com.example.dell.muhingalayoutprototypes;


import com.google.gson.annotations.SerializedName;

public class UserLoginErrorResponse{

	@SerializedName("code")
	private String code;

	@SerializedName("message")
	private String message;

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	@Override
 	public String toString(){
		return 
			"UserLoginErrorResponse{" + 
			"code = '" + code + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}