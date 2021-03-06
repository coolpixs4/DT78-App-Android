package com.fbiego.dt78.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*
import kotlin.collections.ArrayList

class MyDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION ){

    companion object {
        private val DATABASE_NAME = "watch_data"
        private val DATABASE_VERSION = 1
        val STEPS_TABLE = "stepsData"
        val HRM_TABLE = "heartRate"
        val BP_TABLE = "bloodPressure"
        val SP02_TABLE = "bloodOxygen"
        val SLEEP_TABLE = "sleepData"

        val COLUMN_ID = "_id"
        val COLUMN_YEAR = "year"
        val COLUMN_MONTH = "month"
        val COLUMN_DAY = "day"
        val COLUMN_HOUR = "hour"
        val COLUMN_MIN = "minute"
        val COLUMN_STEPS = "steps"
        val COLUMN_CALORIES = "calories"
        val COLUMN_BPM = "bpm"
        val COLUMN_BPH = "bpHigh"
        val COLUMN_BPL = "bpLow"
        val COLUMN_SP02 = "sp02"
        val COLUMN_TYPE = "type"
        val COLUMN_DURATION = "duration"


    }
    override fun onCreate(p0: SQLiteDatabase?) {
        val createStepsTable = ("CREATE TABLE " + STEPS_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_YEAR + " INTEGER," + COLUMN_MONTH + " INTEGER," + COLUMN_DAY + " INTEGER," + COLUMN_HOUR + " INTEGER,"
                + COLUMN_STEPS + " INTEGER," + COLUMN_CALORIES + " INTEGER" + ")"  )
        val createHrmTable = ("CREATE TABLE " + HRM_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_YEAR + " INTEGER," + COLUMN_MONTH + " INTEGER," + COLUMN_DAY + " INTEGER," + COLUMN_HOUR + " INTEGER,"
                + COLUMN_MIN + " INTEGER," + COLUMN_BPM + " INTEGER" + ")"  )
        val createBpTable = ("CREATE TABLE " + BP_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_YEAR + " INTEGER," + COLUMN_MONTH + " INTEGER," + COLUMN_DAY + " INTEGER," + COLUMN_HOUR + " INTEGER,"
                + COLUMN_MIN + " INTEGER," + COLUMN_BPH + " INTEGER," + COLUMN_BPL + " INTEGER" + ")"  )
        val createSp02Table = ("CREATE TABLE " + SP02_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_YEAR + " INTEGER," + COLUMN_MONTH + " INTEGER," + COLUMN_DAY + " INTEGER," + COLUMN_HOUR + " INTEGER,"
                + COLUMN_MIN + " INTEGER," + COLUMN_SP02 + " INTEGER" + ")"  )
        val createSleepTable = ("CREATE TABLE " + SLEEP_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_YEAR + " INTEGER," + COLUMN_MONTH + " INTEGER," + COLUMN_DAY + " INTEGER," + COLUMN_HOUR + " INTEGER,"
                + COLUMN_MIN + " INTEGER," + COLUMN_TYPE + " INTEGER," + COLUMN_DURATION + " INTEGER" + ")"  )
        p0?.execSQL(createStepsTable)
        p0?.execSQL(createHrmTable)
        p0?.execSQL(createBpTable)
        p0?.execSQL(createSp02Table)
        p0?.execSQL(createSleepTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun insertSteps(stepsData: StepsData){
        val values = ContentValues()
        values.put(COLUMN_ID, stepsData.id)
        values.put(COLUMN_YEAR, stepsData.year)
        values.put(COLUMN_MONTH, stepsData.month)
        values.put(COLUMN_DAY, stepsData.day)
        values.put(COLUMN_HOUR, stepsData.hour)
        values.put(COLUMN_STEPS, stepsData.steps)
        values.put(COLUMN_CALORIES, stepsData.calories)

        val db = this.writableDatabase
        db.replace(STEPS_TABLE, null, values)
        db.close()
    }

    fun getSteps(): ArrayList<StepsData>{
        val qr = ArrayList<StepsData>()
        val query = "SELECT * FROM $STEPS_TABLE"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var dy = 0
        var step = 0
        var step2 = 0
        var cal = 0
        var cal2 = 0

        while (cursor.moveToNext()){

            if (dy != cursor.getString(3).toInt()){
                dy = cursor.getString(3).toInt()
                step2 = 0
                cal2 = 0
            }

            step = cursor.getString(5).toInt() - step2
            step2 = cursor.getString(5).toInt()

            if (step > 0){
                cal = cursor.getString(6).toInt() - cal2
                cal2 = cursor.getString(6).toInt()
                qr.add(StepsData(cursor.getString(1).toInt(), cursor.getString(2).toInt(), cursor.getString(3).toInt(),
                    cursor.getString(4).toInt(), step, cal))
            }


        }
        cursor.close()
        db.close()
        return qr
    }

    fun getDaysWithSteps(): ArrayList<StepsData>{
        val qr = ArrayList<StepsData>()

        val query = "SELECT DISTINCT $COLUMN_YEAR, $COLUMN_MONTH,$COLUMN_DAY FROM $STEPS_TABLE"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            qr.add(StepsData(cursor.getString(0).toInt(), cursor.getString(1).toInt(), cursor.getString(2).toInt(),
                0, 0, 0))
        }
        cursor.close()
        db.close()
        qr.sortByDescending {
            it.id
        }
        return qr
    }

    fun getStepsToday(): ArrayList<StepsData>{
        val qr = ArrayList<StepsData>()
        val calendar = Calendar.getInstance(Locale.getDefault())
        val year = calendar.get(Calendar.YEAR)-2000
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val query = "SELECT * FROM $STEPS_TABLE WHERE $COLUMN_DAY = $day AND $COLUMN_MONTH = $month AND $COLUMN_YEAR = $year"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var dy = 0
        var step = 0
        var step2 = 0
        var cal = 0
        var cal2 = 0

        while (cursor.moveToNext()){

            if (dy != cursor.getString(3).toInt()){
                dy = cursor.getString(3).toInt()
                step2 = 0
                cal2 = 0
            }

            step = cursor.getString(5).toInt() - step2
            step2 = cursor.getString(5).toInt()

            if (step > 0){
                cal = cursor.getString(6).toInt() - cal2
                cal2 = cursor.getString(6).toInt()
                qr.add(StepsData(cursor.getString(1).toInt(), cursor.getString(2).toInt(), cursor.getString(3).toInt(),
                    cursor.getString(4).toInt(), step, cal))
            }


        }
        cursor.close()
        db.close()

        var cont = false
        for (x in 0..23){
            cont = false
            if (qr.isNotEmpty()){
                qr.forEach {
                    if (it.hour == x){
                        cont = true
                    }
                }
            }
            if (!cont){
                qr.add(StepsData(year, month, day, x, 0, 0))
            }
        }
        qr.sortBy {
            it.hour
        }

        return qr
    }

    fun getStepsDay(year: Int, month: Int, day: Int): ArrayList<StepsData>{
        val qr = ArrayList<StepsData>()
        val query = "SELECT * FROM $STEPS_TABLE WHERE $COLUMN_DAY = $day AND $COLUMN_MONTH = $month AND $COLUMN_YEAR = $year"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var dy = 0
        var step = 0
        var step2 = 0
        var cal = 0
        var cal2 = 0

        while (cursor.moveToNext()){

            if (dy != cursor.getString(3).toInt()){
                dy = cursor.getString(3).toInt()
                step2 = 0
                cal2 = 0
            }

            step = cursor.getString(5).toInt() - step2
            step2 = cursor.getString(5).toInt()

            if (step > 0){
                cal = cursor.getString(6).toInt() - cal2
                cal2 = cursor.getString(6).toInt()
                qr.add(StepsData(cursor.getString(1).toInt(), cursor.getString(2).toInt(), cursor.getString(3).toInt(),
                    cursor.getString(4).toInt(), step, cal))
            }


        }
        cursor.close()
        db.close()

        var cont = false
        for (x in 0..23){
            cont = false
            if (qr.isNotEmpty()){
                qr.forEach {
                    if (it.hour == x){
                        cont = true
                    }
                }
            }
            if (!cont){
                qr.add(StepsData(year, month, day, x, 0, 0))
            }
        }
        qr.sortBy {
            it.hour
        }

        return qr

    }

    fun insertSleep(data: SleepData){
        val values = ContentValues()
        values.put(COLUMN_ID, data.id)
        values.put(COLUMN_YEAR, data.year)
        values.put(COLUMN_MONTH, data.month)
        values.put(COLUMN_DAY, data.day)
        values.put(COLUMN_HOUR, data.hour)
        values.put(COLUMN_MIN, data.minute)
        values.put(COLUMN_TYPE, data.type)
        values.put(COLUMN_DURATION, data.duration)

        val db = this.writableDatabase
        db.replace(SLEEP_TABLE, null, values)
        db.close()
    }

    fun insertHeart(data: HeartData){
        val values = ContentValues()
        values.put(COLUMN_ID, data.id)
        values.put(COLUMN_YEAR, data.year)
        values.put(COLUMN_MONTH, data.month)
        values.put(COLUMN_DAY, data.day)
        values.put(COLUMN_HOUR, data.hour)
        values.put(COLUMN_MIN, data.minute)
        values.put(COLUMN_BPM, data.bpm)

        val db = this.writableDatabase
        db.replace(HRM_TABLE, null, values)
        db.close()
    }

    fun getHeartToday(): String{
        val calendar = Calendar.getInstance(Locale.getDefault())
        val year = calendar.get(Calendar.YEAR)-2000
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val query = "SELECT $COLUMN_BPM FROM $HRM_TABLE WHERE $COLUMN_DAY = $day AND $COLUMN_MONTH = $month AND $COLUMN_YEAR = $year"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var hrm = "0\nbpm"

        while (cursor.moveToNext()){
            hrm = cursor.getString(0)+"\nbpm"
        }
        cursor.close()
        db.close()
        return hrm
    }

    fun getHeart(): ArrayList<HealthData>{
        val qr = ArrayList<HealthData>()
        val query = "SELECT * FROM $HRM_TABLE"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val hrm = cursor.getString(6)+" bpm"
            qr.add(HealthData(cursor.getInt(1), cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5), hrm))

        }
        cursor.close()
        db.close()

