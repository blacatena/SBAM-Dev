package com.scholastic.sbam.client.util;

import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * This is a utility class to standardize the delivery, location and naming of icons, and to simplify any future transition to the use of GWT IconBundles.
 * 
 * To turn off an icon for 
 * 
 * @author Bob Lacatena
 *
 */
public class IconSupplier {
	
	public static String FULL_COLOR_PATH = "resources/images/icons/colorful/";
	public static String MONOCHROME_PATH = "resources/images/icons/monochrome/";
	
	public static boolean BUTTON_ICONS = false;
	public static boolean HEADER_ICONS = true;
	public static boolean TAB_ICONS    = true;
	public static boolean MENU_ICONS   = true;
	
	public static String getWelcomeIconName() {
		return "welcome.png";
	}
	
	public static String getAgreementIconName() {
		return "agreement.png";
	}
	
	public static String getAgreementLinkIconName() {
		return "agreementlink.png";
	}
	
	public static String getProxyIconName() {
		return "proxy.png";
	}
	
	public static String getAccessMethodIconName() {
		return "access.png";
	}
	
	public static String getMethodConflictIconName() {
		return "conflict.png";
	}
	
	public static String getConfigurationIconName() {
		return "configuration.png";
	}
	
	public static String getExportIconName() {
		return "export.png";
	}
	
	public static String getReportIconName() {
		return "report.png";
	}
	
	public static String getPieChartIconName() {
		return "chart_pie.png";
	}
	
	public static String getBarChartIconName() {
		return "chart_bar.png";
	}
	
	public static String getChartIconName() {
		return "chart.png";
	}
	
	public static String getAdministrationIconName() {
		return "administration.png";
	}
	
	public static String getInstitutionIconName() {
		return "institution.png";
	}
	
	public static String getCustomerIconName() {
		return "customer.png";
	}
	
	public static String getProductIconName() {
		return "product.png";
	}
	
	public static String getServiceIconName() {
		return "service.png";
	}
	
	public static String getPreferenceIconName() {
		return "preference.png";
	}
	
	public static String getValueIconName() {
		return "value.png";
	}
	
	public static String getTermTypeIconName() {
		return "termtype.png";
	}
	
	public static String getDeleteReasonIconName() {
		return "deletereason.png";
	}
	
	public static String getCancelReasonIconName() {
		return "cancelreason.png";
	}
	
	public static String getAgreementTypeIconName() {
		return "agreement.png";
	}
	
	public static String getAgreementTermIconName() {
		return "calendar.png";
	}
	
	public static String getCommissionTypeIconName() {
		return "commissions.png";
	}
	
	public static String getNoteIconName() {
		return "note.png";
	}
	
	public static String getNoteAddIconName() {
		return "note_add.png";
	}
	
	public static String getNoteDeleteIconName() {
		return "note_delete.png";
	}
	
	public static String getNoteEditIconName() {
		return "note_edit.png";
	}
	
	public static String getUsersIconName() {
		return "users.png";
	}
	
	public static String getProfileIconName() {
		return "profile.png";
	}
	
	public static String getContactsIconName() {
		return "contact.png";
	}
	
	public static String getSiteIconName() {
		return "site.png";
	}
	
	public static String getRemoteIconName() {
		return "remotehome.png";
	}
	
	public static String getProgrammingIconName() {
		return "programming.png";
	}
	
	public static String getCacheIconName() {
		return "cache.png";
	}
	
	public static String getVersionIconName() {
		return "version.png";
	}
	
	public static String getMessagesIconName() {
		return "messages.png";
	}
	
	public static String getEditIconName() {
		return "edit.png";
	}
	
	public static String getSaveIconName() {
		return "save.png";
	}
	
	public static String getCancelIconName() {
		return "cancel.png";
	}
	
	public static String getDoneIconName() {
		return "done.png";
	}
	
	public static String getCheckedIconName() {
		return "checked.png";
	}
	
	public static String getUncheckedIconName() {
		return "unchecked.png";
	}
	
