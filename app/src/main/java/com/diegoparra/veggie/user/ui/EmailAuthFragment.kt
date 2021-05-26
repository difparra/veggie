package com.diegoparra.veggie.user.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diegoparra.veggie.R
import com.diegoparra.veggie.databinding.FragmentEmailAuthBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailAuthFragment : Fragment() {

    private var _binding: FragmentEmailAuthBinding? = null
    private val binding get() = _binding!!
    //private val viewModel: EmailAuthViewModel by hiltNavGraphViewModels(R.id.nav_sign_in)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewPager.adapter = EmailAuthAdapter(this)
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun getTabTitle(position: Int) : String {
        return when(position) {
            EmailAuthAdapter.SIGN_IN_INDEX -> getString(R.string.sign_in)
            EmailAuthAdapter.SIGN_UP_INDEX -> getString(R.string.sign_up)
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}