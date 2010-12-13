package com.scholastic.sbam.shared.objects;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserMessageInstance implements IsSerializable {
	private int		id;
	private String	userName;
	private String	locationTag;
	private int		x;
	private int		y;
	private int		z;
	private String	text;
	private String	created;
	private String	deleted;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLocationTag() {
		return locationTag;
	}
	public void setLocationTag(String locationTag) {
		this.locationTag = locationTag;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	
}
