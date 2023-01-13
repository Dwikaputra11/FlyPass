package cthree.user.flypass.ui.mybooking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cthree.user.flypass.R
import cthree.user.flypass.databinding.FragmentRefundBinding
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton

class RefundFragment : Fragment() {

    private lateinit var binding: FragmentRefundBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRefundBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DialogCaller(requireActivity())
            .setTitle(R.string.feature_not_available_title)
            .setMessage(R.string.feature_not_available_msg)
            .setPrimaryButton(R.string.confirm_one_btn_dialog){ dialog, _ ->
                run{
                    dialog.dismiss()
                }
            }
            .create(layoutInflater, AlertButton.ONE)
            .show()
    }
}