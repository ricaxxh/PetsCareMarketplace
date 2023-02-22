package com.softgames.petscare.presentation.others.adapters

import android.R
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.TextView
import com.softgames.petscare.doman.model.SelectorItem

class SelectorAdapter(
    private val context: Context,
    private val item_list: List<SelectorItem>
) {

    val array_adapter = object : ArrayAdapter<SelectorItem>
        (context, R.layout.simple_list_item_1, item_list) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            //CUSTOM TEXTVIEW
            val view = super.getView(position, convertView, parent) as TextView
            val txt_title = view.findViewById(R.id.text1) as TextView
            txt_title.text = item_list[position].title
            txt_title.textSize = 20f

            //SET ICON
            txt_title.setCompoundDrawablesWithIntrinsicBounds(item_list[position].icon, 0, 0, 0)

            //ADD MARGIN BETWEEN ICONS AND TEXT
            val margin = (15 * context.resources.displayMetrics.density).toInt()
            txt_title.compoundDrawablePadding = margin

            return view
        }
    }

    val list_adapter: ListAdapter = object : ArrayAdapter<SelectorItem>
        (context, R.layout.simple_list_item_1, item_list) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            //CUSTOM TEXTVIEW
            val view = super.getView(position, convertView, parent) as TextView
            val txt_title = view.findViewById(R.id.text1) as TextView
            txt_title.text = item_list[position].title
            txt_title.textSize = 20f

            //SET ICON
            txt_title.setCompoundDrawablesWithIntrinsicBounds(item_list[position].icon, 0, 0, 0)

            //ADD MARGIN BETWEEN ICONS AND TEXT
            val margin = (15 * context.resources.displayMetrics.density).toInt()
            txt_title.compoundDrawablePadding = margin

            return view
        }
    }
}
