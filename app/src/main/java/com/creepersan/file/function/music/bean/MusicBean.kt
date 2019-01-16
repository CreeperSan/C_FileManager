package com.creepersan.file.function.music.bean

import android.media.MediaMetadataRetriever
import java.io.File

class MusicBean(receiver: MediaMetadataRetriever, file:File) {

    constructor(receiver: MediaMetadataRetriever, path:String):this(receiver, File(path))

    var name = ""
    var title = ""
    var author = ""
    var album = ""
    var path = ""
    var duration = -1L

    init {
        receiver.setDataSource(file.absolutePath)
        name = file.name
        title = receiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        author = receiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR)
        album = receiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
        path = file.path
    }


}