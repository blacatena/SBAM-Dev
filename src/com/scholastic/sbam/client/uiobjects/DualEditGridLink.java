package com.scholastic.sbam.client.uiobjects;

public interface DualEditGridLink {
	public DualEditGridLinker getGridLinker();
	
	public void setGridLinker(DualEditGridLinker linker);
	
	public void prepareForActivation(Object... args);
}
