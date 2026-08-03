package com.example.data

object AppConfig {
    /**
     * The main website URL to load in the WebView.
     * Change this to your website URL.
     */
    const val BASE_URL = "https://welink.devfanuel.online/" // User website URL

    /**
     * Whether to enable debugging features for the WebView.
     * Should be false in production.
     */
    const val DEBUG_MODE = false

    /**
     * Whether to prevent screenshots on all pages.
     */
    const val PREVENT_SCREENSHOTS = false

    /**
     * List of external domains that should be opened in Chrome Custom Tabs instead of the WebView.
     */
    val EXTERNAL_DOMAINS = listOf(
        "facebook.com",
        "twitter.com",
        "linkedin.com",
        "instagram.com",
        "youtube.com"
    )
}
