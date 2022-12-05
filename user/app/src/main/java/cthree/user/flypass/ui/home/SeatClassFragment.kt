package cthree.user.flypass.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentSeatClassBinding

class SeatClassFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSeatClassBinding
    private lateinit var listener: OnClickListener

    interface OnClickListener{
        fun onClick(seatClass: String)
    }

    fun setOnCLickListener(listener: OnClickListener){
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeatClassBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnSelect.setOnClickListener {
            val selectedRb = binding.rgSeatClass.checkedRadioButtonId
            val selectedSeat = view.findViewById<RadioButton>(selectedRb).text.toString()
            listener.onClick(selectedSeat)
            dismiss()
        }
    }
}