package com.xwl.libdownload

import com.orhanobut.logger.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @author  lxw
 * @date 2024/6/15
 * descripe
 */
class DownloadTask(
    val url: String,
    val filePath: String,
    val client: OkHttpClient,
    val listener: DownloadListener,
    val priority: Priority = Priority.MEDIUM
) : Runnable, Comparable<DownloadTask> {
    private val lock = ReentrantLock()
    private val pauseCondition = lock.newCondition()
    @Volatile
    private var paused = false
    @Volatile
    private var cancelled = false
    @Volatile
    private var status: DownloadStatus = DownloadStatus.PENDING

    fun pause() {
        lock.withLock {
            paused = true
            status = DownloadStatus.PAUSED
            listener.onStatusChange(status)
        }
    }

    fun resume() {
        lock.withLock {
            paused = false
            status = DownloadStatus.RESUMED
            listener.onStatusChange(status)
            pauseCondition.signal()
        }
    }

    override fun run() {
        var downloadedBytes = 0L
        var inputStream: InputStream? = null
        val outputStream: RandomAccessFile
        try {
            val file = File(filePath)
            if(!file.exists()) {
                file.parentFile?.mkdirs()
                file.createNewFile()
            } else {
                downloadedBytes = file.length()
            }
            Logger.e(file.absolutePath)
            status = DownloadStatus.STARTED
            listener.onStatusChange(status)

            val request = Request.Builder()
                .url(url)
                .header("Range", "bytes=$downloadedBytes-")
                .build()
            val response = client.newCall(request).execute()

            if(response.code == 416) {
                status = DownloadStatus.COMPLETED
                listener.onStatusChange(status)
                listener.onComplete()
                return
            }

            if (!response.isSuccessful) throw Exception("Failed to download file: ${response.message}")

            inputStream = response.body?.byteStream()
            outputStream = RandomAccessFile(file, "rw")
            outputStream.seek(downloadedBytes)

            inputStream.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    var totalBytesRead = downloadedBytes
                    val fileSize = response.body?.contentLength()?.let { it + downloadedBytes } ?: -1

                    while (input?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
                        lock.withLock {
                            while (paused) {
                                pauseCondition.await()
                            }
                            if (cancelled) {
                                status = DownloadStatus.CANCELLED
                                listener.onStatusChange(status)
                                return
                            }
                        }
                        output.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        listener.onProgress(totalBytesRead, fileSize)
                    }
                    status = DownloadStatus.COMPLETED
                    listener.onStatusChange(status)
                    listener.onComplete()
                }
            }

        } catch (e: Exception) {
            status = DownloadStatus.FAILED
            listener.onStatusChange(status)
            listener.onError(e)
        } finally {
            inputStream?.close()
        }
    }

    override fun compareTo(other: DownloadTask): Int {
         return this.priority.ordinal - other.priority.ordinal
    }
}