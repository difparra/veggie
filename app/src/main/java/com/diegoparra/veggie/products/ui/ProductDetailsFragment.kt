package com.diegoparra.veggie.products.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.diegoparra.veggie.databinding.FragmentProductDetailsBinding
import com.diegoparra.veggie.products.viewmodels.ProductDetailsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint      //  Check, without this annotation app was also working
class ProductDetailsFragment : BottomSheetDialogFragment() {

    private var _binding : FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ProductDetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.title.text = viewModel.publicProductId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}