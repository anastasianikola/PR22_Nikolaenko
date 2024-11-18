package com.example.pr22_nikolaenko

import android.opengl.Visibility

data class Card(
    val id: Int,
    val image: Int,
    val isFlipped: Boolean = false,
    val isVisible: Boolean = true
    )
