package be.oreel.masi.shoppinglist.adapter;

import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Interface for listener communication between ArticleActivity and ArticleAdapter
 */
public interface ArticleManager {
    boolean startActionMode(View view, ArticleAdapter.ViewHolder viewHolder, List<TextView> textViews);

    void toggleSelection(ArticleAdapter.ViewHolder view);
}
