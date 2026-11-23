package com.example.pokemax

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

class AudioPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playPokemonCry(pokemonId: Int) {
        stop()
        val cryUrl = "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/$pokemonId.ogg"
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(cryUrl)
                prepareAsync()
                setOnPreparedListener { start() }
                setOnCompletionListener {
                    release()
                    mediaPlayer = null
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e("AudioPlayerManager", "Error playing cry for pokemon $pokemonId: what=$what extra=$extra")
                    mp.release()
                    mediaPlayer = null
                    true
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Exception initializing cry player", e)
        }
    }

    fun stop() {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
            } catch (e: Exception) {
                Log.e("AudioPlayerManager", "Error stopping player", e)
            } finally {
                player.release()
            }
        }
        mediaPlayer = null
    }
}
