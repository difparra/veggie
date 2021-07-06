package com.diegoparra.veggie.core.kotlin.input_validation

import android.content.Context
import androidx.annotation.StringRes
import com.diegoparra.veggie.core.R
import com.diegoparra.veggie.core.kotlin.Failure

sealed class InputFailure(
    val field: Field,
    val input: String,
    @StringRes private val maleStringRes: Int? = null,
    @StringRes private val femaleStringRes: Int? = maleStringRes,
    override val formatArgs: Array<out Any> = emptyArray()
) : Failure.FeatureFailure() {

    class InputFailuresList(val failures: Set<InputFailure>) : Failure.FeatureFailure()


    class Empty(field: Field, input: String) :
        InputFailure(field, input, maleStringRes = R.string.failure_empty_field)

    class Short(field: Field, input: String, val minLength: Int) :
        InputFailure(
            field,
            input,
            maleStringRes = R.string.failure_short_field_m,
            femaleStringRes = R.string.failure_short_field_f,
            arrayOf(minLength)
        )

    class Invalid(field: Field, input: String) :
        InputFailure(
            field,
            input,
            maleStringRes = R.string.failure_invalid_field_m,
            femaleStringRes = R.string.failure_invalid_field_f
        )        //  Email

    class Incorrect(field: Field, input: String) :
        InputFailure(
            field,
            input,
            maleStringRes = R.string.failure_incorrect_field_m,
            femaleStringRes = R.string.failure_incorrect_field_f
        )      //  Password or when authenticating

    //  Just in case an unexpected failure, to let make a custom message
    class Unknown(field: Field, input: String,
                  override val debugMessage: String
    ) : InputFailure(field, input)


    override fun getContextMessage(context: Context): String {
        val fieldName = context.getString(field.strRes).lowercase()
        val stringRes = maleStringRes?.let {
            if (field.isFemaleString) {
                context.getString(femaleStringRes ?: maleStringRes, fieldName, formatArgs)
            } else {
                context.getString(maleStringRes, fieldName, formatArgs)
            }
        }
        return stringRes ?: super.getContextMessage(context)
    }

    companion object {
        sealed class Field(val strRes: Int, val isFemaleString: Boolean = false) {
            object EMAIL : Field(R.string.email, false)
            object PASSWORD : Field(R.string.password, true)
            object NAME : Field(R.string.name, false)
            object PHONE_NUMBER : Field(R.string.phone_number, false)
            object ADDRESS : Field(R.string.address, true)
            class OTHER(val fieldName: String, strRes: Int, isFemaleString: Boolean = false) :
                Field(strRes, isFemaleString)
        }
    }
}