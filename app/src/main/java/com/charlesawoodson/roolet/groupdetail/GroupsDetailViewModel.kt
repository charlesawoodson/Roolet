package com.charlesawoodson.roolet.groupdetail

import com.airbnb.mvrx.*
import com.charlesawoodson.roolet.db.DatabaseBuilder
import com.charlesawoodson.roolet.db.DatabaseHelperImpl
import com.charlesawoodson.roolet.db.Group

data class GroupDetailState(
    val group: Async<Group> = Uninitialized
) : MvRxState

class GroupsDetailViewModel(
    initialState: GroupDetailState,
    dbHelper: DatabaseHelperImpl,
    groupId: Long
) : BaseMvRxViewModel<GroupDetailState>(initialState, true) {

    init {
        dbHelper.getGroupById(groupId)
            .subscribe { group ->
                setState {
                    copy(group = Success(group))
                }
            }.disposeOnClear()
    }

    companion object : MvRxViewModelFactory<GroupsDetailViewModel, GroupDetailState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: GroupDetailState
        ): GroupsDetailViewModel {
            val dbHelper =
                DatabaseHelperImpl(DatabaseBuilder.getInstance(viewModelContext.activity.applicationContext))

            return GroupsDetailViewModel(
                state,
                dbHelper,
                viewModelContext.args()
            )
        }
    }

}
