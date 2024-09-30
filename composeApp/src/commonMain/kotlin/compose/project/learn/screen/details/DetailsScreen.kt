package compose.project.learn.screen.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import compose.project.learn.Greeting
import compose.project.learn.Item
import compose.project.learn.screen.home.ErrorView
import compose.project.learn.screen.home.LoadingView
import compose.project.learn.screen.home.ProductListView
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okio.FileSystem

data class DetailsScreen(val productId: Int): Screen {
    @Composable
    override fun Content() {
        MaterialTheme {
            setSingletonImageLoaderFactory { context ->
                getAsyncImageLoader(context)
            }
            val navigator = LocalNavigator.current
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text("Product Details")
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                navigator?.pop()
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Arrow Back"
                                )
                            }
                        },
                        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                        backgroundColor = Color.White
                    )
                },
            ) {
                val scope = rememberCoroutineScope()
                var products by remember { mutableStateOf<Item?>(null) }
                val state = rememberScrollState()
                var error by remember { mutableStateOf<String?>(null) }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    scope.launch {
                        try {
                            val jsonString = Greeting().greeting("products/${productId}")
                            println("Received product: $jsonString")

                            products = Json.decodeFromString<Item>(jsonString)

                            isLoading = false
                        } catch (e: Exception) {
                            error = e.message ?: "An error occurred"
                            isLoading = false
                            e.printStackTrace()
                        }
                    }
                }

                Column {
                    when {
                        isLoading -> LoadingView()
                        error != null -> ErrorView(error!!)
                        products != null -> ProductItem(products!!)
                        else -> Text("No products found", color = Color.Gray)
                    }
                }
            }
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

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ProductItem(product: Item) {
        val navigator = LocalNavigator.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(1.dp)
                            .fillMaxWidth(),
                        elevation = 4.dp
                    ) {
                        Text("Price: $${product.price}")
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(1.dp)
                            .fillMaxWidth(),
                    ) {
                        Text("Rating ${product.rating.rate}")
                        Text("Remaining ${product.rating.count}")
                    }
                }
            }
        }
    }

    private fun getAsyncImageLoader(context: PlatformContext) =
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

    private fun newDiskCache(): DiskCache? {
        return DiskCache.Builder().directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
            .maxSizeBytes(1024L * 1024 * 1024)
            .build()
    }
}