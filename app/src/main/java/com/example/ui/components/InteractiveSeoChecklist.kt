package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarningOrange

data class SeoChecklistItem(
    val id: Int,
    val stepNumber: Int,
    val category: String,
    val categoryBn: String,
    val title: String,
    val titleBn: String,
    val description: String,
    val descriptionBn: String,
    val tip: String,
    val tipBn: String
)

val defaultSeoChecklistItems = listOf(
    SeoChecklistItem(
        id = 0,
        stepNumber = 1,
        category = "Title & CTR Hook",
        categoryBn = "টাইটেল ও সিটিআর হুক",
        title = "Primary Keyword in first 30 characters",
        titleBn = "প্রথম ৩০ অক্ষরের মধ্যে প্রধান কিওয়ার্ড রাখা",
        description = "Ensure your high-volume search term is at the very front of the title so it isn't truncated on mobile screens.",
        descriptionBn = "মোবাইল স্ক্রিনে যাতে টাইটেল কেটে না যায় তাই মূল কিওয়ার্ডটি শুরুতে রাখুন।",
        tip = "Add emotional trigger brackets e.g., '[Proof]', '(Step-by-Step)', '2026 Strategy'",
        tipBn = "ব্র্যাকেটের ভেতরে পাওয়ার ওয়ার্ড যোগ করুন, যেমন: '[প্রমাণসহ]', '(২০২৬ স্পেশাল)'"
    ),
    SeoChecklistItem(
        id = 1,
        stepNumber = 2,
        category = "Thumbnail Psychology",
        categoryBn = "থাম্বনেল সাইকোলজি",
        title = "3-Word Bold Text & High Contrast Colors",
        titleBn = "সর্বোচ্চ ৩ শব্দের বড় টেক্সট ও হাই-কনট্রাস্ট কালার",
        description = "Use contrasting yellow/cyan/red on dark backgrounds and extreme facial emotion for 2x click-through rate.",
        descriptionBn = "কালো ব্যাকগ্রাউন্ডের উপর হলুদ বা নিয়ন টেক্সট এবং স্পষ্ট ফেসিয়াল এক্সপ্রেশন ব্যবহার করুন।",
        tip = "Test readability at 10% zoom (mobile view size)",
        tipBn = "মোবাইল সাইজে থাম্বনেল ছোট করে লেখার স্পষ্টতা চেক করুন"
    ),
    SeoChecklistItem(
        id = 2,
        stepNumber = 3,
        category = "Description & Google SEO",
        categoryBn = "ডেসক্রিপশন ও গুগল এসইও",
        title = "200+ Word Description with Timestamps",
        titleBn = "টাইমস্ট্যাম্প সহ ২০০+ শব্দের বিস্তারিত ডেসক্রিপশন",
        description = "Include primary and secondary keywords naturally in the first 2 lines. Add video chapter timestamps (0:00 Intro).",
        descriptionBn = "প্রথম ২ লাইনে মূল কিওয়ার্ড রাখুন এবং ভিডিও চ্যাপ্টার টাইমস্ট্যাম্প (০:০০) যোগ করুন।",
        tip = "Timestamps enable your video to appear as key moments in Google Search results",
        tipBn = "টাইমস্ট্যাম্প দিলে ভিডিওটি গুগল সার্চে সরাসরি ইনডেক্স হবে"
    ),
    SeoChecklistItem(
        id = 3,
        stepNumber = 4,
        category = "Tags & Meta Classification",
        categoryBn = "ট্যাগ ও মেটা ডাটা",
        title = "Targeted Tags & Misspelling Variations (400+ chars)",
        titleBn = "টার্গেটেড ট্যাগস ও সম্পর্কিত কিওয়ার্ড (৪০০+ অক্ষর)",
        description = "Include 1 exact match tag, 5 long-tail keywords, and 3 common Bengali/English phonetic variations.",
        descriptionBn = "১টি মূল ট্যাগ, ৫টি লং-টেল ট্যাগ এবং সাধারণ বানান ভুলের বৈচিত্র্য যোগ করুন।",
        tip = "Copy ranking tags from our SEO Suite tab directly",
        tipBn = "আমাদের এসইও স্যুট থেকে র‍্যাঙ্কিং ট্যাগগুলো এক ক্লিকে কপি করুন"
    ),
    SeoChecklistItem(
        id = 4,
        stepNumber = 5,
        category = "Audience Retention & Loops",
        categoryBn = "অডিয়েন্স রিটেনশন ও লুপ",
        title = "Zero Fluff 3s Hook & No Long Intro",
        titleBn = "প্রথম ৩ সেকেন্ডের শক্তিশালী হুক (নো বোরিং ইন্ট্রো)",
        description = "Eliminate animated logos or 'welcome back' intros. Deliver immediate visual hook within the first 3 seconds.",
        descriptionBn = "কোনো অ্যানিমেটেড লোগো বা দীর্ঘ সূচনা বাদ দিয়ে সরাসরি মূল পয়েন্ট দিয়ে শুরু করুন।",
        tip = "Keep viewer curiosity gap alive until the last 20% of the video",
        tipBn = "ভিডিওর শেষ পর্যন্ত দর্শকের কৌতূহল ধরে রাখুন"
    ),
    SeoChecklistItem(
        id = 5,
        stepNumber = 6,
        category = "Cards & End Screen Strategy",
        categoryBn = "এন্ড স্ক্রিন ও রিলেটেড কার্ড",
        title = "End Screen with 'Best for Viewer' & Playlist",
        titleBn = "এন্ড স্ক্রিনে সেরা ভিডিও ও প্লেলিস্ট লিঙ্ক সংযুক্ত করা",
        description = "Set a 20-second end screen linking to your highest-converting playlist to drive binge-watching sessions.",
        descriptionBn = "শেষ ২০ সেকেন্ডে অডিয়েন্সকে অন্য ভিডিওতে পাঠাতে এন্ড স্ক্রিন ও সাবস্ক্রাইব বাটন দিন।",
        tip = "Binge sessions tell YouTube's algorithm to promote your channel to broad audiences",
        tipBn = "একটানা ভিডিও দেখার সেশন অ্যালগরিদমে র‍্যাঙ্কিং বহুগুণ বাড়ায়"
    ),
    SeoChecklistItem(
        id = 6,
        stepNumber = 7,
        category = "Subtitles & Accessibility",
        categoryBn = "সাবটাইটেল ও বিশ্বব্যাপী পৌঁছানো",
        title = "Bengali & English Subtitles (CC)",
        titleBn = "বাংলা ও ইংরেজি সাবটাইটেল (CC) আপলোড",
        description = "Adding closed captions increases search discovery by 30% and keeps non-native viewers engaged.",
        descriptionBn = "সাবটাইটেল যোগ করলে ভিডিওটি বৈশ্বিক সার্চ ও অন্যান্য দেশের দর্শকের কাছে পৌঁছায়।",
        tip = "YouTube indexes every subtitle word for internal search matching",
        tipBn = "ইউটিউব সার্চ ইঞ্জিন সাবটাইটেলের প্রতিটি শব্দ ইনডেক্স করে"
    ),
    SeoChecklistItem(
        id = 7,
        stepNumber = 8,
        category = "Monetization & Upload Timing",
        categoryBn = "মনিটাইজেশন ও আপলোড টাইমিং",
        title = "Monetization Green Check & Peak Time Schedule",
        titleBn = "মনিটাইজেশন চেক ও সেরা পিক সময়ে শিডিউল করা",
        description = "Verify copyright checks are clear and schedule video 2 hours before your audience peak activity window.",
        descriptionBn = "কপিরাইট চেক ক্লিয়ার রাখুন এবং দর্শক সক্রিয় হওয়ার ২ ঘণ্টা আগে আপলোড শিডিউল করুন।",
        tip = "Unlist video for 1 hour first to let YouTube process 4K and auto-captions",
        tipBn = "প্রথমে ১ ঘণ্টা আনলিস্টেড রেখে সম্পূর্ণ এইচডি প্রসেসিং হতে দিন"
    )
)

