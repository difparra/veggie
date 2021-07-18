package com.diegoparra.veggie.order.data.retrofit

import com.diegoparra.veggie.order.data.retrofit.order_dto.OrderDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderService {

    @POST("orders.json")
    suspend fun sendOrder(@Body orderDto: OrderDto): PostResponse

    @GET("orders.json?orderBy=\"shippingInfo/userId\"")
    suspend fun getOrdersUser(@Query("equalTo") userId: String): Map<String, OrderDto>
    //  TODO:   Could possibly be improved by using TypeAdapter and TypeAdapterFactory in Gson init
    //          in order to match List<OrderDto>
    //  TODO 2: In addition, userId could also be improved to include quotes automatically, rather
    //          than passing them when method is called.
    //          ** Quotes around string values are necessary in Firebase Rest Api.

}