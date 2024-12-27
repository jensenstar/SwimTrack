package com.example.swimtrack.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
//import androidx.compose.ui.semantics.text
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.swimtrack.databinding.FragmentHomeBinding
import okhttp3.*
import okhttp3.FormBody
import java.io.IOException
import android.util.Log

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val numberOfTimesInput = binding.numberOfTimesInput.editText
        val poolSizeInput = binding.poolSizeInput.editText
        val distanceInput = binding.distanceInput.editText

        binding.buttonSender.setOnClickListener {
            // Получите данные из полей ввода
            val numberOfTimes = numberOfTimesInput?.text.toString() // Используем numberOfTimesInput
            val poolSize = poolSizeInput?.text.toString()
            val distance = distanceInput?.text.toString()

            // Отправьте данные на сервер
            sendDataToServer(numberOfTimes, poolSize, distance)
        }

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }
    private fun sendDataToServer(numberOfTimes: String, poolSize: String, distance: String) {
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("number_of_times", numberOfTimes)
            .add("pool_size", poolSize)
            .add("distance", distance)
            .build()

        val request = Request.Builder()
            .url("http://192.168.1.6:1880/data")
            .post(requestBody)
            .build()

        Log.d("HomeFragment", "Отправка запроса: $requestBody")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HomeFragment", "Ошибка запроса: ${e.message}", e) // Логирование ошибки
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("HomeFragment", "Ответ получен: ${response.body?.string()}") // Логирование ответа
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}