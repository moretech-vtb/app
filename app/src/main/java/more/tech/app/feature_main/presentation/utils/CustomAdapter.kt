package more.tech.app.feature_main.presentation.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import more.tech.app.R
import more.tech.app.feature_main.domain.models.OpenHour

class CustomAdapter(private val dataList: List<OpenHour>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTextView: TextView = view.findViewById(R.id.dayTextView)
        val hoursTextView: TextView = view.findViewById(R.id.hoursTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.dayTextView.text = data.dayOfWeek
        holder.hoursTextView.text = data.hours
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
