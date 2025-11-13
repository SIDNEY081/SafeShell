
package com.sidney081.SafeShell

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

class AppAdapter(context: Context, private val apps: List<AppInfo>) :
    ArrayAdapter<AppInfo>(context, 0, apps) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_app, parent, false)

        val app = apps[position]

        val appIcon = view.findViewById<ImageView>(R.id.appIcon)
        val appName = view.findViewById<TextView>(R.id.appName)

        appIcon.setImageDrawable(app.icon)
        appName.text = app.name

        return view
    }
}