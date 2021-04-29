package com.diegoparra.veggie.products.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.diegoparra.veggie.products.viewmodels.MainProductsViewModel

class TabsAdapter(fragment: Fragment, private val tagsIds: List<String>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tagsIds.size

    override fun createFragment(position: Int): Fragment {
        val fragment = ProductsListFragment()
        fragment.arguments = Bundle().apply {
            putString(MainProductsViewModel.TAG_ID_SAVED_STATE_KEY, tagsIds[position])
        }
        return fragment
    }

}