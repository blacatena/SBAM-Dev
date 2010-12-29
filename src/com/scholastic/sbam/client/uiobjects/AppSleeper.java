package com.scholastic.sbam.client.uiobjects;

/**
 * This interface lets inactive components go to sleep (i.e. unload unused memory objects, etc.) and reawaken.
 * 
 * Each implementing class is responsible for handling it's own tasks, as well as passing the sleep and awaken messages on to any children.
 * 
 * @author Bob Lacatena
 *
 */
public interface AppSleeper {
	public void awaken();
	
	public void sleep();
}
