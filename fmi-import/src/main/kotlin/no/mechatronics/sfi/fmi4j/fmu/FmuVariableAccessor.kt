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


package no.mechatronics.sfi.fmi4j.fmu

import no.mechatronics.sfi.fmi4j.common.Real
import no.mechatronics.sfi.fmi4j.common.RealArray
import no.mechatronics.sfi.fmi4j.common.StringArray
import no.mechatronics.sfi.fmi4j.common.VariableAccessor
import no.mechatronics.sfi.fmi4j.fmu.proxy.v2.Fmi2LibraryWrapper
import no.mechatronics.sfi.fmi4j.modeldescription.variables.ModelVariables
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Lars Ivar Hatledal
 */
class FmuVariableAccessor(
        private val wrapper: Fmi2LibraryWrapper<*>,
        private val modelVariables: ModelVariables
): VariableAccessor {

   companion object {
       private val map = ConcurrentHashMap<String, Int>()
   }

    //read
    override fun readInteger(name: String) = readInteger(process(name))
    override fun readInteger(valueReference: Int) = wrapper.getInteger(valueReference)
    override fun readInteger(vr: IntArray) = wrapper.getInteger(vr)
    override fun readInteger(vr: IntArray, value: IntArray) = wrapper.getInteger(vr, value)

    override fun readReal(name: String) = readReal(process(name))
    override fun readReal(valueReference: Int) = wrapper.getReal(valueReference)
    override fun readReal(vr: IntArray) = wrapper.getReal(vr)
    override fun readReal(vr: IntArray, value: RealArray) = wrapper.getReal(vr, value)

    override fun readString(name: String) = readString(process(name))
    override fun readString(valueReference: Int) = wrapper.getString(valueReference)
    override fun readString(vr: IntArray) = wrapper.getString(vr)
    override fun readString(vr: IntArray, value: StringArray) = wrapper.getString(vr, value)

    override fun readBoolean(name: String) = readBoolean(process(name))
    override fun readBoolean(valueReference: Int) = wrapper.getBoolean(valueReference)
    override fun readBoolean(vr: IntArray) = wrapper.getBoolean(vr)
    override fun readBoolean(vr: IntArray, value: BooleanArray) = wrapper.getBoolean(vr, value)
    override fun readBoolean(vr: IntArray, value: IntArray) = wrapper.getBoolean(vr, value)

    //write
    override fun writeInteger(name: String, value: Int) = wrapper.setInteger(process(name), value)
    override fun writeInteger(valueReference: Int, value: Int) = wrapper.setInteger(valueReference, value)
    override fun writeInteger(vr: IntArray, value: IntArray) = wrapper.setInteger(vr, value)

    override fun writeReal(valueReference: Int, value: Real) =  wrapper.setReal(valueReference, value)
    override fun writeReal(name: String, value: Real) = wrapper.setReal(process(name), value)
    override fun writeReal(vr: IntArray, value: DoubleArray) = wrapper.setReal(vr, value)

    override fun writeString(name: String, value: String) =  wrapper.setString(process(name), value)
    override fun writeString(valueReference: Int, value: String) = wrapper.setString(valueReference, value)
    override fun writeString(vr: IntArray, value: StringArray) = wrapper.setString(vr, value)

    override fun writeBoolean(name: String, value: Boolean) = wrapper.setBoolean(process(name), value)
    override fun writeBoolean(valueReference: Int, value: Boolean) = wrapper.setBoolean(valueReference, value)
    override fun writeBoolean(vr: IntArray, value: BooleanArray) = wrapper.setBoolean(vr, value)
    override fun writeBoolean(vr: IntArray, value: IntArray) = wrapper.setBoolean(vr, value)

    private fun process(name: String): Int {
       return map.getOrPut(name, {modelVariables.getValueReference(name)})
    }


}