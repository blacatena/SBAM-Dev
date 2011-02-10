package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentationInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {
	
	private int		id;
	private int		seq;
	private String	title;
	private String	types;
	private String	link;
	private String	iconImage;
	private String	docVersion;
	private String	description;
	private char	status;
	private boolean active;
	private Date	updatedDatetime;
	private Date	createdDatetime;
	
	@Override
	public void markForDeletion() {
		setStatus('X');
	}

	@Override
	public boolean thisIsDeleted() {
		return status == 'X';
	}

	@Override
	public boolean thisIsValid() {
		return true;
	}

	@Override
	public String returnTriggerProperty() {
		return "junk";
	}

	@Override
	public String returnTriggerValue() {
		return "junk";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}
	
	/**
	 * Return the document types as an array, stripped of leading/trailing blanks and empty doc types. 
	 * @return
	 */
	public String [] parseTypes() {
		String [] split = types.split(types);
		int nonempty = 0;
		for (int i = 0; i < split.length; i++) {
			split [i] = split [i].trim();
			if (split [i].length() > 0)
				nonempty++;
		}
		
		if (nonempty < split.length ) {
			int j = 0;
			String [] newsplit = new String [nonempty];
			for (int i = 0; i < split.length; i++)
				if (split [i].length() > 0)
					newsplit [j++] = split [i];
			split = newsplit;
		}
		
		return split;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getIconImage() {
		return iconImage;
	}

	public void setIconImage(String iconImage) {
		this.iconImage = iconImage;
	}

	public String getDocVersion() {
		return docVersion;
	}

	public void setDocVersion(String docVersion) {
		this.docVersion = docVersion;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getUpdatedDatetime() {
		return updatedDatetime;
	}

	public void setUpdatedDatetime(Date updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
		this.active = (this.status == 'A');
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.status == 'X')
			return;
		setStatus(active?'A':'I');
	}
}
