package com.dvdprime.android.app.model;

public class Scrap extends BaseModel {
	
	private String Id;			//�Խù� ID
	private String No;			//�Խù� ��ȣ
	private String Title;		//�Խù� ����
	private String Url;			//�Խù� URL
	private String UserId;		//����� ���̵�
	private String UserName;	//����� �г���
	private String Date;		//�ۼ���
	private String Comment;		//��ۼ�
	private String Recommend;	//��õ��
	private String Count;		//��ȸ��
	private String Type;		//��ũ������(S:��ũ��,D:��â��,C:���â��)
	
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getNo() {
		return No;
	}
	public void setNo(String no) {
		No = no;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getUrl() {
		return Url;
	}
	public void setUrl(String url) {
		Url = url;
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
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public String getComment() {
		return Comment;
	}
	public void setComment(String comment) {
		Comment = comment;
	}
	public String getRecommend() {
		return Recommend;
	}
	public void setRecommend(String recommend) {
		Recommend = recommend;
	}
	public String getCount() {
		return Count;
	}
	public void setCount(String count) {
		Count = count;
	}

	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	@Override
	public int getModelType() {
		return BaseModel.SCRAP;
	}
}
