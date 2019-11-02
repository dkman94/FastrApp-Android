package app.android.fastrapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class UserPrefsHelper(context: Context) {
    var context:Context = context
    lateinit var currentTestSession:TestSession
    var currentSessionIdLabel= "currentSessionId"
    class TestSession(){
        var id:Int=-1
        var face:Boolean = false
        var arms:Boolean = false
        var speech:Boolean = false
        var baseline:Boolean=false
        var timeOfTest:String
        init{
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            timeOfTest = sdf.format(Date())
            //timeOfTest= getDateInstance().format("yyyy.MMMMM.dd GGG hh:mm aaa");
        }
    }
    enum class TestType {
        FACE,
        ARM,
        SPEECH
    }
    fun CreateNewEntry(){
        val pref:SharedPreferences  = this.context.applicationContext.getSharedPreferences("TestResults",0)
        val lastId = pref.getInt(currentSessionIdLabel, -1)
        currentTestSession = TestSession()
        if (lastId == -1){
            this.currentTestSession.baseline = true
        }
        currentTestSession.id = lastId + 1

        with (pref.edit()) {
            putInt(currentSessionIdLabel,currentTestSession.id)
            commit()
        }
    }

    fun UpdateActivity(test:TestType, result:Boolean){
        if (test == TestType.ARM){
            this.currentTestSession.arms = result
        }
        else if (test == TestType.FACE){
            this.currentTestSession.face = result
        }
        else if (test == TestType.SPEECH){
            this.currentTestSession.speech = result
        }
    }

    fun WriteActivity(){
        val gson = Gson()
        val pref:SharedPreferences  = this.context.applicationContext.getSharedPreferences("TestResults",0)
        val sessIdString:String = "test_" + this.currentTestSession.id.toString()
        val sessResString:String = gson.toJson(this.currentTestSession)
        with(pref.edit()){
            putString(sessIdString, sessResString)
            commit()
        }
    }

}
