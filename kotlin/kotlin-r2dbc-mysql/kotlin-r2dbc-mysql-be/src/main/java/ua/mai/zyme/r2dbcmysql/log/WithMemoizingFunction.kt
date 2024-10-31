package ua.mai.zyme.r2dbcmysql.log

import org.slf4j.Logger
import org.springframework.core.io.buffer.DataBuffer
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.channels.Channels
import java.util.function.Function

interface WithMemoizingFunction {

    companion object {
        val EMPTY_BYTE_ARRAY_OUTPUT_STREAM = object : ByteArrayOutputStream(0) {
            override fun write(b: Int) {
                throw UnsupportedOperationException("stub")
            }

            override fun write(b: ByteArray, off: Int, len: Int) {
                throw UnsupportedOperationException("stub")
            }

            override fun writeTo(out: OutputStream) {
                throw UnsupportedOperationException("stub")
            }
        }
    }

    fun getLogger(): Logger

    fun memoizingFunction(baos: ByteArrayOutputStream): Function<DataBuffer, DataBuffer> {
        return Function { dataBuffer ->
            try {
                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
            } catch (e: IOException) {
                getLogger().error("Unable to log input request due to an error", e)
            }
            dataBuffer
        }
    }

}