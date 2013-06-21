package com.dvdprime.android.app.model;

public class Memo extends BaseModel {
	
	private String Id;			//쪽지 ID
	private String MemoId;		//쪽지 고유 ID
	private String UserId;		//사용자 아이디
	private String UserName;	//사용자 닉네임
	private String Content;		//쪽지 내용
	private String Date;		//작성일
	private int position;		//목록에서의 위치
	
	
	public String getId() {
		return Id;
	}


	public void setId(String id) {
		Id = id;
	}


	public String getMemoId() {
		return MemoId;
	}


	public void setMemoId(String memoId) {
		MemoId = memoId;
	}


	public String getUserId() {
		return UserId;
	}


	public void setUserId(String userId) {
		UserId = userId;
	}


	public String getUserName() {
		return UserName;
	}


	public void setUserName(String userName) {
		UserName = userName;
	}


	public String getContent() {
		return Content;
	}


	public void setContent(String content) {
		Content = content;
	}


	public String getDate() {
		return Date;
	}


	public void setDate(String date) {
		Date = date;
	}


	public int getPosition() {
		return position;
	}


	public void setPosition(int position) {
		this.position = position;
	}


	@Override
	public int getModelType() {
		return BaseModel.MEMO;
	}
}
