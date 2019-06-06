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

#include <stdio.h>
#include <stdlib.h>
#include <string>

#if defined(_MSC_VER) || defined(WIN32) || defined(__MINGW32__)
#include <windows.h>
#define DLL_HANDLE HMODULE
#else
#define DLL_HANDLE void*
#include <dlfcn.h>
#endif

#include "fmiFunctionTypes.h"

namespace {

    template<class T>
    T load_function(DLL_HANDLE handle, const char *function_name) {
    #ifdef WIN32
        return (T) GetProcAddress(handle, function_name);
    #else
        return (T) dlsym(handle, function_name);
    #endif
    }

    DLL_HANDLE load_library(const char* libName) {
    #ifdef WIN32
        return LoadLibrary(libName);
    #else
        return dlopen(libName, RTLD_NOW | RTLD_LOCAL);
    #endif
    }

    const char* status_to_string(fmiStatus status) {
        switch (status){
            case 0: return "OK";
            case 1: return "Warning";
            case 2: return "Discard";
            case 3: return "Error";
            case 4: return "Fatal";
            case 5: return "Pending";
            default: return "Unknown";
        }
    }

    void logger(fmiComponent, fmiString instance_name, fmiStatus status, fmiString category, fmiString message, ...) {

        char msg[1000];
        char* copy;
        va_list argp;

        va_start(argp, message);
        vsprintf(msg, message, argp);
        va_end(argp);

        if (!instance_name) instance_name = "?";
        if (!category) category = "?";

        printf("status = %s, instanceName = %s, category = %s: %s\n", status_to_string(status), instance_name, category, msg);
    }

    const char* full_function_name(std::string modelIdentifier, std::string function_name) {
        return (modelIdentifier + "_" + function_name).c_str();
    }

}

class FmuInstance {

    public:

        fmiCallbackFunctions callback_ = {
            logger, calloc, free, NULL
        };

        DLL_HANDLE handle_;

        fmiGetVersionTYPE *fmiGetVersion_;
        fmiGetTypesPlatformTYPE *fmiGetTypesPlatform_;

        fmiInstantiateSlaveTYPE *fmiInstantiateSlave_;
        fmiInitializeSlaveTYPE *fmiInitializeSlave_;

        fmiResetSlaveTYPE *fmiResetSlave_;
        fmiTerminateSlaveTYPE *fmiTerminateSlave_;

        fmiGetIntegerTYPE *fmiGetInteger_;
        fmiGetRealTYPE *fmiGetReal_;
        fmiGetStringTYPE *fmiGetString_;
        fmiGetBooleanTYPE *fmiGetBoolean_;

        fmiSetIntegerTYPE *fmiSetInteger_;
        fmiSetRealTYPE *fmiSetReal_;
        fmiSetStringTYPE *fmiSetString_;
        fmiSetBooleanTYPE *fmiSetBoolean_;

        fmiFreeSlaveInstanceTYPE *fmiFreeSlaveInstance_;

        fmiSetRealInputDerivativesTYPE *fmiSetRealInputDerivatives_;
        fmiGetRealOutputDerivativesTYPE *fmiGetRealOutputDerivatives_;

        fmiDoStepTYPE *fmiDoStep_;
        fmiCancelStepTYPE *fmiCancelStep_;

        explicit FmuInstance(const char* libName, std::string modelIdentifier) {
            handle_ = load_library(libName);

            fmiGetVersion_ = load_function<fmiGetVersionTYPE *>(handle_, full_function_name(modelIdentifier, "_fmiGetVersion"));
            fmiGetTypesPlatform_ = load_function<fmiGetTypesPlatformTYPE *>(handle_,  full_function_name(modelIdentifier, "fmiGetTypesPlatform"));

            fmiInstantiateSlave_ = load_function<fmiInstantiateSlaveTYPE *>(handle_,  full_function_name(modelIdentifier, "fmiInstantiateSlave"));
            fmiInitializeSlave_ = load_function<fmiInitializeSlaveTYPE *>(handle_,  full_function_name(modelIdentifier, "fmiInitializeSlave"));

            fmiResetSlave_ = load_function<fmiResetSlaveTYPE *>(handle_,  full_function_name(modelIdentifier, "fmiResetSlave"));
            fmiTerminateSlave_ = load_function<fmiTerminateSlaveTYPE *>(handle_,  full_function_name(modelIdentifier, "fmiTerminateSlave"));

            fmiGetInteger_ = load_function<fmiGetIntegerTYPE *>(handle_, full_function_name(modelIdentifier, "fmiGetInteger"));
            fmiGetReal_ = load_function<fmiGetRealTYPE *>(handle_, full_function_name(modelIdentifier, "fmiGetReal"));
            fmiGetString_ = load_function<fmiGetStringTYPE *>(handle_, full_function_name(modelIdentifier, "fmiGetString"));
            fmiGetBoolean_ = load_function<fmiGetBooleanTYPE *>(handle_, full_function_name(modelIdentifier, "fmiGetBoolean"));

            fmiSetInteger_ = load_function<fmiSetIntegerTYPE *>(handle_, full_function_name(modelIdentifier, "fmiSetInteger"));
            fmiSetReal_ = load_function<fmiSetRealTYPE *>(handle_, full_function_name(modelIdentifier, "fmiSetReal"));
            fmiSetString_ = load_function<fmiSetStringTYPE *>(handle_, full_function_name(modelIdentifier, "fmiSetString"));
            fmiSetBoolean_ = load_function<fmiSetBooleanTYPE *>(handle_, full_function_name(modelIdentifier, "fmiSetBoolean"));

            fmiFreeSlaveInstance_ = load_function<fmiFreeSlaveInstanceTYPE *>(handle_,  full_function_name(modelIdentifier, "fmiFreeSlaveInstance"));

            fmiSetRealInputDerivatives_ = load_function<fmiSetRealInputDerivativesTYPE *>(handle_, full_function_name(modelIdentifier, "fmiSetRealInputDerivatives"));
            fmiGetRealOutputDerivatives_ = load_function<fmiGetRealOutputDerivativesTYPE *>(handle_, full_function_name(modelIdentifier, "fmiGetRealOutputDerivatives"));

            fmiDoStep_ = load_function<fmiDoStepTYPE *>(handle_, full_function_name(modelIdentifier, "fmiDoStep"));
            fmiCancelStep_ = load_function<fmiCancelStepTYPE *>(handle_, full_function_name(modelIdentifier, "fmiCancelStep"));

        };

};