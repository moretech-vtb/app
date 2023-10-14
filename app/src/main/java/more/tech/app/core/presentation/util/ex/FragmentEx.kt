package more.tech.app.core.presentation.util.ex

import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment

fun Fragment.toast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.context, text, length).show()
}

fun Fragment.onBackPressedDispatcher(fragment: Fragment) {
    requireActivity().onBackPressedDispatcher.addCallback(fragment) {
        activity?.finishAndRemoveTask()
    }
}