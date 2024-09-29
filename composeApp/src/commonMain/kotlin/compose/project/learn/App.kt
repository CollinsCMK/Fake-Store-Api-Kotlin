package compose.project.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okio.FileSystem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            val scope = rememberCoroutineScope()
            var products by remember { mutableStateOf<List<Item>>(emptyList()) }
            val state = rememberScrollState()
            var error by remember { mutableStateOf<String?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                scope.launch {
                    try {
                        val jsonString = Greeting().greeting("products")
                        println("Received JSON: $jsonString") // Debug log

                        products = Json.decodeFromString<List<Item>>(jsonString)
                        println("Parsed products: $products") // Debug log

                        isLoading = false
                    } catch (e: Exception) {
                        error = e.message ?: "An error occurred"
                        isLoading = false
                        e.printStackTrace() // Print full stack trace
                    }
                }
            }

            Column {
                when {
                    isLoading -> LoadingView()
                    error != null -> ErrorView(error!!)
                    products.isNotEmpty() -> ProductListView(products)
                    else -> Text("No products found", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun fullScreenContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

    }
}

@Composable
fun LoadingView() {
    Text(text = "Loading...", color = Color.Gray)
}

@Composable
fun ErrorView(error: String) {
    Text(text = "Error: $error", color = Color.Red)
}

@Composable
fun ProductItem(product: Item) {
    Card(
        modifier = Modifier
            .padding(1.dp)
            .fillMaxWidth()
            .height(200.dp),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(4.dp),
                model = product.image,
                contentDescription = null,
            )
            Text(
                text = product.title,
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 2.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProductListView(products: List<Item>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductItem(product)
        }
    }
}

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader
        .Builder(context)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.3)
                .strongReferencesEnabled(true)
                .build()
        }.diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                newDiskCache()
            }.crossfade(true).logger(DebugLogger()).build()

fun newDiskCache(): DiskCache? {
    return DiskCache.Builder().directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
        .maxSizeBytes(1024L * 1024 * 1024)
        .build()
}
