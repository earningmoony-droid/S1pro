package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CreatorViewModel
import com.example.ui.components.AppTopBar
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.SavedHistoryBottomSheet
import com.example.ui.screens.*
import com.example.ui.theme.BrandRed
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel: CreatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val savedAnalyses by viewModel.savedAnalyses.collectAsStateWithLifecycle()
            val savedSeoPacks by viewModel.savedSeoPacks.collectAsStateWithLifecycle()
            val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
            val trackedKeywords by viewModel.trackedKeywords.collectAsStateWithLifecycle()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.toastEvent.collectLatest { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

            MyApplicationTheme(darkTheme = uiState.isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppTopBar(
                            isBengali = uiState.isBengali,
                            isDarkMode = uiState.isDarkMode,
                            onToggleLang = { viewModel.toggleLanguage() },
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onOpenHistory = { viewModel.showSavedHistory(true) },
                            onOpenFeedback = { viewModel.showFeedbackDialog(true) }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("bottom_nav_bar"),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 6.dp
                        ) {
                            val items = if (uiState.isBengali) {
                                listOf(
                                    Triple("এনালাইসিস", Icons.Filled.Analytics, Icons.Outlined.Analytics),
                                    Triple("এসইও", Icons.Filled.Search, Icons.Outlined.Search),
                                    Triple("কীওয়ার্ড", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
                                    Triple("এআই কোচ", Icons.Filled.SmartToy, Icons.Outlined.SmartToy),
                                    Triple("ড্যাশবোর্ড", Icons.Filled.InsertChart, Icons.Outlined.InsertChart),
                                    Triple("একাডেমি", Icons.Filled.School, Icons.Outlined.School)
                                )
                            } else {
                                listOf(
                                    Triple("Analysis", Icons.Filled.Analytics, Icons.Outlined.Analytics),
                                    Triple("SEO", Icons.Filled.Search, Icons.Outlined.Search),
                                    Triple("Keywords", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
                                    Triple("AI Coach", Icons.Filled.SmartToy, Icons.Outlined.SmartToy),
                                    Triple("Dashboard", Icons.Filled.InsertChart, Icons.Outlined.InsertChart),
                                    Triple("Academy", Icons.Filled.School, Icons.Outlined.School)
                                )
                            }

                            items.forEachIndexed { index, (label, selectedIcon, unselectedIcon) ->
                                val isSelected = uiState.currentTab == index
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.setTab(index) },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) selectedIcon else unselectedIcon,
                                            contentDescription = label,
                                            tint = if (isSelected) BrandRed else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontSize = 9.sp,
                                            maxLines = 1
                                        )
                                    },
                                    modifier = Modifier.testTag("nav_tab_$index")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Crossfade(targetState = uiState.currentTab, label = "tabCrossfade") { tab ->
                            when (tab) {
                                0 -> AnalysisScreen(
                                    input = uiState.analysisInput,
                                    selectedPlatform = uiState.selectedPlatform,
                                    isAnalyzing = uiState.isAnalyzing,
                                    analysisResult = uiState.analysisResult,
                                    isBengali = uiState.isBengali,
                                    onInputChange = { viewModel.setAnalysisInput(it) },
                                    onPlatformChange = { viewModel.setPlatform(it) },
                                    onAnalyzeClick = { viewModel.startAnalysis() },
                                    onCopyText = { viewModel.copyToClipboard(it) },
                                    onExportCsv = { viewModel.shareExport(isCsv = true) },
                                    onExportPdf = { viewModel.shareExport(isCsv = false) }
                                )
                                1 -> SeoScreen(
                                    topicInput = uiState.seoTopicInput,
                                    isGenerating = uiState.isGeneratingSeo,
                                    seoResult = uiState.seoResult,
                                    checkedIndices = uiState.checkedChecklistIndices,
                                    isBengali = uiState.isBengali,
                                    onTopicChange = { viewModel.setSeoTopicInput(it) },
                                    onGenerateClick = { viewModel.generateSeo() },
                                    onToggleChecklist = { viewModel.toggleChecklistItem(it) },
                                    onCopyText = { viewModel.copyToClipboard(it) },
                                    onCopyAllTags = { viewModel.copyToClipboard(it, "All Tags") }
                                )
                                2 -> KeywordTrackerScreen(
                                    trackedKeywords = trackedKeywords,
                                    checkedChecklistIndices = uiState.checkedChecklistIndices,
                                    isBengali = uiState.isBengali,
                                    onAddKeyword = { viewModel.addTrackedKeyword(it) },
                                    onDeleteKeyword = { viewModel.deleteTrackedKeyword(it) },
                                    onSeedSamples = { viewModel.seedSampleKeywords() },
                                    onToggleChecklist = { viewModel.toggleChecklistItem(it) },
                                    onCopyKeyword = { viewModel.copyToClipboard(it) }
                                )
                                3 -> AiCoachScreen(
                                    chatInput = uiState.chatInput,
                                    isSending = uiState.isSendingChat,
                                    chatMessages = chatMessages,
                                    isBengali = uiState.isBengali,
                                    onInputChange = { viewModel.setChatInput(it) },
                                    onSendMessage = { viewModel.sendChatMessage(it) },
                                    onClearChat = { viewModel.clearChat() },
                                    onCopyText = { viewModel.copyToClipboard(it) }
                                )
                                4 -> DashboardScreen(
                                    metrics = uiState.performanceMetrics,
                                    chartPoints = uiState.chartData,
                                    isBengali = uiState.isBengali,
                                    onExportCsv = { viewModel.shareExport(isCsv = true) },
                                    onExportPdf = { viewModel.shareExport(isCsv = false) }
                                )
                                5 -> AcademyScreen(
                                    guides = uiState.tutorialGuides,
                                    isBengali = uiState.isBengali,
                                    onOpenAiCoach = { viewModel.setTab(3) }
                                )
                            }
                        }
                    }

                    // Saved Offline History Bottom Sheet
                    SavedHistoryBottomSheet(
                        show = uiState.showSavedHistorySheet,
                        isBengali = uiState.isBengali,
                        savedAnalyses = savedAnalyses,
                        savedSeoPacks = savedSeoPacks,
                        onDeleteAnalysis = { viewModel.deleteAnalysis(it) },
                        onDeleteSeoPack = { viewModel.deleteSeoPack(it) },
                        onDismiss = { viewModel.showSavedHistory(false) }
                    )

                    // Feedback Dialog
                    FeedbackDialog(
                        show = uiState.showFeedbackDialog,
                        isBengali = uiState.isBengali,
                        rating = uiState.feedbackRating,
                        feedbackText = uiState.feedbackText,
                        onRatingChange = { viewModel.setFeedbackRating(it) },
                        onTextChange = { viewModel.setFeedbackText(it) },
                        onDismiss = { viewModel.showFeedbackDialog(false) },
                        onSubmit = { viewModel.submitFeedback() }
                    )
                }
            }
        }
    }
}
