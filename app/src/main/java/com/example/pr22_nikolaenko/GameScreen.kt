package com.example.pr22_nikolaenko

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf("MainMenu") }
    var records by remember { mutableStateOf(listOf<Int>()) }

    val imageSet = listOf(
        R.drawable.bee, R.drawable.lion, R.drawable.owl, R.drawable.cat,
        R.drawable.bear, R.drawable.bird, R.drawable.cow, R.drawable.crocodile,
        R.drawable.dog, R.drawable.donkey, R.drawable.elephent, R.drawable.hedgehog,
        R.drawable.monkey, R.drawable.sheep, R.drawable.stork, R.drawable.seagull,
        R.drawable.squirrel, R.drawable.toucan
    )

    when (currentScreen) {
        "MainMenu" -> MainMenu(
            onStartGame = { currentScreen = "Game" },
            onRecords = { currentScreen = "Records" }
        )
        "Game" -> GameScreen(
            imageSet = imageSet,
            onExit = {
                currentScreen = "MainMenu"
            },
            onGameOver = { score ->
                records = (records + score).sortedDescending().take(5)
                currentScreen = "Records"
            }
        )
        "Records" -> RecordsScreen(
            records = records,
            onBack = { currentScreen = "MainMenu" }
        )
    }
}

@Composable
fun MainMenu(onStartGame: () -> Unit, onRecords: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onStartGame) { Text("Начать игру") }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRecords) { Text("Таблица рекордов") }
    }
}

@Composable
fun GameScreen(
    imageSet: List<Int>,
    onExit: () -> Unit,
    onGameOver: (Int) -> Unit
) {
    val totalCards = 36 // 6x6 поле требует 36 карточек
    val doubledImages = (imageSet + imageSet).shuffled().take(totalCards)
    var shuffledCards by remember { mutableStateOf(createShuffledCards(doubledImages)) }
    var selectedCards by remember { mutableStateOf<List<Card>>(emptyList()) }
    var score by remember { mutableIntStateOf(0) }
    var moves by remember { mutableIntStateOf(0) }
    var isCheckingMatch by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCards) {
        if (selectedCards.size == 2) {
            isCheckingMatch = true
            delay(500)
            moves++
            if (selectedCards[0].image == selectedCards[1].image) {
                score += 20
                shuffledCards = shuffledCards.map {
                    if (it.id in selectedCards.map { it.id }) it.copy(isVisible = false) else it
                }
            } else {
                shuffledCards = shuffledCards.map {
                    if (it.id in selectedCards.map { it.id }) it.copy(isFlipped = false) else it
                }
            }
            selectedCards = emptyList()
            isCheckingMatch = false
        }
        if (shuffledCards.all { !it.isVisible }) {
            onGameOver(score)
        }
    }

    fun onCardClick(card: Card) {
        if (selectedCards.size == 2 || card.isFlipped || isCheckingMatch || !card.isVisible) return

        shuffledCards = shuffledCards.map {
            if (it.id == card.id) it.copy(isFlipped = true) else it
        }
        selectedCards = selectedCards + card
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ИГРА НА ПАМЯТЬ",
            style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold)
        )
        Text("Счет: $score, Ходы: $moves")
        Spacer(Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            contentPadding = PaddingValues(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(shuffledCards) { card ->
                if (card.isVisible) {
                    CardItem(card, onCardClick = { onCardClick(it) })
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Button(onClick = { onExit() }) {
            Text("Выйти")
        }
    }
}

@Composable
fun RecordsScreen(records: List<Int>, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Таблица рекордов")
        Spacer(Modifier.height(16.dp))
        records.forEachIndexed { index, record ->
            Text("${index + 1}. $record")
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Назад")
        }
    }
}

fun createShuffledCards(images: List<Int>): List<Card> {
    return images.mapIndexed { index, image -> Card(index, image) }
}

@Composable
fun CardItem(card: Card, onCardClick: (Card) -> Unit) {
    val imageId = if (card.isFlipped) card.image else R.drawable.cardback
    Box(
        modifier = Modifier
            .size(55.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .clickable { onCardClick(card) }
    ) {
        ImageItem(id = imageId)
    }
}

@Composable
fun ImageItem(id: Int) {
    val painter: Painter = painterResource(id = id)
    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

