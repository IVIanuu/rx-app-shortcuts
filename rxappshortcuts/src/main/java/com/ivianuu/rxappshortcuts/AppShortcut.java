/*
 * Copyright 2017 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.rxappshortcuts;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.Set;

/**
 * ShortcutInfo Meta
 */
public class AppShortcut {

    private String packageName;
    private ComponentName activity;
    private Set<String> categories;
    private String id;
    private boolean enabled;
    private Drawable icon;
    private String shortLabel;
    private String longLabel;
    private String disabledMessage;
    private Intent intent;
    private int rank;

    AppShortcut(String id,
                String packageName,
                ComponentName activity,
                boolean enabled,
                Drawable icon,
                String shortLabel,
                String longLabel,
                String disabledMessage,
                int rank) {
        this.id = id;
        this.packageName = packageName;
        this.activity = activity;
        this.enabled = enabled;
        this.icon = icon;
        this.shortLabel = shortLabel;
        this.longLabel = longLabel;
        this.disabledMessage = disabledMessage;
        this.rank = rank;
    }

    /**
     * Returns the intent of this shortcut
     */
    @NonNull
    public Intent getIntent() {
        return intent;
    }

    /**
     * Returns the package name of this shortcut
     */
    @NonNull
    public String getPackageName() {
        return packageName;
    }

    /**
     * Returns the activity of this shortcut
     */
    @NonNull
    public ComponentName getActivity() {
        return activity;
    }

    /**
     * Returns the icon of this shortcut
     */
    @NonNull
    public Drawable getIcon() {
        return icon;
    }

    /**
     * Returns the rank of this shortcut
     */
    public int getRank() {
        return rank;
    }

    /**
     * Returns the disabled message of this shortcut
     */
    @NonNull
    public String getDisabledMessage() {
        return disabledMessage;
    }

    /**
     * Returns the long label of this shortcut
     */
    @NonNull
    public String getLongLabel() {
        return longLabel;
    }

    /**
     * Returns the short label of this shortcut
     */
    @NonNull
    public String getShortLabel() {
        return shortLabel;
    }

    /**
     * Returns if this shortcut is enabled or not
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the id of this shortcut
     */
    @NonNull
    public String getId() {
        return id;
    }

    /**
     * Returns the categories of this shortcut
     */
    @NonNull
    public Set<String> getCategories() {
        return categories;
    }

    /**
     * Sets the categories
     */
    void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    /**
     * Sets the intent
     */
    void setIntent(Intent intent) {
        this.intent = intent;
    }
}
