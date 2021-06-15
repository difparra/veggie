package com.diegoparra.veggie.core.kotlin

inline fun <R> Boolean.runIfTrue(block: () -> R): R? {
    return if(this){
        block.invoke()
    }else{
        null
    }
}