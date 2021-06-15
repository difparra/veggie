package com.diegoparra.veggie.user.edit_profile.auth_phone_number.ui

import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.android.NavWithResultHelper
import com.diegoparra.veggie.user.edit_profile.auth_phone_number.domain.PhoneConstants

object PhoneResultNavigation : NavWithResultHelper<Boolean>(
    startDestination = R.id.phoneNumberAddFragment,
    resultKey = PhoneConstants.PHONE_VERIFIED_SUCCESSFUL
)