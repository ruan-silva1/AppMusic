package com.example.musicapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flipfopradio.R
import com.example.flipfopradio.ui.theme.FlipFopRadioTheme
import formatTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlipFopRadioTheme {
                MusicPlayerApp()
            }
        }
    }
}

@Composable
fun MusicPlayerApp() {
    var isDarkTheme by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color(0xFF121212) else Color(0xFFFFFFFF))
    ) {
        // Botão para trocar tema
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { isDarkTheme = !isDarkTheme },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isDarkTheme) Color(0xFFBB86FC) else Color(0xFF6200EE),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.WbSunny else Icons.Default.NightsStay,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isDarkTheme) "Tema Claro" else "Tema Escuro")
            }
        }

        if (isDarkTheme) {
            MusicPlayerUI(darkTheme = true)
        } else {
            MusicPlayerUI(darkTheme = false)
        }
    }
}

@Composable
fun MusicPlayerUI(darkTheme: Boolean) {
    val imagens = listOf(R.drawable.imagemmusic, R.drawable.imagemusic2)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val musicas = listOf(R.raw.musica1, R.raw.musica2)
    var currentIndex by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var position by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(1f) }

    fun playMusic(index: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, musicas[index])
        duration = mediaPlayer?.duration?.toFloat() ?: 1f
        mediaPlayer?.start()
        isPlaying = true
    }

    fun togglePlayPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPlaying = false
        } else {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    fun nextMusic() {
        currentIndex = (currentIndex + 1) % musicas.size
        playMusic(currentIndex)
    }

    fun previousMusic() {
        currentIndex = if (currentIndex == 0) musicas.lastIndex else currentIndex - 1
        playMusic(currentIndex)
    }

    LaunchedEffect(isPlaying) {
        while (true) {
            if (isPlaying) {
                position = mediaPlayer?.currentPosition?.toFloat() ?: 0f
            }
            delay(500)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    val backgroundColor = if (darkTheme) Color(0xFF121212) else Color(0xFFFFFFFF)
    val textColor = if (darkTheme) Color.White else Color.Black
    val accentColor = if (darkTheme) Color(0xFFBB86FC) else Color(0xFF6200EE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Playlist de Estudos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(270.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            Image(
                painter = painterResource(id = imagens[currentIndex]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Flip fop Radio",
            fontSize = 18.sp,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = formatTime(position.toInt()), fontSize = 12.sp, color = textColor)
            Slider(
                value = position,
                onValueChange = {
                    position = it
                    mediaPlayer?.seekTo(it.toInt())
                },
                valueRange = 0f..duration,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = textColor,
                    activeTrackColor = accentColor
                )
            )
            Text(text = formatTime(duration.toInt()), fontSize = 12.sp, color = textColor)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { previousMusic() }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = textColor, modifier = Modifier.size(40.dp))
            }

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(accentColor),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {
                    if (!isPlaying) playMusic(currentIndex) else togglePlayPause()
                }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            IconButton(onClick = { nextMusic() }) {
                Icon(Icons.Default.SkipNext, contentDescription = null, tint = textColor, modifier = Modifier.size(40.dp))
            }
        }
    }
}
