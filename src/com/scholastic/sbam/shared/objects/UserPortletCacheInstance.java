package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class UserPortletCacheInstance  implements BeanModelTag, IsSerializable {
	private String	userName;
	private int		portletId;
	private String	portletType;
	private int		restoreColumn;
	private int		restoreRow;
	private int		restoreHeight;
	private int		restoreWidth;
	private char	minimized;
	private String	keyData;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getPortletId() {
		return portletId;
	}
	public void setPortletId(int portletId) {
		this.portletId = portletId;
	}
	public String getPortletType() {
		return portletType;
	}
	public void setPortletType(String portletType) {
		this.portletType = portletType;
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
	public int getRestoreHeight() {
		return restoreHeight;
	}
	public void setRestoreHeight(int restoreHeight) {
		this.restoreHeight = restoreHeight;
	}
	public int getRestoreWidth() {
		return restoreWidth;
	}
	public void setRestoreWidth(int restoreWidth) {
		this.restoreWidth = restoreWidth;
	}
	public char getMinimized() {
		return minimized;
	}
	public void setMinimized(char minimized) {
		this.minimized = minimized;
	}
	public void setMinimized(boolean minimized) {
		this.minimized = minimized?'y':'n';
	}
	public boolean isMinimized() {
		return minimized == 'y';
	}
	public String getKeyData() {
		return keyData;
	}
	public void setKeyData(String keyData) {
		this.keyData = keyData;
	}
	
}
