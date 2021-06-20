package com.diegoparra.veggie.user.address.domain

data class Address(
    val id: String,
    val address: String,
    val details: String?,
    val instructions: String?
) {
    fun fullAddress(): String {
        return address + (details?.let { "\n" + it } ?: "")
    }
}



/*
    TODO:
    Get selectedAddress
        Get addressList.
        Get saved selected addressId.
            If savedAddressId is null:
                return null -> if addressList is empty
                return firstAddress -> if addressList is not empty.
            If savedAddressId has a value:
                return address -> If it was found in the addressList.
                return firstAddress -> It if was not found and addressList is not empty.
                return null -> If addressList is empty.


    CONSTANTS:
        AddAddressSuccessful (internal)
        SetAddressSuccessful (final)    ->  Possibly could set in onDestroyView

    Address navigation
        Start fragment = List of addresses
        Add address fragment -> Add and select an address.



 */