package com.diegoparra.veggie.products.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.core.kotlin.Resource
import com.diegoparra.veggie.core.kotlin.toResource
import com.diegoparra.veggie.products.domain.Tag
import com.diegoparra.veggie.products.app.usecases.GetTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagsViewModel @Inject constructor(
    private val getTagsUseCase: GetTagsUseCase
) : ViewModel() {

    private val _tags = MutableLiveData<Resource<List<Tag>>>(Resource.Loading())
    val tags: LiveData<Resource<List<Tag>>> = _tags

    init {
        viewModelScope.launch {
            _tags.value = Resource.Loading()
            _tags.value = getTagsUseCase().toResource()
        }
    }

}