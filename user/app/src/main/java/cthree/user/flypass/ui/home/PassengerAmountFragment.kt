package cthree.user.flypass.ui.home

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentPassengerAmoutBinding

class PassengerAmountFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPassengerAmoutBinding
    private lateinit var listener: OnClickListener

    interface OnClickListener{
        fun onClick(passenger: String)
    }

    fun setOnClickListener(listener: OnClickListener){
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPassengerAmoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding.npPassenger){
            minValue = 1
            maxValue = 10
            textColor = context.getColor(R.color.color_primary)
            textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16F, context.resources.displayMetrics)
        }

        binding.btnSelect.setOnClickListener {
            listener.onClick(binding.npPassenger.value.toString())
            dismiss()
        }
    }

}