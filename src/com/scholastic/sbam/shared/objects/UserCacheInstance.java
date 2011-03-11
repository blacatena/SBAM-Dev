package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class UserCacheInstance  implements BeanModelTag, IsSerializable {
	private String	userName;
	private String	category;
	private int		intKey;
	private String	strKey;
	private int		restoreColumn;
	private int		restoreRow;
	private char	restoreState;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getIntKey() {
		return intKey;
	}
	public void setIntKey(int intKey) {
		this.intKey = intKey;
	}
	public String getStrKey() {
		return strKey;
	}
	public void setStrKey(String strKey) {
		this.strKey = strKey;
	}
	public int getRestoreColumn() {
		return restoreColumn;
	}
	public void setRestoreColumn(int restoreColumn) {
		this.restoreColumn = restoreColumn;
	}
	public int getRestoreRow() {
		return restoreRow;
	}
	public void setRestoreRow(int restoreRow) {
		this.restoreRow = restoreRow;
	}
	public char getRestoreState() {
		return restoreState;
	}
	public void setRestoreState(char restoreState) {
		this.restoreState = restoreState;
	}
	
	
}
