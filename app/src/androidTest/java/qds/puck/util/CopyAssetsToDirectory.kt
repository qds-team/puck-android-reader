package qds.puck.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile

// copy a folder of assets to a directory
fun copyAssetsToDirectory(assetContext: Context, assetDirectory: String, toDirectory: Path) {
    val testComicPagePaths = assetContext.assets.list(assetDirectory)
    for (testComicPageFile: String in testComicPagePaths!!) {
        // use asset file's input stream
        val assetPath = File(assetDirectory, testComicPageFile)
        assetContext.assets.open(assetPath.toString()).use {
            // write asset file's bytes to copyFile
            val copyFile: Path = Paths.get(toDirectory.toString(), testComicPageFile)
            copyFile.parent!!.createDirectories()
            copyFile.createFile()
            val writer = FileOutputStream(copyFile.toFile())
            writer.write(it.readBytes())
        }
    }
}
