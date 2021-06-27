package com.diegoparra.veggie.order.ui

import com.diegoparra.veggie.R
import com.diegoparra.veggie.core.android.NavWithResultHelper
import com.diegoparra.veggie.order.domain.OrderConstants

object OrderResultNavigation: NavWithResultHelper<Boolean> (
    startDestination = R.id.shippingInfoFragment,
    resultKey = OrderConstants.ORDER_SENT_SUCCESSFUL
)