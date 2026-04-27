package com.bitperfect.app.library

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.net.URLDecoder

class LibraryRepository(private val context: Context) {

    fun getLibrary(outputFolderUriString: String?): List<ArtistInfo> {
        if (outputFolderUriString.isNullOrBlank()) {
            return emptyList()
        }

        val decodedUri = URLDecoder.decode(outputFolderUriString, "UTF-8")

        val pathIndex = decodedUri.lastIndexOf(":")
        if (pathIndex == -1 || pathIndex == decodedUri.length - 1) {
            return emptyList()
        }

        var relativePath = decodedUri.substring(pathIndex + 1)
        if (relativePath.endsWith("/")) {
            relativePath = relativePath.dropLast(1)
        }
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.drop(1)
        }

        val projection = arrayOf(
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM
        )

        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("$relativePath/%")

        val sortOrder = "${MediaStore.Audio.Media.ARTIST} ASC, ${MediaStore.Audio.Media.ALBUM} ASC"

        val albumsByArtist = mutableMapOf<Long, MutableMap<Long, AlbumInfo>>()
        val artistNames = mutableMapOf<Long, String>()

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val artistIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            val albumArtBaseUri = Uri.parse("content://media/external/audio/albumart")

            while (cursor.moveToNext()) {
                val artistId = cursor.getLong(artistIdCol)
                val artistName = cursor.getString(artistCol) ?: "Unknown Artist"
                val albumId = cursor.getLong(albumIdCol)
                val albumTitle = cursor.getString(albumCol) ?: "Unknown Album"

                artistNames[artistId] = artistName

                val artistAlbums = albumsByArtist.getOrPut(artistId) { mutableMapOf() }
                if (!artistAlbums.containsKey(albumId)) {
                    val artUri = ContentUris.withAppendedId(albumArtBaseUri, albumId)
                    artistAlbums[albumId] = AlbumInfo(albumId, albumTitle, artUri)
                }
            }
        }

        return albumsByArtist.map { (artistId, albumsMap) ->
            ArtistInfo(
                id = artistId,
                name = artistNames[artistId] ?: "Unknown Artist",
                albums = albumsMap.values.toList()
            )
        }
    }

    fun getTracksForAlbum(albumId: Long): List<TrackInfo> {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.ALBUM_ID} = ?"
        val selectionArgs = arrayOf(albumId.toString())
        val sortOrder = "${MediaStore.Audio.Media.TRACK} ASC"

        val tracks = mutableListOf<TrackInfo>()

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol) ?: "Unknown Track"
                var trackNumber = cursor.getInt(trackCol)
                val durationMs = cursor.getLong(durationCol)
                var discNumber = 1

                if (trackNumber >= 1000) {
                    discNumber = trackNumber / 1000
                    trackNumber %= 1000
                }

                tracks.add(TrackInfo(id, title, trackNumber, durationMs, discNumber))
            }
        }

        return tracks
    }
}
