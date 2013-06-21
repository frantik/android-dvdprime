package com.dvdprime.android.app.model;

public class Comment extends BaseModel {
	
	private String Id;			//��� ID
	private String userId;		//ȸ�� ���̵�
	private String userName;	//ȸ�� �г���
	private String content;		//��� ����
	private String avatarUrl;	//�ƹ�ŸURL
	private String date;		//�ۼ���
	private String recommend;	//��õ��
	private String commentId;	//��� ������ȣ
	private String upper;		//������ ��� ���̵�
	private int position;		//��Ͽ��� ���õ� ��ġ��
	
	
	public String getId() {
		return Id;
	}


	public void setId(String id) {
		Id = id;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getAvatarUrl() {
		return avatarUrl;
	}


	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getRecommend() {
		return recommend;
	}


	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}


	public String getCommentId() {
		return commentId;
	}


	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}


	public String getUpper() {
		return upper;
	}


	public void setUpper(String upper) {
		this.upper = upper;
	}


	public int getPosition() {
		return position;
	}


	public void setPosition(int position) {
		this.position = position;
	}

}
