package com.example.osen.activity

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.osen.R
import com.example.osen.database.database
import com.example.osen.model.Classroom
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_restore.*
import org.json.JSONArray
import org.json.JSONException
import java.io.*

class Restore : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var storage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance().reference

        restoreClasses.setOnClickListener {
            downloadFile("Osen_Classes.json")
        }
    }

    private fun downloadFile(fileName: String){
        try {
            val folder = storage.child("${auth.currentUser?.email}/${fileName}")
            val location = File.createTempFile("application", ".json")

            folder.getFile(location)
                .addOnSuccessListener { p0 ->
                    Log.d("HASILNYA", location.toString())
                    importClasses(location.toString())
                }
                .addOnFailureListener{
                    Log.d("TAG", "GAGAL")
                }
        }catch (e: StorageException){
            e.printStackTrace()
            Log.d("ERROR", e.message.toString())
        }
    }

    @Throws(IOException::class)
    private fun readJsonFile(data: String): String {
        val builder = StringBuilder()
        var inputStream: InputStream? = null
        try {
            var jsonDataString: String?
            inputStream = FileInputStream(data)
            val bufferReader =
                BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            while (bufferReader.readLine().also { jsonDataString = it } != null) {
                builder.append(jsonDataString)
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        Log.d("OUTPUT", builder.toString())
        return String(builder)
    }

    @Throws(IOException::class, JSONException::class)
    fun importClasses(data: String) {
        val ID_ = "ID_"
        val IMAGE = "IMAGE"
        val NAME = "NAME"
        val TYPE = "TYPE"
        val CATEGORY = "CATEGORY"
        val START_DATE = "START_DATE"
        val END_DATE = "END_DATE"
        val START_TIME = "START_TIME"
        val END_TIME = "END_TIME"
        val DAY = "DAY"
        val TEACHER_ID = "TEACHER_ID"
        try {
            val jsonDataString = readJsonFile(data)
            val menuItemJsonArray = JSONArray(jsonDataString)
            for (i in 0 until menuItemJsonArray.length()) {
                val jsonObject = menuItemJsonArray.getJSONObject(i)
                val contentValue = ContentValues()
                contentValue.put(ID_, jsonObject.getString(ID_))
                contentValue.put(IMAGE, jsonObject.getString(IMAGE))
                contentValue.put(NAME, jsonObject.getString(NAME))
                contentValue.put(TYPE, jsonObject.getString(TYPE))
                contentValue.put(CATEGORY, jsonObject.getString(CATEGORY))
                contentValue.put(START_DATE, jsonObject.getString(START_DATE))
                contentValue.put(END_DATE, jsonObject.getString(END_DATE))
                contentValue.put(START_TIME, jsonObject.getString(START_TIME))
                contentValue.put(END_TIME, jsonObject.getString(END_TIME))
                contentValue.put(DAY, jsonObject.getString(DAY))
                contentValue.put(TEACHER_ID, jsonObject.getString(TEACHER_ID))

                database.use {
                    insert(Classroom.TABLE_CLASSROOM, null, contentValue)
                }

                Log.d("IMPORT CLASSES SUCCESS", contentValue.toString())
            }
        } catch (e: JSONException) {
            Log.e("ERROR", e.message.toString())
            e.printStackTrace()
        }
    }
}
