package com.diegoparra.veggie.auth.utils

import com.diegoparra.veggie.auth.R
import com.diegoparra.veggie.auth.utils.AuthConstants
import com.diegoparra.veggie.core.android.NavWithResultHelper

object AuthResultNavigation : NavWithResultHelper<Boolean>(
    startDestination = R.id.signInOptionsFragment,
    resultKey = AuthConstants.LOGIN_SUCCESSFUL
)