package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChartPoint
import com.example.data.model.PerformanceMetric
import com.example.ui.components.AnalyticsCanvasChart
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    metrics: List<PerformanceMetric>,
    chartPoints: List<ChartPoint>,
    isBengali: Boolean,
    onExportCsv: () -> Unit,
    onExportPdf: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Dashboard Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(EmeraldGreen.copy(alpha = 0.18f), NeonCyan.copy(alpha = 0.15f))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.InsertChart,
                                    contentDescription = null,
                                    tint = EmeraldGreen
                                )
                                Text(
                                    text = if (isBengali) "রিয়েল-টাইম পারফরম্যান্স ড্যাশবোর্ড" else "Real-Time Performance Dashboard",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = onExportCsv,
                                    modifier = Modifier.size(34.dp).testTag("export_csv_dashboard")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.FileDownload,
                                        contentDescription = "Export CSV",
                                        tint = EmeraldGreen
                                    )
                                }
                                IconButton(
                                    onClick = onExportPdf,
                                    modifier = Modifier.size(34.dp).testTag("export_pdf_dashboard")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Share,
                                        contentDescription = "Share Report",
                                        tint = CyberPurple
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBengali)
                                "আপনার চ্যানেলের সামগ্রিক স্বাস্থ্য, ভিউ ভেলোসিটি, সিটিআর (CTR), অডিয়েন্স রিটেনশন এবং ৪,০০০ ঘণ্টা ওয়াচ টাইমের অগ্রগতি ট্র্যাকিং।"
                            else
                                "Track organic impression velocity, click-through-rates, viewer retention curves and YouTube monetization milestones.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Metrics Grid (2x3)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                metrics.chunked(2).forEach { rowMetrics ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowMetrics.forEach { metric ->
                            StatMetricCard(
                                metric = metric,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Visual Canvas Chart (Graphical Representation)
        item {
            AnalyticsCanvasChart(
                chartPoints = chartPoints,
                isBengali = isBengali
            )
        }

        // Monetization Milestones Tracker (YPP 1K Subs & 4K Watch Hours)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = AmberGold)
                        Text(
                            text = if (isBengali) "মনিটাইজেশন লক্ষ্য ট্র্যাকার (YPP Tracker)" else "YouTube Partner Program (YPP) Tracker",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Milestone 1: Subscribers
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBengali) "১,০০০ সাবস্ক্রাইবার লক্ষ্য:" else "1,000 Subscribers Goal:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "780 / 1,000 (78%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandRed
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.78f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BrandRed,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }

                    // Milestone 2: Watch Hours
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBengali) "৪,০০০ ঘণ্টা পাবলিক ওয়াচ টাইম:" else "4,000 Public Watch Hours:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "2,840 / 4,000 (71%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricViolet
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.71f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = ElectricViolet,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircleOutline, contentDescription = null, tint = ScoreGreen)
                            Text(
                                text = if (isBengali)
                                    "বর্তমান ভিউ বৃদ্ধির গতি অব্যাহত থাকলে আগামী ৪৫ দিনের মধ্যে মনিটাইজেশন শর্ত পূরণ হবে।"
                                else
                                    "At current audience growth velocity, you will qualify for AdSense monetization in ~45 days.",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
