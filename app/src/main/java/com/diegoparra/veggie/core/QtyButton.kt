package com.diegoparra.veggie.core

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatButton
import com.diegoparra.veggie.R

/*
    Notes when extending from:
    AppCompatButton
        Although enabling the button works, the visual appearance is not modified.
        Will need to setAlpha and color in selector background (or overriding isEnabled method)
    MaterialButton
        Does not accept selectors as backgrounds. So states will not have the desired functionality.
 */
class QtyButton(context: Context, attrs: AttributeSet) : AppCompatButton(context, attrs) {

    companion object{
        enum class EnabledBehaviour { ADD, REDUCE, UNDEFINED }
        enum class QtyStates(@AttrRes val stateResource: Int) {
            ZERO(R.attr.state_qty_zero),
            ONE(R.attr.state_qty_one),
            NORMAL(R.attr.state_qty_normal),
            MAX(R.attr.state_qty_max) }

        private class SavedState : BaseSavedState {
            var mEnabledBehaviour: EnabledBehaviour? = null
            var mQtyState: QtyStates? = null

            constructor(superState: Parcelable) : super(superState)
            private constructor(ins: Parcel?) : super(ins) {
                ins?.let {
                    mEnabledBehaviour = it.readValue(EnabledBehaviour::class.java.classLoader) as EnabledBehaviour
                    mQtyState = it.readValue(QtyStates::class.java.classLoader) as QtyStates
                }
            }

            override fun writeToParcel(out: Parcel?, flags: Int) {
                super.writeToParcel(out, flags)
                out?.writeValue(mEnabledBehaviour)
                out?.writeValue(mQtyState)
            }

            companion object CREATOR : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    //  Can be nullable depending on which method are they called.
    //  For example, mQtyState was null when calling in onCreateDrawableState
    private var mEnabledBehaviour: EnabledBehaviour? = EnabledBehaviour.UNDEFINED
    private var mQtyState: QtyStates? = QtyStates.NORMAL

    init{
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.QtyButton,
            0,0
        ).apply {
            try{
                val btnType = getInteger(R.styleable.QtyButton_enabled_behaviour, -1)
                mEnabledBehaviour = when(btnType){
                    0 -> EnabledBehaviour.ADD
                    1 -> EnabledBehaviour.REDUCE
                    else -> EnabledBehaviour.UNDEFINED
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
        refreshDrawableState()  // it was necessary because with the payload, buttonQty on
                                // mainProduct was not refreshed behind variationsDialog
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
        val enable = when(mEnabledBehaviour){
            EnabledBehaviour.ADD -> qtyState != QtyStates.MAX
            EnabledBehaviour.REDUCE -> qtyState != QtyStates.ZERO
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


    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()?.let {
            val ss = SavedState(it)
            ss.mEnabledBehaviour = mEnabledBehaviour
            ss.mQtyState = mQtyState
            ss
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        state?.let {
            val ss = it as SavedState
            super.onRestoreInstanceState(ss.superState)
            mEnabledBehaviour = ss.mEnabledBehaviour
            setQuantityState(ss.mQtyState ?: QtyStates.NORMAL)
            requestLayout()
        } ?: super.onRestoreInstanceState(state)
    }

}