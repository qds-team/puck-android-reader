package qds.puck.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream

// copy a folder of assets to a directory
fun copyAssetsToDirectory(assetContext: Context, assetDirectory: String, toDirectory: File) {
    val testComicPagePaths = assetContext.assets.list(assetDirectory)
    for (testComicPageFile: String in testComicPagePaths!!) {
        // use asset file's input stream
        val assetPath = File(assetDirectory, testComicPageFile)
        assetContext.assets.open(assetPath.toString()).use {
            // write asset file's bytes to copyFile
            val copyFile = File(toDirectory, testComicPageFile)
            copyFile.parentFile!!.mkdirs()
            copyFile.createNewFile()
            val writer = FileOutputStream(copyFile)
            writer.write(it.readBytes())
        }
    }
}
