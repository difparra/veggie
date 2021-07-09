package com.diegoparra.veggie.auth.additional_features.phone_number.ui

import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.core.android.NavWithResultHelper
import com.diegoparra.veggie.auth.additional_features.phone_number.domain.PhoneConstants

object PhoneResultNavigation : NavWithResultHelper<Boolean>(
    startDestination = R.id.phone_number_add_fragment,
    resultKey = PhoneConstants.PHONE_VERIFIED_SUCCESSFUL
)