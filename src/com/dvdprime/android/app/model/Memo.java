package com.dvdprime.android.app.model;

public class Memo extends BaseModel {
	
	private String Id;			//���� ID
	private String MemoId;		//���� ���� ID
	private String UserId;		//����� ���̵�
	private String UserName;	//����� �г���
	private String Content;		//���� ����
	private String Date;		//�ۼ���
	private int position;		//��Ͽ����� ��ġ
	
	
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
