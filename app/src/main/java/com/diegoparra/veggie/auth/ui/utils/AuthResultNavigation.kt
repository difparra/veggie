package com.diegoparra.veggie.auth.ui.utils

import com.diegoparra.veggie.R
import com.diegoparra.veggie.auth.domain.AuthConstants
import com.diegoparra.veggie.core.NavWithResultHelper

object AuthResultNavigation: NavWithResultHelper<Boolean>(
    startDestination = R.id.signInOptionsFragment,
    resultKey = AuthConstants.LOGIN_SUCCESSFUL
)