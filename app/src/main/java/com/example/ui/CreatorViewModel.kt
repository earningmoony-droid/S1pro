package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AnalysisEntity
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.local.SeoProjectEntity
import com.example.data.local.TrackedKeywordEntity
import com.example.data.local.UserPreferencesDataStore
import com.example.data.model.ChannelAnalysisResult
import com.example.data.model.ChartPoint
import com.example.data.model.PerformanceMetric
import com.example.data.model.SeoPackResult
import com.example.data.model.TutorialGuide
import com.example.data.repository.CreatorRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CreatorUiState(
    val currentTab: Int = 0, // 0: Analysis, 1: SEO, 2: Keywords, 3: AI Coach, 4: Dashboard, 5: Academy
    val isBengali: Boolean = true,
    val isDarkMode: Boolean = true,
    
    // Analysis Screen
    val analysisInput: String = "",
    val selectedPlatform: String = "YouTube",
    val isAnalyzing: Boolean = false,
    val analysisResult: ChannelAnalysisResult? = null,
    
    // SEO Screen
    val seoTopicInput: String = "",
    val isGeneratingSeo: Boolean = false,
    val seoResult: SeoPackResult? = null,
    val checkedChecklistIndices: Set<Int> = emptySet(),
    
    // AI Chat Screen
    val chatInput: String = "",
    val isSendingChat: Boolean = false,
    
    // Dashboard & Academy
    val performanceMetrics: List<PerformanceMetric> = emptyList(),
    val chartData: List<ChartPoint> = emptyList(),
    val tutorialGuides: List<TutorialGuide> = emptyList(),
    
    // Feedback & Settings
    val showFeedbackDialog: Boolean = false,
    val feedbackText: String = "",
    val feedbackRating: Int = 5,
    val feedbackSubmitted: Boolean = false,
    val showSavedHistorySheet: Boolean = false
)

class CreatorViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = CreatorRepository(database.appDao())
    private val preferencesDataStore = UserPreferencesDataStore(application)

    private val _uiState = MutableStateFlow(CreatorUiState())
    val uiState: StateFlow<CreatorUiState> = _uiState.asStateFlow()

    val savedAnalyses: StateFlow<List<AnalysisEntity>> = repository.savedAnalyses
        .let { flow ->
            val state = MutableStateFlow<List<AnalysisEntity>>(emptyList())
            viewModelScope.launch { flow.collect { state.value = it } }
            state.asStateFlow()
        }

    val savedSeoPacks: StateFlow<List<SeoProjectEntity>> = repository.savedSeoPacks
        .let { flow ->
            val state = MutableStateFlow<List<SeoProjectEntity>>(emptyList())
            viewModelScope.launch { flow.collect { state.value = it } }
            state.asStateFlow()
        }

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatHistory
        .let { flow ->
            val state = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
            viewModelScope.launch { flow.collect { state.value = it } }
            state.asStateFlow()
        }

    val trackedKeywords: StateFlow<List<TrackedKeywordEntity>> = repository.trackedKeywords
        .let { flow ->
            val state = MutableStateFlow<List<TrackedKeywordEntity>>(emptyList())
            viewModelScope.launch { flow.collect { state.value = it } }
            state.asStateFlow()
        }

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        // Observe DataStore Preferences for Dark Mode and Language
        viewModelScope.launch {
            preferencesDataStore.isDarkMode.collect { isDark ->
                _uiState.value = _uiState.value.copy(isDarkMode = isDark)
            }
        }
        viewModelScope.launch {
            preferencesDataStore.isBengali.collect { isBn ->
                _uiState.value = _uiState.value.copy(isBengali = isBn)
                refreshDashboardAndAcademy()
            }
        }

        // Seed sample tracked keywords if database table is empty
        viewModelScope.launch {
            val currentList = repository.trackedKeywords.first()
            if (currentList.isEmpty()) {
                repository.seedInitialTrackedKeywords()
            }
        }

        refreshDashboardAndAcademy()
        prefillInitialSample()
    }

    private fun prefillInitialSample() {
        viewModelScope.launch {
            val initial = repository.analyzeChannelOrLink("https://youtube.com/@techbangla", "YouTube", _uiState.value.isBengali)
            val initialSeo = repository.generateSeoPack("YouTube Channel Growth & SEO 2026", _uiState.value.isBengali)
            _uiState.value = _uiState.value.copy(
                analysisInput = "https://youtube.com/@techbangla",
                analysisResult = initial,
                seoTopicInput = "YouTube Channel Growth & SEO 2026",
                seoResult = initialSeo
            )
        }
    }

    fun setTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(currentTab = tabIndex)
    }

    fun toggleLanguage() {
        val newLang = !_uiState.value.isBengali
        viewModelScope.launch {
            preferencesDataStore.setBengali(newLang)
        }
        _uiState.value = _uiState.value.copy(isBengali = newLang)
        refreshDashboardAndAcademy()
        viewModelScope.launch {
            if (_uiState.value.analysisResult != null) {
                val updated = repository.analyzeChannelOrLink(_uiState.value.analysisInput, _uiState.value.selectedPlatform, newLang)
                _uiState.value = _uiState.value.copy(analysisResult = updated)
            }
            if (_uiState.value.seoResult != null) {
                val updatedSeo = repository.generateSeoPack(_uiState.value.seoTopicInput, newLang)
                _uiState.value = _uiState.value.copy(seoResult = updatedSeo)
            }
        }
    }

    fun toggleDarkMode() {
        val newDarkMode = !_uiState.value.isDarkMode
        viewModelScope.launch {
            preferencesDataStore.setDarkMode(newDarkMode)
        }
        _uiState.value = _uiState.value.copy(isDarkMode = newDarkMode)
    }

    fun addTrackedKeyword(keywordEntity: TrackedKeywordEntity) {
        viewModelScope.launch {
            repository.saveTrackedKeyword(keywordEntity)
            emitToast(if (_uiState.value.isBengali) "নতুন কীওয়ার্ড সফলভাবে যুক্ত হয়েছে!" else "Keyword tracked successfully in Room Database!")
        }
    }

    fun deleteTrackedKeyword(id: Long) {
        viewModelScope.launch {
            repository.deleteTrackedKeyword(id)
            emitToast(if (_uiState.value.isBengali) "কীওয়ার্ড মুছে ফেলা হয়েছে" else "Tracked keyword deleted")
        }
    }

    fun seedSampleKeywords() {
        viewModelScope.launch {
            repository.seedInitialTrackedKeywords()
            emitToast(if (_uiState.value.isBengali) "নমুনা ভাইরাল কীওয়ার্ড লোড করা হয়েছে!" else "Sample viral keywords loaded!")
        }
    }

    fun setAnalysisInput(text: String) {
        _uiState.value = _uiState.value.copy(analysisInput = text)
    }

    fun setPlatform(platform: String) {
        _uiState.value = _uiState.value.copy(selectedPlatform = platform)
    }

    fun startAnalysis() {
        val input = _uiState.value.analysisInput.trim()
        if (input.isBlank()) {
            emitToast(if (_uiState.value.isBengali) "দয়া করে একটি চ্যানেল লিংক বা বিষয় লিখুন" else "Please enter a channel link or topic")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true)
            try {
                val result = repository.analyzeChannelOrLink(input, _uiState.value.selectedPlatform, _uiState.value.isBengali)
                _uiState.value = _uiState.value.copy(analysisResult = result, isAnalyzing = false)
                repository.saveAnalysis(result)
                emitToast(if (_uiState.value.isBengali) "এনালাইসিস সফলভাবে সম্পন্ন ও সংরক্ষিত হয়েছে!" else "Analysis completed and saved offline!")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isAnalyzing = false)
                emitToast("Analysis failed: ${e.message}")
            }
        }
    }

    fun setSeoTopicInput(text: String) {
        _uiState.value = _uiState.value.copy(seoTopicInput = text)
    }

    fun generateSeo() {
        val topic = _uiState.value.seoTopicInput.trim()
        if (topic.isBlank()) {
            emitToast(if (_uiState.value.isBengali) "দয়া করে ভিডিও টপিক লিখুন" else "Please enter a video topic")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingSeo = true, checkedChecklistIndices = emptySet())
            try {
                val seo = repository.generateSeoPack(topic, _uiState.value.isBengali)
                _uiState.value = _uiState.value.copy(seoResult = seo, isGeneratingSeo = false)
                repository.saveSeoPack(seo)
                emitToast(if (_uiState.value.isBengali) "এসইও প্যাক সফলভাবে প্রস্তুত হয়েছে!" else "SEO pack generated and saved!")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isGeneratingSeo = false)
                emitToast("SEO generation failed: ${e.message}")
            }
        }
    }

    fun toggleChecklistItem(index: Int) {
        val current = _uiState.value.checkedChecklistIndices.toMutableSet()
        if (current.contains(index)) {
            current.remove(index)
        } else {
            current.add(index)
        }
        _uiState.value = _uiState.value.copy(checkedChecklistIndices = current)
    }

    fun setChatInput(text: String) {
        _uiState.value = _uiState.value.copy(chatInput = text)
    }

    fun sendChatMessage(preset: String? = null) {
        val text = preset ?: _uiState.value.chatInput.trim()
        if (text.isBlank()) return

        _uiState.value = _uiState.value.copy(chatInput = "", isSendingChat = true)
        viewModelScope.launch {
            try {
                repository.sendChatMessage(text, _uiState.value.isBengali)
            } catch (e: Exception) {
                emitToast("Chat error: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(isSendingChat = false)
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
            emitToast(if (_uiState.value.isBengali) "চ্যাট ইতিহাস মুছে ফেলা হয়েছে" else "Chat history cleared")
        }
    }

    fun copyToClipboard(text: String, label: String = "Copied text") {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        emitToast(if (_uiState.value.isBengali) "ক্লিপবোর্ডে কপি করা হয়েছে!" else "Copied to clipboard!")
    }

    fun shareExport(isCsv: Boolean) {
        val text = if (isCsv) {
            repository.exportToCsv(_uiState.value.analysisResult, _uiState.value.seoResult)
        } else {
            repository.exportToFormattedText(_uiState.value.analysisResult, _uiState.value.seoResult, _uiState.value.isBengali)
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = if (isCsv) "text/csv" else "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val shareIntent = Intent.createChooser(sendIntent, if (_uiState.value.isBengali) "রিপোর্ট শেয়ার / এক্সপোর্ট করুন" else "Share / Export Growth Report").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        getApplication<Application>().startActivity(shareIntent)
    }

    fun deleteAnalysis(id: Long) {
        viewModelScope.launch {
            repository.deleteAnalysis(id)
            emitToast(if (_uiState.value.isBengali) "এনালাইসিস মুছে ফেলা হয়েছে" else "Analysis deleted")
        }
    }

    fun deleteSeoPack(id: Long) {
        viewModelScope.launch {
            repository.deleteSeoPack(id)
            emitToast(if (_uiState.value.isBengali) "এসইও প্যাক মুছে ফেলা হয়েছে" else "SEO pack deleted")
        }
    }

    fun showFeedbackDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showFeedbackDialog = show)
    }

    fun setFeedbackRating(rating: Int) {
        _uiState.value = _uiState.value.copy(feedbackRating = rating)
    }

    fun setFeedbackText(text: String) {
        _uiState.value = _uiState.value.copy(feedbackText = text)
    }

    fun submitFeedback() {
        _uiState.value = _uiState.value.copy(showFeedbackDialog = false, feedbackSubmitted = true, feedbackText = "")
        emitToast(if (_uiState.value.isBengali) "আপনার মূল্যবান মতামতের জন্য ধন্যবাদ!" else "Thank you for your feedback!")
    }

    fun showSavedHistory(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSavedHistorySheet = show)
    }

    private fun refreshDashboardAndAcademy() {
        _uiState.value = _uiState.value.copy(
            performanceMetrics = repository.getPerformanceMetrics(_uiState.value.isBengali),
            chartData = repository.getChartData(),
            tutorialGuides = repository.getTutorialGuides()
        )
    }

    private fun emitToast(msg: String) {
        viewModelScope.launch {
            _toastEvent.emit(msg)
        }
    }
}
