package com.wen.aivoiceapp.adapter

import com.wen.aivoiceapp.R
import com.wen.aivoiceapp.data.ChatList
import com.wen.aivoiceapp.entity.AppConstants
import com.wen.lib_base.base.adapter.CommonAdapter
import com.wen.lib_base.base.adapter.CommonViewHolder
import com.wen.module_weather.tools.WeatherIconTools

class ChatListAdapter(mList: List<ChatList>) :
    CommonAdapter<ChatList>(mList, object : OnMoreBindDataListener<ChatList> {


        override fun onBindViewHolder(
            model: ChatList,
            viewHolder: CommonViewHolder,
            type: Int,
            position: Int
        ) {
            when (type) {
                AppConstants.TYPE_MINE_TEXT -> viewHolder.setText(R.id.tv_mine_text, model.text)
                AppConstants.TYPE_AI_TEXT -> viewHolder.setText(R.id.tv_ai_text, model.text)
                AppConstants.TYPE_AI_WEATHER -> {
                    viewHolder.setText(R.id.tv_city, model.city)
                    viewHolder.setText(R.id.tv_info, model.info)
                    viewHolder.setText(R.id.tv_temperature, model.temperature)
                    viewHolder.setImageResource(R.id.iv_icon, WeatherIconTools.getIcon(model.wid))
                }
                else -> 0
            }
        }

        override fun getLayoutId(type: Int): Int {
            return when (type) {
                AppConstants.TYPE_MINE_TEXT -> R.layout.layout_mine_text
                AppConstants.TYPE_AI_TEXT -> R.layout.layout_ai_text
                AppConstants.TYPE_AI_WEATHER -> R.layout.layout_ai_weather
                else -> 0
            }
        }

        override fun getItemViewType(position: Int): Int {
            return mList[position].type
        }

    }) {
}