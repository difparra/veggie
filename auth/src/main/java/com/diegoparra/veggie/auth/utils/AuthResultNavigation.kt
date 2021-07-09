package com.diegoparra.veggie.auth.utils

import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.core.android.NavWithResultHelper

object AuthResultNavigation : NavWithResultHelper<Boolean>(
    startDestination = R.id.sign_in_options_fragment,
    resultKey = AuthConstants.LOGIN_SUCCESSFUL
)