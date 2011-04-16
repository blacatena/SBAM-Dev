package com.scholastic.sbam.client;


//import com.extjs.gxt.themes.client.Slate;
//import com.extjs.gxt.ui.client.GXT;
//import com.extjs.gxt.ui.client.util.Theme;
//import com.extjs.gxt.ui.client.util.ThemeManager;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.util.ThemeManager;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
//import com.scholastic.sbam.client.uiobjects.AppSplash;
import com.scholastic.sbam.client.uiobjects.uitop.AppHolder;
import com.scholastic.sbam.client.uiobjects.uitop.AppNav;
import com.scholastic.sbam.client.uiobjects.uitop.LoginUiManager;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SBAM implements EntryPoint {
	
	private LoginUiManager		loginUiManager;
	
	@Override
	public void onModuleLoad() {
		Theme myTheme = new Theme("wheat", "Wheat", "resources/themes/xtheme-wheat/css/xtheme-wheat.css");
		ThemeManager.register(myTheme);
		GXT.setDefaultTheme(myTheme, true);
		
		//	Instantiate the login manager (must be done here, after theme is applied... not in the private declaration).
		loginUiManager		= new LoginUiManager(new AppNav());
		
		RootPanel rootPanel = RootPanel.get();
		
		//	Use a viewport to take up the whole window
		Viewport viewport = new Viewport();
		viewport.setLayout(new FitLayout());  

		//	Use an AppHolder to manage the header, footer and content areas
		AppHolder app = new AppHolder(loginUiManager.getLoggedInPanel(), loginUiManager.getTheApp(), null, loginUiManager);
		viewport.add(app);
		
		rootPanel.add(viewport);
		
		//	Start with the user not logged in
		loginUiManager.show(false);
		
		//	Hide the "loading" div which is displayed in the default HTML on startup.
		if (RootPanel.get("loading-indicator") != null) {
			RootPanel.get("loading-indicator").setVisible(false);
		}
	}
}
