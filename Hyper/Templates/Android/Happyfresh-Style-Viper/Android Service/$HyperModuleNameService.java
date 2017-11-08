package com.happyfresh.snowflakes.hoverfly.services.$HyperModuleName;

import com.happyfresh.snowflakes.hoverfly.Sprinkles;
import com.happyfresh.snowflakes.hoverfly.services.BaseService;

import rx.Observable;

/**
 * Created by $HyperAuthorName on $HyperCreatedDate.
 */

public class $HyperModuleNameService extends BaseService<$HyperModuleNameAPI, $HyperModuleNameStorage> {

    // region BaseService

    @Override
    protected Class<$HyperModuleNameAPI> serviceClass() {
        return $HyperModuleNameAPI.class;
    }

    @Override
    protected $HyperModuleNameStorage provideStorage() {
        return Sprinkles.createStorage($HyperModuleNameStorage.class);
    }

    // endregion

    // region Functionality


    // endregion
}
