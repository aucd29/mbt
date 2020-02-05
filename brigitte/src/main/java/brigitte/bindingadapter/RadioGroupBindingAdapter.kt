@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.bindingadapter

import android.view.View
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-12 <p/>
 */

object RadioGroupBindingAdapter {
    private val logger = LoggerFactory.getLogger(RadioGroupBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindCheckedChangeListener")
    fun bindCheckedChangeListener(viewgroup: RadioGroup, listener: ((Int, Int) -> Unit)?) {
        if (logger.isDebugEnabled) {
            logger.debug("BIND CHECKED CHANGE LISTENER $listener")
        }

        listener?.let {
            viewgroup.setOnCheckedChangeListener { _, id ->
                val view = viewgroup.findViewById<View>(id)
                val index = viewgroup.indexOfChild(view)

                it.invoke(id, index)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindCheckId")
    fun bindCheckId(view: RadioGroup, id: Int) {
        if (logger.isDebugEnabled) {
            logger.debug("BIND CHECK ID $id")
        }

        view.check(id)
    }
}