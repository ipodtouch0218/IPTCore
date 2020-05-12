package me.ipodtouch0218.iptcore.inventory;

import java.util.HashMap;

import lombok.Getter;

@Getter
public class PagedGuiManager {

	private GuiInventory template;
	private HashMap<Integer, GuiInventory> pages = new HashMap<>();
	private int maxPages;
	private PagedUpdater updater;
	
	public PagedGuiManager(GuiInventory template, PagedUpdater updater) {
		this.template = template;
		template.setUpdateConsumer(updater);
		this.updater = updater;
	}
	
	public void updatePages() {
		pages.values().forEach(GuiInventory::updateInventory);
	}

	public GuiInventory getPage(int num) {
		if (num <= 0) { num = 0; }
		GuiInventory page = pages.get(num);
		if (page == null) {
			page = template.clone();
			page.setPageNumber(num);
			pages.put(num, page);
			if (updater.accept(page)) {
				maxPages = Math.min(num, maxPages);
			}
		}
		page.updateInventory(false);
		return page;
	}
	
	public int getTotalPages() {
		return pages.size();
	}
	
}
