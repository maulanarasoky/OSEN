package com.example.osen.preference

data class DataPreference(
    var osen_classes: Boolean = false,
    var osen_students: Boolean = false,
    var osen_absents: Boolean = false,
    var osen_scores: Boolean = false,
    var osen_categories: Boolean = false
)