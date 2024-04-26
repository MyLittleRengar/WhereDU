package com.project.wheredu.promise

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.project.wheredu.MainActivity
import com.project.wheredu.MyPageActivity
import com.project.wheredu.R
import com.project.wheredu.dialog.CustomDeletePromiseDialogAdapter
import com.project.wheredu.friend.FriendsActivity
import com.project.wheredu.recycler.PromiseItem
import com.project.wheredu.recycler.PromiseListAdapter
import com.project.wheredu.utility.Service
import com.project.wheredu.utility.ToastMessage
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.json.JSONObject
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class PromiseActivity : AppCompatActivity() {

    private lateinit var promiseAddIv: ImageView
    private lateinit var promiseListRv: RecyclerView
    private lateinit var promiseCalendar: MaterialCalendarView

    private lateinit var promiseBottomNav: BottomNavigationView

    private var listItems = arrayListOf<PromiseItem>()
    private var promiseListAdapter = PromiseListAdapter(listItems)

    private val service = Service.getService()

    private lateinit var bottomSheetPromiseInfoBehavior: BottomSheetBehavior<LinearLayout>
    private val promiseInfoBehavior by lazy { findViewById<LinearLayout>(R.id.per_bottom_sheet_PromiseInfo) }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promise)

        promiseAddIv = findViewById(R.id.promiseAddIv)
        promiseListRv = findViewById(R.id.promise_ListRV)
        promiseCalendar = findViewById(R.id.promiseCalendar)

        promiseBottomNav = findViewById(R.id.promise_bottomNav)

        persistentBottomSheetPromiseInfoEvent()

        promiseListRv.layoutManager = LinearLayoutManager(this@PromiseActivity, LinearLayoutManager.HORIZONTAL, false)
        promiseListRv.adapter = promiseListAdapter
        promiseListAdapter.setItemClickListener(object: PromiseListAdapter.OnItemClickListener{
            override fun onInfoClick(position: Int, name: String, date: String) {
                returnPromiseData(name)
            }

            override fun onDeleteClick(position: Int, name: String, date: String) {
                val dlg = CustomDeletePromiseDialogAdapter(this@PromiseActivity)
                dlg.setOnAcceptClickedListener { content ->
                    if(content == "pass") {
                        deletePromise(name)
                    }
                }
                dlg.show()
            }
        })

        promiseAddIv.setOnClickListener {
            startActivity(Intent(this@PromiseActivity, PromiseAdd1Activity::class.java))
        }

        promiseBottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.main_home -> {
                    startActivity(Intent(this@PromiseActivity, MainActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_friend -> {
                    startActivity(Intent(this@PromiseActivity, FriendsActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_mypage -> {
                    startActivity(Intent(this@PromiseActivity, MyPageActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }

                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }

        promiseCalendar.setTitleFormatter { day ->
            val inputText = day.date
            val calendarHeaderElements = inputText.toString().split(" ")
            val calendarHeaderBuilder = StringBuilder()

            calendarHeaderBuilder.append(calendarHeaderElements[5]).append("년 ")
                .append(getKoreanMonthName(calendarHeaderElements[1])).append("월")

            calendarHeaderBuilder.toString()
        }

        promiseCalendar.setHeaderTextAppearance(R.style.CalendarWidgetHeader)
        promiseCalendar.addDecorators(SaturdayDecorator(), SundayDecorator(), TodayDecorator(this))

        val todayDecorator = TodayDecorator(promiseCalendar.context)
        val sundayDecorator = SundayDecorator()
        val saturdayDecorator = SaturdayDecorator()
        var selectedMonthDecorator: SelectedMonthDecorator

        calendarPromiseData()

        promiseCalendar.setOnMonthChangedListener { _, date ->
            promiseCalendar.invalidateDecorators()
            promiseCalendar.removeDecorators()

            selectedMonthDecorator = SelectedMonthDecorator(date.month)
            promiseCalendar.addDecorators(todayDecorator, sundayDecorator, saturdayDecorator, selectedMonthDecorator)
            calendarPromiseData()
        }
        promiseCalendar.setOnDateChangedListener { _, date, _ ->
            val formattedDate = SimpleDateFormat("yyyy.M.dd", Locale.getDefault()).format(date.calendar.time)
            listItems.clear()
            promiseListAdapter.notifyDataSetChanged()
            returnSelectPromiseData(formattedDate)
        }
    }

    private fun returnSelectPromiseData(date: String) {
        val callPost = service.returnSelectPromiseData(date)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result != "noData") {
                            loopInt(date, result.toInt())
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    ToastMessage.show(this@PromiseActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                ToastMessage.show(this@PromiseActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }

    private fun loopInt(date: String, cnt: Int) {
        for(i in 0 until cnt) {
            selectPromiseData(date, i)
        }
    }

    private fun selectPromiseData(date: String, cnt: Int) {
        val callPost = service.selectPromiseData(date, cnt)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result != "noData") {
                            selectReplaceData(result)
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    ToastMessage.show(this@PromiseActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                ToastMessage.show(this@PromiseActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }

    private fun selectReplaceData(result: String) {
        val jsonObject = JSONObject(result)

        val promiseName = jsonObject.getString("promiseName")
        val promisePlace = jsonObject.getString("promisePlace")
        val promisePlaceDetail = jsonObject.getString("promisePlaceDetail")
        val promiseDate = jsonObject.getString("promiseDate")
        val promiseTime = jsonObject.getString("promiseTime")
        val promiseMembers = jsonObject.getString("promiseMember")
        val promiseMemo = jsonObject.getString("promiseMemo")

        val membersList = promiseMembers.split(", ")

        val textSplit = listOf(promiseName, promisePlace, promisePlaceDetail, promiseDate, promiseTime, membersList.joinToString(", "), promiseMemo)
        initRecycle(textSplit)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycle(data: List<String>) {
        if(data.isNotEmpty()) {
            val item = PromiseItem(name = data[0], place = data[1], time = data[4], date = data[3])
            listItems.add(item)
            promiseListAdapter.notifyDataSetChanged()
        }
    }

    private fun calendarPromiseData() {
        val callPost = service.calendarPromiseData("")
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        calendarReplaceData(result)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    ToastMessage.show(this@PromiseActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                ToastMessage.show(this@PromiseActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }
    private fun calendarReplaceData(result: String) {
        val replace = result.replace("[","").replace("{","").replace("\"","").replace("}","").replace("]","")
            .replace("promiseDate:","")
        val textSplit = replace.split(",")
        val dataList = textSplit.map { convertStringToDate(it) }
        promiseCalendar.addDecorator(EventDecorator(Color.BLUE, dataList))
    }

    private fun convertStringToDate(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy.M.d")
        return LocalDate.parse(dateString, formatter)
    }

    private fun getKoreanMonthName(month: String): String {
        return when (month.substring(0, 1).uppercase(Locale.ROOT) + month.substring(1).lowercase(
            Locale.ROOT
        )) {
            "Jan" -> "1"
            "Feb" -> "2"
            "Mar" -> "3"
            "Apr" -> "4"
            "May" -> "5"
            "Jun" -> "6"
            "Jul" -> "7"
            "Aug" -> "8"
            "Sep" -> "9"
            "Oct" -> "10"
            "Nov" -> "11"
            "Dec" -> "12"
            else -> throw IllegalArgumentException("Invalid month: $month")
        }
    }

    private inner class SelectedMonthDecorator(val selectedMonth : Int) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day.month != selectedMonth
        }
        @SuppressLint("ResourceType")
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(this@PromiseActivity,
                R.color.gray
            )))
        }
    }

    private class TodayDecorator(context: Context) : DayViewDecorator {
        private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_selector)
        private val date = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date) ?: false
        }

        override fun decorate(view: DayViewFacade?) {
            view?.setBackgroundDrawable(drawable!!)
        }
    }
    private class SundayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val calendar = Calendar.getInstance()
            calendar.time = day.date
            return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        }
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.RED))
        }
    }

    private class SaturdayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val calendar = Calendar.getInstance()
            calendar.time = day.date
            return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
        }
        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.BLUE))
        }
    }

    private fun deletePromise(name: String) {
        val callPost = service.deletePromise(name)
        callPost.enqueue(object: Callback<String?> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        ToastMessage.show(this@PromiseActivity, "약속이 삭제되었습니다")
                        promiseListAdapter.notifyDataSetChanged()
                        //startActivity(Intent(this@PromiseActivity, PromiseActivity::class.java))
                        //ActivityCompat.finishAffinity(this@PromiseActivity)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    ToastMessage.show(this@PromiseActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                ToastMessage.show(this@PromiseActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }

    private fun returnPromiseData(name: String) {
        val callPost = service.returnPromiseData(name)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        replaceData(result)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    ToastMessage.show(this@PromiseActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                ToastMessage.show(this@PromiseActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }

    private fun replaceData(result: String) {
        try {
            val jsonArray = JSONArray(result)

            var resultList: List<String>? = null

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                val promiseName = jsonObject.getString("promiseName")
                val promisePlace = jsonObject.getString("promisePlace")
                val promisePlaceDetail = jsonObject.getString("promisePlaceDetail")
                val promiseDate = jsonObject.getString("promiseDate")
                val promiseTime = jsonObject.getString("promiseTime")
                val promiseMembers = jsonObject.getString("promiseMember")
                val promiseMemo = jsonObject.getString("promiseMemo")

                val membersList = promiseMembers.split(", ")

                resultList = listOf(promiseName, promisePlace, promisePlaceDetail, promiseDate, promiseTime, membersList.joinToString(", "), promiseMemo)
            }
            bottomSheetExpanded(resultList!![0], resultList[1], resultList[2], resultList[3], resultList[4], resultList[5], resultList[6])
        } catch (e: Exception) {
            e.printStackTrace()
        }//세부 정보 순서 수정
    }

    private fun View.setTextViewText(id: Int, text: String) {
        findViewById<TextView>(id).text = text
    }
    private fun bottomSheetExpanded(name: String, place: String, placeDetail: String, date: String, time: String, member: String, memo: String) {
        with(findViewById<View>(R.id.per_bottom_sheet_PromiseInfo)) {
            setTextViewText(R.id.bottomSheet_promiseNameTV, name)
            setTextViewText(R.id.bottomSheet_PromiseDateTV, "$date, $time")
            setTextViewText(R.id.bottomSheet_PromisePlaceTV, place)
            setTextViewText(R.id.bottomSheet_PromiseDetailTV, placeDetail)
            setTextViewText(R.id.bottomSheet_PromiseMemberTV, member)
            setTextViewText(R.id.bottomSheet_PromiseMemoTV, memo)
        }
        bottomSheetPromiseInfoBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    private fun persistentBottomSheetPromiseInfoEvent() {
        bottomSheetPromiseInfoBehavior = BottomSheetBehavior.from(promiseInfoBehavior)
        bottomSheetPromiseInfoBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(p0: View, newState: Int) {
                val tag = "friendInfoBehavior"
                when(newState) {
                    BottomSheetBehavior.STATE_COLLAPSED-> {
                        Log.d(tag, "onStateChanged: 접음")
                    }
                    BottomSheetBehavior.STATE_DRAGGING-> {
                        Log.d(tag, "onStateChanged: 드래그")
                    }
                    BottomSheetBehavior.STATE_EXPANDED-> {
                        Log.d(tag, "onStateChanged: 펼침")
                    }
                    BottomSheetBehavior.STATE_HIDDEN-> {
                        Log.d(tag, "onStateChanged: 숨기기")
                    }
                    BottomSheetBehavior.STATE_SETTLING-> {
                        Log.d(tag, "onStateChanged: 고정됨")
                    }
                }
            }
        })
    }

    class EventDecorator(private val color: Int, dates: Collection<LocalDate>?) : DayViewDecorator {
        private val dates: HashSet<LocalDate>
        init { this.dates = HashSet(dates ?: emptyList()) }
        override fun shouldDecorate(day: CalendarDay): Boolean { return dates.contains(LocalDate.of(day.year, day.month + 1, day.day)) }
        override fun decorate(view: DayViewFacade) { view.addSpan(DotSpan(6f, color)) }
    }
}