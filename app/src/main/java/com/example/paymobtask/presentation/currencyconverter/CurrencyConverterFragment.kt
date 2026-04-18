package com.example.paymobtask.presentation.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.paymobtask.databinding.FragmentConverterBinding
import com.example.paymobtask.domain.model.remote.Currency
import com.example.paymobtask.domain.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrencyConverterFragment : Fragment() {

    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CurrencyConverterViewModel by viewModels()

    private var isSwapping = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConverterBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnHistory.setOnClickListener {
            val from = viewModel.getSelectedFromCode() ?: return@setOnClickListener
            val to = viewModel.getSelectedToCode() ?: return@setOnClickListener
            val action =
                CurrencyConverterFragmentDirections.actionConverterFragmentToHistoryFragment(
                    from = from,
                    to = to
                )
            findNavController().navigate(action)
        }
        collectCurrenciesState()
        collectErrorEvents()
        collectConvertedValue()
        collectIsConverting()
        setupAmountInput()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectCurrenciesState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currenciesState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.contentGroup.visibility = View.GONE
                            binding.errorLayout.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentGroup.visibility = View.VISIBLE
                            binding.errorLayout.visibility = View.GONE
                            initSpinners(state.data)
                        }

                        is Resource.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentGroup.visibility = View.GONE
                            binding.errorLayout.visibility = View.VISIBLE
                            binding.tvErrorMessage.text = state.exception.message
                        }
                    }
                }
            }
        }
    }

    private fun collectErrorEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorEvent.collect { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                        .setAction("Dismiss") { /* auto-dismiss */ }
                        .show()
                }
            }
        }
    }

    private fun initSpinners(currencies: List<Currency>) {
        val adapter = CurrencySpinnerAdapter(requireContext(), currencies)

        binding.spinnerFrom.adapter = adapter
        binding.spinnerTo.adapter = adapter
        binding.spinnerFrom.setSelection(viewModel.fromIndex.value)
        binding.spinnerTo.setSelection(viewModel.toIndex.value)
        binding.spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isSwapping) viewModel.onFromCurrencySelected(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isSwapping) viewModel.onToCurrencySelected(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setupAmountInput() {
        binding.etAmount.filters = arrayOf(
            DecimalDigitsInputFilter(7, 2)
        )
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.onAmountChanged(s?.toString() ?: "1")
            }
        })
    }

    private fun collectConvertedValue() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.convertedValue.collect { value ->
                    if (binding.etConvertedAmount.text.toString() != value) {
                        binding.etConvertedAmount.setText(value)
                    }
                }
            }
        }
    }

    private fun collectIsConverting() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isConverting.collect { isLoading ->
                    binding.convertingProgress.visibility =
                        if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
    }
}