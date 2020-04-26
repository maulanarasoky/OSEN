package com.example.osen.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.osen.R
import com.example.osen.alarm.AutoBackup
import com.example.osen.preference.BackupPreference
import com.example.osen.preference.DataPreference
import kotlinx.android.synthetic.main.activity_backup.*

class Backup : AppCompatActivity() {

    var dataPreference = DataPreference()
    lateinit var backupPreference: BackupPreference

    lateinit var alarmManager: AutoBackup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        backupPreference = BackupPreference(this)
        alarmManager = AutoBackup()

        showExistingPreference()

        backupClasses.setOnClickListener {
            if (!dataPreference.osen_classes) {
                dataPreference.osen_classes = true
                alarmManager.setAutoBackup(
                    this,
                    AutoBackup.TYPE_BACKUP_CLASSES,
                    AutoBackup.ID_BACKUP_CLASSES
                )
                Toast.makeText(this, "Auto Backup kelas dinyalakan", Toast.LENGTH_SHORT).show()
            } else {
                dataPreference.osen_classes = false
                alarmManager.cancelAutoBackup(this, AutoBackup.ID_BACKUP_CLASSES)
                Toast.makeText(this, "Auto Backup kelas dimatikan", Toast.LENGTH_SHORT).show()
            }
            backupPreference.setClasses(dataPreference)
        }
        backupStudents.setOnClickListener {
            if (!dataPreference.osen_students) {
                dataPreference.osen_students = true
                alarmManager.setAutoBackup(
                    this,
                    AutoBackup.TYPE_BACKUP_STUDENTS,
                    AutoBackup.ID_BACKUP_STUDENTS
                )
                Toast.makeText(this, "Auto Backup murid dinyalakan", Toast.LENGTH_SHORT).show()
            } else {
                dataPreference.osen_students = false
                alarmManager.cancelAutoBackup(this, AutoBackup.ID_BACKUP_STUDENTS)
                Toast.makeText(this, "Auto Backup murid dimatikan", Toast.LENGTH_SHORT).show()
            }
            backupPreference.setStudents(dataPreference)
        }
        backupAbsents.setOnClickListener {
            if (!dataPreference.osen_absents) {
                dataPreference.osen_absents = true
                alarmManager.setAutoBackup(
                    this,
                    AutoBackup.TYPE_BACKUP_ABSENTS,
                    AutoBackup.ID_BACKUP_ABSENTS
                )
                Toast.makeText(this, "Auto Backup absen dinyalakan", Toast.LENGTH_SHORT).show()
            } else {
                dataPreference.osen_absents = false
                alarmManager.cancelAutoBackup(this, AutoBackup.ID_BACKUP_ABSENTS)
                Toast.makeText(this, "Auto Backup absen dimatikan", Toast.LENGTH_SHORT).show()
            }
            backupPreference.setAbsents(dataPreference)
        }
        backupScores.setOnClickListener {
            if (!dataPreference.osen_scores) {
                dataPreference.osen_scores = true
                alarmManager.setAutoBackup(
                    this,
                    AutoBackup.TYPE_BACKUP_SCORES,
                    AutoBackup.ID_BACKUP_SCORES
                )
                Toast.makeText(this, "Auto Backup nilai dinyalakan", Toast.LENGTH_SHORT).show()
            } else {
                dataPreference.osen_scores = false
                alarmManager.cancelAutoBackup(this, AutoBackup.ID_BACKUP_SCORES)
                Toast.makeText(this, "Auto Backup nilai dimatikan", Toast.LENGTH_SHORT).show()
            }
            backupPreference.setScores(dataPreference)
        }
        backupCategories.setOnClickListener {
            if (!dataPreference.osen_categories) {
                dataPreference.osen_categories = true
                alarmManager.setAutoBackup(
                    this,
                    AutoBackup.TYPE_BACKUP_CATEGORIES,
                    AutoBackup.ID_BACKUP_CATEGORIES
                )
                Toast.makeText(this, "Auto Backup kategori dinyalakan", Toast.LENGTH_SHORT).show()
            } else {
                dataPreference.osen_categories = false
                alarmManager.cancelAutoBackup(this, AutoBackup.ID_BACKUP_CATEGORIES)
                Toast.makeText(this, "Auto Backup kategori dimatikan", Toast.LENGTH_SHORT).show()
            }
            backupPreference.setCategories(dataPreference)
        }
    }

    private fun showExistingPreference() {
        dataPreference = backupPreference.getPreference()

        setCheckView(dataPreference)
    }

    private fun setCheckView(dataPreference: DataPreference) {
        backupClasses.isChecked = dataPreference.osen_classes
        backupStudents.isChecked = dataPreference.osen_students
        backupAbsents.isChecked = dataPreference.osen_absents
        backupScores.isChecked = dataPreference.osen_scores
        backupCategories.isChecked = dataPreference.osen_categories
    }
}
