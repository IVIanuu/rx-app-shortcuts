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

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Fetches app shortcuts for the passed package
 */
class RetrieveShortcutsForPackageSingle implements SingleOnSubscribe<List<AppShortcut>> {

    private static final String METADATA_KEY = "android.app.shortcuts";

    private Context context;
    private String packageName;

    private RetrieveShortcutsForPackageSingle(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    /**
     * Returns a single which emits the app shortcuts for the passed package
     */
    static Single<List<AppShortcut>> create(Context context, String packageName) {
        return Single.create(new RetrieveShortcutsForPackageSingle(context, packageName))
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public void subscribe(SingleEmitter<List<AppShortcut>> e) throws Exception {
        List<AppShortcut> appShortcuts = new ArrayList<>();

        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                packageName, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);

        if (packageInfo.activities != null) {
            // loop trough activity infos
            for (ActivityInfo activityInfo : packageInfo.activities) {
                if (activityInfo.metaData == null) {
                    // no meta data
                    continue;
                }

                XmlResourceParser resourceParser = activityInfo.loadXmlMetaData(context.getPackageManager(), METADATA_KEY);

                if (resourceParser == null) {
                    // should not happen
                    continue;
                }

                // parse the shortcuts
                List<AppShortcut> shortcuts = ShortcutParser.parseShortcuts(
                        context, resourceParser, activityInfo, packageName);
                // add the result
                if (shortcuts != null) {
                    appShortcuts.addAll(shortcuts);
                }
            }
        }

        if (!e.isDisposed()) {
            e.onSuccess(appShortcuts);
        }
    }

}
