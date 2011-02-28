package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.CardPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.HelpTextService;
import com.scholastic.sbam.client.services.HelpTextServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.HelpTextInstance;

/**
 * A dialog window to display and navigate help text.
 * 
 * @author Bob Lacatena
 *
 */
public class HelpTextDialog extends EffectsDialog implements HelpIndexTreeActor {
	private final int DEFAULT_WIDTH		= 650;
	private final int DEFAULT_HEIGHT	= 500;
	
	/**
	 * A button designed specifically to jump to another page.
	 * @author Bob Lacatena
	 *
	 */
	protected class JumpButton extends Button {
		private String jumpId;
		private String jumpTitle;
		private String jumpIconName;
		private String defaultTitle;
		private String defaultIconName;
		
		public JumpButton(String defaultTitle, String defaultIconName) {
			super(defaultTitle);
			this.defaultTitle = defaultTitle;
			this.defaultIconName = defaultIconName;
			if (defaultIconName != null && defaultIconName.length() > 0)
				IconSupplier.setIcon(this, defaultIconName);
			this.addListener(Events.Select, getListener());
			this.setJumpId(null);
		}
		
		public SelectionListener<ButtonEvent> getListener() {
			SelectionListener<ButtonEvent> listener = new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					jumpTo(jumpId);
				}
			};
			return listener;
		}
		
		public void setJump(String jumpId, String jumpTitle) {
			setJumpId(jumpId);
			setJumpTitle(jumpTitle);
		}
		
		public void setJump(String jumpId, String jumpTitle, String jumpIconName) {
			setJumpId(jumpId);
			setJumpTitle(jumpTitle);
			setJumpIconName(jumpIconName);
		}

		public String getJumpId() {
			return jumpId;
		}
		public void setJumpId(String jumpId) {
			this.jumpId = jumpId;
			if (jumpId == null || jumpId.length() == 0)
				disable();
			else
				enable();
		}
		public String getJumpTitle() {
			return jumpTitle;
		}
		public void setJumpTitle(String jumpTitle) {
			this.jumpTitle = jumpTitle;
			if (jumpTitle != null && jumpTitle.length() > 0)
				setText(jumpTitle);
			else
				setText(defaultTitle);
		}
		public String getJumpIconName() {
			return jumpIconName;
		}
		public void setJumpIconName(String jumpIconName) {
			this.jumpIconName = jumpIconName;
			if (jumpIconName != null && jumpIconName.length() > 0) {
				IconSupplier.forceIcon(this, jumpIconName);
			} else if (defaultIconName != null && defaultIconName.length() > 0) {
				IconSupplier.forceIcon(this, defaultIconName);
			} else {
				setIcon(null);
			}
		}
		
	}
	
	/**
	 * This class encapsulates a MenuItem that will perform jumps to spots in history.
	 * @author Bob Lacatena
	 *
	 */
	protected class JumpMenuItem extends MenuItem {
		private String jumpId;
		private int    histIndex;
		
		public JumpMenuItem(String setJumpId, String title, String iconName, int setHistIndex) {
			super(title);
			this.jumpId = setJumpId;
			this.histIndex = setHistIndex;
			IconSupplier.setIcon(this, iconName);
			this.addListener(Events.Select, new Listener<BaseEvent>() {
					public void handleEvent(BaseEvent me) {
						jumpTo(jumpId, histIndex);
					}
				});
		}
		
		public JumpMenuItem(String setJumpId, String title, String iconName) {
			this(setJumpId, title, iconName, -1);
		}
		
		public JumpMenuItem(int histIndex) {
			this(historyIds.get(histIndex), historyTitles.get(histIndex), historyIconNames.get(histIndex), histIndex);
		}
	}
	
	private final HelpTextServiceAsync helpTextService = GWT.create(HelpTextService.class);
	
	
	private	String 				helpTextId;
	private	HelpTextInstance	helpText;
	
	protected Html					text = new Html();
	protected LayoutContainer		content;
	protected CardLayout			cards;
	protected CardPanel				textCard;
	protected CardPanel				indexCard;
	protected Button				done;
	protected Status				status;
	// Navigation toolbars
	protected ToolBar				topToolBar;
	protected ToolBar				bottomToolBar;
	// Content navigation buttons
	protected JumpButton			prevPageButton;
	protected JumpButton			nextPageButton;
	protected Button				indexButton;
	protected Button				relatedButton;
	protected Menu					relatedMenu;
	protected Button				childrenButton;
	protected Menu					childrenMenu;
	protected JumpButton			parentButton;
	protected ComboBox<ModelData>	searchBox;
	protected ListStore<ModelData>	searchStore;
	// History navigation buttons
	protected Button				histFirstButton;
	protected Button				histLastButton;
	protected SplitButton			histForwButton;
	protected SplitButton			histBackButton;
	protected Menu					histBackMenu;
	protected Menu					histForwMenu;
	
	protected List<String>			historyIds = new ArrayList<String>();
	protected List<String>			historyTitles = new ArrayList<String>();
	protected List<String>			historyIconNames = new ArrayList<String>();
	protected int					histIndex;
	
	protected TreePanel<ModelData>	indexTree = null;
	

	public HelpTextDialog(String helpTextId) {
		this.helpTextId = helpTextId;
		init();
	}

	public HelpTextDialog() {
		init();
	}

	public void init() {
		FlowLayout layout = new FlowLayout();
		setLayout(layout);
	
//		setAnimCollapse(true);
		setButtonAlign(HorizontalAlignment.LEFT);
		setButtons("");
		IconSupplier.setIcon(this, IconSupplier.getHelpIconName());
		setHeading("SBAM Help");
		setModal(true);
		setBodyBorder(true);
		setBodyStyle("padding: 8px;background: none");
		setWidth(DEFAULT_WIDTH);
		setHeight(DEFAULT_HEIGHT);
		setResizable(true);
		setClosable(true);
		setShadow(true);
		setShadowOffset(getShadowOffset() + (getShadowOffset() / 2));
//		setAutoHide(true);
		this.removeFromParentOnHide = true;
		
		
		topToolBar = getNavigationToolBar();
		bottomToolBar = getHistoryToolBar();
		
		content = new LayoutContainer();

		cards = new CardLayout();
		content.setLayout(cards);
		
		textCard = new CardPanel();
		textCard.add(text);
		content.add(textCard);
//		content.add(text);
		
		setTopComponent(topToolBar);
		
		add(content);
		
		setBottomComponent(bottomToolBar);
		
		if (helpTextId != null && helpTextId.length() > 0)
			jumpTo(helpTextId);
		else
			formatBlankPage();

	}
	
	/**
	 * Create the navigation toolbar.
	 * @return
	 */
	protected ToolBar getNavigationToolBar() {
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.LEFT);
		toolBar.setSpacing(5);
		
		prevPageButton = new JumpButton("Previous", IconSupplier.getHelpPrevIconName());
		
		nextPageButton = new JumpButton("Next", IconSupplier.getHelpNextIconName());
		
		relatedButton = new Button("Related");
		relatedButton.disable();
		IconSupplier.forceIcon(relatedButton, IconSupplier.getHelpRelatedIconName());
		
		relatedMenu = new Menu();
		relatedMenu.setMaxHeight(200);
		relatedButton.setMenu(relatedMenu);
		
		indexButton = new Button("Index");
		IconSupplier.forceIcon(indexButton, IconSupplier.getHelpIndexIconName());
		indexButton.addListener(Events.Select,  new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					formatIndex();
				}
			});

		childrenButton = new Button("More");
		childrenButton.disable();
		IconSupplier.forceIcon(childrenButton, IconSupplier.getHelpMoreIconName());
		
		childrenMenu = new Menu();
		childrenMenu.setMaxHeight(200);
		childrenButton.setMenu(childrenMenu);
		
		parentButton = new JumpButton("Up", IconSupplier.getHelpUpIconName());
		
		searchStore = getSearchStore();
		
		searchBox = new ComboBox<ModelData>();  
		searchBox.setFieldLabel("Search");  
		searchBox.setDisplayField("title");  
		searchBox.setName("id");  
		searchBox.setValueField("id");  
		searchBox.setForceSelection(true);  
		searchBox.setStore(searchStore);  
		searchBox.setTriggerAction(TriggerAction.ALL);
	
		toolBar.add(prevPageButton);
		toolBar.add(nextPageButton); 
		
		toolBar.add(new SeparatorToolItem());
		
		toolBar.add(parentButton);
		toolBar.add(childrenButton);
		toolBar.add(relatedButton);

		toolBar.add(new FillToolItem());
		
		toolBar.add(indexButton);
		
		toolBar.add(new SeparatorToolItem());
		
		toolBar.add(searchBox);
		
		return toolBar;
	}
	
	/**
	 * 
	 * @return
	 */
	protected ListStore<ModelData> getSearchStore() {
	
		List<ModelData> list = new ArrayList<ModelData>();
		ModelData model = new BaseModelData(); model.set("id", "X"); model.set("title", "Mr. X"); list.add(model);
		model = new BaseModelData(); model.set("id", "Y"); model.set("title", "Mr. Y"); list.add(model);
		model = new BaseModelData(); model.set("id", "Z"); model.set("title", "Mr. Z"); list.add(model);
		ListStore<ModelData> searchStore = new ListStore<ModelData>();  
		searchStore.add(list);
		
		return searchStore;
	}
	
	/**
	 * Create the history navigation toolbar.
	 * @return
	 */
	protected ToolBar getHistoryToolBar() {
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		toolBar.setSpacing(15);
		
		histFirstButton = new Button("First", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				jumpTo(0);
			}
		});
		histFirstButton.disable();
		IconSupplier.forceIcon(histFirstButton, IconSupplier.getFirstIconName());
		
		histBackButton = new SplitButton("Back", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				jumpTo(histIndex - 1);
			}
		});
		histBackButton.disable();
		IconSupplier.forceIcon(histBackButton, IconSupplier.getBackwardIconName());
		histBackMenu = new Menu();
		histBackMenu.setMaxHeight(200);
		histBackButton.setMenu(histBackMenu);
		
		histForwButton = new SplitButton("Forward", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				jumpTo(histIndex + 1);
			}
		});
		histForwButton.disable();
		IconSupplier.forceIcon(histForwButton, IconSupplier.getForwardIconName());
		histForwMenu = new Menu();
		histForwMenu.setMaxHeight(200);
		histForwButton.setMenu(histForwMenu);
		
		histLastButton = new Button("Last", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				jumpTo(historyIds.size() - 1);
			}
		});
		histLastButton.disable();
		IconSupplier.forceIcon(histLastButton, IconSupplier.getLastIconName());
		
		toolBar.add(histFirstButton);
		toolBar.add(histBackButton);
		toolBar.add(histForwButton);
		toolBar.add(histLastButton);
		
		return toolBar;
	}
	
	/**
	 * Show the dialog.
	 */
	public void show(boolean animate) {
		super.show(false);
	}
	
	/**
	 * Load a particular page by ID, possibly already at a particular location in history
	 * @param id
	 * @param histIndex
	 */
	protected void jumpTo(String id, int histIndex) {
		if (id != null && id.length() > 0) {
			loadHelpText(id, histIndex);
		}
	}
	
	/**
	 * Jump to a point in history, by index
	 * @param histIndex
	 */
	protected void jumpTo(int histIndex) {
		// If this index is displayed, re-show the text
		if (cards.getActiveItem() == indexCard) {
			cards.setActiveItem(textCard);
			//	If this index was displayed, and we're not going back to the beginning of a history chain (i.e. a "first") then just stay where we are
			//		(which is the same as "going back from the index")
			if (histIndex > 0 || historyIds.size() <= 1) {
				adjustHistoryNavigation();
				return;
			}
		}
		if (histIndex >= 0 && histIndex < historyIds.size())
			jumpTo(historyIds.get(histIndex), histIndex);
	}
	
	/**
	 * Jump to a new page, by ID
	 */
	public void jumpTo(String id) {
		jumpTo(id, -1);
	}
	
	/**
	 * This is a new page load, to be added to history.
	 * @param helpText
	 */
	protected void addToHistory(HelpTextInstance helpText) {
		//	First, erase history beyond the current pointer
		for (int removeIndex = historyIds.size() - 1; removeIndex > histIndex; removeIndex--) {
			historyIds.remove(removeIndex);
			historyTitles.remove(removeIndex);
			historyIconNames.remove(removeIndex);
		}
		//	Then add this to the top of history, and set the pointer there
		historyIds.add(helpText.getId());
		historyTitles.add(helpText.getTitle());
		historyIconNames.add(helpText.getIconName());
		histIndex = historyIds.size() - 1;
		//	Now adjust the navigation state
		adjustHistoryNavigation();
	}
	
	/**
	 * This is a history page load, so just set the index and adjust the controls.
	 * @param histIndex
	 */
	protected void setHistIndex(int histIndex) {
		this.histIndex = histIndex;
		adjustHistoryNavigation();
	}
	
	/**
	 * Adjust the history navigation buttons to match the state of the history and the current position.
	 */
	protected void adjustHistoryNavigation() {
		if (histIndex + 1 < historyIds.size()) {
			histForwButton.enable();
			IconSupplier.forceIcon(histForwButton, IconSupplier.getForwardIconName());
			histLastButton.enable();
			IconSupplier.forceIcon(histLastButton, IconSupplier.getLastIconName());
			histForwMenu.removeAll();
			for (int i = histIndex + 1; i < historyIds.size(); i++) {
				MenuItem item = new JumpMenuItem(i);
				histForwMenu.add(item);
			}
		} else {
			histForwButton.disable();
			IconSupplier.forceIcon(histForwButton, IconSupplier.getForwardIconName());
			histLastButton.disable();
			IconSupplier.forceIcon(histLastButton, IconSupplier.getLastIconName());
			histForwMenu.removeAll();
		}

		if (histIndex > 0 && historyIds.size() > 0) {
			System.out.println("enable");
			histBackButton.enable();
			IconSupplier.forceIcon(histBackButton, IconSupplier.getBackwardIconName());
			histFirstButton.enable();
			IconSupplier.forceIcon(histFirstButton, IconSupplier.getFirstIconName());
			histBackMenu.removeAll();
			for (int i = histIndex - 1; i >= 0; i--) {
				MenuItem item = new JumpMenuItem(i);
				histBackMenu.add(item);
			}
		} else {
			System.out.println("disable()");
			histBackButton.disable();
			IconSupplier.forceIcon(histBackButton, IconSupplier.getBackwardIconName());
			histFirstButton.disable();
			IconSupplier.forceIcon(histFirstButton, IconSupplier.getFirstIconName());
			histBackMenu.removeAll();
		}
	}

	protected void loadHelpText(final String id, final int histIndex) {
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		helpTextService.getHelpText(id,
				new AsyncCallback<HelpTextInstance>() {
					public void onFailure(Throwable caught) {
						setHelpText(null);
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Help text access failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(HelpTextInstance helpText) {
						setHelpText(helpText);
						if (histIndex < 0)
							addToHistory(helpText);
						else
							setHistIndex(histIndex);
					}
			});
	}
	
	/**
	 * Format a page of Help Text
	 */
	private void formatPage() {
		if (helpText == null) {
			formatBlankPage();
			return;
		}
		
		// Set the heading
		this.setHeading(helpText.getTitle());
		if (helpText.getIconName() != null && helpText.getIconName().length() > 0)
			IconSupplier.setIcon(this, helpText.getIconName());
		else
			IconSupplier.setIcon(this, IconSupplier.getHelpIconName());
		
		// Set the text
		StringBuffer sb = new StringBuffer();
		sb.append(helpText.getText());
		text.setHtml(sb.toString());
		
		//	Set navigation buttons
		prevPageButton.setJump(helpText.getPrevSiblingId(), helpText.getPrevSiblingTitle(),		helpText.getPrevSiblingIconName());
		nextPageButton.setJump(helpText.getNextSiblingId(), helpText.getNextSiblingTitle(),		helpText.getNextSiblingIconName());
		parentButton.setJump(helpText.getParentId(),      	helpText.getParentTitle(),			helpText.getParentIconName());
		
		//	Set children menu
		if (helpText.getChildIdCount() == 0) {
			childrenButton.disable();
			IconSupplier.forceIcon(childrenButton, IconSupplier.getHelpMoreIconName());	//	To switch icon for enabled/disabled
			childrenMenu.removeAll();
		} else {
			childrenButton.enable();
			IconSupplier.forceIcon(childrenButton, IconSupplier.getHelpMoreIconName());	//	To switch icon for enabled/disabled
			childrenMenu.removeAll();
			for (int i = 0; i < helpText.getChildIdCount(); i++) {
				MenuItem item = new JumpMenuItem(helpText.extractChildId(i), helpText.extractChildTitle(i), helpText.extractChildIconName(i));
				childrenMenu.add(item);
			}
		}
		
		//	Set related menu
		if (helpText.getRelatedIdCount() == 0) {
			relatedButton.disable();
			IconSupplier.forceIcon(relatedButton, IconSupplier.getHelpRelatedIconName());	//	To switch icon for enabled/disabled
			relatedMenu.removeAll();
		} else {
			relatedButton.enable();
			IconSupplier.forceIcon(relatedButton, IconSupplier.getHelpRelatedIconName());	//	To switch icon for enabled/disabled
			relatedMenu.removeAll();
			for (int i = 0; i < helpText.getRelatedIdCount(); i++) {
				final MenuItem item = new JumpMenuItem(helpText.extractRelatedId(i), helpText.extractRelatedTitle(i), helpText.extractRelatedIconName(i));
				relatedMenu.add(item);
			}
		}
		
		//	Make sure the text is displayed (in case another card is active)
		if (cards.getActiveItem() != textCard) {
			cards.setActiveItem(textCard);
		}
	}
	
	/**
	 * Format a blank page... used when no Help Text is unexpectedly returned.
	 */
	protected void formatBlankPage() {
		text.setHtml("<em>This page intentionally left blank.</em>");
		
		if (cards.getActiveItem() != textCard) {
			cards.setActiveItem(textCard);
		}
	}
	
	/**
	 * Show the index.
	 */
	protected void formatIndex() {
		//	If the index hasn't been shown yet, create it
		if (indexCard == null) {
			indexTree = HelpIndexTree.getTreePanel(this);
			indexCard = new CardPanel();
			indexCard.add(indexTree);
			content.add(indexCard);
		}
		//	Make the index active
		cards.setActiveItem(indexCard);
		//	Set the nav buttons for going back to the last page (or first page)
		histFirstButton.enable();
		IconSupplier.forceIcon(histFirstButton,IconSupplier.getFirstIconName());
		histBackButton.enable();
		IconSupplier.forceIcon(histBackButton, IconSupplier.getBackwardIconName());
	}
	
	/**
	 * Set the help text and format the page to contain it.
	 * @param helpText
	 */
	public void setHelpText(HelpTextInstance helpText) {
		if(helpText != null)
			this.helpTextId = helpText.getId();	//	Can't use the setter here of all places, because it will trigger an infinite loop of reloads
		this.helpText = helpText;
		formatPage();
	}

	public String getHelpTextId() {
		return helpTextId;
	}

	/**
	 * Set the help text to a particular ID (loading and displaying the page for that ID).
	 * @param helpTextId
	 */
	public void setHelpTextId(String helpTextId) {
		this.helpTextId = helpTextId;
		loadHelpText(helpTextId, -1);
	}

	public HelpTextInstance getHelpText() {
		return helpText;
	}
	
}
