package cthree.user.flypass.ui.booking

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cthree.user.flypass.R
import cthree.user.flypass.data.Contact
import cthree.user.flypass.data.Traveler
import cthree.user.flypass.databinding.FragmentTravelerDetailsBinding
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "TravelerDetailsFragment"
class TravelerDetailsFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentTravelerDetailsBinding
    private lateinit var listener: OnClickListener
    private val cal = Calendar.getInstance()
    private var traveler: Traveler? = null

    interface OnClickListener{
        fun onClick(traveler: Traveler)
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
        binding = FragmentTravelerDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // set date dialog listener
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // after set we have to update in view
                updateDateInView()
            }

        // set date picker dialog
        binding.etBirthDate.setOnClickListener {
            DatePickerDialog(
                requireActivity(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSave.setOnClickListener {
            if(isValid()){
                val rbChecked           = binding.root.findViewById<RadioButton>(binding.rgTitle.checkedRadioButtonId)
                val title               = rbChecked.text.toString()
                val dateBirth           = binding.etBirthDate.text.toString()
                val surname             = binding.etSurname.text.toString()
                val idCard              = binding.etIdCard.text.toString()
                val name                = binding.etName.text.toString()
                val traveler            = Traveler(title, name, surname, dateBirth, idCard)

                listener.onClick(traveler)
                dismiss()
            }
            removeSetError()
        }

        binding.toolbarLayout.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        if(traveler != null){
            setInput()
        }
        super.onResume()
    }

    override fun onPause() {
        traveler = null
        super.onPause()
    }

    private fun setInput() {
        with(traveler!!){
            binding.etName.setText(name)
            binding.etBirthDate.setText(dateBirth)
            binding.etIdCard.setText(idCard)
            binding.etSurname.setText(surname)
        }
    }

    // update view base on date selected
    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etBirthDate.setText(sdf.format(cal.time))
    }

    private fun isValid() : Boolean{
        val rbChecked       = binding.root.findViewById<RadioButton>(binding.rgTitle.checkedRadioButtonId)
        val title           = rbChecked.text.toString()
        if(title.isNotEmpty() && binding.etBirthDate.text.isNotEmpty() && binding.etName.text.isNotEmpty() && binding.etIdCard.text.isNotEmpty()
            && binding.etSurname.text.isNotEmpty()) return true
        else{
            if(binding.etBirthDate.text.isEmpty()){
                binding.etBirthDate.error = "Fill The Blank"
            }
            if(binding.etSurname.text.isEmpty()){
                binding.etSurname.error = "Fill The Blank"
            }
            if(binding.etName.text.isEmpty()){
                binding.etName.error = "Fill The Blank"
            }
            if(binding.etIdCard.text.isEmpty()){
                binding.etIdCard.error = "Fill The Blank"
            }
            return false
        }
    }


    private fun removeSetError() {
        binding.etBirthDate.setOnClickListener {
            binding.etBirthDate.error = null
        }
        binding.etIdCard.setOnClickListener {
            binding.etIdCard.error = null
        }
        binding.etSurname.setOnClickListener {
            binding.etSurname.error = null
        }
        binding.etName.setOnClickListener {
            binding.etName.error = null
        }
    }

    fun setTraveler(traveler: Traveler) {
        this.traveler = traveler
    }

    override fun onDismiss(dialog: DialogInterface) {
        with(binding){
            etBirthDate.text.clear()
            etName.text.clear()
            etIdCard.text.clear()
            etSurname.text.clear()
        }
        super.onDismiss(dialog)
    }
}
