package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChannelAnalysisResult
import com.example.data.model.ViralIngredient
import com.example.ui.components.ViralityScoreGauge
import com.example.ui.theme.*

@Composable
fun AnalysisScreen(
    input: String,
    selectedPlatform: String,
    isAnalyzing: Boolean,
    analysisResult: ChannelAnalysisResult?,
    isBengali: Boolean,
    onInputChange: (String) -> Unit,
    onPlatformChange: (String) -> Unit,
    onAnalyzeClick: () -> Unit,
    onCopyText: (String) -> Unit,
    onExportCsv: () -> Unit,
    onExportPdf: () -> Unit
) {
    val platforms = listOf("YouTube", "Facebook", "Instagram", "TikTok")
    val quickTopics = if (isBengali) {
        listOf("টেক রিভিউ ও আনবক্সিং", "লাইফস্টাইল ও ট্রাভেল ভ্লগ", "কুকিং ও রেসিপি", "গেমিং ও লাইভ স্ট্রিম", "অনলাইন ইনকাম গাইড")
    } else {
        listOf("Tech Review & Unboxing", "Travel & Daily Vlog", "Cooking & Food Recipes", "Gaming & Streaming", "Make Money Online Guide")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Hero Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    BrandRed.copy(alpha = 0.15f),
                                    ElectricViolet.copy(alpha = 0.15f)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BrandRed
                            )
                            Text(
                                text = if (isBengali) "সোশ্যাল মিডিয়া ও চ্যানেল অডিট" else "AI Channel & Link Analyzer",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBengali)
                                "আপনার ইউটিউব চ্যানেল লিংক, ইনস্টাগ্রাম, ফেসবুক বা ভিডিও টপিক দিন—AI বলে দেবে ভিডিওতে কী মসলা থাকতে হবে ও কীভাবে দ্রুত ভাইরাল করবেন!"
                            else
                                "Input your YouTube channel/video, Instagram or Facebook link—AI will uncover viral hooks, pacing ingredients & growth secrets!",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Input Box & Platform Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBengali) "১. প্ল্যাটফর্ম নির্বাচন করুন" else "1. Select Platform",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Platform Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        platforms.forEach { p ->
                            val isSelected = selectedPlatform.equals(p, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onPlatformChange(p) },
                                label = { Text(p, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (p) {
                                            "YouTube" -> Icons.Default.PlayCircle
                                            "Facebook" -> Icons.Default.Public
                                            "Instagram" -> Icons.Default.CameraAlt
                                            else -> Icons.Default.Videocam
                                        },
                                        contentDescription = p,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) BrandRed else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("platform_chip_$p")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isBengali) "২. চ্যানেলের লিংক বা ভিডিও টপিক লিখুন" else "2. Enter Channel Link or Topic",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = input,
                        onValueChange = onInputChange,
                        placeholder = {
                            Text(
                                text = if (isBengali) "যেমন: youtube.com/@mychannel অথবা 'টেক রিভিউ বাংলা'" else "e.g. youtube.com/@mychannel or 'Tech Review 2026'",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "Link Icon",
                                tint = BrandRed
                            )
                        },
                        trailingIcon = {
                            if (input.isNotEmpty()) {
                                IconButton(onClick = { onInputChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("analysis_input_field"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Sample Topics
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(quickTopics) { topic ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onInputChange(topic) }
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            ) {
                                Text(
                                    text = "+ $topic",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Analyze Action Button
                    Button(
                        onClick = onAnalyzeClick,
                        enabled = !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("analyze_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandRed
                        )
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBengali) "অ্যালগরিদম বিশ্লেষণ হচ্ছে..." else "Analyzing Algorithm...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBengali) "এনালাইসিস করুন (Start Analysis)" else "Run Deep AI Analysis",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Analysis Results Section
        if (analysisResult != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "📊 বিশ্লেষণ ফলাফল ও নির্দেশিকা" else "📊 Analysis Insights & Roadmap",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onExportCsv,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FileDownload,
                                contentDescription = "Export CSV",
                                tint = EmeraldGreen
                            )
                        }
                        IconButton(
                            onClick = onExportPdf,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Share Report",
                                tint = ElectricViolet
                            )
                        }
                    }
                }
            }

            // Virality & Reach Metric Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ViralityScoreGauge(
                            score = analysisResult.viralityScore,
                            isBengali = isBengali
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (isBengali) "সম্ভাব্য অডিয়েন্স রিচ:" else "Estimated Reach:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = analysisResult.reachEstimate,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Column {
                                Text(
                                    text = if (isBengali) "প্রত্যাশিত এনগেজমেন্ট:" else "Expected Engagement:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = analysisResult.engagementRate,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ScoreGreen
                                )
                            }

                            Column {
                                Text(
                                    text = if (isBengali) "টার্গেট ডেমোগ্রাফিক:" else "Target Audience:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = analysisResult.targetAudience,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Secret Recipe & Viral Ingredients (ভিডিওতে কি কি মসলা থাকতে হবে)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AmberGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isBengali) "ভিডিও ভাইরাল করার মসলা (Secret Formula)" else "Viral Ingredients & Retention Formula",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isBengali) "ভিডিওতে অবশ্যই যা যা উপাদান থাকতে হবে" else "Essential high-retention components",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        analysisResult.viralIngredients.forEachIndexed { index, ing ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${index + 1}. ${ing.title}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = BrandRed.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = ing.importance,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BrandRed,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = ing.description,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Hook Suggestions (প্রথম ৩ সেকেন্ডের হুক)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = NeonCyan
                            )
                            Text(
                                text = if (isBengali) "প্রথম ৩ সেকেন্ডের ভাইরাল হুক আইডিয়া" else "Top 3-Second Opening Hook Lines",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        analysisResult.hooksSuggestions.forEach { hook ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { onCopyText(hook) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = hook,
                                    fontSize = 12.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Best Upload Times & Strategy Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isBengali) "⏰ ভিডিও আপলোডের সেরা সময় ও স্ট্র্যাটেজি" else "⏰ Best Upload Times & Content Format",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = if (isBengali) "📅 সেরা দিনসমূহ:" else "📅 Best Days:",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = analysisResult.bestUploadDays,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandRed
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = if (isBengali) "🕒 সেরা সময় (Peak Hours):" else "🕒 Prime Time:",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = analysisResult.bestUploadTimes,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricViolet
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isBengali) "🎬 সাজেস্টেড কনটেন্ট ফরম্যাট:" else "🎬 Recommended Content Format:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = analysisResult.recommendedContentType,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "💡 ${analysisResult.thumbnailTip}",
                                    fontSize = 11.sp,
                                    color = AmberGold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
