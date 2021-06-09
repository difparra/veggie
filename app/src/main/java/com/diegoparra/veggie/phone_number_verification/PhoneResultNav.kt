package com.diegoparra.veggie.phone_number_verification

import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.NavWithResultHelper

object PhoneResultNav : NavWithResultHelper<Boolean>(
    startDestination = R.id.phoneNumberAddFragment,
    resultKey = PhoneConstants.PHONE_VERIFIED_SUCCESSFUL
)