package cthree.user.flypass.customview

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TableLayout
import android.widget.TableRow
import androidx.annotation.IdRes
import com.google.android.material.button.MaterialButton

class ToggleButtonRadioGroupTableLayout : TableLayout, View.OnClickListener {
    private var activeRadioButton: RadioButton? = null
    private var checkedButtonID = -1;

    /**
     * @param context
     */
    constructor(context: Context) : super(context) {        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(
        context,
        attrs
    ) {        // TODO Auto-generated constructor stub
    }

    override fun onClick(v: View) {
        val rb = v as RadioButton
        if (activeRadioButton != null) {
            activeRadioButton!!.isChecked = false
        }
        rb.isChecked = true
        activeRadioButton = rb
    }

    private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
        val checkedView = findViewById<View>(viewId)
        if (checkedView != null && checkedView is RadioButton) {
            checkedView.isChecked = checked
        }
    }

    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, int, android.view.ViewGroup.LayoutParams)
     */
    override fun addView(
        child: View, index: Int,
        params: ViewGroup.LayoutParams?
    ) {
        super.addView(child, index, params)
        setChildrenOnClickListener(child as TableRow)
    }

    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    override fun addView(child: View, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
        setChildrenOnClickListener(child as TableRow)
    }

    private fun setChildrenOnClickListener(tr: TableRow) {
        val c: Int = tr.childCount
        for (i in 0 until c) {
            val v: View = tr.getChildAt(i)
            if (v is RadioButton) {
                v.setOnClickListener(this)
            }
        }
    }

    val checkedRadioButtonId: Int
        get() = if (activeRadioButton != null) {
            activeRadioButton!!.id
        } else -1

    companion object {
        private const val TAG = "ToggleButtonGroupTableLayout"
    }
    /**
     * Check the id
     *
     * @param id
     */    private fun check(@IdRes id: Int) {
        // don't even bother
        if (id != -1 && id == checkedButtonID) {
            return
        }
        if (checkedButtonID != -1) {
            setCheckedStateForView(checkedButtonID, false)
        }
        if (id != -1) {
            setCheckedStateForView(id, true)
        }
        setCheckedId(id)
    }

    /**
     * set the checked button Id
     *
     * @param id
     */
    private fun setCheckedId(id: Int) {
        this.checkedButtonID = id
    }

    fun clearCheck() {
        check(-1)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        val ss = state
        super.onRestoreInstanceState(ss.superState)
        this.checkedButtonID = ss.buttonId
        setCheckedStateForView(checkedButtonID, true)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.buttonId = checkedButtonID
        return savedState
    }

    internal class SavedState : BaseSavedState {
        var buttonId = 0

        /**
         * Constructor used when reading from a parcel. Reads the state of the superclass.
         *
         * @param source
         */
        constructor(source: Parcel) : super(source) {
            buttonId = source.readInt()
        }

        /**
         * Constructor called by derived classes when creating their SavedState objects
         *
         * @param superState The state of the superclass of this view
         */
        constructor(superState: Parcelable?) : super(superState) {}

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(buttonId)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> = object :
                Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        override fun describeContents(): Int {
            return 0
        }

        object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}