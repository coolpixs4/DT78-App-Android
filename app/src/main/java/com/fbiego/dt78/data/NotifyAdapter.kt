package com.fbiego.dt78.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.fbiego.dt78.R

class NotifyAdapter(context: Context): BaseAdapter() {

    var inflater: LayoutInflater = LayoutInflater.from(context)
    private val num = arrayListOf(0, 1, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val icons = arrayListOf(R.raw.sms, R.raw.whatsapp, R.raw.twitter, R.raw.instagram, R.raw.facebook, R.raw.messenger, R.raw.skype,
        R.raw.penguin, R.raw.wechat, R.raw.line, R.raw.weibo, R.raw.kakao)
    private val desc = arrayListOf("Message", "WhatsApp", "Twitter", "Instagram", "Facebook", "Messenger", "Skype", "QQ", "Wechat", "Line", "Weibo", "KakaoTalk")

    override fun getView(i: Int, view1: View?, viewGroup: ViewGroup): View {
        var view = inflater.inflate(R.layout.spinner_item, null)
        val icon = view.findViewById<ImageView>(R.id.icon)
        val text = view.findViewById<TextView>(R.id.text)
        icon.setImageResource(icons[i])
        text.text = desc[i]
        return view

    }

    override fun getItem(p0: Int): Any? {
        return num[p0]
    }

    override fun getItemId(p0: Int): Long {
        return num[p0].toLong()
    }

    override fun getCount(): Int {
        return num.size
    }
}