	public static String getLoginIconName() {
		return "lock.png";
	}
	
	public static String getRefreshIconName() {
		return "refresh.png";
	}
	
	public static String getGoOpenIconName() {
		return "go_open.png";
	}
	
	public static String getResetIconName() {
		return getRefreshIconName();
	}
	
	public static String getRemoveIconName() {
		return "remove.png";
	}
	
	public static String getInsertIconName() {
		return "insert.png";
	}
	
	public static String getRenameIconName() {
		return "rename.png";
	}
	
	public static String getFolderAddIconName() {
		return "folder_add.png";
	}
	
	public static String getExpandIconName() {
		return "expand.png";
	}
	
	public static String getCollapseIconName() {
		return "collapse.png";
	}
	
	public static String getHelpIconName() {
		return "help.png";
	}
	
	public static String getInfoIconName() {
		return "info.png";
	}
	
	public static String getAlertIconName() {
		return "alert.png";
	}
	
	public static String getWarningIconName() {
		return getAlertIconName();
	}
	
	public static String getPortletsIconName() {
		return "portlets.png";
	}
	
	public static String getViewAccessIconName() {
		return "view_access.png";
	}
	
	public static String getHistoryIconName() {
		return "history.png";
	}
	
	public static String getNewIconName() {
		return getInsertIconName();
	}
	
	public static String getDeleteIconName() {
		return getRemoveIconName();
	}
	
	public static String getRecentIconName() {
		return getHistoryIconName();
	}
	
	public static String getContactTypeIconName() {
		return getContactsIconName();
	}
	
	public static String getLinkTypeIconName() {
		return "link.png";
	}
	
	public static String getFirstIconName() {
		return "first.png";
	}
	
	public static String getLastIconName() {
		return "last.png";
	}
	
	public static String getBackwardIconName() {
		return "back.png";
	}
	
	public static String getForwardIconName() {
		return "forward.png";
	}
	
	public static String getSearchIconName() {
		return getFindIconName();
	}
	
	public static String getFindIconName() {
		return "find.png";
	}
	
	public static String getHelpIndexIconName() {
		return "help_index.png";
	}
	
	public static String getHelpParentIconName() {
		return getHelpUpIconName();
	}
	
	public static String getHelpUpIconName() {
		return "help_up.png";
	}
	
	public static String getHelpChidrenIconName() {
		return getHelpMoreIconName();
	}
	
	public static String getHelpMoreIconName() {
		return "help_more.png";
	}
	
	public static String getHelpRelatedIconName() {
		return "help_related.png";
	}
	
	public static String getHelpPrevIconName() {
		return "help_prev.png";
	}
	
	public static String getHelpNextIconName() {
		return "help_next.png";
	}

	public static String getHelpMainIconName() {
		return "help_main.png";
	}
	
	public static String getSnapshotIconName() {
		return "snapshot.png";
	}
	
	public static String getSnapshotClearIconName() {
		return "snapshot_clear.png";
	}
	
	public static String getSnapshotAddIconName() {
		return "snapshot_add.png";
	}
	
	public static String getSnapshotEditIconName() {
		return "snapshot_edit.png";
	}
	
	public static String getSnapshotFolderIconName() {
		return "snapshot_folder.png";
	}
	
	public static String getSnapshotGoIconName() {
		return "snapshot_go.png";
	}
	
	public static String getSnapshot_smallIconName() {
		return "snapshot_small.png";
	}
	
	public static String getSnapshotAlertIconName() {
		return "snapshot_alert.png";
	}
	
	public static String getSnapshotPageIconName() {
		return "snapshot_page.png";
	}
	
	public static String getSnapshotCopyIconName() {
		return "snapshot_copy.png";
	}
	
	public static String getCopyIconName() {
		return "copy.png";
	}
	
	public static String getUsaIconName() {
		return "usa.png";
	}
	
	public static String getCountriesIconName() {
		return "world.png";
	}
	
