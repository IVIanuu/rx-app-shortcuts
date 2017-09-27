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
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Single;

import static com.ivianuu.preconditions.Preconditions.checkNotNull;

/**
 * Rx app shortcuts
 */
public final class RxAppShortcuts {

    private RxAppShortcuts() {
        // no instances
    }

    /**
     * Returns the shortcuts for the package
     */
    @CheckResult @NonNull
    public static Single<List<AppShortcut>> getShortcutsFor(@NonNull Context context, @NonNull String packageName) {
        checkNotNull(context, "context == null");
        checkNotNull(packageName, "packageName == null");
        return RetrieveShortcutsForPackageSingle.create(context, packageName);
    }
}
