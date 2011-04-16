package com.scholastic.sbam.client.uiobjects.foundation;


public interface DualEditGridLink {
	public DualEditGridLinker getGridLinker();
	
	public void setGridLinker(DualEditGridLinker linker);
	
	public void prepareForActivation(Object... args);
}
