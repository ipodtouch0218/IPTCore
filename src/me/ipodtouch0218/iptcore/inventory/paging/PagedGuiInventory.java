package me.ipodtouch0218.iptcore.inventory.paging;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.ipodtouch0218.iptcore.inventory.GuiInventory;
import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;

@Getter @Setter
public class PagedGuiInventory extends GuiInventory {

	private List<Integer> pagedSlots;
	private int page;
	private PagedItemProvider provider;
	private boolean readyForNewPage = true;
	
	public PagedGuiInventory(int size, String title, GuiElement[] elements, List<Integer> pagedSlots, PagedItemProvider provider) {
		super(size, title, elements);
		this.pagedSlots = pagedSlots;
		this.provider = provider;
	}

	@Override
	public void updateInventory() {
		if (pagedSlots == null || provider == null || !readyForNewPage) {
			super.updateInventory();
			return;
		}
		
		int slots = pagedSlots.size();
		GuiElement[] ret;
		while ((ret = provider.provide(page, slots, this)) == null && page > 0) {
			page--;
		}
		
		if (ret == null) {
			super.updateInventory();
			return;
		}
		
		for (int i = 0; i < slots; i++) {
			int slot = pagedSlots.get(i);
			if (i >= ret.length) {
				setElement(slot, null);
			} else {
				setElement(slot, ret[i]);
			}
		}
		
		super.updateInventory();
	}
	
	public void updateInventory(boolean useProvider) {
		if (useProvider) {
			updateInventory();
		} else {
			super.updateInventory();
		}
	}
	
	public void nextPage() {
		page++;
	}
	
	public void previousPage() {
		page = Math.max(0, page-1);
	}
	
	@Override
	public GuiInventory clone() {
		return new PagedGuiInventory(size, title, Arrays.copyOf(elements, size), pagedSlots, provider);
	}
}
