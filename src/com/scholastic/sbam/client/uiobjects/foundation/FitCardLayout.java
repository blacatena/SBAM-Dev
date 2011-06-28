package com.scholastic.sbam.client.uiobjects.foundation;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;

public class FitCardLayout extends CardLayout {

	  @Override
	  protected void onLayout(Container<?> container, El target) {
	    if (container.getItemCount() == 0) {
	      return;
	    }
	    activeItem = activeItem != null ? activeItem : container.getItem(0);
	    super.onLayout(container, target);

	    setItemSize(activeItem, target.getStyleSize());
	  }

	  protected void setItemSize(Component item, Size size) {
	    if (item != null && item.isRendered()) {
	      size.width -= getSideMargins(item);
	      size.height -= item.el().getMargins("tb");
	      setSize(item, size.width, size.height);
	    }
	  }

}
