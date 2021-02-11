package com.charlesawoodson.roolet.groupdetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
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
    groupId: Long,
    private val context: Context
) : BaseMvRxViewModel<GroupDetailState>(initialState, true) {

    private var set = mutableSetOf<String>()

    init {
        dbHelper.getGroupById(groupId)
            .subscribe { group ->
                set = group.members.map { it.number }.toMutableSet()
                setState {
                    copy(group = Success(group))
                }
            }.disposeOnClear()
    }

    fun callGroupMember(allowRepeatCalls: Boolean) {
        if (set.size > 0) {
            set.apply {
                val number = random()
                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$number")
                    startActivity(context, this, null)
                }
                if (!allowRepeatCalls) {
                    remove(number)
                }
            }
        }
    }

    companion object : MvRxViewModelFactory<GroupsDetailViewModel, GroupDetailState> {
        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: GroupDetailState
        ): GroupsDetailViewModel {
            val context = viewModelContext.activity.applicationContext

            val dbHelper =
                DatabaseHelperImpl(DatabaseBuilder.getInstance(context))

            return GroupsDetailViewModel(
                state,
                dbHelper,
                viewModelContext.args(),
                context
            )
        }
    }

}
