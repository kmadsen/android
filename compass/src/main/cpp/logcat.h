#include <sstream>
#include <android/log.h>

#ifndef ANDROID_LOGCAT_H
#define ANDROID_LOGCAT_H

#include <iostream>
#include <iomanip>

#include <unwind.h>
#include <dlfcn.h>

namespace {

    struct BacktraceState {
        void** current;
        void** end;
    };

    static _Unwind_Reason_Code unwindCallback(struct _Unwind_Context* context, void* arg) {
        BacktraceState* state = static_cast<BacktraceState*>(arg);
        uintptr_t pc = _Unwind_GetIP(context);
        if (pc) {
            if (state->current == state->end) {
                return _URC_END_OF_STACK;
            } else {
                *state->current++ = reinterpret_cast<void*>(pc);
            }
        }
        return _URC_NO_REASON;
    }

}

size_t captureBacktrace(void** buffer, size_t max) {
    BacktraceState state = { buffer, buffer + max };
    _Unwind_Backtrace(unwindCallback, &state);

    return state.current - buffer;
}

void dumpBacktrace(std::ostream& os, void** buffer, size_t count) {
    for (size_t idx = 0; idx < count; ++idx) {
        const void* address = buffer[idx];
        const char* symbol = "";

        Dl_info info;
        if (dladdr(address, &info) && info.dli_sname) {
            symbol = info.dli_sname;
        }

        os << "  #" << std::setw(2) << idx << ": " << address << "  " << symbol << "\n";
    }
}

void logcat_stacktrace(const char* message) {
    const size_t max = 30;
    void* buffer[max];
    std::ostringstream oss;

    dumpBacktrace(oss, buffer, captureBacktrace(buffer, max));

    __android_log_print(ANDROID_LOG_INFO, "native-lib", "%s\n%s", message, oss.str().c_str());
}


void logcat_info() {
    logcat_stacktrace("testing stack trace");
    __android_log_write(ANDROID_LOG_INFO, "native-lib", "native call to initialize ");
}


#endif //ANDROID_LOGCAT_H
