package com.xwl.libdownload

import okhttp3.OkHttpClient
import java.util.concurrent.Executors
import java.util.concurrent.PriorityBlockingQueue

/**
 * @author  lxw
 * @date 2024/6/15
 * descripe
 */
class DownloadManager private constructor() {
    private val maxConcurrentDownload = 1
    private val executors = Executors.newFixedThreadPool(maxConcurrentDownload)
    private val client = OkHttpClient()
    private val taskQueue = PriorityBlockingQueue<DownloadTask>()
    private val taskMap = mutableMapOf<String, DownloadController>()
    @Volatile private var isRunning = false

    companion object{
        val INSTANCE: DownloadManager by lazy { DownloadManager() }
    }

    fun download(url: String, destination: String, listener: DownloadListener, priority: Priority = Priority.MEDIUM) {
        val downloadTask = DownloadTask(url, destination, client, listener, priority)
        val controller = DownloadController(downloadTask)
        taskMap[url] = controller
        taskQueue.add(downloadTask)
        startNext()
    }

    fun pauseDownload(url: String) {
        taskMap[url]?.pause()
    }

    fun resumeDownload(url: String) {
        taskMap[url]?.resume()
    }

    private fun startNext() {
        synchronized(this) {
            if (isRunning || taskQueue.isEmpty()) return

            val task = taskQueue.poll()
            executors.submit {
                isRunning = true
                task.run()
                isRunning = false
                startNext()
            }
        }
    }
}