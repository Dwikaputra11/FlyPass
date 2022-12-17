package cthree.user.flypass.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cthree.user.flypass.adapter.AddBaggageAdapter
import cthree.user.flypass.data.PassengerBaggage
import cthree.user.flypass.databinding.FragmentBaggageBinding
import cthree.user.flypass.utils.SessionManager

class BaggageFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBaggageBinding
    private lateinit var listener: OnClickListener
    private lateinit var sessionManager: SessionManager

    interface OnClickListener{
        fun onClick(baggageList: List<PassengerBaggage>)
    }

    fun setOnClickListener(listener: OnClickListener){
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        sessionManager = SessionManager(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaggageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = AddBaggageAdapter(sessionManager.getPassenger())
        binding.rvBaggageTraveler.adapter = adapter
        binding.rvBaggageTraveler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.btnSave.setOnClickListener {
            listener.onClick(adapter.getTravelerBaggage())
            dismiss()
        }
        binding.toolbarLayout.ivClose.setOnClickListener {
            dismiss()
        }
    }
}