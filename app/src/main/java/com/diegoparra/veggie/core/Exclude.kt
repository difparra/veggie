package com.diegoparra.veggie.core

/**
 * Annotation to exclude fields from being serialized when using Gson library
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Exclude