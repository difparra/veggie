package com.diegoparra.veggie.core

import android.view.View
import androidx.core.view.isVisible

/**
 * To send the state of the data from the viewModel to the Ui.
 */
sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val failure: Failure) : Resource<T>()
}


/**
 * To manage visibility of components in the ui in different states.
 * A group of views is defined for each state, and when calling displayViewsForState,
 * method will hide the views of the other states and show the views defined for the desired state.
 */
class ResourceViews(
        loadingViews: List<View>,
        successViews: List<View>,
        failureViews: List<View>
) {

    enum class State { LOADING, SUCCESS, ERROR }
    private val viewsMap = mapOf(
            State.LOADING to loadingViews,
            State.SUCCESS to successViews,
            State.ERROR to failureViews
    )


    fun displayViewsForState(state: State) {
        setVisibility(getVisibilityMap(state))
    }

    private fun setVisibility(viewVisibilityMap : Map<View, Boolean>){
        for(view in viewVisibilityMap){
            if(view.key.isVisible != view.value){
                view.key.isVisible = view.value
            }
        }
    }

    private fun getVisibilityMap(state: State) : Map<View, Boolean> {
        val visibilityMap = mutableMapOf<View, Boolean>()
        for(views in viewsMap){
            if(views.key != state){
                views.value.forEach { visibilityMap[it] = false }
            }else{
                //  Must be at last. If there are repeated views on different states,
                //  the important thing is that they are visible after setting the state
                //  (more than hiding when moving to another state)
                views.value.forEach { visibilityMap[it] = true }
            }
        }
        return visibilityMap
    }
}