	public static void setIcon(TabItem tabItem, String iconName) {
		if (TAB_ICONS && tabItem != null && iconName != null && iconName.length() > 0) {
			tabItem.setIcon(getTabIcon(iconName));
		}
	}
	
	public static void setIcon(ContentPanel panel, String iconName) {
		if (HEADER_ICONS && panel != null && iconName != null && iconName.length() > 0) {
			panel.setIcon(getHeaderIcon(iconName));
		}
	}
	
	/**
	 * Set button icons... application wide use of button icons can be toggled on and off by setting the BUTTON_ICONS constant.
	 * 
	 * Icons will be used, despite any BUTTON_ICONS setting, if the button has no label.
	 * 
	 * @param button
	 * @param iconName
	 */
	public static void setIcon(Button button, String iconName) {
		if (button != null && iconName != null && iconName.length() > 0) {
			if (BUTTON_ICONS || button.getText() == null || button.getText().length() == 0)
				button.setIcon(getButtonIcon(iconName, button.isEnabled()));
		}
	}
	
	/**
	 * Force (set) button icons.  This method ignores the application wide BUTTON_ICONS constant, and always applies the icons if supplied.
	 * @param button
	 * @param iconName
	 */
	public static void forceIcon(Button button, String iconName) {
		if (button != null && iconName != null && iconName.length() > 0) {
			button.setIcon(getButtonIcon(iconName, button.isEnabled()));
		}
	}
	
	/**
	 * Force (set) button icons.  This method ignores the application wide BUTTON_ICONS constant, and always applies the icons if supplied.
	 * @param button
	 * @param iconName
	 */
	public static void forceMonochromeIcon(Button button, String iconName) {
		if (button != null && iconName != null && iconName.length() > 0) {
			button.setIcon(getMonochromeIcon(iconName));
		}
	}
	
	public static void setIcon(MenuItem menuItem, String iconName) {
		if (MENU_ICONS && menuItem != null && iconName != null && iconName.length() > 0) {
			menuItem.setIcon(getMenuIcon(iconName));
		}
	}
	
	public static String getColorfulIconPath(String iconName) {
		if (iconName.startsWith(FULL_COLOR_PATH))
			return iconName;
		if (iconName.startsWith(MONOCHROME_PATH))
			iconName = iconName.substring(MONOCHROME_PATH.length());
		return FULL_COLOR_PATH + iconName;
	}
	
	public static String getMonochromeIconPath(String iconName) {
		if (iconName.startsWith(MONOCHROME_PATH))
			return iconName;
		if (iconName.startsWith(FULL_COLOR_PATH))
			iconName = iconName.substring(FULL_COLOR_PATH.length());
		return MONOCHROME_PATH + iconName;
	}
	
	public static AbstractImagePrototype getHeaderIcon(String iconName) {
		return getColorfulIcon(iconName);
	}
	
	public static AbstractImagePrototype getButtonIcon(String iconName) {
		return getButtonIcon(iconName, true);
	}
	
	public static AbstractImagePrototype getButtonIcon(String iconName, boolean enabled) {
		if (enabled)
			return getColorfulIcon(iconName);
		else
			return getMonochromeIcon(iconName);
	}
	
	public static AbstractImagePrototype getTabIcon(String iconName) {
		return getMonochromeIcon(iconName);
	}
	
	public static AbstractImagePrototype getMenuIcon(String iconName) {
		return getColorfulIcon(iconName);
	}
	
	public static AbstractImagePrototype getAppNavIcon(String iconName) {
		return getColorfulIcon(iconName);
	}
	
	public static AbstractImagePrototype getColorfulIcon(String iconName) {
		return IconHelper.create(getColorfulIconPath(iconName));
	}
	
	public static AbstractImagePrototype getMonochromeIcon(String iconName) {
		return IconHelper.create(getMonochromeIconPath(iconName));
	}
	
	public static AbstractImagePrototype getRawIcon(String iconName) {
		return IconHelper.create(iconName);
	}
}
