package compose.project.learn

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
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
fun LoadingView() {
    Text(text = "Loading...", color = Color.Gray)
}

@Composable
fun ErrorView(error: String) {
    Text(text = "Error: $error", color = Color.Red)
}

@Composable
fun ProductItem(product: Item) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = product.title)
        Text(text = product.image)
    }
}

@Composable
fun ProductListView(products: List<Item>) {
    LazyColumn {
        items(products) { product ->
            ProductItem(product)
        }
    }
}