package com.anemonesdk.general.client

/**
 * Created by Cecilia on 28/05/2021
 * Copyright (c) 2020 Playtomic.  All rights reserved. */

data class MultipartBody(
    val multipartBodyParams: List<MultipartBodyParam>,
    val multipartDataFiles: List<MultipartDataFile>
)

data class MultipartBodyParam(
    val name: String,
    val params: Any
)

class MultipartDataFile(
    val name: String,
    dataFile: DataFile
) {
    val contentType: String = dataFile.contentType

    val extensionFileName: String = dataFile.extensionFileName

    var fileName: String = dataFile.name

    var data: ByteArray = dataFile.data
}

sealed class DataFile {
    data class JpegPicture(val fileName: String, val file: ByteArray) : DataFile() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as JpegPicture
            if (fileName != other.fileName) return false
            if (!file.contentEquals(other.file)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = fileName.hashCode()
            result = 31 * result + file.contentHashCode()
            return result
        }
    }

    val contentType: String
        get() {
            return when (this) {
                is JpegPicture -> "image/jpeg"
            }
        }

    val extensionFileName: String
        get() {
            return when (this) {
                is JpegPicture -> ".jpeg"
            }
        }

    val data: ByteArray
        get() {
            return when (this) {
                is JpegPicture -> this.file
            }
        }

    val name: String
        get() {
            return when (this) {
                is JpegPicture -> fileName
            }
        }
}
