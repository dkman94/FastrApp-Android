package app.android.fastrapp

import android.content.Context
import android.content.SharedPreferences
import java.sql.Time
import java.sql.Timestamp
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat

class UserPrefsHelper(context: Context) {
    lateinit var context:Context
    lateinit var currentTestSession:TestSession
    var currentSessionIdLabel= "currentSessionId"
    class TestSession(){
        var id:Int=-1
        var face:Boolean = false
        var arms:Boolean = false
        var baseline:Boolean=false
        lateinit var timeOfTest:String
        init{
            timeOfTest= getDateInstance().format("yyyy.MMMMM.dd GGG hh:mm aaa");
        }
    }
    fun CreateNewEntry(){
        val pref:SharedPreferences  = this.context.applicationContext.getSharedPreferences("TestResults",0)
        val lastId = pref.getInt(currentSessionIdLabel, -1)
        currentTestSession = TestSession()
        currentTestSession.id = lastId + 1
        with (pref.edit()) {
            putInt(currentSessionIdLabel,currentTestSession.id)
            commit()
        }
    }
}
