package com.xwl.downloaddemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import com.orhanobut.logger.Logger
import com.xwl.libdownload.DownloadListener
import com.xwl.libdownload.DownloadManager
import com.xwl.libdownload.DownloadStatus
import com.xwl.libdownload.Priority

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val url1 = "https://qiniup-v.cztv.com/cztv/vod/2023/04/17/fb16ccfb3d6143495738b3630d51199f/fb16ccfb3d6143495738b3630d51199f_h264_800k_mp4.mp4"
        val url2 = "https://qiniup-v.cztv.com/cztv/vod/2023/04/20/a8c5cc3cf6508f4d91f83ebf61465e38/a8c5cc3cf6508f4d91f83ebf61465e38_h264_800k_mp4.mp4"
        val url3 = "https://qiniup-v.cztv.com/cztv/vod/2023/04/19/d6c8db6cf4342f445cfacf1f114fd680/d6c8db6cf4342f445cfacf1f114fd680_h264_800k_mp4.mp4"
        val url4 = "https://qiniup-v.cztv.com/cztv/vod/2023/04/19/d6c8db6cf4342f445cfacf1f114fd680/d6c8db6cf4342f445cfacf1f114fd680_h264_800k_mp4.mp4"

        val destination1 = getExternalFilesDir(null)?.absolutePath + "/file1.mp4"
        val destination2 = getExternalFilesDir(null)?.absolutePath + "/file2.mp4"
        val destination3 = getExternalFilesDir(null)?.absolutePath + "/file3.mp4"
        val destination4 = getExternalFilesDir(null)?.absolutePath + "/file4.mp4"

        val downloadButton = findViewById<Button>(R.id.downloadButton)
        val pauseButton = findViewById<Button>(R.id.pauseButton)
        val resumeButton = findViewById<Button>(R.id.resumeButton)

        downloadButton.setOnClickListener {
            DownloadManager.INSTANCE.download(url1, destination1, object : DownloadListener {
                override fun onStart() {
                    Logger.d("Started 1")
                }

                override fun onProgress(downloaded: Long, total: Long) {
                    val progress = if (total > 0) (downloaded * 100 / total).toInt() else 0
                    if(progress %10 ==0){
                        Logger.d("Progress1: $progress%")
                    }
                }

                override fun onComplete() {
                    Logger.d("Completed 1")
                }

                override fun onError(exception: Exception) {
                    Logger.e("Error 1: ${exception.message}")
                }

                override fun onStatusChange(status: DownloadStatus) {

                }
            }, Priority.HIGH)

            DownloadManager.INSTANCE.download(url2, destination2, object : DownloadListener {
                override fun onStart() {
                    Logger.d( "Started 2")
                }

                override fun onProgress(downloaded: Long, total: Long) {
                    val progress = if (total > 0) (downloaded * 100 / total).toInt() else 0
                    if(progress %10 ==0){
                        Logger.d("Progress2: $progress%")
                    }
                }

                override fun onComplete() {
                    Logger.d("Completed 2")
                }

                override fun onError(exception: Exception) {
                    Logger.e( "Error2: ${exception.message}")
                }

                override fun onStatusChange(status: DownloadStatus) {

                }
            }, Priority.LOW)

            DownloadManager.INSTANCE.download(url3, destination3, object : DownloadListener {
                override fun onStart() {
                    Logger.d( "Started3")
                }

                override fun onProgress(downloaded: Long, total: Long) {
                    val progress = if (total > 0) (downloaded * 100 / total).toInt() else 0
                    if(progress %10 ==0){
                        Logger.d("Progress3: $progress%")
                    }
                }

                override fun onComplete() {
                    Logger.d("Completed3")
                }

                override fun onError(exception: Exception) {
                    Logger.e("Error3: ${exception.message}")
                }

                override fun onStatusChange(status: DownloadStatus) {

                }
            }, Priority.MEDIUM)

            DownloadManager.INSTANCE.download(url4, destination4, object : DownloadListener {
                override fun onStart() {
                    Logger.d("Started4")
                }

                override fun onProgress(downloaded: Long, total: Long) {
                    val progress = if (total > 0) (downloaded * 100 / total).toInt() else 0
                    if(progress %10 ==0){
                        Logger.d( "Progress4: $progress%")
                    }
                }

                override fun onComplete() {
                    Log.d("Download", "Completed4")
                }

                override fun onError(exception: Exception) {
                    Logger.e("Error4: ${exception.message}")
                }

                override fun onStatusChange(status: DownloadStatus) {

                }
            }, Priority.HIGH)
        }

        pauseButton.setOnClickListener {
            DownloadManager.INSTANCE.pauseDownload(url1)
            DownloadManager.INSTANCE.pauseDownload(url2)
            DownloadManager.INSTANCE.pauseDownload(url3)
            DownloadManager.INSTANCE.pauseDownload(url4)
        }

        resumeButton.setOnClickListener {
            DownloadManager.INSTANCE.resumeDownload(url1)
            DownloadManager.INSTANCE.resumeDownload(url2)
            DownloadManager.INSTANCE.resumeDownload(url3)
            DownloadManager.INSTANCE.resumeDownload(url4)
        }
    }
}