package com.example.osen.model

data class Score(
    val id: Int?,
    val studentId: Int?,
    val uts: Int?,
    val persentaseUts: Int?,
    val uas: Int?,
    val persentaseUas: Int?,
    val assessment1: Int?,
    val persentaseAssessment1: Int?,
    val assessment2: Int?,
    val persentaseAssessment2: Int?,
    val assessment3: Int?,
    val persentaseAssessment3: Int?,
    val className: String?,
    val teacherId: String?
) {
    companion object{
        const val TABLE_SCORE = "SCORE"
        const val ID_ = "ID_"
        const val STUDENT_ID = "STUDENT_ID"
        const val UTS = "UTS"
        const val PERSENTASE_UTS = "PERSENTASE_UTS"
        const val UAS = "UAS"
        const val PERSENTASE_UAS = "PERSENTASE_UAS"
        const val ASSESSMENT_1 = "ASSESSMENT_1"
        const val PERSENTASE_ASSESSMENT_1 = "PERSENTASE_ASSESSMENT_1"
        const val ASSESSMENT_2 = "ASSESSMENT_2"
        const val PERSENTASE_ASSESSMENT_2 = "PERSENTASE_ASSESSMENT_2"
        const val ASSESSMENT_3 = "ASSESSMENT_3"
        const val PERSENTASE_ASSESSMENT_3 = "PERSENTASE_ASSESSMENT_3"
        const val CLASS_NAME = "CLASS"
        const val TEACHER_ID = "TEACHER_ID"
    }
}