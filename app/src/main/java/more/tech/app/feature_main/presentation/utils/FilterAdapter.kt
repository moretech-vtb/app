package more.tech.app.feature_main.presentation.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import more.tech.app.R
import more.tech.app.feature_main.domain.models.FilterOption

class FilterAdapter(private val items: Array<FilterOption>) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    private val selectedItems = HashSet<String>()

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.checkedTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.grid_item_filter, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val currentItem = items[position]
        holder.textView.text = currentItem.value
        val isSelected = selectedItems.contains(currentItem.key)
        if (isSelected) {
            holder.textView.setTextColor(Color.WHITE)
            holder.textView.setBackgroundResource(R.drawable.selected_item_background)
        } else {
            holder.textView.setTextColor(Color.BLACK)
            holder.textView.setBackgroundResource(R.drawable.unselected_item_background)
        }

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = 20
        if (position == 0) {
            layoutParams.setMargins(margin, 0, margin, 0)
        } else {
            layoutParams.setMargins(0, 0, margin, 0)
        }
        holder.itemView.layoutParams = layoutParams

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(currentItem.key)) {
                selectedItems.remove(currentItem.key)
                holder.itemView.isActivated = false
            } else {
                selectedItems.add(currentItem.key)
                holder.itemView.isActivated = true
            }
            notifyItemChanged(position) // Update the view
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getSelectedKeys(): Set<String> {
        return selectedItems
    }
}
