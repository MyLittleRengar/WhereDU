package com.project.wheredu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.wheredu.friend.FriendsActivity
import com.prolificinteractive.materialcalendarview.*
import java.util.*


class PromiseActivity : AppCompatActivity() {

    private lateinit var promiseAddIv: ImageView
    private lateinit var promiseCalendar: MaterialCalendarView

    private lateinit var promiseBottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promise)

        promiseAddIv = findViewById(R.id.promiseAddIv)
        promiseCalendar = findViewById(R.id.promiseCalendar)

        promiseBottomNav = findViewById(R.id.promise_bottomNav)

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

        promiseCalendar.setOnMonthChangedListener { _, date ->
            promiseCalendar.invalidateDecorators()
            promiseCalendar.removeDecorators()

            selectedMonthDecorator = SelectedMonthDecorator(date.month)
            promiseCalendar.addDecorators(todayDecorator, sundayDecorator, saturdayDecorator, selectedMonthDecorator)
        }
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
            view.addSpan(ForegroundColorSpan(ContextCompat.getColor(this@PromiseActivity, R.color.gray)))
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
}