package com.happyfresh.modules.$HyperModuleName;

import android.os.Bundle;

import com.happyfresh.abstracts.BaseActivity;
import com.happyfresh.abstracts.BasePresenter;


/**
 * Created by $HyperAuthorName on $HyperCreatedDate.
 */

public class $HyperModuleNameActivity extends BaseActivity<$HyperModuleNameFragment, $HyperModuleNamePresenter> {

    // region Life Cycle


    // endregion

    // region Base Activity

    @Override
    protected $HyperModuleNamePresenter providePresenter() {
        return new $HyperModuleNamePresenter(this);
    }

    @Override
    protected $HyperModuleNameFragment viewLayer(Bundle bundle) {
        return $HyperModuleNameFragment.newInstance(bundle);
    }

    @Override
    protected String title() {
        return $HyperModuleName+"";
    }

    @Override
    protected boolean showBackButton() {
        return true;
    }

    // endregion
}
