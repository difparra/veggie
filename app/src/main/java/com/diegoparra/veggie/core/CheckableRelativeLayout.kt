package com.diegoparra.veggie.core

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.RelativeLayout
import androidx.core.view.children

/*  Take as reference CompoundButton.CheckBox */
class CheckableRelativeLayout(context: Context, attrs: AttributeSet) :
    RelativeLayout(context, attrs), Checkable {

    private var mChecked: Boolean = false
    private var mBroadcasting: Boolean = false
    private var mOnCheckedChangedListener: OnCheckedChangedListener? = null



    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            refreshDrawableState()

            // Avoid infinite recursions if setChecked() is called from a listener
            if (mBroadcasting) {
                return
            }
            mBroadcasting = true
            mOnCheckedChangedListener?.onCheckedChanged(this, mChecked)
            mBroadcasting = false
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }



    fun setOnCheckedChangedListener(listener: OnCheckedChangedListener) {
        mOnCheckedChangedListener = listener
    }

    override fun performClick(): Boolean {
        toggle()
        setCheckedOnChildViews()
        return super.performClick()
    }

    private fun setCheckedOnChildViews() {
        val iterator = children.iterator()
        iterator.forEach {
            if (it is Checkable) {
                it.toggle()
            }
        }
    }



    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        val drawable = background
        if(drawable != null && drawable.isStateful && drawable.setState(drawableState)) {
            invalidateDrawable(drawable)
        }
    }






    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()?.let {
            val ss = SavedState(it)
            ss.checked = isChecked
            ss
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        state?.let {
            val ss = it as SavedState
            super.onRestoreInstanceState(ss.superState)
            isChecked = ss.checked ?: false
            requestLayout()
        } ?: super.onRestoreInstanceState(state)
    }



    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)

        interface OnCheckedChangedListener {
            fun onCheckedChanged(relativeLayout: RelativeLayout, isChecked: Boolean)
        }


        private class SavedState : BaseSavedState {
            var checked: Boolean? = null

            constructor(superState: Parcelable) : super(superState)
            private constructor(ins: Parcel?) : super(ins) {
                checked = ins?.readValue(null) as Boolean
            }

            override fun writeToParcel(out: Parcel?, flags: Int) {
                super.writeToParcel(out, flags)
                out?.writeValue(checked)
            }

            override fun toString(): String {
                return "CheckableRelativeLayout.SavedState{${
                    Integer.toHexString(System.identityHashCode(this))
                } checked=$checked}"
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
}