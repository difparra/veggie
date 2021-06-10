package com.diegoparra.veggie.core

inline fun <R> Boolean.runIfTrue(block: () -> R): R? {
    return if(this){
        block.invoke()
    }else{
        null
    }
}