        qr.sortByDescending {
            it.id
        }
        return qr
    }

    fun insertBp(data: PressureData){
        val values = ContentValues()
        values.put(COLUMN_ID, data.id)
        values.put(COLUMN_YEAR, data.year)
        values.put(COLUMN_MONTH, data.month)
        values.put(COLUMN_DAY, data.day)
        values.put(COLUMN_HOUR, data.hour)
        values.put(COLUMN_MIN, data.minute)
        values.put(COLUMN_BPH, data.bpHigh)
        values.put(COLUMN_BPL, data.bpLow)

        val db = this.writableDatabase
        db.replace(BP_TABLE, null, values)
        db.close()
    }

    fun getBpToday(): String{
        val calendar = Calendar.getInstance(Locale.getDefault())
        val year = calendar.get(Calendar.YEAR)-2000
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val query = "SELECT $COLUMN_BPL,$COLUMN_BPH FROM $BP_TABLE WHERE $COLUMN_DAY = $day AND $COLUMN_MONTH = $month AND $COLUMN_YEAR = $year"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var bp = "00/00\nmmHg"

        while (cursor.moveToNext()){
            bp = cursor.getString(0)+"/"+cursor.getString(1)+"\nmmHg"
        }
        cursor.close()
        db.close()
        return bp
    }

    fun getBp(): ArrayList<HealthData>{
        val qr = ArrayList<HealthData>()
        val query = "SELECT * FROM $BP_TABLE"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val bp = cursor.getString(7)+"/"+cursor.getString(6)+" mmHg"
            qr.add(HealthData(cursor.getInt(1), cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5), bp))

        }
        cursor.close()
        db.close()
        qr.sortByDescending {
            it.id
        }

        return qr
    }

    fun insertSp02(data: OxygenData){
        val values = ContentValues()
        values.put(COLUMN_ID, data.id)
        values.put(COLUMN_YEAR, data.year)
        values.put(COLUMN_MONTH, data.month)
        values.put(COLUMN_DAY, data.day)
        values.put(COLUMN_HOUR, data.hour)
        values.put(COLUMN_MIN, data.minute)
        values.put(COLUMN_SP02, data.sp02)

        val db = this.writableDatabase
        db.replace(SP02_TABLE, null, values)
        db.close()
    }
    fun getSp02Today(): String{
        val calendar = Calendar.getInstance(Locale.getDefault())
        val year = calendar.get(Calendar.YEAR)-2000
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val query = "SELECT $COLUMN_SP02 FROM $SP02_TABLE WHERE $COLUMN_DAY = $day AND $COLUMN_MONTH = $month AND $COLUMN_YEAR = $year"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var sp = "0\n%"

        while (cursor.moveToNext()){
            sp = cursor.getString(0)+"\n%"
        }
        cursor.close()
        db.close()
        return sp
    }

    fun getSp02(): ArrayList<HealthData>{
        val qr = ArrayList<HealthData>()
        val query = "SELECT * FROM $SP02_TABLE"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val sp = cursor.getString(6)+" %"
            qr.add(HealthData(cursor.getInt(1), cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5), sp))

        }
        cursor.close()
        db.close()
        qr.sortByDescending {
            it.id
        }
        return qr
    }


}