package com.dvdprime.android.app.model;

public class Comment extends BaseModel {
	
	private String Id;			//댓글 ID
	private String userId;		//회원 아이디
	private String userName;	//회원 닉네임
	private String content;		//댓글 내용
	private String avatarUrl;	//아바타URL
	private String date;		//작성일
	private String recommend;	//추천수
	private String commentId;	//댓글 고유번호
	private String upper;		//덧글의 댓글 아이디
	private int position;		//목록에서 선택된 위치값
	
	
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
