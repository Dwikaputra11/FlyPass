package cthree.user.flypass.ui.profile

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.adapter.BookingAdapter
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentHistoryProfileBinding
import cthree.user.flypass.databinding.FragmentProfileBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.BookingViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "HistoryProfileFragment"

@AndroidEntryPoint
class HistoryProfileFragment : Fragment() {

    private lateinit var binding: FragmentHistoryProfileBinding
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private val bookingVM: BookingViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private lateinit var userToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        setBottomNav()
        initProgressDialog()
        progressAlertDialog.show()
        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                userToken = it
                userVM.saveToken(it)
                bookingVM.getUserBooking(it)
            }
        }
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                userToken = it.token
                Log.d(TAG, "access token: ${it.token}")

                Log.d(TAG, "token status: ${Utils.isTokenExpired(it.token)}")
                if(Utils.isTokenExpired(it.token)){
                    progressAlertDialog.dismiss()
                    showSessionExpiredDialog()
                }else if(!Utils.isTokenExpired(it.token)){
                    bookingVM.getUserBooking(it.token)
                }
            }
        }
        val adapter = BookingAdapter()
        binding.rvHistoryProfile.adapter = adapter
        binding.rvHistoryProfile.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        bookingVM.userBookingResponse().observe(viewLifecycleOwner){
            if(it != null){
                if(it.booking.isEmpty()){
                    binding.swipeRefresh.isRefreshing = false
                    binding.notFound.root.isVisible = true
                }else{
                    binding.swipeRefresh.isRefreshing = false
                    binding.notFound.root.isVisible = false
                    binding.rvHistoryProfile.isVisible = true
                    adapter.submitList(it.booking)
                }
                progressAlertDialog.dismiss()
            }
        }
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

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Booking"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }
}