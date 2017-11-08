package com.happyfresh.widgets.$HyperModuleName;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.happyfresh.R;
import com.happyfresh.widgets.BaseLayout;

/**
 * Created by $HyperAuthorName on $HyperCreatedDate.
 */

public class $HyperModuleName extends BaseLayout<$HyperModuleNamePresenter> implements $HyperModuleNameViewBehavior {
	

	// region Constructor

    public $HyperModuleName(Context context) {
        super(context);
    }

    public $HyperModuleName(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public $HyperModuleName(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // endregion

    // region BaseLayout

    @Override
    protected void initialize() {

    }

    @Override
    protected int layoutResID() {
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
