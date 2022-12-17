package cthree.user.flypass.ui.mybooking

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cthree.user.flypass.adapter.BookingAdapter
import cthree.user.flypass.databinding.FragmentSearchBinding
import cthree.user.flypass.models.booking.bookings.Booking
import cthree.user.flypass.viewmodels.BookingViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SearchFragment"

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val bookingViewModel : BookingViewModel by viewModels()
    private val bookingAdapter: BookingAdapter = BookingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.svBookCode.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.progressBar.isVisible = true
                binding.rvFlights.isVisible = false
                if (query != null) {
                    bookingViewModel.searchBookingCode(query)
                }
                Log.d(TAG, "onQueryTextSubmit: $query")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        bookingViewModel.getSearchBooking().observe(viewLifecycleOwner){
            if(it != null){
                binding.progressBar.isVisible = false
                binding.rvFlights.isVisible = true
                binding.rvFlights.adapter = bookingAdapter
                bookingAdapter.submitList(it.booking)
                binding.rvFlights.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
        }
        bookingAdapter.setOnItemClickListener(object : BookingAdapter.OnItemClickListener{
            override fun onItemClick(booking: Booking) {
                Log.d(TAG, "onItemClick: $booking")
            }
        })
    }
}