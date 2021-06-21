package com.diegoparra.veggie.user.address.ui

import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.android.NavWithResultHelper
import com.diegoparra.veggie.user.address.domain.AddressConstants

object AddressResultNavigation: NavWithResultHelper<Boolean>(
    startDestination = R.id.addressListFragment,
    resultKey = AddressConstants.ADDRESS_SELECTED_SUCCESSFUL
)
