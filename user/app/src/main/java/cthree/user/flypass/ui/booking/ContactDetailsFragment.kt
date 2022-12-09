package cthree.user.flypass.ui.booking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cthree.user.flypass.R
import cthree.user.flypass.data.Contact
import cthree.user.flypass.databinding.FragmentContactDetailsBinding

class ContactDetailsFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentContactDetailsBinding
    private lateinit var listener: OnClickListener

    interface OnClickListener{
        fun onClick(contact: Contact)
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
    ): View {
        binding = FragmentContactDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbarLayout.ivClose.setOnClickListener {
            dismiss()
        }

        // send date from this fragment to booking fragment
        binding.btnSave.setOnClickListener {
            if(isValid()){
                val rbChecked           = binding.root.findViewById<RadioButton>(binding.rgTitle.checkedRadioButtonId)
                val title               = rbChecked.text.toString()
                val email               = binding.etEmail.text.toString()
                val surname             = binding.etLastName.text.toString()
                val phoneNumber         = binding.etPhoneNumber.text.toString()
                val name                = binding.etFirstName.text.toString()
                val contact             = Contact(name, email, phoneNumber, title, surname)
                listener.onClick(contact)
                dismiss()
            }
        }
        removeSetError()
    }

    private fun isValid() : Boolean{
        val rbChecked       = binding.root.findViewById<RadioButton>(binding.rgTitle.checkedRadioButtonId)
        val title           = rbChecked.text.toString()
        if(title.isNotEmpty() && binding.etEmail.text.isNotEmpty() && binding.etFirstName.text.isNotEmpty() && binding.etPhoneNumber.text.isNotEmpty()
            && binding.etLastName.text.isNotEmpty()) return true
        else{
            if(binding.etEmail.text.isEmpty()){
                binding.etEmail.error = "Fill The Blank"
            }
            if(binding.etLastName.text.isEmpty()){
                binding.etLastName.error = "Fill The Blank"
            }
            if(binding.etFirstName.text.isEmpty()){
                binding.etFirstName.error = "Fill The Blank"
            }
            if(binding.etPhoneNumber.text.isEmpty()){
                binding.etPhoneNumber.error = "Fill The Blank"
            }
            return false
        }
    }
    private fun removeSetError(){
        binding.etEmail.setOnClickListener {
            binding.etEmail.error = null
        }
        binding.etPhoneNumber.setOnClickListener {
            binding.etPhoneNumber.error = null
        }
        binding.etLastName.setOnClickListener {
            binding.etLastName.error = null
        }
        binding.etFirstName.setOnClickListener {
            binding.etFirstName.error = null
        }
    }

}