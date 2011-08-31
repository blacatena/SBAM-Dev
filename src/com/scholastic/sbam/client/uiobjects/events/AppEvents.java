package com.scholastic.sbam.client.uiobjects.events;

import com.extjs.gxt.ui.client.event.EventType;

/**
 * These are the application local events.
 * 
 * To fire an event:
 * 
 * 		<code>EventBus.getSinglton().fireEvent(AppEvents.NewAgreement, new BaseEvent(AppEvents.NewAgreement));</code>
 * 
 * or
 * 
 * 		<code>EventBus.getSinglton().fireEvent(AppEvents.NewAgreement, new AppEvent(AppEvents.NewAgreement));</code>
 * 
 * If necessary, first create the event and set values.
 * 
 * To listen for an event:
 * 
 * 		<code>AppEventBus.getSingleton().addListener(AppEvents.GenericAppEvent, new Listener<BaseEvent>() {
 * 					public void handleEvent(BaseEvent e) {
 * 						// do something in response to the event
 * 					}
 * 				}</code>
 * 
 * or
 * 
 * 		<code>AppEventBus.getSingleton().addListener(AppEvents.GenericAppEvent, new Listener<AppEvent>() {
 * 					public void handleEvent(AppEvent e) {
 * 						// do something in response to the event
 * 					}
 * 				}</code>
 * 
 * @author Bob Lacatena
 *
 */
public class AppEvents {
	public static final int			APP_EVENT_BASE		=	2011000;
	public static final int			GENERIC_APP_EVENT	=	0 + APP_EVENT_BASE;
	public static final int			NEW_AGREEMENT		=	1 + APP_EVENT_BASE;
	public static final int			CHANGE_AGREEMENT	=	2 + APP_EVENT_BASE;
	public static final int			NEW_SITE			=	3 + APP_EVENT_BASE;
	public static final int			NEW_CUSTOMER		=	4 + APP_EVENT_BASE;
	public static final int			NEW_PROXY			=	5 + APP_EVENT_BASE;

	
	public static final EventType GenericAppEvent		= new EventType(GENERIC_APP_EVENT);
	public static final EventType AgreementAccess		= new EventType(NEW_AGREEMENT);
	public static final EventType ChangeAgreementTerms	= new EventType(CHANGE_AGREEMENT);
	public static final EventType SiteAccess			= new EventType(NEW_SITE);
	public static final EventType NewCustomer			= new EventType(NEW_CUSTOMER);
	public static final EventType ProxyAccess			= new EventType(NEW_PROXY);
}
