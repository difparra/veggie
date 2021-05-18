package com.diegoparra.veggie.products.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.core.Resource
import com.diegoparra.veggie.products.domain.Tag
import com.diegoparra.veggie.products.usecases.GetTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagsViewModel @Inject constructor(
    private val getTagsUseCase: GetTagsUseCase
) : ViewModel() {

    private val _tags = MutableLiveData<Resource<List<Tag>>>(Resource.Loading())
    val tags : LiveData<Resource<List<Tag>>> = _tags

    init {
        viewModelScope.launch {
            _tags.value = Resource.Loading()
            getTagsUseCase().fold(::handleFailureTags, ::handleTags)
        }
    }

    private fun handleTags(tags: List<Tag>){
        if(tags.isNullOrEmpty()){
            _tags.value = Resource.Error(Failure.ProductsFailure.TagsNotFound)
        }else{
            _tags.value = Resource.Success(tags)
        }
    }

    private fun handleFailureTags(failure: Failure) {
        _tags.value = Resource.Error(failure)
    }

}