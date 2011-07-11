package com.scholastic.sbam.client.uiobjects.uireports;

import com.scholastic.sbam.client.uiobjects.foundation.ParentCardPanel;

public interface SnapshotParentCardPanel extends ParentCardPanel {
	public static int	SNAPSHOT_SELECTOR_PANEL	=	1;
	public static int	SERVICE_PANEL			=	2;

	public void setTargetSnapshotId(int snapshotId);
}
