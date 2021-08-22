package com.example.nagwatask.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nagwatask.R
import com.example.nagwatask.adapter.OnRecyclerItemClickListener
import com.example.nagwatask.adapter.RecyclerItemsAdapter
import com.example.nagwatask.databinding.ActivityMainBinding
import com.example.nagwatask.model.DownloadResult
import com.example.nagwatask.model.ItemModel
import com.example.nagwatask.util.Utils.downloadUsingConnection
import com.example.nagwatask.util.Utils.downloadUsingConnectionRx
import com.example.nagwatask.util.Utils.openFile
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import java.io.File
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RecyclerItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(get())).get(MainViewModel::class.java)
        binding.viewModel = mainViewModel
        adapter = RecyclerItemsAdapter(ArrayList(), object : OnRecyclerItemClickListener {
            override fun onRecyclerItemClickListener(item: ItemModel) {
                var extention: String = when (item.type) {
                    "VIDEO" -> {
                        ".mp4"
                    }
                    "PDF" -> {
                        ".pdf"
                    }
                    else -> {
                        ""
                    }
                }
                var file =
                    File(
                        this@MainActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                        item.name + extention
                    )
                when {
                    item.isLoading -> {
                        //Do nothing
                    }
                    file.exists() -> openFile(file)
                    else -> {
                        try {
                            downloadWithHttpRx(item, file)
                        } catch (e: Exception) {
                            //generic error while downloading
                        }
                    }
                }
            }
        })
        binding.rvJobs.adapter = adapter
        observeViewModel()
        lifecycleScope.launch {
            mainViewModel.userIntent.send(ItemsIntent.FetchItems)
        }

        binding.btnFakeData.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.userIntent.send(ItemsIntent.FetchFakeItems)
            }
        }
        binding.btnTryAgain.setOnClickListener {

        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            mainViewModel.state.collect {
                when (it) {
                    is ItemsState.Idle -> {

                    }
                    is ItemsState.Loading -> {
                        binding.progressbar.visibility = View.VISIBLE
                    }

                    is ItemsState.Items -> {
                        binding.progressbar.visibility = View.GONE
                        binding.layoutError.visibility = View.GONE
                        binding.rvJobs.visibility = View.VISIBLE
                        adapter.setList(it.items)
                        binding.invalidateAll()
                    }
                    is ItemsState.Error -> {
                        binding.tvErrorMessage.text = it.error
                        binding.progressbar.visibility = View.GONE
                        binding.rvJobs.visibility = View.GONE
                        binding.layoutError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun downloadWithHttpFlow(dummy: ItemModel, file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            downloadUsingConnection(file, dummy.url).collect {
                withContext(Dispatchers.Main) {
                    when (it) {
                        is DownloadResult.Success -> {
                            adapter.setDownloading(dummy, false)
                        }
                        is DownloadResult.Error -> {
                            adapter.setDownloading(dummy, false)
                            Toast.makeText(
                                this@MainActivity,
                                "Error while downloading ${dummy.name}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is DownloadResult.Progress -> {
                                adapter.setProgress(dummy, it.progress,it.type)
                        }
                    }
                }
            }
        }
    }
    private fun downloadWithHttpRx(dummy: ItemModel, file: File) {
        downloadUsingConnectionRx(file,dummy.url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is DownloadResult.Success -> {
                        adapter.setDownloading(dummy, false)
                    }
                    is DownloadResult.Error -> {
                        adapter.setDownloading(dummy, false)
                        Toast.makeText(
                            this@MainActivity,
                            "Error while downloading ${dummy.name}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is DownloadResult.Progress -> {
                        adapter.setProgress(dummy, it.progress,it.type)
                    }
                }
            }
    }



}