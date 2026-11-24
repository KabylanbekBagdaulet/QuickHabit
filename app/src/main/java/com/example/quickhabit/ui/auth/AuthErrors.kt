package com.example.quickhabit.ui.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

fun Throwable.toUserMessage(): String = when (this) {
    is FirebaseAuthWeakPasswordException -> "Use at least 6 characters."
    is FirebaseAuthInvalidCredentialsException -> "Wrong email or password."
    is FirebaseAuthInvalidUserException -> "Wrong email or password."
    is FirebaseAuthUserCollisionException -> "This email already has an account."
    is FirebaseNetworkException -> "No connection. Try again."
    else -> "Something went wrong. Try again."
}