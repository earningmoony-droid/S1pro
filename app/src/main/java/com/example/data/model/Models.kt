package com.example.data.model

data class ViralIngredient(
    val title: String,
    val description: String,
    val importance: String, // "High", "Critical", "Pro Tip"
    val iconName: String
)

data class ChannelAnalysisResult(
    val urlOrTopic: String,
    val platform: String,
    val viralityScore: Int, // 0 - 100
    val reachEstimate: String, // e.g. "50K - 250K impressions"
    val engagementRate: String, // e.g. "8.4% expected engagement"
    val targetAudience: String, // e.g. "Ages 18-34, Tech enthusiasts & Students"
    val bestUploadDays: String, // e.g. "Thursday, Saturday, Sunday"
    val bestUploadTimes: String, // e.g. "5:30 PM - 8:30 PM (Local Prime Time)"
    val hooksSuggestions: List<String>,
    val viralIngredients: List<ViralIngredient>,
    val pacingAdvice: String,
    val soundAndMusicAdvice: String,
    val recommendedContentType: String, // "Shorts (under 45s) with fast cut" or "Long-form (8-12 mins)"
    val thumbnailTip: String,
    val monetizationReadiness: String
)

data class SeoTitleOption(
    val title: String,
    val ctrScore: Int, // e.g. 92
    val styleTag: String // "Curiosity Gap", "Urgency/Viral", "SEO Keyword Heavy"
)

data class SeoTagOption(
    val tag: String,
    val searchVolume: String, // "High", "Very High", "Medium"
    val competition: String, // "Low", "Medium", "High"
    val isTrending: Boolean
)

data class SeoPackResult(
    val topic: String,
    val primaryKeyword: String,
    val overallSeoScore: Int, // 0 - 100
    val titleOptions: List<SeoTitleOption>,
    val recommendedTags: List<SeoTagOption>,
    val formattedDescription: String,
    val hashtags: List<String>,
    val thumbnailVisualPrompt: String,
    val thumbnailTextOverlay: String,
    val thumbnailColorTheme: String,
    val rankingChecklist: List<String>
)

data class TutorialGuide(
    val id: String,
    val stepNumber: Int,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val detailsBn: List<String>,
    val detailsEn: List<String>,
    val icon: String,
    val badge: String
)

data class PerformanceMetric(
    val label: String,
    val value: String,
    val change: String,
    val isPositive: Boolean
)

data class ChartPoint(
    val day: String,
    val views: Float,
    val ctr: Float,
    val subscribers: Float
)
