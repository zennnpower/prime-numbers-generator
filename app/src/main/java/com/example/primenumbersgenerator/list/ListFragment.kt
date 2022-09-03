package com.example.primenumbersgenerator.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.primenumbersgenerator.databinding.FragmentListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Integer.parseInt

// Fragment for displaying the list of prime numbers
class ListFragment : Fragment() {
    lateinit var binding: FragmentListBinding
    lateinit var nums_array: TextView
    private val listViewModel: ListViewModel by activityViewModels<ListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nums_array = binding.numsArray

        val start = parseInt(arguments?.get("start").toString())
        val end = parseInt(arguments?.get("end").toString())

        listViewModel.loading.asLiveData().observe(viewLifecycleOwner) {
            // Remove the loading cycle when the calculations are complete
            Log.i("listfragment", "_loading was changed!")

            binding.loading.visibility = View.GONE
            binding.numbersTitle.text = "Prime Numbers (${start} to ${end}):"
        }

        listViewModel.emptyArr.asLiveData().observe(viewLifecycleOwner) {
            // Display "No prime number found" for a range where (start = end)
            Log.i("listfragment", "_emptyArr was changed!")
            binding.noPrimes.visibility = View.VISIBLE
        }

        lifecycleScope.launch {
            // Display the prime numbers using the nums_array TextView
            val result = listViewModel.generateNumbers(start, end)
            nums_array.text = result.joinToString(separator = ",   ")
        }

        binding.back.setOnClickListener {

            NavHostFragment.findNavController(this).popBackStack()
        }
    }
}
