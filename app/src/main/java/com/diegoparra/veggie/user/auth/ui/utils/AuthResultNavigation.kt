package com.diegoparra.veggie.user.auth.ui.utils

import com.diegoparra.veggie.R
import com.diegoparra.veggie.user.auth.domain.AuthConstants
import com.diegoparra.veggie.core.NavWithResultHelper

object AuthResultNavigation : NavWithResultHelper<Boolean>(
    startDestination = R.id.signInOptionsFragment,
    resultKey = AuthConstants.LOGIN_SUCCESSFUL
)