package me.ipodtouch0218.iptcore.inventory.paging;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import me.ipodtouch0218.iptcore.inventory.elements.GuiElement;
import me.ipodtouch0218.iptcore.inventory.paging.PagedGuiInventory;
import me.ipodtouch0218.iptcore.inventory.paging.PagedItemProvider;

@RequiredArgsConstructor
public class BasicProvider implements PagedItemProvider {

	private final List<? extends GuiElement> elements;
	
	@Override
	public GuiElement[] provide(int page, int slots, PagedGuiInventory inventory) {
		
		if (page > (elements.size() / slots)) {
			return null;
		}
		
		return elements.stream()
			.skip(page * slots)
			.collect(Collectors.toList())
			.toArray(new GuiElement[0]);
	}

}
