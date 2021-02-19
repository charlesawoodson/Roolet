package com.charlesawoodson.roolet.groupdetail

import android.util.Log
import com.airbnb.mvrx.*
import com.charlesawoodson.roolet.api.RulesResponse
import com.charlesawoodson.roolet.api.RulesService
import com.charlesawoodson.roolet.api.RulesServiceFactory
import com.charlesawoodson.roolet.contacts.model.GroupMember
import com.charlesawoodson.roolet.db.DatabaseBuilder
import com.charlesawoodson.roolet.db.DatabaseHelperImpl
import com.charlesawoodson.roolet.db.Group
import com.charlesawoodson.roolet.extensions.updateItems
import io.reactivex.schedulers.Schedulers

data class GroupDetailState(
    val group: Async<Group> = Uninitialized,
    val groupMembers: List<GroupMember> = emptyList()
) : MvRxState

class GroupsDetailViewModel(
    initialState: GroupDetailState,
    rulesService: RulesService,
    dbHelper: DatabaseHelperImpl,
    selectedGroupId: Long
) : BaseMvRxViewModel<GroupDetailState>(initialState, true) {

    private var set = mutableSetOf<String>()
    private var rules = mutableSetOf<String>()

    init {
        dbHelper.getGroupById(selectedGroupId)
            .subscribe { group ->
                set = group.members.map { it.number }.toMutableSet()
                setState {
                    copy(group = Success(group), groupMembers = group.members)
                }
            }.disposeOnClear()

        rulesService.getRules()
            .subscribeOn(Schedulers.io())
            .subscribe(this::handleResponse, this::handleError)
            .disposeOnClear()
    }

    fun getRandomNumber(): String {
        if (set.isNotEmpty()) {
            return set.random()
        }
        return "" // todo: drop down error when all calls have been made
    }

    fun getRandomRule(): String {
        if (rules.size > 0) {
            rules.apply {
                val rule = random()
                remove(rule)
                return rule
            }
        }
        return "No More Rules!"
    }

    fun removeNumber(number: String) {
        set.remove(number)
    }

    fun setGroupMemberCalled(number: String) {
        setState {
            copy(groupMembers = groupMembers.updateItems({ it.number == number }, {
                copy(beenCalled = true)
            }))
        }
    }

    private fun handleResponse(rulesResponse: RulesResponse) {
        rules = rulesResponse.rules.map { it.ruleDescription }.toMutableSet()
    }

    private fun handleError(error: Throwable) {
        Log.d("Error", error.toString())
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
                RulesServiceFactory.rulesServiceApi,
                dbHelper,
                viewModelContext.args()
            )
        }
    }
}
