#include <Python.h>
#include "pyjni.h"
#include <android/log.h>
#include <stdlib.h>
#include <string.h>

 
#define APPNAME "pyjni"
#define LOG(x) __android_log_write(ANDROID_LOG_INFO, APPNAME, (x))
 
JNIEXPORT jint JNICALL Java_com_mariogrip_electrumbitcoinwallet_PythonWrapper_start
(JNIEnv *env, jclass jc, jstring datapath, jstring pyfold)
{
	   LOG("Initializing the Python interpreter");
	    // Get the location of data files
	    jboolean iscopy;
	    const char *data_path = (*env)->GetStringUTFChars(env, datapath, &iscopy);
	    const char *py_path = (*env)->GetStringUTFChars(env, pyfold, &iscopy);
	    LOG(data_path);
	    LOG(py_path);
	    // Set Python environment variables
	    setenv("PYTHONHOME", py_path, 1);
	    setenv("PYTHONPATH", data_path, 1);
	    // Initialize Python interpreter and log
	    Py_OptimizeFlag = 1; // Allow to run .pyo files outside zip (one of [0,1,2])
	    Py_Initialize();
	    init_androidembed();
	    return 0;
}

JNIEXPORT jstring JNICALL Java_com_mariogrip_electrumbitcoinwallet_PythonWrapper_quarry
(JNIEnv *env, jclass jc, jstring cmd1)
{
        jboolean iscopy;
        const char *pythonScript = (*env)->GetStringUTFChars(env, cmd1, 0);
	    LOG(pythonScript);
	    PyObject* main = PyImport_AddModule("__main__");
	    PyObject* globalDictionary = PyModule_GetDict(main);
	    PyObject* localDictionary = PyDict_New();
	    PyRun_String(pythonScript, Py_file_input, globalDictionary, localDictionary);
	    const char* result = PyString_AsString(PyDict_GetItemString(localDictionary, "backto"));
	    return (*env)->NewStringUTF(env, result);

}


 
JNIEXPORT jint JNICALL Java_com_mariogrip_electrumbitcoinwallet_PythonWrapper_end
  (JNIEnv *env, jclass jc)
{
    LOG("Finalizing the Python interpreter");
    Py_Finalize();
    return 0;
}

static PyObject *androidembed_log(PyObject *self, PyObject *args) {
    char *logstr = NULL;
    if (!PyArg_ParseTuple(args, "s", &logstr)) {
        return NULL;
    }
    LOG(logstr);
    Py_RETURN_NONE;
}

static PyMethodDef AndroidEmbedMethods[] = {
    {"log", androidembed_log, METH_VARARGS,
     "Log on android platform"},
    {NULL, NULL, 0, NULL}
};

PyMODINIT_FUNC init_androidembed(void) {
    (void) Py_InitModule("androidembed", AndroidEmbedMethods);
    /* inject our bootstrap code to redirect python stdin/stdout */
    PyRun_SimpleString(
            "import sys\n" \
            "import androidembed\n" \
            "class LogFile(object):\n" \
            "    def __init__(self):\n" \
            "        self.buffer = ''\n" \
            "    def write(self, s):\n" \
            "        s = self.buffer + s\n" \
            "        lines = s.split(\"\\n\")\n" \
            "        for l in lines[:-1]:\n" \
            "            androidembed.log(l)\n" \
            "        self.buffer = lines[-1]\n" \
            "    def flush(self):\n" \
            "        return\n" \
            "sys.stdout = sys.stderr = LogFile()\n"
    );
}

