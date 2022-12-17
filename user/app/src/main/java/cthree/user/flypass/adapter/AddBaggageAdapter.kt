package cthree.user.flypass.adapter

//noinspection SuspiciousImport
import android.R.*
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import cthree.user.flypass.R
import cthree.user.flypass.data.PassengerBaggage
import cthree.user.flypass.databinding.AddBaggageItemBinding

private const val TAG = "AddBaggageAdapter"
class AddBaggageAdapter(private val passengerAmount: Int):RecyclerView.Adapter<AddBaggageAdapter.ViewHolder>() {

//    private val travelerBaggage = listOf(
//        Baggage(1, 20,0),
//        Baggage(2, 20,0)
//    )
    private val travelerBaggage: MutableList<PassengerBaggage> = mutableListOf()
    private val list: MutableList<String> = mutableListOf()
    private var baggageWeight = 0
    private var baggagePrice = 0


    class ViewHolder(val binding: AddBaggageItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AddBaggageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = holder.absoluteAdapterPosition
        val baggageList = holder.binding.root.resources.getStringArray(R.array.baggage_list)
        val adapter = ArrayAdapter(holder.binding.root.context, layout.simple_spinner_item, baggageList)
        holder.binding.spBaggage.adapter = adapter
        adapter.setDropDownViewResource(layout.simple_spinner_dropdown_item)
        holder.binding.tvNameTraveler.text = "Adult ${position + 1}"

        holder.binding.spBaggage.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                Log.d(TAG, "onItemSelected: $pos")
                when(pos){
                    0 ->{
                        baggageWeight = 20
                    }
                    1 ->{
                        baggageWeight = 25
                    }
                    2 ->{
                        baggageWeight = 30
                    }
                    3 ->{
                        baggageWeight = 40
                    }
                }
                val passengerBaggage = PassengerBaggage(mutableListOf(baggageWeight.toString()))
                if(travelerBaggage.isEmpty()){
                    travelerBaggage.add(itemPosition,passengerBaggage)
                }else{
                    if(travelerBaggage.indices.contains(itemPosition)){
                        travelerBaggage[itemPosition] = passengerBaggage
                    }else{
                        travelerBaggage.add(itemPosition,passengerBaggage)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    override fun getItemCount(): Int {
        return passengerAmount
    }

    fun getTravelerBaggage(): List<PassengerBaggage> = travelerBaggage
}