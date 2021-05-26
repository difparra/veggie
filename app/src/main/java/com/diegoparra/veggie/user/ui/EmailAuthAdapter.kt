package com.diegoparra.veggie.user.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class EmailAuthAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentCreators: Map<Int, ()->Fragment> = mapOf(
        SIGN_IN_INDEX to { EmailSignInFragment() },
        SIGN_UP_INDEX to { EmailSignUpFragment() }
    )

    override fun getItemCount(): Int = tabFragmentCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }


    companion object {
        const val SIGN_IN_INDEX = 0
        const val SIGN_UP_INDEX = 1
    }

}