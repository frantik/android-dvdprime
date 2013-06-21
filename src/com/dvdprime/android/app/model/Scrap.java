package com.dvdprime.android.app.model;

public class Scrap extends BaseModel {
	
	private String Id;			//게시물 ID
	private String No;			//게시물 번호
	private String Title;		//게시물 제목
	private String Url;			//게시물 URL
	private String UserId;		//사용자 아이디
	private String UserName;	//사용자 닉네임
	private String Date;		//작성일
	private String Comment;		//댓글수
	private String Recommend;	//추천수
	private String Count;		//조회수
	private String Type;		//스크랩종류(S:스크랩,D:글창고,C:댓글창고)
	
	
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
