package cthree.user.flypass.ui.wishlist

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.adapter.WishlistAdapter
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentWishlistBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import cthree.user.flypass.viewmodels.WishlistViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "WishlistFragment"

@AndroidEntryPoint
class WishlistFragment : Fragment() {

    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var binding: FragmentWishlistBinding
    private val adapter: WishlistAdapter = WishlistAdapter()
    private val wishlistVM: WishlistViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private var isFirstFetch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Started")
    }

    override fun onPause() {
        super.onPause()
        adapter.submitList(null)
        Log.d(TAG, "onPause: Started")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishlistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressDialog()
        setViews()
        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                prefVM.saveToken(it)
                binding.rvMovie.isVisible = false
                binding.progressBar.isVisible = true
                wishlistVM.getUserWishlist(it)
                progressAlertDialog.dismiss()
            }
        }
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty() && isFirstFetch){
                if(!Utils.isTokenExpired(it.token.toString())){
                    binding.rvMovie.isVisible = false
                    binding.progressBar.isVisible = true
                    Log.d(TAG, "Adapter count: ${adapter.getList().isEmpty()}")
                    if(adapter.getList().isEmpty()) wishlistVM.getUserWishlist(it.token)
                }else{
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
                                dialog.dismiss()
                            }
                        }
                        .create(layoutInflater, AlertButton.TOKEN)
                        .show()
                }
            }else{
                // unregistered user
//                DialogCaller(requireActivity())
            }
        }
        wishlistVM.getAllWishlist().observe(viewLifecycleOwner){
            if(it != null){
                binding.rvMovie.isVisible = true
                binding.progressBar.isVisible = false
                adapter.submitList(it.wishlistItem)
            }
        }
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

    private fun setViews() {
        binding.rvMovie.adapter = adapter
        binding.rvMovie.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }
}