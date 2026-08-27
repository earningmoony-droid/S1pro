package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TrackedKeywordEntity
import com.example.ui.components.InteractiveSeoChecklist
import com.example.ui.theme.BrandRed
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarningOrange
import org.json.JSONArray

@Composable
fun KeywordTrackerScreen(
    trackedKeywords: List<TrackedKeywordEntity>,
    checkedChecklistIndices: Set<Int>,
    isBengali: Boolean,
    onAddKeyword: (TrackedKeywordEntity) -> Unit,
    onDeleteKeyword: (Long) -> Unit,
    onSeedSamples: () -> Unit,
    onToggleChecklist: (Int) -> Unit,
    onCopyKeyword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(0) } // 0: All, 1: Top 3, 2: Rising, 3: High Volume
    var showAddDialog by remember { mutableStateOf(false) }
    var showChecklistSheet by remember { mutableStateOf(false) }

    val filteredKeywords = remember(trackedKeywords, searchQuery, selectedFilter) {
        trackedKeywords.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.keyword.contains(searchQuery, ignoreCase = true) ||
                    item.videoTitle.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                1 -> item.currentRank <= 3
                2 -> item.currentRank < item.previousRank
                3 -> item.searchVolume.contains("Viral", ignoreCase = true) || item.searchVolume.contains("High", ignoreCase = true)
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    val topRankedCount = trackedKeywords.count { it.currentRank <= 3 }
    val totalEstViews = trackedKeywords.sumOf { it.estimatedViews }
    val avgCtr = if (trackedKeywords.isNotEmpty()) {
        trackedKeywords.map { it.ctrTrendPercent }.average()
    } else 0.0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BrandRed,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_tracked_keyword_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Keyword")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengali) "নতুন কীওয়ার্ড" else "Track Keyword",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
        ) {
            // Header Overview Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("keyword_tracker_overview_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        EmeraldGreen.copy(alpha = 0.15f),
                                        NeonCyan.copy(alpha = 0.10f),
                                        BrandRed.copy(alpha = 0.08f)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(BrandRed.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (isBengali) "ইউটিউব ভিডিও কীওয়ার্ড ট্র্যাকার" else "YouTube Video Keyword Tracker",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (isBengali) "রুম ডেটাবেজে সংরক্ষিত লাইভ র‍্যাঙ্কিং ও ট্রেন্ড" else "Live search rank & CTR performance trends",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { showChecklistSheet = !showChecklistSheet },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen.copy(alpha = 0.2f))
                                    .testTag("toggle_checklist_banner_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Checklist,
                                    contentDescription = "SEO Checklist",
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Grid (3 columns)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatMiniCard(
                                title = if (isBengali) "মোট ট্র্যাকড" else "Total Tracked",
                                value = "${trackedKeywords.size}",
                                subtitle = if (isBengali) "ভিডিও কিওয়ার্ড" else "Keywords",
                                color = NeonCyan,
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniCard(
                                title = if (isBengali) "টপ ৩ র‍্যাঙ্ক" else "Top 3 Rank",
                                value = "$topRankedCount",
                                subtitle = if (isBengali) "শীর্ষ অবস্থানে" else "In Top 3",
                                color = GoldYellow,
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniCard(
                                title = if (isBengali) "গড় CTR গ্রোথ" else "Avg. CTR",
                                value = String.format("%.1f%%", avgCtr),
                                subtitle = if (isBengali) "সার্চ ক্লিক রেট" else "Click Rate",
                                color = EmeraldGreen,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Search Bar & Filter Chips
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("keyword_search_input"),
                        placeholder = {
                            Text(
                                text = if (isBengali) "ভিডিও নাম বা কিওয়ার্ড খুঁজুন..." else "Search video title or keyword...",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val filterLabels = if (isBengali) {
                            listOf("সকল (${trackedKeywords.size})", "টপ ৩ (#১-#৩)", "রাইজিং 🚀", "হাই ভলিউম")
                        } else {
                            listOf("All (${trackedKeywords.size})", "Top 3 (#1-#3)", "Rising 🚀", "High Volume")
                        }

                        filterLabels.forEachIndexed { index, label ->
                            FilterChip(
                                selected = selectedFilter == index,
                                onClick = { selectedFilter = index },
                                label = { Text(label, fontSize = 11.sp) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandRed.copy(alpha = 0.2f),
                                    selectedLabelColor = BrandRed
                                ),
                                modifier = Modifier.testTag("filter_chip_$index")
                            )
                        }
                    }
                }
            }

            // Collapsible Pre-Upload SEO Checklist Component Card
            item {
                AnimatedVisibility(visible = showChecklistSheet) {
                    InteractiveSeoChecklist(
                        checkedIndices = checkedChecklistIndices,
                        isBengali = isBengali,
                        onToggleItem = onToggleChecklist,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            // Empty State
            if (filteredKeywords.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                            .testTag("empty_keywords_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Filled.QueryStats,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(54.dp)
                            )
                            Text(
                                text = if (isBengali) "কোনো ট্র্যাকড কিওয়ার্ড পাওয়া যায়নি" else "No Tracked Keywords Found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isBengali) "আপনার ইউটিউব ভিডিওর সার্চ র‍্যাঙ্ক ও সিটিআর ট্র্যাক করতে নতুন কিওয়ার্ড যোগ করুন।"
                                else "Add keywords for your YouTube videos to track search rankings & CTR growth.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = onSeedSamples,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("load_sample_keywords_btn")
                            ) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBengali) "ডেমো ভাইরাল কিওয়ার্ড লোড করুন" else "Load Demo Viral Keywords",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // List of Tracked Keywords with Performance Trends
                items(filteredKeywords, key = { it.id }) { item ->
                    TrackedKeywordCard(
                        item = item,
                        isBengali = isBengali,
                        onDelete = { onDeleteKeyword(item.id) },
                        onCopy = { onCopyKeyword(item.keyword) }
                    )
                }
            }
        }
    }

    // Dialog for adding a new tracked keyword
    if (showAddDialog) {
        AddTrackedKeywordDialog(
            isBengali = isBengali,
            onDismiss = { showAddDialog = false },
            onAdd = { newKeyword ->
                onAddKeyword(newKeyword)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun StatMiniCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.Black.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.5f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun TrackedKeywordCard(
    item: TrackedKeywordEntity,
    isBengali: Boolean,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    val rankDelta = item.previousRank - item.currentRank // Positive = improved rank (e.g., 7 - 2 = +5 spots)
    val rankColor = when {
        item.currentRank == 1 -> GoldYellow
        item.currentRank <= 3 -> EmeraldGreen
        item.currentRank <= 10 -> NeonCyan
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tracked_keyword_card_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            if (item.currentRank <= 3) rankColor.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Video info & Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = "YouTube Video",
                            tint = BrandRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.videoTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = item.videoUrl,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            contentDescription = "Copy Keyword",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            Icons.Filled.DeleteOutline,
                            contentDescription = "Delete Keyword",
                            tint = BrandRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Keyword & Rank Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBengali) "টার্গেটেড কিওয়ার্ড" else "Targeted Search Keyword",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.keyword,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Rank Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = rankColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, rankColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "#${item.currentRank}",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = rankColor
                        )
                        if (rankDelta > 0) {
                            Text(
                                text = "▲ +$rankDelta",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = EmeraldGreen
                            )
                        } else if (rankDelta < 0) {
                            Text(
                                text = "▼ $rankDelta",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = BrandRed
                            )
                        } else {
                            Text(
                                text = "●",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Performance Trend Sparkline Canvas (Rank / CTR evolution)
            val rankPoints = remember(item.rankHistoryJson) {
                parseJsonIntList(item.rankHistoryJson)
            }
            val ctrPoints = remember(item.ctrHistoryJson) {
                parseJsonDoubleList(item.ctrHistoryJson)
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) "📈 র‍্যাঙ্কিং ও সিটিআর পারফরম্যান্স ট্রেন্ড (৭ দিন)" else "📈 7-Day Search Rank & CTR Trend",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "+${item.ctrTrendPercent}% CTR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Custom Performance Trend Curve Canvas
                    TrendSparklineCanvas(
                        rankPoints = rankPoints,
                        strokeColor = NeonCyan,
                        fillColor = NeonCyan.copy(alpha = 0.2f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Search Volume, Competition, Est Views
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetaBadge(
                    icon = Icons.Filled.Search,
                    label = item.searchVolume,
                    tint = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MetaBadge(
                    icon = Icons.Filled.Speed,
                    label = "${if (isBengali) "প্রতিযোগিতা" else "Comp"}: ${item.competitionLevel}",
                    tint = when (item.competitionLevel.lowercase()) {
                        "low" -> EmeraldGreen
                        "high" -> BrandRed
                        else -> WarningOrange
                    },
                    modifier = Modifier.weight(1f)
                )
                MetaBadge(
                    icon = Icons.Filled.Visibility,
                    label = "${formatNumber(item.estimatedViews)} ${if (isBengali) "ভিউ" else "views"}",
                    tint = GoldYellow,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TrendSparklineCanvas(
    rankPoints: List<Int>,
    strokeColor: Color,
    fillColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (rankPoints.size < 2) return@Canvas

        val w = size.width
        val h = size.height

        // In ranking, lower number (e.g. 1) is highest/best on screen!
        val minRank = rankPoints.minOrNull()?.toFloat() ?: 1f
        val maxRank = (rankPoints.maxOrNull()?.toFloat() ?: 20f).coerceAtLeast(minRank + 1f)

        val stepX = w / (rankPoints.size - 1)
        val path = Path()
        val fillPath = Path()

        val points = rankPoints.mapIndexed { index, rank ->
            val x = index * stepX
            // Invert so rank #1 is top (y = 4dp) and higher rank numbers are lower (y = h - 4dp)
            val normalized = (rank - minRank) / (maxRank - minRank)
            val y = 6f + (normalized * (h - 16f))
            Offset(x, y)
        }

        path.moveTo(points.first().x, points.first().y)
        fillPath.moveTo(points.first().x, h)
        fillPath.lineTo(points.first().x, points.first().y)

        for (i in 1 until points.size) {
            val p0 = points[i - 1]
            val p1 = points[i]
            val midX = (p0.x + p1.x) / 2
            path.cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
            fillPath.cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
        }

        fillPath.lineTo(points.last().x, h)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(fillColor, fillColor.copy(alpha = 0.02f)),
                startY = 0f,
                endY = h
            )
        )

        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw points on start and end
        points.forEachIndexed { index, point ->
            if (index == 0 || index == points.size - 1 || index == points.size / 2) {
                drawCircle(
                    color = strokeColor,
                    radius = 3.5.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 1.8.dp.toPx(),
                    center = point
                )
            }
        }
    }
}

@Composable
private fun MetaBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = tint.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AddTrackedKeywordDialog(
    isBengali: Boolean,
    onDismiss: () -> Unit,
    onAdd: (TrackedKeywordEntity) -> Unit
) {
    var videoTitle by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var keyword by remember { mutableStateOf("") }
    var initialRankText by remember { mutableStateOf("1") }
    var searchVolume by remember { mutableStateOf("85K/mo (High)") }
    var competition by remember { mutableStateOf("Medium") }
    var estimatedViewsText by remember { mutableStateOf("25000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Addchart, contentDescription = null, tint = BrandRed)
                Text(
                    text = if (isBengali) "নতুন ভিডিও কিওয়ার্ড ট্র্যাক করুন" else "Track YouTube Video Keyword",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = videoTitle,
                    onValueChange = { videoTitle = it },
                    label = { Text(if (isBengali) "ভিডিওর শিরোনাম" else "YouTube Video Title") },
                    placeholder = { Text("e.g. 10 AI Tools in 2026") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_dialog_video_title")
                )

                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text(if (isBengali) "ভিডিও লিংক / আইডি" else "YouTube Video URL / ID") },
                    placeholder = { Text("https://youtube.com/watch?v=...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_dialog_video_url")
                )

                OutlinedTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    label = { Text(if (isBengali) "সার্চ কিওয়ার্ড" else "Search Query / Keyword") },
                    placeholder = { Text("e.g. best ai tools bangla") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_dialog_keyword")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = initialRankText,
                        onValueChange = { initialRankText = it },
                        label = { Text(if (isBengali) "র‍্যাঙ্ক (#১-১০০)" else "Current Rank") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_dialog_rank")
                    )

                    OutlinedTextField(
                        value = competition,
                        onValueChange = { competition = it },
                        label = { Text(if (isBengali) "কম্পিটিশন" else "Competition") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_dialog_competition")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rank = initialRankText.toIntOrNull() ?: 1
                    val views = estimatedViewsText.toIntOrNull() ?: 20000
                    val prevRank = (rank + 4).coerceAtMost(50)
                    val sampleHistory = listOf(prevRank + 6, prevRank + 3, prevRank + 1, prevRank, rank + 2, rank + 1, rank)
                    val sampleCtr = listOf(4.2, 5.8, 7.4, 9.1, 11.5, 13.0, 14.8)

                    val entity = TrackedKeywordEntity(
                        videoTitle = videoTitle.ifBlank { "Untitled YouTube Video" },
                        videoUrl = videoUrl.ifBlank { "https://youtube.com" },
                        keyword = keyword.ifBlank { "youtube video seo" },
                        currentRank = rank,
                        previousRank = prevRank,
                        searchVolume = searchVolume,
                        competitionLevel = competition.ifBlank { "Medium" },
                        ctrTrendPercent = 14.8,
                        estimatedViews = views,
                        rankHistoryJson = JSONArray(sampleHistory).toString(),
                        ctrHistoryJson = JSONArray(sampleCtr).toString()
                    )
                    onAdd(entity)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandRed),
                modifier = Modifier.testTag("dialog_submit_keyword_btn")
            ) {
                Text(if (isBengali) "সংরক্ষণ করুন" else "Save Keyword", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBengali) "বাতিল" else "Cancel")
            }
        }
    )
}

private fun parseJsonIntList(json: String): List<Int> {
    return try {
        val array = JSONArray(json)
        (0 until array.length()).map { array.getInt(it) }
    } catch (e: Exception) {
        listOf(12, 10, 8, 6, 5, 3, 2)
    }
}

private fun parseJsonDoubleList(json: String): List<Double> {
    return try {
        val array = JSONArray(json)
        (0 until array.length()).map { array.getDouble(it) }
    } catch (e: Exception) {
        listOf(4.2, 6.0, 7.8, 9.5, 11.2, 13.4, 14.8)
    }
}

private fun formatNumber(num: Int): String {
    return when {
        num >= 1_000_000 -> String.format("%.1fM", num / 1_000_000f)
        num >= 1_000 -> String.format("%.1fK", num / 1_000f)
        else -> num.toString()
    }
}
