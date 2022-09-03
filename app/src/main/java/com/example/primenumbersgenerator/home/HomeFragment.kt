package com.example.primenumbersgenerator.home

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.NavHostFragment
import com.example.primenumbersgenerator.databinding.FragmentHomeBinding
import java.lang.Integer.parseInt

// Fragment for the number inputs
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by activityViewModels<HomeViewModel>()

    lateinit var start: Editable
    lateinit var end: Editable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Grabs the inputs and if the fields are not empty, trigger the prime number list generation
        binding.calculate.setOnClickListener {
            start = binding.startInput.text
            end = binding.endInput.text

            if (start.isEmpty() || end.isEmpty()) {
                homeViewModel.nullWarning()
            } else {
                homeViewModel.generate(parseInt(start.toString()), parseInt(end.toString()))
            }
        }

        // Displays error message ranges in backwards direction
        homeViewModel.error.asLiveData().observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        // If the inputs are acceptable, proceed to the List Fragment
        homeViewModel.finish.asLiveData().observe(viewLifecycleOwner) {
            val action = HomeFragmentDirections.actionHomeFragmentToListFragment(parseInt(start.toString()), parseInt(end.toString()))
            NavHostFragment.findNavController(this).navigate(action)
        }
    }
}