package com.lihao.lisa.view;

import com.lihao.lisa.presenter.BasePresenter;

public interface BaseView<P extends BasePresenter> {
    void setPresenter(P presenter);
}
