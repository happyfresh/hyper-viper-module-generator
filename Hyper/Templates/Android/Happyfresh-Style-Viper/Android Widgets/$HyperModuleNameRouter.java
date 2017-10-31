package com.happyfresh.widgets.$HyperModuleName;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.happyfresh.abstracts.BaseActivity;
import com.happyfresh.abstracts.BaseRouter;

import java.util.ArrayList;

/**
 * Created by $HyperAuthorName on $HyperCreatedDate.
 */

public class $HyperModuleNameRouter extends BaseRouter {

    public $HyperModuleNameRouter(Context context) {
        super(context);
    }

    // region BaseRouter

    @Override
    public void detach() {

    }

    @Override
    public Class<? extends BaseActivity> viewClass() {
        return $HyperModuleNameActivity.class;
    }

    // endregion

    // region Methods

    // endregion 
}
