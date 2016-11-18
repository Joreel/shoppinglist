package be.oreel.masi.shoppinglist.adapter;

import be.oreel.masi.shoppinglist.model.ToolbarMode;
import be.oreel.masi.shoppinglist.model.Article;

/**
 * Interface for listener communication between ArticleActivity and ArticleAdapter
 */
public interface ArticleManager {
    ToolbarMode getToolbarMode();
    void setToolbarMode(ToolbarMode mode);
    void updateArticle(Article article);
}
