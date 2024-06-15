package com.example.abbfinalprojectweatherapp.location

import android.content.Context
import android.hardware.input.InputManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.abbfinalprojectweatherapp.data.RemoteLocation
import com.example.abbfinalprojectweatherapp.databinding.FragmentLocationBinding
import com.example.abbfinalprojectweatherapp.home.HomeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.http.Query

class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val locationViewModel: LocationViewModel by viewModel()

    private val locationAdapter = LocationAdapter(onLocationClicked = {remoteLocation ->
        setLocation(remoteLocation)
    })

    private fun setLocation(remoteLocation: RemoteLocation) {
        with(remoteLocation){
            val locationText="$name,$region,$country"
            setFragmentResult(
                requestKey = HomeFragment.REQUEST_KEY_MANUAL_LOCATION_SEARCH,
                result = bundleOf(
                    HomeFragment.KEY_LOCATION_TEXT to locationText,
                    HomeFragment.KEY_LATITUDE to lat,
                    HomeFragment.KEY_LONGITUDE to lon
                )
            )
            findNavController().popBackStack()
        }

    }

    private fun setupLocationsRecyclerView(){
        with(binding.locationsRecyclerview){
            addItemDecoration(DividerItemDecoration(requireContext(),RecyclerView.VERTICAL))
            adapter= locationAdapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setupLocationsRecyclerView()
        setObservers()
    }

    private fun setListeners() {
        binding.imageClose.setOnClickListener { findNavController().popBackStack() }
        binding.inputSearch.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideSoftKeyboard()
                val query = binding.inputSearch.editText?.text
                if (query.isNullOrBlank()) return@setOnEditorActionListener true
                searchLocation(query.toString())
            }
            return@setOnEditorActionListener true
        }
    }

        private fun searchLocation(query: String) {
            locationViewModel.searchLocation(query)
        }



    private fun setObservers(){
        locationViewModel.seacrhResult.observe(viewLifecycleOwner){
            val searchResultDataState = it?:return@observe
            if (searchResultDataState.isLoading){
                binding.locationsRecyclerview.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE
            }
            searchResultDataState.locations?.let { remoteLocations ->
                binding.locationsRecyclerview.visibility = View.VISIBLE
                locationAdapter.setData(remoteLocations)
            }
            searchResultDataState.error?.let { error->
                Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
            }
        }
    }

        private fun hideSoftKeyboard() {
            val inputManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                binding.inputSearch.editText?.windowToken, 0
            )
        }
    }

