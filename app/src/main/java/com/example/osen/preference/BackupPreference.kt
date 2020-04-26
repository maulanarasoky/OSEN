package com.example.osen.preference

import android.content.Context

class BackupPreference(context: Context) {
    private val pref_name = "osen_pref"
    private val osen_classes = "osen_classes"
    private val osen_students = "osen_students"
    private val osen_absents = "osen_absents"
    private val osen_scores = "osen_scores"
    private val osen_categories = "osen_categories"

    private val preference = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)

    fun setClasses(value: DataPreference) {
        val editor = preference.edit()
        editor.putBoolean(osen_classes, value.osen_classes)
        editor.apply()
    }

    fun setStudents(value: DataPreference) {
        val editor = preference.edit()
        editor.putBoolean(osen_students, value.osen_students)
        editor.apply()
    }

    fun setAbsents(value: DataPreference) {
        val editor = preference.edit()
        editor.putBoolean(osen_absents, value.osen_absents)
        editor.apply()
    }

    fun setScores(value: DataPreference) {
        val editor = preference.edit()
        editor.putBoolean(osen_scores, value.osen_scores)
        editor.apply()
    }

    fun setCategories(value: DataPreference) {
        val editor = preference.edit()
        editor.putBoolean(osen_categories, value.osen_categories)
        editor.apply()
    }

    fun getPreference(): DataPreference {
        val data = DataPreference()
        data.osen_classes = preference.getBoolean(osen_classes, false)
        data.osen_students = preference.getBoolean(osen_students, false)
        data.osen_absents = preference.getBoolean(osen_absents, false)
        data.osen_scores = preference.getBoolean(osen_scores, false)
        data.osen_categories = preference.getBoolean(osen_categories, false)

        return data
    }
}