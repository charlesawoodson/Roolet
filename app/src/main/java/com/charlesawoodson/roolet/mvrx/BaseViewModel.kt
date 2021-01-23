package com.charlesawoodson.roolet.mvrx

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.BuildConfig
import com.airbnb.mvrx.MvRxState

/**
 * Created by charles.adams on 05/23/2020
 */

abstract class BaseViewModel<S: MvRxState>(initialState: S) : BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG)