package com.charlesawoodson.roolet.settings

import com.airbnb.mvrx.*
import com.charlesawoodson.roolet.db.DatabaseBuilder
import com.charlesawoodson.roolet.db.DatabaseHelperImpl

data class SettingsState(
    val settings: Async<String> = Uninitialized
) : MvRxState

class SettingsViewModel(
    initialState: SettingsState,
    dbHelper: DatabaseHelperImpl
) : BaseMvRxViewModel<SettingsState>(initialState, true) {

    init {
        // todo: dbHelper.getSettings().subscribe {  }

    }

    companion object : MvRxViewModelFactory<SettingsViewModel, SettingsState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: SettingsState
        ): SettingsViewModel {

            val dbHelper =
                DatabaseHelperImpl(DatabaseBuilder.getInstance(viewModelContext.activity.applicationContext))

            return SettingsViewModel(state, dbHelper)
        }
    }
}
