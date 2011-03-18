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
	private char	closed;
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
	public char getClosed() {
		return closed;
	}
	public void setClosed(char closed) {
		this.closed = closed;
	}
	public void setClosed(boolean closed) {
		this.closed = closed?'y':'n';
	}
	public boolean isClosed() {
		return closed == 'y';
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
	public boolean isEqualPrevious(String one, String two) {
		if (one == null)
			return true;
		if (two == null)
			return false;
		return one.equals(two);
	}
	public boolean equalsPrevious(UserPortletCacheInstance other) {
		if (other == null) return false;
		if (!isEqualPrevious(userName, other.userName)) return false;
		if (portletId != other.portletId)		return false;
		if (!isEqualPrevious(portletType, other.portletType)) return false;
		if (restoreColumn >= 0 && restoreColumn != other.restoreColumn) return false;
		if (restoreRow >= 0 && restoreRow != other.restoreRow) return false;
		if (restoreHeight > 0 && restoreHeight != other.restoreHeight) return false;
		if (restoreWidth > 0 && restoreWidth != other.restoreWidth) return false;
		if (minimized != other.minimized) return false;
		if (closed != other.closed) return false;
		if (!isEqualPrevious(keyData, other.keyData)) return false;
		return true;
	}
}
