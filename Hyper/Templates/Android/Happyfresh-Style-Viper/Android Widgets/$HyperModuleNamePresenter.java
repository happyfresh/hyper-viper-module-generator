package com.happyfresh.widgets.$HyperModuleName;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.happyfresh.abstracts.BasePresenter;

/**
 * Created by $HyperAuthorName on $HyperCreatedDate.
 */

public class $HyperModuleNamePresenter extends BasePresenter<$HyperModuleNameInteractor, $HyperModuleNameRouter, $HyperModuleNameViewBehavior>
        implements $HyperModuleNameInteractorOutput {

    // region Attributes

    // endregion

    // region Constructor

    public $HyperModuleNamePresenter(Context context) {
        super(context);
    }

    // endregion

    // region BasePresenter

    @Override
    protected $HyperModuleNameInteractor provideInteractor() {
        return new $HyperModuleNameInteractor();
    }

    @Override
    protected $HyperModuleNameRouter provideRouter(Context context) {
        return new $HyperModuleNameRouter(context);
    }

    // endregion

    //region $HyperModuleNameInteractorOutput


    // endregion

    // region Functionality


    // endregion
}
