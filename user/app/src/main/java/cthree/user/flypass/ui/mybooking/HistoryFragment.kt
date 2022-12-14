package cthree.user.flypass.ui.mybooking

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.adapter.BookingAdapter
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentHistoryBinding
import cthree.user.flypass.models.booking.bookings.Booking
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.BookingViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "HistoryFragment"

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private lateinit var binding :FragmentHistoryBinding
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private val bookingVM: BookingViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressDialog()
        progressAlertDialog.show()
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty() && it.refreshToken.isNotEmpty()){
                Log.d(TAG, "access token: ${it.token}")
                Log.d(TAG, "refresh token: ${it.refreshToken}")
                Log.d(TAG, "token status: ${Utils.isTokenExpired(it.token) && Utils.isTokenExpired(it.refreshToken)}")
                if(Utils.isTokenExpired(it.token) && Utils.isTokenExpired(it.refreshToken)){
                    progressAlertDialog.dismiss()
                    showSessionExpiredDialog()
                }else if(!Utils.isTokenExpired(it.token)){
                    bookingVM.getUserBooking(it.token)
                }else{
                    bookingVM.getUserBooking(it.refreshToken)
                }
            }
        }
        val adapter = BookingAdapter()
        binding.rvBookingHistory.adapter = adapter
        binding.rvBookingHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        bookingVM.userBookingResponse().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                adapter.submitList(it.booking)
            }
        }
        adapter.setOnItemClickListener(object : BookingAdapter.OnItemClickListener{
            override fun onItemClick(booking: Booking) {
                Log.d(TAG, "onItemClick: $booking")
                findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToHistorySummaryFragment(booking))
            }
        })
    }

    private fun showSessionExpiredDialog(){
        DialogCaller(requireActivity())
            .setTitle(R.string.token_expired_title)
            .setMessage(R.string.token_expired_subtitle)
            .setPrimaryButton(R.string.token_expired_login){dialog, _ ->
                run{
                    // handle data
                    prefVM.clearToken()
                    prefVM.clearRefreshToken()
                    dialog.dismiss()
                    callLoginDialog()
                }
            }
            .setSecondaryButton(R.string.token_expired_later){dialog, _ ->
                run{
                    // handle without token
                    dialog.dismiss()
                }
            }
            .create(layoutInflater, AlertButton.TOKEN)
            .show()
    }

    private fun callLoginDialog() {
        DialogCaller(requireActivity())
            .setTitle(R.string.login_dialog_title)
            .setLoginButton(R.string.login_dialog_login_btn, object : DialogCaller.OnClickLoginListener{
                override fun onClick(dialog: DialogInterface, email: String?, password: String?) {
                    if(email != null && password != null){
                        userVM.callLoginUser(LoginData(email, password))
                        dialog.dismiss()
                        progressAlertDialog.show()
                    }
                }
            })
            .setGoogleButton(R.string.login_dialog_google_btn, object : DialogCaller.OnClickGoogleListener{
                override fun onClick(dialog: DialogInterface, email: String?, password: String?) {
                    Log.d(TAG, "onClick Google: $email, $password")
                    dialog.dismiss()
                }
            })
            .create(layoutInflater, AlertButton.LOGIN)
            .show()
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }
}