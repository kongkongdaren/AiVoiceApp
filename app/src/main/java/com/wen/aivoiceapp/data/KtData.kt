package com.wen.aivoiceapp.data

data class MainListData(
    val title: String,
    val icon: Int,
    val color: Int
)


data class ChatList(
    val type: Int
) {
    lateinit var text: String

    //天气
    lateinit var wid: String
    lateinit var info: String
    lateinit var city: String
    lateinit var temperature: String
}