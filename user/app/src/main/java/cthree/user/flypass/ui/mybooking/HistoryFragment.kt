package cthree.user.flypass.ui.mybooking

import android.content.DialogInterface
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import cthree.user.flypass.R
import cthree.user.flypass.adapter.BookingAdapter
import cthree.user.flypass.data.Contact
import cthree.user.flypass.data.GoogleTokenRequest
import cthree.user.flypass.data.Traveler
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentHistoryBinding
import cthree.user.flypass.models.booking.bookings.Booking
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.BookingStatus
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
    private lateinit var userToken: String
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val resolutionForResult = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        run {
            if(activityResult != null){
                Log.d(TAG, "Activity Result: ${activityResult.data.toString()}")
                val credential = oneTapClient.getSignInCredentialFromIntent(activityResult.data)
                Log.d(TAG, "Credential: ${credential.id}")
                val email = credential.id
                val idToken = credential.googleIdToken
                val username = credential.givenName
                val password = credential.password
                Log.d(TAG, "Got Email: $email")
                Log.d(TAG, "Got ID token -> $idToken")
                Log.d(TAG, "Got password -> $password")
                Log.d(TAG, "Got json -> $username")

                if(idToken != null) userVM.callGoogleIdTokenLogin(GoogleTokenRequest(idToken))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oneTapClient = Identity.getSignInClient(requireContext())
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
        configSignInGoogle()
        progressAlertDialog.show()
        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                val profile = Utils.decodeAccountToken(it)
                // save incoming data
                prefVM.saveToken(it)
                userToken = it
                bookingVM.getUserBooking(it)
                prefVM.saveData(profile)
            }
        }
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                userToken = it.token
                Log.d(TAG, "access token: ${it.token}")
                Log.d(TAG, "refresh token: ${it.refreshToken}")
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
        binding.rvBookingHistory.adapter = adapter
        binding.rvBookingHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        bookingVM.userBookingResponse().observe(viewLifecycleOwner){
            if(it != null){
                if(it.booking.isEmpty()){
                    binding.swipeRefresh.isRefreshing = false
                    binding.notFound.root.isVisible = true
                    progressAlertDialog.dismiss()
                }else{
                    binding.swipeRefresh.isRefreshing = false
                    binding.notFound.root.isVisible = false
                    binding.rvBookingHistory.isVisible = true
                    progressAlertDialog.dismiss()
                    adapter.submitList(it.booking)
                }
            }
        }
        adapter.setOnItemClickListener(object : BookingAdapter.OnItemClickListener{
            override fun onItemClick(booking: Booking) {
                Log.d(TAG, "onItemClick: $booking")
                if(booking.bookingStatus.id == BookingStatus.WAITING.ordinal){
                    continueBookingProcess(booking)
                }else{
                    val directions = MyBookingFragmentDirections.actionMyBookingFragmentToHistorySummaryFragment(booking)
                    findNavController().navigate(directions)
                }
            }
        })

        binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(),R.color.color_primary))
        binding.swipeRefresh.setOnRefreshListener {
            adapter.submitList(listOf())
            bookingVM.getUserBooking(userToken)
        }
    }

    private fun continueBookingProcess(booking: Booking) {
        DialogCaller(requireActivity())
            .setTitle(R.string.continue_booking_process_title)
            .setMessage(R.string.continue_booking_process_msg)
            .setPrimaryButton(R.string.continue_booking_process_btn_continue){ dialog, _ ->
                run{
                    val contact = Contact(
                        firstName = booking.passengerContact.firstName,
                        lastName = booking.passengerContact.lastName,
                        email = booking.passengerContact.email,
                        phoneNumber = booking.passengerContact.phone,
                        title = booking.passengerContact.title
                    )
                    val traveler = booking.passengers.map {
                        Traveler(
                            title = "Tuan",
                            firstName = it.firstName,
                            lastName = it.lastName,
                            dateBirth = "11/11/2001",
                            idCard = it.identityNumber
                        )
                    }.toList()

                    val directions = MyBookingFragmentDirections.actionMyBookingFragmentToPaymentFragment(
                        depFlight = booking.depFlightBooking,
                        arrFlight = booking.arrFlightBooking,
                        flypassCode = booking.bookingCode,
                        contactData = contact,
                        travelerList = traveler.toTypedArray(),
                        bookingId = booking.id
                    )
                    findNavController().navigate(directions)
                    dialog.dismiss()
                }
            }
            .setSecondaryButton(R.string.maybe_later){ dialog, _ ->
                run{
                    dialog.dismiss()
                }
            }
            .create(layoutInflater, AlertButton.TWO)
            .show()
    }


    private fun showSessionExpiredDialog(){
        DialogCaller(requireActivity())
            .setTitle(R.string.token_expired_title)
            .setMessage(R.string.token_expired_subtitle)
            .setPrimaryButton(R.string.token_expired_login){dialog, _ ->
                run{
                    // handle data
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
                    oneTapClient.beginSignIn(signInRequest)
                        .addOnCompleteListener(requireActivity()){ result ->
                            try {
                                Log.d(TAG, "Login Success: ${result.result.pendingIntent}")
                                val intentSenderRequest = IntentSenderRequest.Builder(result.result.pendingIntent).build()
                                resolutionForResult.launch(intentSenderRequest)
                            }catch (e: IntentSender.SendIntentException) {
                                Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                            }
                        }
                        .addOnFailureListener(requireActivity()) { e ->
                            // No saved credentials found. Launch the One Tap sign-up flow, or
                            // do nothing and continue presenting the signed-out UI.
                            Log.d(TAG, e.localizedMessage)
                        }
                }
            })
            .create(layoutInflater, AlertButton.LOGIN)
            .show()
    }

    private fun configSignInGoogle() {
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(requireContext().getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }
}