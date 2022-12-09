package cthree.admin.flypass.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.FragmentUserAccountBinding
import cthree.admin.flypass.viewmodels.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAccountFragment : Fragment() {

    lateinit var binding : FragmentUserAccountBinding
    private val adminVM: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        showDataUser()
    }

//    private fun showDataUser(){
//        viewModel.getLiveDataFilem().observe(this, Observer {
//            if (it != null){
//                binding.rvListFilm.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//                val adapter = FilmAdapter(it)
//                binding.rvListFilm.adapter = adapter
//                adapter.onDeleteClick = {
//                    deleteFilm(it.id.toInt())
//                }
//                adapter.notifyDataSetChanged()
//                adapter.onFavoriteClick = {
//                    viewModelFavorite.callPostApiFilm(it.name, it.image, it.director, it.description)
//                    viewModelFavorite.postLiveDataFilm().observe(this,{
//                        if(it != null){
//                            Toast.makeText(this,"Tambah Favorit Berhasil", Toast.LENGTH_SHORT).show()
//                        }
//                    })
//                }
//                adapter.notifyDataSetChanged()
//            }
//        })
//        viewModel.callApiFilm()
//    }
}