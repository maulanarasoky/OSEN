package com.example.osen.alarm

import android.R
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.osen.activity.MainActivity
import com.example.osen.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*

class AutoBackup: BroadcastReceiver() {

    companion object{
        const val TYPE_BACKUP_CLASSES = "TYPE_BACKUP_CLASSES"
        const val TYPE_BACKUP_STUDENTS = "TYPE_BACKUP_STUDENTS"
        const val TYPE_BACKUP_ABSENTS = "TYPE_BACKUP_ABSENTS"
        const val TYPE_BACKUP_SCORES = "TYPE_BACKUP_SCORES"
        const val TYPE_BACKUP_CATEGORIES = "TYPE_BACKUP_CATEGORIES"

        const val ID_BACKUP_CLASSES = 101
        const val ID_BACKUP_STUDENTS = 102
        const val ID_BACKUP_ABSENTS = 103
        const val ID_BACKUP_SCORES = 104
        const val ID_BACKUP_CATEGORIES = 105
    }

    val EXTRA_TYPE = "EXTRA_TYPE"

    lateinit var storage: StorageReference
    lateinit var auth: FirebaseAuth


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("CONTEXT", context.toString())
        if(context != null){
            auth = FirebaseAuth.getInstance()
            storage = FirebaseStorage.getInstance().reference
            val type = intent?.getStringExtra(EXTRA_TYPE)
            when(type){
                TYPE_BACKUP_CLASSES -> {
                    export(context, Classroom.TABLE_CLASSROOM, "Osen_Classes.json")
                    showNotif(context, "Osen", "Data kelas Osen berhasil di backup", ID_BACKUP_CLASSES)
                    Log.d("BACKUP", "BACKUP AKTIF")
                }
                TYPE_BACKUP_STUDENTS -> {
                    export(context, Student.TABLE_STUDENT, "Osen_Students.json")
                    showNotif(context, "Osen", "Data murid Osen berhasil di backup", ID_BACKUP_STUDENTS)
                }
                TYPE_BACKUP_ABSENTS -> {
                    export(context, Absent.TABLE_ABSENT, "Osen_Absents.json")
                    showNotif(context, "Osen", "Data absen Osen berhasil di backup", ID_BACKUP_ABSENTS)
                }
                TYPE_BACKUP_SCORES -> {
                    export(context, Score.TABLE_SCORE, "Osen_Scores.json")
                    showNotif(context, "Osen", "Data nilai Osen berhasil di backup", ID_BACKUP_SCORES)
                }
                TYPE_BACKUP_CATEGORIES -> {
                    export(context, Category.TABLE_CATEGORY, "Osen_Categories.json")
                    showNotif(context, "Osen", "Data kategori Osen berhasil di backup", ID_BACKUP_CATEGORIES)
                }
                else -> {
                    Log.d("ERROR", "ERROR BACK UP")
                }
            }
        }
    }

    private fun showNotif(context: Context, title: String, message: String, notifId: Int){
        val CHANNEL_ID = "channel_01"
        val CHANNEL_NAME = "AlarmManager channel"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(Color.parseColor("#48cfad"))
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(alarmSound)

        val notificationIntent = Intent(context, MainActivity::class.java)

        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        builder.setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /* Create or update. */
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(CHANNEL_ID)
            notificationManager?.createNotificationChannel(channel)
        }

        val notification = builder.build()

        notificationManager.notify(notifId, notification)
    }

    fun setAutoBackup(context: Context, type: String, id: Int){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AutoBackup::class.java)
        intent.putExtra(EXTRA_TYPE, type)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 7)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun cancelAutoBackup(context: Context, id: Int){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AutoBackup::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
    }

    private fun getResults(context: Context, myTable: String): JSONArray? {
        val myPath: String = context.getDatabasePath("Osen.db").toString()
        val searchQuery = "SELECT  * FROM $myTable WHERE TEACHER_ID = '${auth.currentUser?.uid}'"
        val myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)
        val cursor: Cursor = myDataBase.rawQuery(searchQuery, null)
        val resultSet = JSONArray()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val totalColumn: Int = cursor.columnCount
            val rowObject = JSONObject()
            for (i in 0 until totalColumn) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i))
                        } else {
                            rowObject.put(cursor.getColumnName(i), "")
                        }
                    } catch (e: Exception) {
                        Log.d("TAG_NAME", e.message.toString())
                    }
                }
            }
            resultSet.put(rowObject)
            cursor.moveToNext()
        }
        cursor.close()
        Log.d("TAG_NAME", resultSet.toString())
        return resultSet
    }

    private fun export(context: Context, myTable: String, fileName: String) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            outputStreamWriter.write(getResults(context, myTable).toString())
            outputStreamWriter.close()

            val fileLocation = File(context.filesDir, fileName)
            val path: Uri =
                FileProvider.getUriForFile(context, "com.example.osen.fileprovider", fileLocation)
            val folder = storage.child("${auth.currentUser?.email}/${fileName}")
            folder.putFile(path)
                .addOnSuccessListener{
                    Log.d("FIREBASE", "BERHASIL")
                }
                .addOnFailureListener {
                    Log.d("FIREBASE", "GAGAL")
                }
        } catch (e: IOException) {
            Log.e("ERROR", "File write failed :", e)
        }
    }
}