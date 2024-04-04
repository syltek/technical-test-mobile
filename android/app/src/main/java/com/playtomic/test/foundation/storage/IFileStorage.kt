package com.playtomic.foundation.storage

import com.playtomic.foundation.promise.Promise
import java.io.IOException

/**
 * Created by agarcia on 09/02/2017.
 */

interface IFileStorage {

    @Throws(IOException::class)
    fun writeData(data: ByteArray, file: String)
    fun writeDataAsync(data: ByteArray, file: String): Promise<Unit>

    fun readData(file: String): ByteArray?
    fun readDataAsync(file: String): Promise<ByteArray>

    fun removeData(file: String)
    fun removeDataAsync(file: String): Promise<Unit>
}
