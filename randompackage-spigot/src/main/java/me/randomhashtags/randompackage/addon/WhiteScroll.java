package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;

public interface WhiteScroll extends Scroll, Applyable {
    String getRequiredWhiteScroll();
    boolean removesRequiredAfterApplication();
}
