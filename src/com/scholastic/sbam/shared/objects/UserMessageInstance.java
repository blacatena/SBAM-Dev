package com.scholastic.sbam.shared.objects;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserMessageInstance implements IsSerializable {
	private int		id;
	private String	userName;
	private String	locationTag;
	private int		x;
	private int		y;
	private int		z;
	private int		width;
	private int		height;
	private String	text;
	private String	created;
	private char	status;
	
	private int		restoreX;
	private int		restoreY;
	private int		restoreWidth;
	private int		restoreHeight;
	private boolean minimized;
	private boolean maximized;
	private boolean collapsed;
	
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
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
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
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public int getRestoreX() {
		return restoreX;
	}
	public void setRestoreX(int restoreX) {
		this.restoreX = restoreX;
	}
	public int getRestoreY() {
		return restoreY;
	}
	public void setRestoreY(int restoreY) {
		this.restoreY = restoreY;
	}
	public int getRestoreWidth() {
		return restoreWidth;
	}
	public void setRestoreWidth(int restoreWidth) {
		this.restoreWidth = restoreWidth;
	}
	public int getRestoreHeight() {
		return restoreHeight;
	}
	public void setRestoreHeight(int restoreHeight) {
		this.restoreHeight = restoreHeight;
	}
	public boolean isMinimized() {
		return minimized;
	}
	public void setMinimized(boolean minimized) {
		this.minimized = minimized;
	}
	public boolean isMaximized() {
		return maximized;
	}
	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}
	public boolean isCollapsed() {
		return collapsed;
	}
	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}
	public UserMessageInstance clone() {
		UserMessageInstance clone = new UserMessageInstance();
		
		clone.setId(id);
		clone.setUserName(userName);
		clone.setLocationTag(locationTag);
		clone.setText(text);
		clone.setCreated(created);
		clone.setStatus(status);
		clone.setX(x);
		clone.setY(y);
		clone.setZ(z);
		clone.setWidth(width);
		clone.setHeight(height);
		clone.setRestoreX(restoreX);
		clone.setRestoreY(restoreY);
		clone.setRestoreWidth(restoreWidth);
		clone.setRestoreHeight(restoreHeight);
		clone.setMinimized(minimized);
		clone.setMaximized(maximized);
		clone.setCollapsed(collapsed);
		
		return clone;
	}
	
	public boolean equal(UserMessageInstance other) {
		return	userName.equals(other.getUserName())
		&&		locationTag.equals(other.getLocationTag())
		&&		text == other.getText()
		&&		windowEqual(other)
		;
	}
	
	public boolean windowEqual(UserMessageInstance other) {
//		System.out.println("status" + (status == other.getStatus()));
//		System.out.println("x" + (x == other.getX()));
//		System.out.println("y" + (y == other.getY()));
//		System.out.println("z" + (z == other.getZ()));
//		System.out.println("width" + (width == other.getWidth()));
//		System.out.println("height" + (height == other.getHeight()));
//		System.out.println("restoreX" + (restoreX == other.getRestoreX()));
//		System.out.println("restoreY" + (restoreY == other.getRestoreY()));
//		System.out.println("restoreWidth" + (restoreWidth == other.getRestoreWidth()));
//		System.out.println("restoreHeight" + (restoreHeight == other.getRestoreHeight()));
//		System.out.println("minimized" + (minimized == other.isMinimized()));
//		System.out.println("maximized" + (maximized == other.isMaximized()));
//		System.out.println("collapsed" + (collapsed == other.isCollapsed()));
		return	status == other.getStatus()
		&&		x == other.getX()
		&&		y == other.getY()
		&&		z == other.getZ()
		&&		width == other.getWidth()
		&&		height == other.getHeight()
		&&		restoreX == other.getRestoreX()
		&&		restoreY == other.getRestoreY()
		&&		restoreWidth == other.getRestoreWidth()
		&&		restoreHeight == other.getRestoreHeight()
		&&		minimized == other.isMinimized()
		&&		maximized == other.isMaximized()
		&&		collapsed == other.isCollapsed()
		;
	}
	
}
