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

package no.mechatronics.sfi.fmi4j.common

import no.mechatronics.sfi.fmi4j.modeldescription.variables.Real
import no.mechatronics.sfi.fmi4j.modeldescription.variables.RealArray
import no.mechatronics.sfi.fmi4j.modeldescription.variables.StringArray
import org.jetbrains.annotations.NotNull
import java.io.Serializable

/***
 *
 * Represents the result of reading a variable from an FMU
 *
 * @author Lars Ivar Hatledal
 */
sealed class FmuRead<E>(

        /**
         * The value returned by the FMU during the call to getXXX
         */
        @NotNull
        val value: E,

        /**
         * The status returned by the FMU during the call to getXXX
         */
        @NotNull
        val status: FmiStatus

): Serializable {

    override fun toString(): String {
        return "FmuRead(value=$value, status=$status)"
    }

}

/**
 * @author Lars Ivar Hatledal
 */
class FmuIntegerRead(
        value: Int,
        status: FmiStatus
): FmuRead<Int>(value, status)

/**
 * @author Lars Ivar Hatledal
 */
class FmuIntegerArrayRead(
        value: IntArray,
        status: FmiStatus
): FmuRead<IntArray>(value, status)

/**
 * @author Lars Ivar Hatledal
 */
class FmuRealRead(
        value: Real,
        status: FmiStatus
): FmuRead<Real>(value, status)

/**
 * @author Lars Ivar Hatledal
 */
class FmuRealArrayRead(
        value: RealArray,
        status: FmiStatus
): FmuRead<RealArray>(value, status)

/**
 * @author Lars Ivar Hatledal
 */
class FmuStringRead(
        value: String,
        status: FmiStatus
): FmuRead<String>(value, status)

/**
 * @author Lars Ivar Hatledal
 */
class FmuStringArrayRead(
        value: StringArray,
        status: FmiStatus
): FmuRead<StringArray>(value, status)

/**
 * @author Lars Ivar Hatledal
 */
class FmuBooleanRead(
        value: Boolean,
        status: FmiStatus
): FmuRead<Boolean>(value, status)

/**
 * @author Lars Ivar Hatledal
 */
class FmuBooleanArrayRead(
        value: BooleanArray,
        status: FmiStatus
): FmuRead<BooleanArray>(value, status)


typealias FmuEnumerationRead = FmuIntegerRead
typealias FmuEnumerationArrayRead = FmuIntegerArrayRead