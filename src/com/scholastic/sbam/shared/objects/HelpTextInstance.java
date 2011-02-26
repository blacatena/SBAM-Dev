package com.scholastic.sbam.shared.objects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A shared instance object for help text.
 * @author Bob Lacatena
 *
 */
public class HelpTextInstance implements IsSerializable, BeanModelTag {
	private String id;
	private String iconName;
	private String title;
	private String text;
	private String parentId;
	private String nextSiblingId;
	private String prevSiblingId;
	private String relatedIdsList;
	
	private List<String> relatedIds			=	new ArrayList<String>();
	private List<String> relatedTitles		=	new ArrayList<String>();
	private List<String> relatedIconNames	=	new ArrayList<String>();
	
	private String nextSiblingTitle;
	private String nextSiblingIconName;

	private String prevSiblingTitle;
	private String prevSiblingIconName;

	private String parentTitle;
	private String parentIconName;
	
	/**
	 * Parse a string of related help text IDs into individual (non-empty) IDs. 
	 * @param idList
	 * @return
	 */
	public List<String> parseIdList(String idList) {
		List<String> returnList = new ArrayList<String>();
		if (idList == null)
			return returnList;
		String [] parts = idList.split(";");
		for (String part : parts) {
			if (part.trim().length() > 0)
				returnList.add(part.trim());
		}
		return returnList;
	}
	/**
	 * Add a single related ID and title to the list.
	 * @param id
	 * @param title
	 */
	public void addRelated(String id, String title, String iconName) {
		relatedIds.add(id);
		relatedTitles.add(title);
		relatedIconNames.add(iconName);
	}
	public int getRelatedIdCount() {
		return relatedIds.size();
	}
	public String extractRelatedId(int index) {
		return relatedIds.get(index);
	}
	public String extractRelatedTitle(int index) {
		return relatedTitles.get(index);
	}
	public String extractRelatedIconName(int index) {
		return relatedTitles.get(index);
	}
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getNextSiblingId() {
		return nextSiblingId;
	}
	public void setNextSiblingId(String nextSiblingId) {
		this.nextSiblingId = nextSiblingId;
	}
	public String getPrevSiblingId() {
		return prevSiblingId;
	}
	public void setPrevSiblingId(String prevSiblingId) {
		this.prevSiblingId = prevSiblingId;
	}
	public List<String> getRelatedIds() {
		return relatedIds;
	}
	public void setRelatedIds(List<String> relatedIds) {
		this.relatedIds = relatedIds;
	}
	public List<String> getRelatedTitles() {
		return relatedTitles;
	}
	public void setRelatedTitles(List<String> relatedTitles) {
		this.relatedTitles = relatedTitles;
	}
	public List<String> getRelatedIconNames() {
		return relatedIconNames;
	}
	public void setRelatedIconNames(List<String> relatedIconNames) {
		this.relatedIconNames = relatedIconNames;
	}
	public String getNextSiblingTitle() {
		return nextSiblingTitle;
	}
	public void setNextSiblingTitle(String nextSiblingTitle) {
		this.nextSiblingTitle = nextSiblingTitle;
	}
	public String getNextSiblingIconName() {
		return nextSiblingIconName;
	}
	public void setNextSiblingIconName(String nextSiblingIconName) {
		this.nextSiblingIconName = nextSiblingIconName;
	}
	public String getPrevSiblingTitle() {
		return prevSiblingTitle;
	}
	public void setPrevSiblingTitle(String prevSiblingTitle) {
		this.prevSiblingTitle = prevSiblingTitle;
	}
	public String getPrevSiblingIconName() {
		return prevSiblingIconName;
	}
	public void setPrevSiblingIconName(String prevSiblingIconName) {
		this.prevSiblingIconName = prevSiblingIconName;
	}
	public String getParentTitle() {
		return parentTitle;
	}
	public void setParentTitle(String parentTitle) {
		this.parentTitle = parentTitle;
	}
	public String getParentIconName() {
		return parentIconName;
	}
	public void setParentIconName(String parentIconName) {
		this.parentIconName = parentIconName;
	}
	public String getRelatedIdsList() {
		return relatedIdsList;
	}
	public void setRelatedIdsList(String relatedIdsList) {
		this.relatedIdsList = relatedIdsList;
	}
	
}
