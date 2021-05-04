package com.diegoparra.veggie.products.domain.entities

sealed class Description {
    class Discounted(val finalPrice: Int, val discount: Float, val unit: String, val weightGr: Int) : Description()
    class Basic(val price: Int, val unit: String, val weightGr: Int) : Description()

    companion object{
        fun createDescription(price: Int, discount: Float, unit: String, weightGr: Int) : Description {
            return if(discount > 0){
                Discounted(price, discount, unit, weightGr)
            }else{
                Basic(price, unit, weightGr)
            }
        }
    }
}

sealed class Label {
    object NoStock : Label()
    class Discounted(val discount: Float) : Label()
    class DisplayLabel(val suggestedLabel: String) : Label()
    object Hidden : Label()

    companion object {
        fun createLabel(stock: Boolean, discount: Float, suggestedLabel: String?) : Label {
            return if(!stock){
                NoStock
            }else if(discount > 0){
                Discounted(discount)
            }else if(suggestedLabel == null || suggestedLabel.isEmpty()){
                Hidden
            }else{
                DisplayLabel(suggestedLabel)
            }
        }
    }
}