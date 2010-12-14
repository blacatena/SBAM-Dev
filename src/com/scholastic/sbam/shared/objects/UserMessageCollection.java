package com.scholastic.sbam.shared.objects;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserMessageCollection implements IsSerializable {
	List<UserMessageInstance> userMessages	=	new ArrayList<UserMessageInstance>();

	public List<UserMessageInstance> getUserMessages() {
		return userMessages;
	}

	public void setUserMessages(List<UserMessageInstance> userMessages) {
		this.userMessages = userMessages;
	}
	
	public int size() {
		return userMessages.size();
	}
	
	public UserMessageInstance getUserMessage(int index) {
		if (index < 0 || index >= userMessages.size())
			return null;
		return userMessages.get(index);
	}
	
	public void add(UserMessageInstance userMessage) {
		userMessages.add(userMessage);
	}
	
	public void remove(UserMessageInstance userMessage) {
		userMessages.remove(userMessage);
	}
	
	public void remove(int index) {
		userMessages.remove(index);
	}
}
