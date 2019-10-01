package com.example.downloadprocess.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.downloadprocess.R
import com.example.downloadprocess.downloads.scheduling.TaskStatus
import com.example.downloadprocess.extensions.nonNullObserve
import com.example.downloadprocess.extensions.round
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val downloadViewModel: DownloadViewModel by viewModel()
    private lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rxPermissions = RxPermissions(this)
        lifecycle.addObserver(downloadViewModel)

        urlText.setText(DOWNLOAD_URL)

        downloadViewModel.apply {
            init(rxPermissions)
            error.nonNullObserve(this@MainActivity) { showToast(it) }
            getDownloadProcess(DOWNLOAD_URL).nonNullObserve(this@MainActivity) { status ->
                when (status) {
                    is TaskStatus.Running ->
                        textViewLoaded.text = "${status.progress.round(2)}%"
                    is TaskStatus.Finished.Failed -> showToast(status.error ?: "Failed")
                    is TaskStatus.Finished.Succeeded -> showToast("Succeeded")
                    is TaskStatus.Pending -> showToast("Pending")
                }
                val visibility = if (status is TaskStatus.Running) View.VISIBLE else View.INVISIBLE
                progressBar.visibility = visibility
                textViewLoaded.visibility = visibility
            }
            btStart.setOnClickListener {
                downloadViewModel.startDownload(urlText.text.toString())
            }
        }
    }

    private fun showToast(it: String) {
        Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val DOWNLOAD_URL =
//            "http://demo.astra-net.com/download/ASTRA-Proxy_Full_v1.01.0000.zip" // 7.56M
            "http://demo.astra-net.com/download/CentOS-6.3-x86_64-bin-DVD1.iso" // 69M
//            "http://demo.astra-net.com/download/FC3-i386-disc1.iso" // 646M
    }
}
