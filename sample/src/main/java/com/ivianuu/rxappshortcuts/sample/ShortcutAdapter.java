package com.ivianuu.rxappshortcuts.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivianuu.rxappshortcuts.AppShortcut;

import java.util.ArrayList;
import java.util.List;

/**
 * Shortcut adapter
 */
class ShortcutAdapter extends RecyclerView.Adapter<ShortcutAdapter.ShortcutViewHolder> {

    private List<AppShortcut> appShortcuts = new ArrayList<>();

    void update(List<AppShortcut> appShortcuts) {
        this.appShortcuts.clear();
        this.appShortcuts.addAll(appShortcuts);
        notifyDataSetChanged();
    }

    @Override
    public ShortcutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShortcutViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shortcut, parent, false));
    }

    @Override
    public void onBindViewHolder(final ShortcutViewHolder holder, int position) {
        final AppShortcut shortcut = appShortcuts.get(position);

        holder.shortcutIcon.setImageDrawable(shortcut.getIcon());
        holder.shortcutLabel.setText(shortcut.getShortLabel());

        holder.itemView.setOnClickListener(view -> holder.itemView.getContext().startActivity(shortcut.getIntent()));
    }

    @Override
    public int getItemCount() {
        return appShortcuts.size();
    }

    static class ShortcutViewHolder extends RecyclerView.ViewHolder {

        private ImageView shortcutIcon;
        private TextView shortcutLabel;

        ShortcutViewHolder(View itemView) {
            super(itemView);
            shortcutIcon = itemView.findViewById(R.id.shortcut_icon);
            shortcutLabel = itemView.findViewById(R.id.shortcut_label);
        }
    }
}

