package cthree.user.flypass.ui.dialog

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.databinding.DialogLoginBinding
import cthree.user.flypass.databinding.DialogOneButtonAlertBinding
import cthree.user.flypass.databinding.DialogTokenExpiredBinding
import cthree.user.flypass.databinding.DialogTwoButtonAlertBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.utils.AlertButton
import kotlin.math.log


class DialogCaller(val context: Activity) {
    private lateinit var firstBtnListener: OnClickListener
    private lateinit var secondBtnListener: OnClickListener
    private lateinit var thirdBtnListener: OnClickListener
    private lateinit var loginListener: OnClickLoginListener
    private lateinit var googleListener: OnClickGoogleListener
    private var title: String? = null
    private var message: String? = null
    private var firstButtonTitle: String? = null
    private var secondButtonTitle: String? = null
    private var thirdButtonTitle: String? = null
    private var showIcon = false
    private var cancellable = false

    interface OnClickLoginListener{
        fun onClick(dialog: DialogInterface,email: String?, password: String?)
    }
    interface OnClickGoogleListener{
        fun onClick(dialog: DialogInterface,email: String?, password: String?)
    }

    fun setLoginButton(@StringRes title: Int,listener: OnClickLoginListener) : DialogCaller{
        this.title = context.getString(title)
        this.loginListener = listener
        return this
    }
    fun setGoogleButton(@StringRes title: Int,listener: OnClickGoogleListener) : DialogCaller{
        this.title = context.getString(title)
        this.googleListener = listener
        return this
    }


    fun expiredTokenDialog(
        layoutInflater: LayoutInflater,
        context: Context,
        loginListener: OnClickListener,
        registerListener: OnClickListener,
        laterListener: OnClickListener
    ) {
        val notEnoughBinding = DialogTokenExpiredBinding.inflate(layoutInflater, null, false)
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)

        materialAlertDialogBuilder.setView(notEnoughBinding.root)

        val materAlertDialog = materialAlertDialogBuilder.create()
        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        materAlertDialog.show()
    }
    fun setTitle(@StringRes title: Int): DialogCaller {
        this.title = context.getString(title)
        return this
    }

    fun setTitle(title: String): DialogCaller? {
        this.title = title
        return this
    }

    fun setMessage(@StringRes message: Int): DialogCaller {
        this.message = context.getString(message)
        return this
    }

    fun setMessage(message: String): DialogCaller {
        this.message = message
        return this
    }

    fun setShowIcon(): DialogCaller {
        showIcon = true
        return this
    }

    fun setPrimaryButton(@StringRes title: Int, listener: OnClickListener): DialogCaller {
        firstButtonTitle = context.getString(title)
        firstBtnListener = listener
        return this
    }

    fun setSecondaryButton(@StringRes title: Int, listener: OnClickListener): DialogCaller {
        secondButtonTitle = context.getString(title)
        secondBtnListener = listener
        return this
    }
    fun setThirdButton(@StringRes title: Int, listener: OnClickListener): DialogCaller {
        thirdButtonTitle = context.getString(title)
        thirdBtnListener = listener
        return this
    }

    fun setCancellable(cancellable: Boolean): DialogCaller {
        this.cancellable = cancellable
        return this
    }

    fun create(layoutInflater: LayoutInflater, alertButton: AlertButton): AlertDialog {
        val binding =
            when (alertButton) {
                AlertButton.ONE -> DialogOneButtonAlertBinding.inflate(layoutInflater, null, false)
                AlertButton.TWO -> DialogTwoButtonAlertBinding.inflate(layoutInflater, null, false)
                AlertButton.LOGIN -> DialogLoginBinding.inflate(layoutInflater, null, false)
                else -> DialogTokenExpiredBinding.inflate(layoutInflater, null, false)
            }
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
        materialAlertDialogBuilder.setView(binding.root)
        val materAlertDialog = materialAlertDialogBuilder.create()

        // get your custom views here and configure them based on given settings (field values of this class)
        when (alertButton) {
            AlertButton.ONE -> {
                (binding as DialogOneButtonAlertBinding).tvTitle.text = title
                binding.tvSubtitle.text = message
                binding.btnYes.text = firstButtonTitle
                binding.btnYes.setOnClickListener {
                    firstBtnListener.onClick(materAlertDialog, 1)
                }
            }
            AlertButton.TWO -> {
                (binding as DialogTwoButtonAlertBinding).tvTitle.text = title
                binding.tvSubtitle.text = message
                binding.btnYes.text = firstButtonTitle
                binding.tvNo.text = secondButtonTitle
                binding.btnYes.setOnClickListener {
                    firstBtnListener.onClick(materAlertDialog, 1)
                }
                binding.tvNo.setOnClickListener {
                    secondBtnListener.onClick(materAlertDialog, 2)
                }
            }
            AlertButton.TOKEN -> {
                (binding as DialogTokenExpiredBinding).tvTitle.text = title
                binding.tvSubtitle.text = message
                binding.btnLogin.text = firstButtonTitle
                binding.btnLater.text = secondButtonTitle
                binding.btnLogin.setOnClickListener {
                    firstBtnListener.onClick(materAlertDialog, 1)
                }
                binding.btnLater.setOnClickListener {
                    secondBtnListener.onClick(materAlertDialog, 2)
                }
            }
            else -> {
                materAlertDialog.setCanceledOnTouchOutside(false)
                (binding as DialogLoginBinding).btnLogin.setOnClickListener {
                    val email = binding.loginEmail.text.toString()
                    val password = binding.loginPassword.text.toString()
                    if(email.isEmpty() || password.isEmpty()) {
                        binding.loginEmail.error = "Field Masih Kosong"
                        binding.loginPassword.error = "Field Masih Kosong"
                        loginListener.onClick(materAlertDialog, null, null)
                    }else{
                        loginListener.onClick(materAlertDialog, email, password)
                    }
                }
                binding.btnGoogle.setOnClickListener {
                    val email = binding.loginEmail.text.toString()
                    val password = binding.loginPassword.text.toString()

                    if(email.isEmpty() || password.isEmpty()) {
                        binding.loginEmail.error = "Field Masih Kosong"
                        binding.loginPassword.error = "Field Masih Kosong"
                        googleListener.onClick(materAlertDialog, null, null)
                    }else{
                        googleListener.onClick(materAlertDialog, email, password)
                    }
                }
            }
        }
//        val materAlertDialog = materialAlertDialogBuilder.create()
        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return materAlertDialog
    }
}