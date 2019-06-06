/*
 * The MIT License
 *
 * Copyright 2017-2018 Norwegian University of Technology
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING  FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package no.ntnu.ihb.fmi4j.importer.fmi1.jni

import no.ntnu.ihb.fmi4j.*
import no.ntnu.ihb.fmi4j.importer.fmi2.jni.Fmi2Library
import no.ntnu.ihb.fmi4j.modeldescription.StringArray
import no.ntnu.ihb.fmi4j.modeldescription.ValueReference
import no.ntnu.ihb.fmi4j.modeldescription.ValueReferences
import no.ntnu.ihb.fmi4j.util.ArrayBuffers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable

internal typealias NativeStatus = Int
internal typealias FmiComponent = Long

/**
 * @author Lars Ivar Hatledal
 */
open class Fmi1Library(
        libName: String,
        modelIdentifier: String
) : Closeable {

    protected val p: Long = load(libName, modelIdentifier)
    private var isClosed = false

    override fun close() {
        if (!isClosed) {
            free(p).also {
                val msg = if (it) "successfully" else "unsuccessfully"
                LOG.debug("Freed native library resources $msg!")
            }
            isClosed = true
        }
    }

    protected fun finalize() {
        if (!isClosed) {
            LOG.warn("FMU was not closed prior do garbage collection! Doing it for you..")
            close()
        }
    }

    private external fun load(libName: String, modelIdentifier: String): Long

    private external fun free(p: Long): Boolean

    private external fun getVersion(p: Long): String

    private external fun getTypesPlatform(p: Long): String

    private external fun instantiate(p: Long, instanceName: String, guid: String,
                                     fmuLocation: String, mimeType: String, visible: Boolean, interactive: Boolean, loggingOn: Boolean): Long

    private external fun setup(p: Long, c: FmiComponent, startTime: Double, stopTime: Double): NativeStatus

    private external fun terminate(p: Long, c: FmiComponent): NativeStatus

    private external fun reset(p: Long, c: FmiComponent): NativeStatus

    private external fun freeInstance(p: Long, c: FmiComponent)

    private external fun getInteger(p: Long, c: FmiComponent, vr: ValueReferences, ref: IntArray): NativeStatus
    private external fun getReal(p: Long, c: FmiComponent, vr: ValueReferences, ref: DoubleArray): NativeStatus
    private external fun getString(p: Long, c: FmiComponent, vr: ValueReferences, ref: Array<String>): NativeStatus
    private external fun getBoolean(p: Long, c: FmiComponent, vr: ValueReferences, ref: BooleanArray): NativeStatus


    private external fun setInteger(p: Long, c: FmiComponent, vr: ValueReferences, values: IntArray): NativeStatus
    private external fun setReal(p: Long, c: FmiComponent, vr: ValueReferences, values: DoubleArray): NativeStatus
    private external fun setString(p: Long, c: FmiComponent, vr: ValueReferences, values: Array<String>): NativeStatus
    private external fun setBoolean(p: Long, c: FmiComponent, vr: ValueReferences, values: BooleanArray): NativeStatus


    protected fun NativeStatus.transform(): FmiStatus {
        return FmiStatus.valueOf(this)
    }

    fun getVersion(): String {
        return getVersion(p)
    }

    fun getTypesPlatform(): String {
        return getTypesPlatform(p)
    }

    fun instantiate(instanceName: String, guid: String,
                    fmuLocation: String, visible: Boolean, interactive: Boolean, loggingOn: Boolean): Long {
        return instantiate(p, instanceName, guid, fmuLocation, "application/x-fmu-sharedlibrary", visible, interactive, loggingOn)
    }

    fun setup(c: FmiComponent, startTime: Double, stopTime: Double): FmiStatus {
        return setup(p, c, startTime, stopTime).transform()
    }

    fun terminate(c: FmiComponent): FmiStatus {
        return terminate(p, c).transform()
    }

    fun reset(c: FmiComponent): FmiStatus {
        return reset(p, c).transform()
    }

    fun freeInstance(c: FmiComponent) {
        freeInstance(p, c)
    }

    fun getInteger(c: FmiComponent, vr: ValueReferences, ref: IntArray): FmiStatus {
        return getInteger(p, c, vr, ref).transform()
    }

    fun getReal(c: FmiComponent, vr: ValueReferences, ref: DoubleArray): FmiStatus {
        return getReal(p, c, vr, ref).transform()
    }

    fun getString(c: FmiComponent, vr: ValueReferences, ref: Array<String>): FmiStatus {
        return getString(p, c, vr, ref).transform()
    }

    fun getBoolean(c: FmiComponent, vr: ValueReferences, ref: BooleanArray): FmiStatus {
        return getBoolean(p, c, vr, ref).transform()
    }


    fun setInteger(c: FmiComponent, vr: ValueReferences, values: IntArray): FmiStatus {
        return setInteger(p, c, vr, values).transform()
    }

    fun setReal(c: FmiComponent, vr: ValueReferences, values: DoubleArray): FmiStatus {
        return setReal(p, c, vr, values).transform()
    }

    fun setString(c: FmiComponent, vr: ValueReferences, values: Array<String>): FmiStatus {
        return setString(p, c, vr, values).transform()
    }

    fun setBoolean(c: FmiComponent, vr: ValueReferences, values: BooleanArray): FmiStatus {
        return setBoolean(p, c, vr, values).transform()
    }

    private companion object {

        val LOG: Logger = LoggerFactory.getLogger(Fmi1Library::class.java)

        init {
            FMI4j.init()
        }

    }

}

