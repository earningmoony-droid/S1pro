package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AnalysisEntity
import com.example.data.local.SeoProjectEntity
import com.example.data.model.ChartPoint
import com.example.data.model.PerformanceMetric
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    isBengali: Boolean,
    isDarkMode: Boolean,
    onToggleLang: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenFeedback: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(BrandRed, ElectricViolet)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "App Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "SocialGrow AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isBengali) "ক্রিয়েটর গ্রোথ ও এসইও স্টুডিও" else "Creator SEO & Growth Studio",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            // Language Switcher
            FilledTonalButton(
                onClick = onToggleLang,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .height(34.dp)
                    .testTag("lang_toggle_button"),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isBengali) "বাংলা" else "EN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Dark Mode Toggle
            IconButton(
                onClick = onToggleDarkMode,
                modifier = Modifier.testTag("dark_mode_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Saved Library
            IconButton(
                onClick = onOpenHistory,
                modifier = Modifier.testTag("history_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.FolderSpecial,
                    contentDescription = "Saved Offline Library",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Feedback
            IconButton(
                onClick = onOpenFeedback,
                modifier = Modifier.testTag("feedback_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Feedback,
                    contentDescription = "Feedback",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun ViralityScoreGauge(
    score: Int,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "scoreProgress"
    )

    val gaugeColor = when {
        score >= 85 -> ScoreGreen
        score >= 70 -> ScoreYellow
        else -> ScoreRed
    }

    Box(
        modifier = modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(diameter, diameter)

            // Background Track Arc (240 degrees)
            drawArc(
                color = Color(0x22888888),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress Arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        BrandRed,
                        AmberGold,
                        gaugeColor
                    )
                ),
                startAngle = 150f,
                sweepAngle = 240f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$score%",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = gaugeColor
            )
            Text(
                text = if (isBengali) "ভাইরাল সম্ভাবনা" else "Virality Score",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatMetricCard(
    metric: PerformanceMetric,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = metric.label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metric.value,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (metric.isPositive) ScoreGreen.copy(alpha = 0.15f) else ScoreRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = metric.change,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (metric.isPositive) ScoreGreen else ScoreRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsCanvasChart(
    chartPoints: List<ChartPoint>,
    isBengali: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) "📈 গত ৭ দিনের ভিউ ও সিটিআর ট্রেন্ড" else "📈 7-Day Views & CTR Velocity",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrandRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isBengali) "রিয়েল-টাইম" else "Live Growth",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (chartPoints.isEmpty()) return@Canvas

                val maxVal = (chartPoints.maxOfOrNull { it.views } ?: 50000f).coerceAtLeast(1000f)
                val widthPerStep = size.width / (chartPoints.size - 1)

                val linePath = Path()
                val fillPath = Path()

                chartPoints.forEachIndexed { index, point ->
                    val x = index * widthPerStep
                    val y = size.height - (point.views / maxVal) * (size.height * 0.85f)

                    if (index == 0) {
                        linePath.moveTo(x, y)
                        fillPath.moveTo(x, size.height)
                        fillPath.lineTo(x, y)
                    } else {
                        val prevX = (index - 1) * widthPerStep
                        val prevY = size.height - (chartPoints[index - 1].views / maxVal) * (size.height * 0.85f)
                        val midX = (prevX + x) / 2f
                        linePath.cubicTo(midX, prevY, midX, y, x, y)
                        fillPath.cubicTo(midX, prevY, midX, y, x, y)
                    }
                }

                fillPath.lineTo(size.width, size.height)
                fillPath.close()

                // Draw Gradient Fill under line
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BrandRed.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )

                // Draw main curve line
                drawPath(
                    path = linePath,
                    brush = Brush.horizontalGradient(
                        listOf(BrandRed, ElectricViolet, NeonCyan)
                    ),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw Point markers
                chartPoints.forEachIndexed { index, point ->
                    val x = index * widthPerStep
                    val y = size.height - (point.views / maxVal) * (size.height * 0.85f)

                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = BrandRed,
                        radius = 2.5.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                chartPoints.forEach {
                    Text(
                        text = it.day,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TagPill(
    tag: String,
    searchVolume: String,
    competition: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                RoundedCornerShape(10.dp)
            ),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "#$tag",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (searchVolume.contains("High", ignoreCase = true)) ScoreGreen.copy(alpha = 0.15f) else ScoreYellow.copy(alpha = 0.15f)
            ) {
                Text(
                    text = searchVolume,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (searchVolume.contains("High", ignoreCase = true)) ScoreGreen else ScoreYellow,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
fun FeedbackDialog(
    show: Boolean,
    isBengali: Boolean,
    rating: Int,
    feedbackText: String,
    onRatingChange: (Int) -> Unit,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBengali) "⭐ ক্রিয়েটর ফিডব্যাক দিন" else "⭐ Give Creator Feedback",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isBengali) "আপনার এই অ্যাপটি কেমন লাগছে? আপনার মূল্যবান পরামর্শ আমাদের জানান:" else "How is your experience with SocialGrow AI? Share your thoughts:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Rating Stars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { onRatingChange(star) }) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "$star Star",
                                tint = if (star <= rating) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = onTextChange,
                    label = { Text(if (isBengali) "আপনার মতামত বা পরামর্শ..." else "Your feedback or suggestions...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = BrandRed)
            ) {
                Text(if (isBengali) "জমা দিন" else "Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBengali) "বাতিল" else "Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedHistoryBottomSheet(
    show: Boolean,
    isBengali: Boolean,
    savedAnalyses: List<AnalysisEntity>,
    savedSeoPacks: List<SeoProjectEntity>,
    onDeleteAnalysis: (Long) -> Unit,
    onDeleteSeoPack: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = if (isBengali) "📁 সংরক্ষিত অফলাইন লাইব্রেরি" else "📁 Saved Offline Library",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isBengali) "সব ডেটা অফলাইনে ডিভাইসে ক্যাশ করা থাকে" else "All analyses and SEO reports are cached on-device",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (savedAnalyses.isEmpty() && savedSeoPacks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBengali) "কোনো সংরক্ষিত ফাইল পাওয়া যায়নি" else "No saved items yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (savedAnalyses.isNotEmpty()) {
                        item {
                            Text(
                                text = if (isBengali) "চ্যানেল এনালাইসিসসমূহ (${savedAnalyses.size})" else "Channel Analyses (${savedAnalyses.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandRed
                            )
                        }
                        items(savedAnalyses, key = { "a_${it.id}" }) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.urlOrTopic,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${item.platform} • ভাইরাল স্কোর: ${item.viralityScore}%",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { onDeleteAnalysis(item.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = ScoreRed
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (savedSeoPacks.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isBengali) "সংরক্ষিত এসইও প্যাক (${savedSeoPacks.size})" else "Saved SEO Packs (${savedSeoPacks.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricViolet
                            )
                        }
                        items(savedSeoPacks, key = { "s_${it.id}" }) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.topic,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "এসইও স্কোর: ${item.seoScore}%",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { onDeleteSeoPack(item.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = ScoreRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
