/*
 * The MIT License
 *
 * Copyright 2017-2018 Norwegian University of Technology (NTNU)
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

package no.mechatronics.sfi.fmi4j.modeldescription.misc

import java.io.Serializable
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute


/**
 * Provides default settings for the integrator, such as stop time and
 * relative tolerance.
 *
 * DefaultExperiment consists of the optional default start time, stop time, relative tolerance, and step size
 * for the first simulation run. A tool may ignore this information. However, it is convenient for a user that
 * startTime, stopTime, tolerance and stepSize have already a meaningful default value for the model at
 * hand. Furthermore, for CoSimulation the stepSize defines the preferred communicationStepSize.
 *
 * @author Lars Ivar Hatledal
 */
@XmlAccessorType(XmlAccessType.FIELD)
data class DefaultExperiment(

        /**
         * Default start time of simulation
         */
        @XmlAttribute
        val startTime: Double = 0.0,

        /**
         * Default stop time of simulation
         */
        @XmlAttribute
        val stopTime: Double = 0.0,

        /**
         * Default relative integration tolerance
         */
        @XmlAttribute
        val tolerance: Double = 1E-4,

        /***
         * ModelExchange: Default step size for fixed step integrators
         * CoSimulation: Preferred communicationStepSize
         */
        @XmlAttribute
        val stepSize: Double = 1E-3

) : Serializable {

    override fun toString(): String {
        return "DefaultExperiment(startTime=$startTime, stopTime=$stopTime, tolerance=$tolerance, stepSize=$stepSize)"
    }

}
