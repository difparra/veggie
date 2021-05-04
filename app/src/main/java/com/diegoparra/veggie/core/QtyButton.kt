package com.diegoparra.veggie.core

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import com.diegoparra.veggie.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors

/*
        NOTES: When extending the Button from:
        ->  androidx.appcompat.widget.AppCompatButton(context, attrs)
                Override the setEnabled method, and set, for example, a different alpha on the button
                depending on its enabled state. In MaterialButton it is not necessary as it is already implemented.
        ->  MaterialButton
                Text is not correctly supported on small buttons, as the ones used in QtyButtons.
                So, in the case of qtyButton in mainProducts, there are some more things to do in the
                xml layout file:
                    padding=0dp and includeFontPadding=false    will reduce internal padding greately
                    insetBottom=0dp and insetTop=0dp            will reduce even more the padding
                    With the previous four values, padding has almost completely disappeared on button.
                    minWidth and minHeight set to 0dp will let me use wrap_content, and be almost as
                    small as possible.
     */

class QtyButton(context: Context, attrs: AttributeSet) : MaterialButton(context, attrs) {

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
        if(mQtyState == qtyState){
            return
        }
        mQtyState = qtyState
        setNewEnabledState(qtyState)
        //  invalidate() and requestLayout() are called into setNewEnabledState method.
    }

    fun setQuantityState(quantity: Int, maxOrder: Int) {
        when(quantity){
            maxOrder -> setQuantityState(QtyStates.MAX)
            0 -> setQuantityState(QtyStates.ZERO)
            1 -> setQuantityState(QtyStates.ONE)
            else -> setQuantityState(QtyStates.NORMAL)
        }
    }

    private fun setNewEnabledState(qtyState: QtyStates) {
        //  Do not modify qtyState on this method.
        val enable = when(mQtyBtnType){
            QtyBtnType.ADD -> qtyState != QtyStates.MAX
            QtyBtnType.REDUCE -> qtyState != QtyStates.ZERO
            else -> null
        }
        //  Making the change in the ui with the deduced enabled state
        if(enable != null){
            isEnabled = enable
        }else{
            invalidate()
            requestLayout()
        }
    }

    //  This is the method that will effectively take into account attrs to change state of the view,
    //  and be able to use selectors in colors and drawables
    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace+1)
        return mQtyState?.let {
            val state = intArrayOf( it.stateResource )
            mergeDrawableStates(drawableState, state)
        } ?: drawableState
    }


}