package me.ipodtouch0218.iptcore.inventory.paging;

import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;

@FunctionalInterface
public interface PagedItemProvider {

	public GuiElement[] provide(int page, int slots, PagedGuiInventory inventory);
	
}
