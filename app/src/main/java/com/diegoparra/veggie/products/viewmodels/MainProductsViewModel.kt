package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.products.domain.usecases.GetMainProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainProductsViewModel @Inject constructor(
    private val getMainProductsUseCase: GetMainProductsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tagId : MutableStateFlow<String> = MutableStateFlow(savedStateHandle.get(TAG_ID_SAVED_STATE_KEY) ?: "")
    init {
        viewModelScope.launch {
            tagId.collect { newTagId ->
                savedStateHandle.set(TAG_ID_SAVED_STATE_KEY, newTagId)
            }
        }
        /*savedStateHandle.getLiveData<String>(TAG_ID_SAVED_STATE_KEY).switchMap {
            getMainProductsUseCase(GetMainProductsUseCase.Params.ForTag(it)).asLiveData()
        }*/
    }


    fun setTagId(tagId: String){
        this.tagId.value = tagId
    }

    private val prodsEither = tagId.flatMapLatest { tagId ->
        getMainProductsUseCase(GetMainProductsUseCase.Params.ForTag(tagId))
    }
    val products = prodsEither.filter { it.isRight }.map { (it as Either.Right).b }.asLiveData()
    val failure = prodsEither.filter { it.isLeft }.map { (it as Either.Left).a }.asLiveData()

    companion object {
        private const val TAG_ID_SAVED_STATE_KEY = "tagId"
    }

}