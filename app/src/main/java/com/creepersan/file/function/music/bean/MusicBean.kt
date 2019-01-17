package com.creepersan.file.function.music.bean

import android.media.MediaMetadataRetriever
import java.io.File
import java.lang.IllegalStateException

class MusicBean(retriever: MediaMetadataRetriever, file:File) {

    constructor(retriever: MediaMetadataRetriever, path:String):this(retriever, File(path))

    var name = ""
    var title = ""
    var author = ""
    var album = ""
    var path = ""
    var duration = -1L

    init {
        retriever.setDataSource(file.absolutePath)
        name = file.name
        title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        path = file.path
    }


}