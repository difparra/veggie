package com.diegoparra.veggie.support.usecases

import com.diegoparra.veggie.support.domain.SupportConstants
import com.diegoparra.veggie.support.domain.SupportRepository
import javax.inject.Inject

class GetContactEmailUseCase @Inject constructor(
    private val supportRepository: SupportRepository
) {

    suspend operator fun invoke() =
        supportRepository.getContactEmail().fold(
            { SupportConstants.contactEmail },
            { it }
        )

}