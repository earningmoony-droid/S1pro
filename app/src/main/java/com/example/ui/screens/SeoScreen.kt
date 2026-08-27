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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SeoPackResult
import com.example.ui.components.InteractiveSeoChecklist
import com.example.ui.components.TagPill
import com.example.ui.theme.*

@Composable
fun SeoScreen(
    topicInput: String,
    isGenerating: Boolean,
    seoResult: SeoPackResult?,
    checkedIndices: Set<Int>,
    isBengali: Boolean,
    onTopicChange: (String) -> Unit,
    onGenerateClick: () -> Unit,
    onToggleChecklist: (Int) -> Unit,
    onCopyText: (String) -> Unit,
    onCopyAllTags: (String) -> Unit
) {
    val sampleTopics = if (isBengali) {
        listOf("ইউটিউব এসইও টিউটোরিয়াল", "স্মার্টফোন দিয়ে ভিডিও এডিটিং", "ফেসবুক পেজ মনিটাইজেশন", "ড্রপশিপিং ব্যবসা শুরু", "AI দিয়ে কার্টুন ভিডিও তৈরি")
    } else {
        listOf("YouTube SEO Masterclass", "Mobile Video Editing", "Facebook Monetization 2026", "AI Faceless Video Blueprint", "Python for Beginners")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Banner
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
                                listOf(ElectricViolet.copy(alpha = 0.2f), NeonCyan.copy(alpha = 0.15f))
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
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = ElectricViolet
                            )
                            Text(
                                text = if (isBengali) "পার্সোনালাইজড এসইও ও মেটাডাটা স্টুডিও" else "Personalized Video SEO & Metadata Studio",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBengali)
                                "হাই-ক্লিক থ্রু রেট (CTR) টাইটেল, র‍্যাঙ্কিং ট্যাগ, অপ্টিমাইজড ডেসক্রিপশন, থাম্বনেল সাইকোলজি এবং ৮-ধাপের এসইও চেকলিস্ট মুহূর্তেই তৈরি করুন।"
                            else
                                "Generate high-converting CTR titles, ranking search tags, timestamped descriptions, thumbnail concepts & ranking checklists.",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBengali) "ভিডিও টপিক বা প্রধান কিওয়ার্ড লিখুন" else "Enter Video Topic or Primary Keyword",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = onTopicChange,
                        placeholder = {
                            Text(
                                text = if (isBengali) "যেমন: 'ইউটিউব চ্যানেল খোলার নিয়ম'" else "e.g. 'How to start a YouTube channel'",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Tag,
                                contentDescription = "Topic",
                                tint = ElectricViolet
                            )
                        },
                        trailingIcon = {
                            if (topicInput.isNotEmpty()) {
                                IconButton(onClick = { onTopicChange("") }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("seo_topic_input_field"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Sample Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(sampleTopics) { sample ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onTopicChange(sample) }
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            ) {
                                Text(
                                    text = "+ $sample",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onGenerateClick,
                        enabled = !isGenerating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_seo_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBengali) "এসইও মেটাডাটা তৈরি হচ্ছে..." else "Generating SEO Pack...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBengali) "সম্পূর্ণ এসইও প্যাক তৈরি করুন (Generate SEO)" else "Generate Complete SEO Suite",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // SEO Results Section
        if (seoResult != null) {
            // Overall Score Card
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
                        Column {
                            Text(
                                text = if (isBengali) "সামগ্রিক এসইও অপ্টিমাইজেশন স্কোর" else "Overall Video SEO Optimization Score",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isBengali) "সার্চ অ্যালগরিদম ও ট্রেন্ডিং ফ্যাক্টর অনুযায়ী" else "Based on search volume, intent & CTR prediction",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = ScoreGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${seoResult.overallSeoScore}/100",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = ScoreGreen,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Title Optimizer Section
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
                            Icon(imageVector = Icons.Default.Title, contentDescription = null, tint = BrandRed)
                            Text(
                                text = if (isBengali) "উচ্চ CTR ও ভাইরাল টাইটেলসমূহ" else "High-CTR Optimized Title Options",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        seoResult.titleOptions.forEachIndexed { i, opt ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onCopyText(opt.title) }
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${i + 1}. ${opt.title}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = ElectricViolet.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = opt.styleTag,
                                                    fontSize = 10.sp,
                                                    color = ElectricViolet,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                text = "সম্ভাব্য CTR: ${opt.ctrScore}%",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ScoreGreen
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { onCopyText(opt.title) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tag Generator & Keyword Analysis Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null, tint = EmeraldGreen)
                                Text(
                                    text = if (isBengali) "ভিডিও ট্যাগ ও কিওয়ার্ড এনালাইসিস" else "Video Tags & Keyword Analysis",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            FilledTonalButton(
                                onClick = {
                                    val allTags = seoResult.recommendedTags.joinToString(", ") { it.tag }
                                    onCopyAllTags(allTags)
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = if (isBengali) "সব কপি করুন" else "Copy All",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Flow layout simulation for tags
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            seoResult.recommendedTags.chunked(2).forEach { rowTags ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    rowTags.forEach { tag ->
                                        TagPill(
                                            tag = tag.tag,
                                            searchVolume = tag.searchVolume,
                                            competition = tag.competition,
                                            onClick = { onCopyText(tag.tag) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Thumbnail Visual Strategy & AI Prompt
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = AmberGold)
                            Text(
                                text = if (isBengali) "থাম্বনেল সাইকোলজি ও ডিজাইন গাইডলাইন" else "Thumbnail Psychology & Design Prompt",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isBengali) "🎨 প্রস্তাবিত কালার থিম:" else "🎨 Color Palette:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = seoResult.thumbnailColorTheme,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (isBengali) "🔤 থাম্বনেল টেক্সট ওভারলে (সর্বোচ্চ ৩ শব্দ):" else "🔤 Overlay Bold Text (Max 3 Words):",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "“${seoResult.thumbnailTextOverlay}”",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrandRed
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (isBengali) "🤖 AI ইমেজ জেনারেশন প্রম্পট:" else "🤖 AI Visual Generation Prompt:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = seoResult.thumbnailVisualPrompt,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { onCopyText(seoResult.thumbnailVisualPrompt) }) {
                                        Icon(imageVector = Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = if (isBengali) "প্রম্পট কপি করুন" else "Copy Prompt", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Description Optimizer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = NeonCyan)
                                Text(
                                    text = if (isBengali) "এসইও অপ্টিমাইজড ডেসক্রিপশন" else "SEO-Optimized Description",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(
                                onClick = { onCopyText(seoResult.formattedDescription) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = seoResult.formattedDescription,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // Reusable 8-Step Pre-Upload Interactive SEO Checklist
            item {
                InteractiveSeoChecklist(
                    checkedIndices = checkedIndices,
                    isBengali = isBengali,
                    onToggleItem = onToggleChecklist
                )
            }
        }
    }
}
