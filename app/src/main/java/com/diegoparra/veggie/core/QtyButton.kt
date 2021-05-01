package com.diegoparra.veggie.core

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import com.diegoparra.veggie.R

class QtyButton(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatButton(context, attrs) {

    companion object{
        enum class QtyBtnType { ADD, REDUCE, UNDEFINED }
        enum class QtyStates(@AttrRes val stateResource: Int) {
            ZERO(R.attr.state_qty_zero),
            ONE(R.attr.state_qty_one),
            NORMAL(R.attr.state_qty_normal),
            MAX(R.attr.state_qty_max) }
    }

    //  Can be nullable depending on which method are they called.
    //  For example, mQtyState was null when calling in onCreateDrawableState
    private val mQtyBtnType: QtyBtnType?
    private var mQtyState: QtyStates? = QtyStates.NORMAL

    init{
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.QtyButton,
            0,0
        ).apply {
            try{
                val btnType = getInteger(R.styleable.QtyButton_qty_btn_type, -1)
                mQtyBtnType = when(btnType){
                    0 -> QtyBtnType.ADD
                    1 -> QtyBtnType.REDUCE
                    else -> QtyBtnType.UNDEFINED
                }
            }finally {
                recycle()
            }
        }
    }

    fun setQuantityState(qtyState: QtyStates){
        if(mQtyState == qtyState)   return
        mQtyState = qtyState
        getNewEnabledStateBasedOnQuantityState(qtyState)?.let {
            if(it != isEnabled){
                isEnabled = it
                return@setQuantityState
            }
        }
        invalidate()
        requestLayout()
    }


    private fun getNewEnabledStateBasedOnQuantityState(qtyState: QtyStates) : Boolean? {
        return when(mQtyBtnType){
            QtyBtnType.ADD -> !(mQtyState == QtyStates.MAX && isEnabled)
            QtyBtnType.REDUCE -> !(mQtyState == QtyStates.ZERO && isEnabled)
            else -> null
        }
    }



    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace+1)
        return mQtyState?.let {
            val state = intArrayOf( it.stateResource )
            mergeDrawableStates(drawableState, state)
        } ?: drawableState
    }


}