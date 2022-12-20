package com.example.explorerx.repository

import android.util.Base64
import com.example.explorerx.model.googlechart.FileEntry
import com.example.explorerx.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.nio.ByteBuffer
import java.security.MessageDigest

private const val TAG = "Repository"

object IORepo {
	private val digest = MessageDigest.getInstance("MD5")
	private val block = ByteArray(4096)

	suspend fun computeHash(file: File): Int = withContext(Dispatchers.IO) {
		try {
			BufferedInputStream(FileInputStream(file)).use {
				var length = it.read(block)
				while (length > 0) {
					digest.update(block, 0, length)
					length = it.read(block)
				}
			}
		} catch (e: IOException) {
			e.message?.toast()
		}
		return@withContext ByteBuffer.wrap(digest.digest()).int
	}

	suspend fun getTreeData(rootFile: File): List<FileEntry> =
		withContext(Dispatchers.IO) {
			val data = mutableListOf<FileEntry>()
			if (rootFile.isDirectory) {
				val list = rootFile.listFiles()
				list?.let {
					for (file in it) {
						_addTreeData(data, file)
					}
				}
			}
			return@withContext data
		}

	private fun _addTreeData(data: MutableList<FileEntry>, rootFile: File) {
		if (rootFile.isDirectory) {
			data += FileEntry(rootFile.absolutePath, rootFile.parent, 0)
			val list = rootFile.listFiles()
			list?.let {
				for (file in it) {
					_addTreeData(data, file)
				}
			}
		} else {
			data += FileEntry(rootFile.absolutePath, rootFile.parent, rootFile.length())
		}
	}

	suspend fun getEncodedChatHtml(html: String): String = withContext(Dispatchers.Default) {
		return@withContext Base64.encodeToString(html.toByteArray(), Base64.NO_PADDING)
	}

	/**
	 * read text from buffered reader
	 */
	suspend fun readText(stream: InputStream): String = withContext(Dispatchers.IO) {
		val buffer = StringBuffer()
		BufferedReader(InputStreamReader(stream)).use {
			var line = it.readLine()
			while (line != null) {
				buffer.append(line + "\n")
				line = it.readLine()
			}
		}
		return@withContext buffer.toString()
	}

	suspend fun writeText(str: String, fos: FileOutputStream) = withContext(Dispatchers.IO) {
		BufferedOutputStream(fos).use {
			it.write(str.toByteArray())
		}
	}
}