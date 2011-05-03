package com.scholastic.sbam.client.uiobjects.uiapp;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.ResizeListener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.UpdateUserCacheService;
import com.scholastic.sbam.client.services.UpdateUserCacheServiceAsync;
import com.scholastic.sbam.client.services.UpdateUserPortletCacheService;
import com.scholastic.sbam.client.services.UpdateUserPortletCacheServiceAsync;
import com.scholastic.sbam.client.uiobjects.uitop.HelpTextDialog;
import com.scholastic.sbam.shared.objects.UserCacheTarget;
import com.scholastic.sbam.shared.objects.UserPortletCacheInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public abstract class AppPortlet extends Portlet {
	
	protected AppPortletPresenter presenter;
	
	protected int		forceHeight = 550;
	
	protected String	helpTextId;
	protected boolean	resizeable = true;
	protected Resizable	resizer;
	
	protected int		portalColumn	= -1;
	protected int		portalRow		= -1;
	
	protected int		portletId		= -1;
	
	protected UserPortletCacheInstance lastCacheInstance;
	
	protected final UpdateUserCacheServiceAsync userCacheUpdateService = GWT.create(UpdateUserCacheService.class);
	protected final UpdateUserPortletCacheServiceAsync userPortletCacheUpdateService = GWT.create(UpdateUserPortletCacheService.class);
	
	public AppPortlet(String helpTextId) {
		super();
		this.helpTextId = helpTextId;
	}
	
	@Override
	protected void onRender(Element el, int index) {
		super.onRender(el, index);
		if (resizeable) {
			resizer = new Resizable(this)
			/* {
					@Override
					protected void onComponentResize() {
						super.onComponentResize();
//						System.out.println("Updating due to resizer");
						updateUserPortlet();
				}
			}*/	;
			
			resizer.setDynamic(false);	//	This can't be set to true, because of a bug in the resizer, where it sets the page position wrong and moves portlets to overlap over those above them
//			resizer.setMaxWidth(this.getWidth());
//			resizer.setMinWidth(this.getWidth());
			
			ResizeListener watchResize = new ResizeListener() {

				@Override
				public void handleEvent(ResizeEvent re) {
					handleResize();
				}
				
			};
			resizer.addResizeListener(watchResize);
		}
	}

	@Override
	protected void initTools() {
		addHelp();
		super.initTools();
		addClosable();
	}
	
	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		
		updateUserPortlet();
		
		//	If there is a resizer, and portel width has changed due to portal dimensions change, then adjust the max and min width on the resizer to compensate
//		if (resizer != null && width > -1) {
//			resizer.setMaxWidth(this.getWidth());	//	This turns off horizontal resizing
//			resizer.setMinWidth(this.getWidth());	//	This turns off vertical resizing
//		}
	}
	
	protected void addHelp() {
		if (helpTextId == null)
			return;
		
		ToolButton helpBtn = new ToolButton("x-tool-help");
//		if (GXT.isAriaEnabled()) {
//			helpBtn.setTitle(GXT.MESSAGES.pagingToolBar_beforePageText());
//		}
		helpBtn.addListener(Events.Select, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent ce) {
				HelpTextDialog htd = new HelpTextDialog(helpTextId);
				htd.show();
			}
		});
		head.addTool(helpBtn);
	}
	
	protected void addClosable() {
		
		ToolButton closeBtn = new ToolButton("x-tool-close");
		if (GXT.isAriaEnabled()) {
			closeBtn.setTitle(GXT.MESSAGES.messageBox_close());
		}
		closeBtn.addListener(Events.Select, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent ce) {
				closePortlet();
//				updateUserPortlet();
			}
		});
		head.addTool(closeBtn);
	}
	
	public void closePortlet() {
		System.out.println("Close AppPortlet");
		
		if (presenter != null) {
			System.out.println("Presenter close");
			presenter.close(this);
		} else {
			System.out.println("Vanilla close");
			if (getParent() != null && getParent().getParent() != null && getParent().getParent() instanceof Portal) {
				Portal thePortal = (Portal) getParent().getParent();
				thePortal.remove(this, portalColumn);
			}
		}
	}
	
	public void handleResize() {
		//	Handle column resizes for width changes
		if (getParent() != null) {
			if (getParent().getParent() != null) { 
				if (getParent().getParent() instanceof Portal) {
					Portal myPortal = (Portal) getParent().getParent();
					myPortal.setColumnWidth(portalColumn, getWidth());
					myPortal.getItem(portalColumn).setWidth(getWidth() + myPortal.getSpacing());
				}
			}
		}
		
//		updateUserPortlet();	//	Done in onResize, so that all impacted portlets update, not just this one
	}

	public String getHelpTextId() {
		return helpTextId;
	}

	public void setHelpTextId(String helpTextId) {
		this.helpTextId = helpTextId;
	}

	public boolean isResizeable() {
		return resizeable;
	}

	public void setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
	}
	
	@Override
	public void onCollapse() {
		super.onCollapse();
		updateUserPortlet();
	}
	
	@Override
	public void onExpand() {
		super.onExpand();
		updateUserPortlet();
	}
	
	public void registerUserCache(UserCacheTarget target, String hint) {
		
		if (!AppConstants.USER_ACCESS_CACHE_ACTIVE)
			return;
		
		if (target != null) {
			userCacheUpdateService.updateUserCache(target, hint,
					new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							// In production, this might all be removed, and treated as something users don't care about
							// Show the RPC error message to the user
							if (caught instanceof IllegalArgumentException)
								MessageBox.alert("Alert", caught.getMessage(), null);
							else {
								MessageBox.alert("Alert", "User cache update failed unexpectedly.", null);
								System.out.println(caught.getClass().getName());
								System.out.println(caught.getMessage());
							}
						}

						public void onSuccess(String result) {
							//	Do nothing
						}
				});
		}
	}
	
	public void registerUserPortlet(int portletId, int row, int col) {
		this.portletId = portletId;
		updateUserPortlet(row, col, getParent() == null, this.isCollapsed());
	}
	
	public void updateUserPortlet() {
//		System.out.println("Default update user portlet " + portletId + " where parent = null " + (getParent() == null));
		updateUserPortlet(portalRow, portalColumn, getParent() == null, this.isCollapsed());
	}
	
	public void updateUserPortlet(int row, int col) {
//		System.out.println("Row/col update user portlet " + portletId + " to " + row + " and " + col + " where parent = null " + (getParent() == null));
		updateUserPortlet(row, col, getParent() == null, this.isCollapsed());
	}
	
	public void updateUserPortlet(int row, int col, boolean closed, boolean minimized) {
//		System.out.println("Update " + portletId + " at " + row + " and " + col + " is closed " + closed);
		//	When the portlet ID hasn't been properly set (or has been purposely unset, such as on logout), there are no valid updates to do.
		if (portletId < 0) {
//			System.out.println("No ID, no update");
			return;
		}
		
		portalRow = row;
		portalColumn = col;
		
		if (!AppConstants.USER_PORTLET_CACHE_ACTIVE)
			return;
		
		UserPortletCacheInstance cacheInstance = new UserPortletCacheInstance();
		
		cacheInstance.setPortletId(portletId);
		cacheInstance.setPortletType(getClass().getName());
		cacheInstance.setKeyData(getKeyData());
		
		cacheInstance.setClosed(closed);

		cacheInstance.setRestoreColumn(-1);
		cacheInstance.setRestoreRow(-1);
		if (col >= 0)
			cacheInstance.setRestoreColumn(col);
		if (row >= 0)
			cacheInstance.setRestoreRow(row);

		cacheInstance.setRestoreWidth(-1);
		cacheInstance.setRestoreWidth(-1);
		if (!closed && !minimized && isRendered()) {
//			System.out.println("Doing width/height " + getWidth() + ",  " + getHeight());
			if (getWidth() > 0)
				cacheInstance.setRestoreWidth(getWidth());
			if (getHeight() > 0)
				cacheInstance.setRestoreHeight(getHeight());
		}
		
		cacheInstance.setMinimized(minimized);
		
		if (cacheInstance.equalsPrevious(lastCacheInstance)) {
//			System.out.println("No diffs... optimized");
			return;
		}
		
		lastCacheInstance = cacheInstance;
		
		userPortletCacheUpdateService.updateUserPortletCache(cacheInstance,
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// In production, this might all be removed, and treated as something users don't care about
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "User portlet cache update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(String result) {
						//	Do nothing
					}
			});
	}
	
	/**
	 * Get the preferred insert column for a portlet requested/created by this portlet.
	 * @return
	 */
	public int getInsertColumn() {
		//	If this portal is not in the first column, just insert left
		if (portalColumn > 0)
			return portalColumn - 1;
			
		//	If we can get the column count from the parent portal, use it
		if (getParent() != null && getParent().getParent() != null && getParent().getParent() instanceof Portal) {
			int columns = ( (Portal) getParent().getParent()).getItemCount();
			//	We don't have enough columns to insert to the right, so just use the one and only column.
			if (columns <= 1)
				return 0;
			//	We didn't insert left, and we have enough columns, so insert right.
			return portalColumn + 1;
		}
		
		//	If we're in the leftmost column, just assume we can insert right
		if (portalColumn <= 0)
			return 1;
		else	// If we're in a right column, insert one left
			return portalColumn - 1;
	}
	
	public String getShortPortletName() {
		return portalColumn + " - " + portalRow;
	}
	
	public int getPortalColumn() {
		return portalColumn;
	}

	public void setPortalColumn(int portalColumn) {
		this.portalColumn = portalColumn;
	}

	public int getPortalRow() {
		return portalRow;
	}

	public void setPortalRow(int portalRow) {
		this.portalRow = portalRow;
	}

	public int getPortletId() {
		return portletId;
	}

	public void setPortletId(int portletId) {
		this.portletId = portletId;
	}

	public int getForceHeight() {
		return forceHeight;
	}

	public void setForceHeight(int forceHeight) {
		this.forceHeight = forceHeight;
	}

	public UserPortletCacheInstance getLastCacheInstance() {
		return lastCacheInstance;
	}

	public void setLastCacheInstance(UserPortletCacheInstance lastCacheInstance) {
		this.lastCacheInstance = lastCacheInstance;
	}

	public AppPortletPresenter getPresenter() {
		return presenter;
	}

	public void setPresenter(AppPortletPresenter presenter) {
		this.presenter = presenter;
	}

	public abstract void setFromKeyData(String keyData);

	public abstract String getKeyData();
}
