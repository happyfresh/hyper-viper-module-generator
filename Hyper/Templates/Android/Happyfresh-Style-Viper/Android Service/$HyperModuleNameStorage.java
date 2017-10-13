package com.happyfresh.snowflakes.hoverfly.services.$HyperModuleName;

import android.content.Context;

import com.happyfresh.snowflakes.hoverfly.Sprinkles;

/**
 * Created by $HyperAuthorName on $HyperCreatedDate.
 */

public class $HyperModuleNameStorage extends BaseStorage<$HyperModuleNameDatabase, $HyperModuleNamePrefs> {

    // region BaseService

    protected $HyperModuleNameStorage(Context context) {
        super(context);
    }

    @Override
    protected $HyperModuleNameDatabase provideDatabase() {
        return new $HyperModuleNameDatabase();
    }

    @Override
    protected $HyperModuleNamePrefs providePreference(Context context) {
        return new $HyperModuleNamePrefs(context);
    }

    // endregion

    // region Functionality


    // endregion
    
}
