package com.happyfresh.modules.$HyperModuleName;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;

import com.happyfresh.R;
import com.happyfresh.abstracts.BaseFragment;

/**
 * Created by $HyperAuthorName on $HyperCreatedDate.
 */

public class $HyperModuleNameFragment extends BaseFragment<$HyperModuleNamePresenter> implements $HyperModuleNameViewBehavior {

    // region Butterknife Bind

    // endregion

    // region Attributes

    // endregion

    // region static

    public static $HyperModuleNameFragment newInstance(Bundle bundle) {
        $HyperModuleNameFragment fragment = new $HyperModuleNameFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    // endregion

    // region Life Cycle

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // endregion

    // region BaseFragment

    @Override
    public int layoutResID() {
        return 0;
    }

    @Override
    protected $HyperModuleNamePresenter providePresenter() {
        return new $HyperModuleNamePresenter(getContext());
    }

    // endregion

    // region $HyperModuleNameViewBehavior

    // endregion

    // region $HyperModuleName Listener

    // endregion

    // region Events

    // endregion

    //region Methods

    // endregion
}
