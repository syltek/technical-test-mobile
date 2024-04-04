package com.playtomic.foundation.storage

import com.playtomic.foundation.extension.tryOrNull
import com.playtomic.foundation.promise.Promise
import java.io.*
import java.lang.Exception

/**
 * Created by agarcia on 09/02/2017.
 */

class FileStorage(private val directory: String) : IFileStorage {
    sealed class Exception : java.lang.Exception {

        constructor()

        constructor(message: String?) : super(message)

        constructor(cause: Exception) : super(cause)

        object notFound : Exception()
        object wrongUrl : Exception()
    }

    @Throws(IOException::class)
    override fun writeData(data: ByteArray, file: String) {
        try {
            synchronized(this) {
                val stream = FileOutputStream(getFile(file))
                stream.write(data)
                stream.flush()
                stream.close()
            }
        } catch (e: FileNotFoundException) {
            throw IOException(e)
        }
    }

    override fun readData(file: String): ByteArray? {
        try {
            synchronized(this) {
                val stream = tryOrNull { FileInputStream(getFile(file)) }
                val data = tryOrNull { stream?.readBytes() }
                stream?.close()
                return data
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun getFile(name: String): File = File(directory, name)

    override fun writeDataAsync(data: ByteArray, file: String): Promise<Unit> =
        Promise(executeInBackground = true) { fulfill, _ ->
            writeData(data, file)
            fulfill(Unit)
        }

    override fun readDataAsync(file: String): Promise<ByteArray> =
        Promise(executeInBackground = true) { fulfill, reject ->
            val data = readData(file)
            if (data != null) {
                fulfill(data)
            } else {
                reject(IOException())
            }
        }

    override fun removeData(file: String) {
        getFile(name = file).delete()
    }

    override fun removeDataAsync(file: String): Promise<Unit> =
        Promise(executeInBackground = true) { fulfill, _ ->
            this.removeData(file = file)
            fulfill(Unit)
        }
}
