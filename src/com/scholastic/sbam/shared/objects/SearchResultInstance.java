package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A shared instance object for generic full text search results.
 * @author Bob Lacatena
 *
 */
public class SearchResultInstance implements IsSerializable, BeanModelTag {
	private String id;
	private String title;
	private double score;
	private String text;
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
	public int getScore100() {
		return (int) (Math.round(score * 100));
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String toString() {
		return id + " / " + title + " / " + text;
	}
}
