package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class InstitutionPubPrivInstance implements BeanModelTag, IsSerializable {

	private String publicPrivateCode;
	private String shortName;
	private String description;
	
	public String getPublicPrivateCode() {
		return publicPrivateCode;
	}
	public void setPublicPrivateCode(String publicPrivateCode) {
		this.publicPrivateCode = publicPrivateCode;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
