package com.d4rk.cleaner
import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.d4rk.cleaner.plus.FileScanner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var fs: FileScanner

    @Before
    fun init() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val path = File(Environment.getExternalStorageDirectory(), "")
        val res = appContext.resources
        fs = FileScanner(path, appContext).apply {
            setAutoWhite(false)
            setResources(res)
            setDelete(true)
        }
    }

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.d4rk.cleaner.plus", appContext.packageName)
    }

    @Test
    fun checkLogFiles() {
        val logFile = createFile("testfile.loG")
        val clogFile = createFile("clogs.pnG")
        fs.setUpFilters(generic = true, aggressive = false, apk = false)
        fs.startScan()
        Assert.assertTrue(clogFile.exists())
        Assert.assertFalse(logFile.exists())
    }

    @Test
    fun checkTempFiles() {
        val tmpFile = createFile("testfile.tMp")
        fs.setUpFilters(generic = true, aggressive = false, apk = false)
        fs.startScan()
        Assert.assertFalse(tmpFile.exists())
    }

    @Test
    fun checkThumbFiles() {
        val thumbFile = createFile("thumbs.Db")
        fs.setUpFilters(generic = false, aggressive = true, apk = false)
        fs.startScan()
        Assert.assertFalse(thumbFile.exists())
    }

    @Test
    fun checkAPKFiles() {
        val thumbFile = createFile("chrome.aPk")
        fs.setUpFilters(generic = true, aggressive = true, apk = true)
        fs.startScan()
        Assert.assertFalse(thumbFile.exists())
    }

    @Test
    fun checkEmptyDir() {
        val emptyDir = createDir()
        fs.setUpFilters(generic = true, aggressive = false, apk = false)
        fs.setEmptyDir(true)
        fs.startScan()
        Assert.assertFalse(emptyDir.exists())
    }

    private fun createFile(name: String): File {
        val file = File(Environment.getExternalStorageDirectory(), name)
        file.createNewFile().let {
            Assert.assertTrue(it)
        }
        Assert.assertTrue(file.exists())
        return file
    }

    private fun createDir(): File {
        val file = File(Environment.getExternalStorageDirectory(), "testdir")
        file.mkdir().let {
            Assert.assertTrue(it)
        }
        Assert.assertTrue(file.exists())
        return file
    }
}