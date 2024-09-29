package compose.project.learn

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform