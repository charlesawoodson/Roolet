package com.charlesawoodson.roolet.groups

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.charlesawoodson.roolet.db.DatabaseBuilder
import com.charlesawoodson.roolet.db.DatabaseHelperImpl
import com.charlesawoodson.roolet.db.Group

data class GroupsState(
    val groups: List<Group> = emptyList(),
) : MvRxState

class GroupsViewModel(
    initialState: GroupsState,
    dbHelper: DatabaseHelperImpl
) : BaseMvRxViewModel<GroupsState>(initialState, false) {

    init {
        dbHelper.getGroups().subscribe { groups ->
            setState {
                copy(groups = groups)
            }
        }.disposeOnClear()
    }

    companion object : MvRxViewModelFactory<GroupsViewModel, GroupsState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: GroupsState
        ): GroupsViewModel {
            val dbHelper =
                DatabaseHelperImpl(DatabaseBuilder.getInstance(viewModelContext.activity.applicationContext))

            return GroupsViewModel(
                state,
                dbHelper
            )
        }
    }

}