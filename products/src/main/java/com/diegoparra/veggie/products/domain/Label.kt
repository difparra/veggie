package com.diegoparra.veggie.products.domain

/*
 * Additional properties defined in Product___ classes that could be common between them.
 */

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