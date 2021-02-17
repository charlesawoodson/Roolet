package com.charlesawoodson.roolet.groupdetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.airbnb.mvrx.*
import com.charlesawoodson.roolet.api.Rule
import com.charlesawoodson.roolet.api.RulesResponse
import com.charlesawoodson.roolet.api.RulesService
import com.charlesawoodson.roolet.api.RulesServiceFactory
import com.charlesawoodson.roolet.db.DatabaseBuilder
import com.charlesawoodson.roolet.db.DatabaseHelperImpl
import com.charlesawoodson.roolet.db.Group
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class GroupDetailState(
    val group: Async<Group> = Uninitialized,
    val rules: List<Rule> = emptyList()
) : MvRxState

class GroupsDetailViewModel(
    initialState: GroupDetailState,
    rulesService: RulesService,
    dbHelper: DatabaseHelperImpl,
    selectedGroupId: Long,
    private val context: Context
) : BaseMvRxViewModel<GroupDetailState>(initialState, true) {

    private var set = mutableSetOf<String>()

    init {
        dbHelper.getGroupById(selectedGroupId)
            .subscribe { group ->
                set = group.members.map { it.number }.toMutableSet()
                setState {
                    copy(group = Success(group))
                }
            }.disposeOnClear()

        rulesService.getRules()
            .subscribeOn(Schedulers.io())
            .subscribe(this::handleResponse, this::handleError)
            .disposeOnClear()
    }

    fun callGroupMember(allowRepeatCalls: Boolean) {
        if (set.size > 0) {
            set.apply {
                val number = random()
                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$number")
                    startActivity(context, this, null) // todo: is this a memory leak?
                }
                if (!allowRepeatCalls) {
                    remove(number)
                }
            }
        }
    }

    private fun handleResponse(rulesResponse: RulesResponse) {
        setState {
            copy(rules = rulesResponse.rules)
        }
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
                viewModelContext.args(),
                context
            )
        }
    }

}