/**
 * @author Lars Ivar Hatledal
 */
abstract class Fmi1LibraryWrapper<E : Fmi1Library>(
        protected var c: Long,
        library: E
) : FmuVariableAccessor {

    private val buffers: ArrayBuffers by lazy {
        ArrayBuffers()
    }

    private var _library: E? = library

    protected val library: E
        get() = _library ?: throw IllegalAccessException("Library is no longer accessible!")

    val isInstanceFreed: Boolean
        get() = _library == null

    /**
     * The status returned from the last call to a FMU function
     */
    var lastStatus: FmiStatus = FmiStatus.NONE
        private set

    /**
     * Has terminate been called on the FMU?
     */
    var isTerminated: Boolean = false
        private set


    protected fun updateStatus(status: FmiStatus): FmiStatus {
        return status.also { lastStatus = it }
    }

    /**
     * @see Fmi2Library.getTypesPlatform
     */
    val typesPlatform: String
        get() = library.getTypesPlatform()

    /**
     *
     * @see Fmi2Library.getVersion()
     */
    val version: String
        get() = library.getVersion()


    fun instantiate(instanceName: String, guid: String,
                    fmuLocation: String, visible: Boolean, interactive: Boolean, loggingOn: Boolean): Long {
        return library.instantiate(instanceName, guid, fmuLocation, visible, interactive, loggingOn)
    }

    fun setup(startTime: Double, stopTime: Double): FmiStatus {
        return library.setup(c, startTime, stopTime)
    }

    /**
     * @see Fmi2Library.terminate
     */
    @JvmOverloads
    fun terminate(freeInstance: Boolean = true): FmiStatus {

        if (!isTerminated) {

            return try {
                updateStatus(library.terminate(c))
            } catch (ex: Error) {
                LOG.error("Error caught on fmi2Terminate: ${ex.javaClass.simpleName}")
                updateStatus(FmiStatus.OK)
            } finally {
                isTerminated = true
                if (freeInstance) {
                    freeInstance()
                }
            }

        } else {
            LOG.warn("Terminated has already been called..")
            return FmiStatus.OK
        }
    }

    /**
     * @see Fmi2Library.freeInstance
     */
    internal fun freeInstance() {
        if (!isInstanceFreed) {
            var success = false
            try {
                library.freeInstance(c)
                success = true
            } catch (ex: Error) {
                LOG.error("Error caught on fmi2FreeInstance: ${ex.javaClass.simpleName}")
            } finally {
                val msg = if (success) "successfully" else "unsuccessfully"
                LOG.debug("Instance freed $msg")
                _library = null
                System.gc()
            }
        }
    }

    /**
     * @see Fmi2Library.reset
     */
    fun reset(): FmiStatus {
        return updateStatus(library.reset(c)).also { status ->
            if (status == FmiStatus.OK) {
                isTerminated = false
            }
        }
    }

    /**
     * @see Fmi2Library.getInteger
     */
    @Synchronized
    fun readInteger(valueReference: ValueReference): FmuIntegerRead {
        return with(buffers) {
            vr[0] = valueReference
            library.getInteger(c, vr, iv).let {
                FmuIntegerRead(iv[0], updateStatus(it))
            }
        }
    }

    /**
     * @see Fmi2Library.getInteger
     */
    override fun read(vr: ValueReferences, ref: IntArray): FmiStatus {
        return library.getInteger(c, vr, ref).let { updateStatus(it) }
    }

    /**
     * @see Fmi2Library.getReal
     */
    @Synchronized
    fun readReal(valueReference: ValueReference): FmuRealRead {
        return with(buffers) {
            vr[0] = valueReference
            library.getReal(c, vr, rv).let {
                FmuRealRead(rv[0], updateStatus(it))
            }
        }
    }

    /**
     * @see Fmi2Library.getReal
     */
    override fun read(vr: ValueReferences, ref: DoubleArray): FmiStatus {
        return library.getReal(c, vr, ref).let { updateStatus(it) }
    }

    /**
     * @see Fmi2Library.getString
     */
    @Synchronized
    fun readString(valueReference: ValueReference): FmuStringRead {
        return with(buffers) {
            vr[0] = valueReference
            library.getString(c, vr, sv).let {
                FmuStringRead(sv[0], updateStatus(it))
            }
        }
    }

    /**
     * @see Fmi2Library.getString
     */
    override fun read(vr: ValueReferences, ref: StringArray): FmiStatus {
        return library.getString(c, vr, ref).let { updateStatus(it) }
    }

    /**
     * @see Fmi2Library.getBoolean
     */
    @Synchronized
    fun readBoolean(valueReference: ValueReference): FmuBooleanRead {
        return with(buffers) {
            vr[0] = valueReference
            library.getBoolean(c, vr, bv).let {
                FmuBooleanRead(bv[0], updateStatus(it))
            }
        }
    }

    /**
     * @see Fmi2Library.getBoolean
     */
    override fun read(vr: ValueReferences, ref: BooleanArray): FmiStatus {
        return library.getBoolean(c, vr, ref).let { updateStatus(it) }
    }

    /**
     * @see Fmi2Library.setInteger
     */
    @Synchronized
    fun writeInteger(valueReference: ValueReference, ref: Int): FmiStatus {
        return with(buffers) {
            vr[0] = valueReference
            iv[0] = ref
            write(vr, iv)
        }
    }

    /**
     * @see Fmi2Library.setInteger
     */
    override fun write(vr: ValueReferences, value: IntArray): FmiStatus {
        return updateStatus((library.setInteger(c, vr, value)))
    }

    /**
     * @see Fmi2Library.setReal
     */
    @Synchronized
    fun writeReal(valueReference: ValueReference, value: Double): FmiStatus {
        return with(buffers) {
            vr[0] = valueReference
            rv[0] = value
            write(vr, rv)
        }
    }

    /**
     * @see Fmi2Library.setReal
     */
    override fun write(vr: ValueReferences, value: DoubleArray): FmiStatus {
        return updateStatus((library.setReal(c, vr, value)))
    }

    /**
     * @see Fmi2Library.setString
     */
    @Synchronized
    fun writeString(valueReference: ValueReference, value: String): FmiStatus {
        return with(buffers) {
            vr[0] = valueReference
            sv[0] = value
            write(vr, sv)
        }
    }

    /**
     * @see Fmi2Library.setString
     */
    override fun write(vr: ValueReferences, value: StringArray): FmiStatus {
        return updateStatus((library.setString(c, vr, value)))
    }

    /**
     * @see Fmi2Library.setBoolean
     */
    @Synchronized
    fun writeBoolean(valueReference: ValueReference, value: Boolean): FmiStatus {
        return with(buffers) {
            vr[0] = valueReference
            bv[0] = value
            write(vr, bv)
        }
    }

    /**
     * @see Fmi2Library.setBoolean
     */
    override fun write(vr: ValueReferences, value: BooleanArray): FmiStatus {
        return updateStatus(library.setBoolean(c, vr, value))
    }

    private companion object {
        val LOG: Logger = LoggerFactory.getLogger(Fmi1LibraryWrapper::class.java)
    }

}