/**
 * Reusable Interactive Checklist Component for Video SEO Verification before uploading.
 * Follows Material 3 design guidelines with interactive Checkboxes, progress bar, and tips.
 */
@Composable
fun InteractiveSeoChecklist(
    checkedIndices: Set<Int>,
    isBengali: Boolean,
    onToggleItem: (Int) -> Unit,
    modifier: Modifier = Modifier,
    items: List<SeoChecklistItem> = defaultSeoChecklistItems,
    onSelectAll: (() -> Unit)? = null,
    onResetAll: (() -> Unit)? = null
) {
    val totalCount = items.size
    val checkedCount = items.count { checkedIndices.contains(it.id) }
    val progressRatio = if (totalCount > 0) checkedCount.toFloat() / totalCount else 0f
    val animatedProgress by animateFloatAsState(targetValue = progressRatio, label = "seoProgress")

    val isAllCompleted = checkedCount == totalCount
    val statusColor by animateColorAsState(
        targetValue = when {
            isAllCompleted -> EmeraldGreen
            progressRatio >= 0.5f -> NeonCyan
            else -> WarningOrange
        },
        label = "statusColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("interactive_seo_checklist_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Bar
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        statusColor.copy(alpha = 0.25f),
                                        statusColor.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAllCompleted) Icons.Filled.CheckCircle else Icons.Filled.FactCheck,
                            contentDescription = "Checklist Status",
                            tint = statusColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isBengali) "ভিডিও এসইও ভেরিফিকেশন চেকলিস্ট" else "Video SEO Pre-Upload Checklist",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isBengali) "আপলোডের পূর্বে প্রতিটি পয়েন্ট নিশ্চিত করুন" else "Verify every factor for maximum viral reach",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) {
                        "অগ্রগতি: $checkedCount/$totalCount টি সম্পন্ন (${(progressRatio * 100).toInt()}%)"
                    } else {
                        "Progress: $checkedCount/$totalCount Completed (${(progressRatio * 100).toInt()}%)"
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = statusColor
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = when {
                            isAllCompleted -> if (isBengali) "🚀 আপলোডের জন্য প্রস্তুত!" else "🚀 Ready to Publish!"
                            progressRatio >= 0.5f -> if (isBengali) "⚡ ভালো হচ্ছে" else "⚡ Good Progress"
                            else -> if (isBengali) "⚠️ আরও অপ্টিমাইজেশন প্রয়োজন" else "⚠️ Needs Attention"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linear Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("seo_checklist_progress_bar"),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Actions (Select All / Reset)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (onSelectAll != null) {
                            onSelectAll()
                        } else {
                            items.forEach { onToggleItem(it.id) }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("checklist_select_all_btn"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Filled.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBengali) "সব সিলেক্ট" else "Select All",
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = {
                        if (onResetAll != null) {
                            onResetAll()
                        } else {
                            items.filter { checkedIndices.contains(it.id) }.forEach { onToggleItem(it.id) }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("checklist_reset_all_btn"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBengali) "রিসেট করুন" else "Reset All",
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Checklist Items List
            items.forEachIndexed { index, item ->
                val isChecked = checkedIndices.contains(item.id)
                var isExpanded by remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            onToggleItem(item.id)
                        }
                        .testTag("seo_checklist_item_${item.id}"),
                    shape = RoundedCornerShape(14.dp),
                    color = if (isChecked) {
                        EmeraldGreen.copy(alpha = 0.08f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    },
                    border = BorderStroke(
                        1.dp,
                        if (isChecked) EmeraldGreen.copy(alpha = 0.35f) else Color.Transparent
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Material 3 Checkbox
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onToggleItem(item.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = EmeraldGreen,
                                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("seo_checkbox_${item.id}")
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isChecked) EmeraldGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                    ) {
                                        Text(
                                            text = "Step ${item.stepNumber}: ${if (isBengali) item.categoryBn else item.category}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isChecked) EmeraldGreen else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = if (isBengali) item.titleBn else item.title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface,
                                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }

                            IconButton(
                                onClick = { isExpanded = !isExpanded },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = "Expand info",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Expanded Description & Pro Tip
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 38.dp, top = 6.dp, end = 4.dp)
                            ) {
                                Text(
                                    text = if (isBengali) item.descriptionBn else item.description,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.TipsAndUpdates,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Pro Tip: ${if (isBengali) item.tipBn else item.tip}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
