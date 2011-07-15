package com.scholastic.sbam.client.uiobjects.uireports;

import com.scholastic.sbam.client.uiobjects.foundation.ParentCardPanel;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public interface SnapshotParentCardPanel extends ParentCardPanel {
	public static int	SNAPSHOT_SELECTOR_PANEL	=	1;
	public static int	SERVICE_SELECTOR_PANEL	=	2;
	public static int	PRODUCT_SELECTOR_PANEL	=	3;
	public static int	CUSTOMER_SELECTOR_PANEL	=	4;
	public static int	CRITERIA_PANEL			=	5;
	public static int	VIEW_DATA_PANEL			=	6;
	public static int	VIEW_GRAPHS_PANEL		=	7;
	

	public void setTargetSnapshot(SnapshotInstance snapshot);
}
