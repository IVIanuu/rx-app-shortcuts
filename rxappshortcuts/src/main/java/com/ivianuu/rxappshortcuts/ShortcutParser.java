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
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Parses the shortcuts
 */
final class ShortcutParser {

    private static final String KEY_ICON = "icon";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_ID = "shortcutId";
    private static final String KEY_SHORT_LABEL = "shortcutShortLabel";
    private static final String KEY_LONG_LABEL = "shortcutLongLabel";
    private static final String KEY_DISABLED_MESSAGE = "shortcutDisabledMessage";
    private static final String KEY_NAME = "name";

    private static final String TAG_SHORTCUTS = "shortcuts";
    private static final String TAG_SHORTCUT = "shortcut";
    private static final String TAG_INTENT = "intent";
    private static final String TAG_CATEGORIES = "categories";

    private ShortcutParser() {
        // no instances
    }

    static List<AppShortcut> parseShortcuts(
            Context context,
            XmlResourceParser parser,
            ActivityInfo activityInfo,
            String packageName) throws IOException, XmlPullParserException {

        List<AppShortcut> result = new ArrayList<>();

        try {
            final ComponentName activity = new ComponentName(packageName, activityInfo.name);

            int type;
            int rank = 0;
            // We instantiate AppShortcut at <shortcut>, but we add it to the list at </shortcut>,
            // after parsing <intent>.  We keep the current one in here.
            AppShortcut currentShortcut = null;
            Set<String> categories = null;
            Intent intent = null;

            outer:
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && (type != XmlPullParser.END_TAG || parser.getDepth() > 0)) {
                final int depth = parser.getDepth();
                final String tag = parser.getName();
                // When a shortcut tag is closing, publish.
                if ((type == XmlPullParser.END_TAG) && (depth == 2) && (TAG_SHORTCUT.equals(tag))) {
                    if (currentShortcut == null) {
                        // Shortcut was invalid.
                        continue;
                    }
                    final AppShortcut si = currentShortcut;
                    currentShortcut = null; // Make sure to null out for the next iteration.
                    if (intent == null) {
                        // no intents available
                        continue;
                    }
                    // Same flag as what TaskStackBuilder adds.
                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_TASK_ON_HOME);

                    try {
                        si.setIntent(intent);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        continue;
                    }

                    intent = null;

                    // if no categories where found pass a empty list
                    si.setCategories(Collections.<String>emptySet());

                    result.add(si);
                    rank++;
                    continue;
                }
                // Otherwise, just look at start tags.
                if (type != XmlPullParser.START_TAG) {
                    continue;
                }

                if (depth == 1 && TAG_SHORTCUTS.equals(tag)) {
                    continue; // Root tag.
                }

                if (depth == 2 && TAG_SHORTCUT.equals(tag)) {
                    final AppShortcut si = parseShortcutAttributes(
                            context, parser, packageName, activity, rank);
                    if (si == null) {
                        // Shortcut was invalid.
                        continue;
                    }

                    for (int i = result.size() - 1; i >= 0; i--) {
                        if (si.getId().equals(result.get(i).getId())) {
                            continue outer;
                        }
                    }
                    currentShortcut = si;
                    categories = null;
                    continue;
                }

                if (depth == 3 && TAG_INTENT.equals(tag)) {
                    if ((currentShortcut == null)) {
                        continue;
                    }

                    intent = Intent.parseIntent(context.getResources(),
                            parser, parser);

                    if (TextUtils.isEmpty(intent.getAction())) {
                        currentShortcut = null; // Invalidate the current shortcut.
                        continue;
                    }

                    if (intent.getComponent() != null) {
                        PackageManager packageManager = context.getPackageManager();
                        ComponentName component = intent.getComponent();
                        try {
                            ActivityInfo info = packageManager.getActivityInfo(component, 0);
                            if (!info.exported) {
                                // we need to exclude activities which are not exported
                                currentShortcut = null;
                                continue;
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    continue;
                }

                if (depth == 3 && TAG_CATEGORIES.equals(tag)) {
                    if (currentShortcut == null) {
                        continue;
                    }

                    final String name = parseCategories(parser);

                    if (TextUtils.isEmpty(name)) {
                        continue;
                    }

                    if (categories == null) {
                        categories = new HashSet<>();
                    }

                    categories.add(name);
                }
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
        return result;
    }
    private static String parseCategories(XmlResourceParser parser) {
        String name = null;
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if (parser.getAttributeName(i).equals(KEY_NAME)) {
                name = parser.getAttributeValue(i);
            }
        }

        return name;
    }

    private static AppShortcut parseShortcutAttributes(Context context,
                                                       XmlResourceParser parser,
                                                       String packageName,
                                                       ComponentName activity,
                                                       int rank) {
        String id = null;
        boolean enabled = false;
        int iconResId = 0;
        int shortLabelResId = 0;
        int longLabelResId = 0;
        int disabledMessageResId = 0;
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String name = parser.getAttributeName(i);
            switch (name) {
                case KEY_ICON:
                    iconResId = parser.getAttributeResourceValue(i, 0);
                    break;
                case KEY_ENABLED:
                    enabled = parser.getAttributeBooleanValue(i, false);
                    break;
                case KEY_ID:
                    id = parser.getAttributeValue(i);
                    break;
                case KEY_SHORT_LABEL:
                    shortLabelResId = parser.getAttributeResourceValue(i, 0);
                    break;
                case KEY_LONG_LABEL:
                    longLabelResId = parser.getAttributeResourceValue(i, 0);
                    break;
                case KEY_DISABLED_MESSAGE:
                    disabledMessageResId = parser.getAttributeResourceValue(i, 0);
                    break;
            }
        }

        if (TextUtils.isEmpty(id)) {
            return null;
        }
        if (shortLabelResId == 0) {
            return null;
        }

        return createShortcut(
                context,
                id,
                packageName,
                activity,
                shortLabelResId,
                longLabelResId,
                disabledMessageResId,
                rank,
                iconResId,
                enabled);
    }
    private static AppShortcut createShortcut(Context context,
                                              String id, String packageName, ComponentName activityComponent,
                                              int shortLabelResId, int longLabelResId, int disabledMessageResId,
                                              int rank, int iconResId, boolean enabled) {
        try {
            Context packageContext = context.createPackageContext(packageName, 0);

            String title = packageContext.getString(shortLabelResId);
            String text = longLabelResId != 0 ? packageContext.getString(longLabelResId) : null;
            String disabledMessage = disabledMessageResId != 0 ? packageContext.getString(disabledMessageResId) : "";
            Drawable icon = ContextCompat.getDrawable(packageContext, iconResId);

            return new AppShortcut(
                    id,
                    packageName,
                    activityComponent,
                    enabled,
                    icon,
                    title,
                    text,
                    disabledMessage,
                    rank);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}