package com.diegoparra.veggie.order.domain

data class PaymentInfo(
    val status: PaymentStatus,
    val paymentMethod: PaymentMethod,
    val details: String,                    //  Information shown to the user.
    //  All information collected in the transaction.
    //  May include info like cardNumber, paidAt, bank, buyerInfo, ticketImage, ...
    val additionalInfo: Map<String, String>
)

enum class PaymentStatus {
    //  Cash on delivery
    PENDING,
    //  Payment flow was completed, or user sent information about the payment (i.e.
    //  if user pay through Nequi and send confirmation)
    COMPLETED,
    //  When admin has checked transfer was successful (check bank account or has
    //  the cash money)
    VERIFIED
}

enum class PaymentMethod {
    CASH,
    POS,
    CARD,
    OTHER       //  Maybe Nequi or some bank transfer out of the app
}