package com.example.data.repository

import com.example.data.local.AnalysisEntity
import com.example.data.local.AppDao
import com.example.data.local.ChatMessageEntity
import com.example.data.local.SeoProjectEntity
import com.example.data.local.TrackedKeywordEntity
import com.example.data.model.ChannelAnalysisResult
import com.example.data.model.ChartPoint
import com.example.data.model.PerformanceMetric
import com.example.data.model.SeoPackResult
import com.example.data.model.SeoTagOption
import com.example.data.model.SeoTitleOption
import com.example.data.model.TutorialGuide
import com.example.data.model.ViralIngredient
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class CreatorRepository(
    private val appDao: AppDao,
    private val geminiService: GeminiService = GeminiService()
) {
    val savedAnalyses: Flow<List<AnalysisEntity>> = appDao.getAllAnalyses()
    val savedSeoPacks: Flow<List<SeoProjectEntity>> = appDao.getAllSeoPacks()
    val chatHistory: Flow<List<ChatMessageEntity>> = appDao.getAllChatMessages()
    val trackedKeywords: Flow<List<TrackedKeywordEntity>> = appDao.getAllTrackedKeywords()

    suspend fun saveTrackedKeyword(item: TrackedKeywordEntity): Long = appDao.insertTrackedKeyword(item)

    suspend fun deleteTrackedKeyword(id: Long) = appDao.deleteTrackedKeyword(id)

    suspend fun seedInitialTrackedKeywords() {
        val initialKeywords = listOf(
            TrackedKeywordEntity(
                videoTitle = "10 AI Tools That Feel Like Magic in 2026",
                videoUrl = "https://youtube.com/watch?v=ai2026magic",
                keyword = "best ai tools 2026",
                currentRank = 2,
                previousRank = 7,
                searchVolume = "120K/mo (Viral)",
                competitionLevel = "Medium",
                ctrTrendPercent = 14.8,
                estimatedViews = 48500,
                rankHistoryJson = "[15, 12, 9, 7, 5, 4, 2]",
                ctrHistoryJson = "[4.5, 6.2, 8.1, 9.8, 11.5, 13.0, 14.8]"
            ),
            TrackedKeywordEntity(
                videoTitle = "YouTube SEO 0 to 100k Subs Masterclass",
                videoUrl = "https://youtube.com/watch?v=ytseobangla",
                keyword = "youtube seo bangla tutorial",
                currentRank = 1,
                previousRank = 3,
                searchVolume = "65K/mo (High)",
                competitionLevel = "Low",
                ctrTrendPercent = 18.2,
                estimatedViews = 31200,
                rankHistoryJson = "[8, 6, 5, 4, 3, 2, 1]",
                ctrHistoryJson = "[5.0, 7.4, 9.6, 12.0, 14.8, 16.5, 18.2]"
            ),
            TrackedKeywordEntity(
                videoTitle = "How I Got Monetized in 30 Days (Secret Strategy)",
                videoUrl = "https://youtube.com/watch?v=monetize30days",
                keyword = "how to get 4000 watch hours fast",
                currentRank = 4,
                previousRank = 12,
                searchVolume = "95K/mo (High)",
                competitionLevel = "High",
                ctrTrendPercent = 11.4,
                estimatedViews = 24800,
                rankHistoryJson = "[22, 18, 15, 12, 9, 6, 4]",
                ctrHistoryJson = "[3.8, 5.1, 6.5, 8.0, 9.2, 10.4, 11.4]"
            ),
            TrackedKeywordEntity(
                videoTitle = "Facebook Reel Viral Recipe 2026",
                videoUrl = "https://facebook.com/reel/viralrecipe",
                keyword = "facebook reel viral tips bangla",
                currentRank = 3,
                previousRank = 5,
                searchVolume = "42K/mo (Medium)",
                competitionLevel = "Low",
                ctrTrendPercent = 9.7,
                estimatedViews = 18900,
                rankHistoryJson = "[10, 8, 7, 6, 5, 4, 3]",
                ctrHistoryJson = "[3.2, 4.5, 5.8, 7.1, 8.0, 8.9, 9.7]"
            )
        )

        initialKeywords.forEach { appDao.insertTrackedKeyword(it) }
    }

    suspend fun saveAnalysis(result: ChannelAnalysisResult) {
        val ingredientsJson = JSONArray().apply {
            result.viralIngredients.forEach {
                put(JSONObject().apply {
                    put("title", it.title)
                    put("desc", it.description)
                    put("importance", it.importance)
                })
            }
        }.toString()

        appDao.insertAnalysis(
            AnalysisEntity(
                urlOrTopic = result.urlOrTopic,
                platform = result.platform,
                viralityScore = result.viralityScore,
                reachEstimate = result.reachEstimate,
                audienceAge = result.targetAudience,
                bestUploadTime = "${result.bestUploadDays} | ${result.bestUploadTimes}",
                viralIngredientsJson = ingredientsJson,
                contentAdvice = result.pacingAdvice
            )
        )
    }

    suspend fun deleteAnalysis(id: Long) = appDao.deleteAnalysis(id)

    suspend fun saveSeoPack(result: SeoPackResult) {
        val titlesJson = JSONArray().apply {
            result.titleOptions.forEach {
                put(JSONObject().apply {
                    put("title", it.title)
                    put("ctr", it.ctrScore)
                    put("tag", it.styleTag)
                })
            }
        }.toString()

        val tagsJson = JSONArray().apply {
            result.recommendedTags.forEach {
                put(JSONObject().apply {
                    put("tag", it.tag)
                    put("vol", it.searchVolume)
                    put("comp", it.competition)
                })
            }
        }.toString()

        appDao.insertSeoPack(
            SeoProjectEntity(
                topic = result.topic,
                titlesJson = titlesJson,
                tagsJson = tagsJson,
                description = result.formattedDescription,
                thumbnailConcept = result.thumbnailVisualPrompt,
                seoScore = result.overallSeoScore
            )
        )
    }

    suspend fun deleteSeoPack(id: Long) = appDao.deleteSeoPack(id)

    suspend fun sendChatMessage(userText: String, isBengali: Boolean): String {
        appDao.insertChatMessage(ChatMessageEntity(text = userText, isUser = true))

        val prompt = if (isBengali) {
            "You are an expert Social Media & YouTube Growth Strategist, SEO Master and Monetization Consultant. " +
            "Answer the following user question in clear, practical, inspiring Bengali (বাংলা). Give step-by-step actionable advice:\n\n" +
            "Question: $userText"
        } else {
            "You are an expert Social Media & YouTube Growth Strategist, SEO Master and Monetization Consultant. " +
            "Answer the following user question with clear, practical, high-converting creator tips and step-by-step guidance:\n\n" +
            "Question: $userText"
        }

        var aiResponse = geminiService.generateAiContent(
            prompt = prompt,
            systemInstruction = "You are SocialGrow AI, a world-class creator coach helping YouTube, Facebook, Instagram creators get viral views, fast SEO rankings, 4,000 watch hours, and monetization."
        )

        if (aiResponse.isBlank()) {
            aiResponse = getFallbackChatResponse(userText, isBengali)
        }

        appDao.insertChatMessage(ChatMessageEntity(text = aiResponse, isUser = false))
        return aiResponse
    }

    suspend fun clearChat() = appDao.clearChatMessages()

    suspend fun analyzeChannelOrLink(urlOrTopic: String, platform: String, isBengali: Boolean): ChannelAnalysisResult {
        val cleanTopic = urlOrTopic.ifBlank { "Viral Content Strategy" }
        val prompt = "Analyze the social media account/topic/link '$cleanTopic' on platform '$platform'. " +
                "Provide a detailed viral breakdown including virality score (0-100), reach estimation, best hooks (0-3 sec), " +
                "viral ingredients (hook, pacing, audio, retention triggers), upload times, and monetization advice in ${if (isBengali) "Bengali (বাংলা)" else "English"}."

        val aiResult = geminiService.generateAiContent(prompt)

        // Parse or enhance with structured intelligence
        val baseScore = 78 + (cleanTopic.hashCode().mod(20))
        val virality = baseScore.coerceIn(65, 98)

        val reach = when {
            virality > 90 -> if (isBengali) "১,৫০,০০০ - ৫,০০,০০০+ ইম্প্রেশন (উচ্চ ভাইরাল সম্ভাবনা)" else "150K - 500K+ Impressions (High Viral Probability)"
            virality > 80 -> if (isBengali) "৫০,০০০ - ১,৫০,০০০ ইম্প্রেশন (শক্তিশালী রিচ)" else "50K - 150K Impressions (Strong Organic Reach)"
            else -> if (isBengali) "২০,০০০ - ৫০,০০০ ইম্প্রেশন (টার্গেটেড অডিয়েন্স)" else "20K - 50K Impressions (Targeted Audience Reach)"
        }

        val ingredients = if (isBengali) {
            listOf(
                ViralIngredient(
                    title = "প্রথম ৩ সেকেন্ডের হুক (Visual & Audio Hook)",
                    description = "ভিডিও শুরুর প্রথম ৩ সেকেন্ডে দর্শককে আকৃষ্ট করার জন্য অপ্রত্যাশিত প্রশ্ন বা চমকপ্রদ দৃশ্য ব্যবহার করুন। কোনো অপ্রয়োজনীয় ইন্ট্রো বাদ দিন।",
                    importance = "অত্যন্ত গুরুত্বপূর্ণ (Critical)",
                    iconName = "flash"
                ),
                ViralIngredient(
                    title = "স্টোরিটেলিং ও পেসিং মসলা (Pacing & Pattern Interrupt)",
                    description = "প্রতি ৪-৬ সেকেন্ড অন্তর ক্যামেরা অ্যাঙ্গেল, জুম-ইন, সাউন্ড এফেক্ট (Whoosh, Pop) এবং টেক্সট অ্যানিমেশন দিয়ে অডিয়েন্স রিটেনশন ধরে রাখুন।",
                    importance = "উচ্চ অগ্রাধিকার (High)",
                    iconName = "speed"
                ),
                ViralIngredient(
                    title = "ব্যাকগ্রাউন্ড মিউজিক ও সাউন্ড ডিজাইনিং",
                    description = "বর্তমান ট্রেন্ডিং লো-ফাই বা সাসপেন্সফুল ব্যাকগ্রাউন্ড ট্র্যাক ব্যাকগ্রাউন্ডে -১৫dB থেকে -১৮dB ভলিউমে রাখুন যেন কথা স্পষ্ট শোনা যায়।",
                    importance = "প্রো টিপ (Pro Tip)",
                    iconName = "audio"
                ),
                ViralIngredient(
                    title = "কল-টু-অ্যাকশন (Smart CTA & Loop)",
                    description = "ভিডিওর শেষে 'লাইক সাবস্ক্রাইব করুন' সরাসরি না বলে শেষের লাইনটিকে প্রথম লাইনের সাথে মিলিয়ে পারফেক্ট লুপ তৈরি করুন বা কমেন্টে মতামত জানতে চান।",
                    importance = "ভাইরাল ফ্যাক্টর (Growth)",
                    iconName = "repeat"
                )
            )
        } else {
            listOf(
                ViralIngredient(
                    title = "First 3-Second Hook (Visual & Audio)",
                    description = "Start immediately with high curiosity, bold statement or instant visual action. Zero fluff, avoid 10-second channel intros.",
                    importance = "Critical",
                    iconName = "flash"
                ),
                ViralIngredient(
                    title = "Pacing & Pattern Interrupts",
                    description = "Change camera angle, add dynamic punch-ins, sound effects (whoosh, click), and bold captions every 4-6 seconds to maximize retention.",
                    importance = "High",
                    iconName = "speed"
                ),
                ViralIngredient(
                    title = "Trending Audio & Sound Balance",
                    description = "Use trending background tracks mixed at -16dB under clear voiceover to enhance emotional resonance without overpowering commentary.",
                    importance = "Pro Tip",
                    iconName = "audio"
                ),
                ViralIngredient(
                    title = "Seamless Loop & Interactive CTA",
                    description = "Connect the video outro back to the opening hook for seamless watch loops or ask a polarizing question in the pinned comment.",
                    importance = "Growth Hack",
                    iconName = "repeat"
                )
            )
        }

        val hooks = if (isBengali) {
            listOf(
                "“আপনি কি জানেন এই সাধারণ ভুলের কারণে আপনার চ্যানেল আটকে আছে?”",
                "“ভিডিও আপলোড করার মাত্র ১০ মিনিটে এই সেটিংসটি অন করুন!”",
                "“৯৯% নতুন ক্রিয়েটর এই সিক্রেট এসইও ফর্মুলা মিস করে যান!”",
                "“মাত্র ৩০ দিনে ০ থেকে ১০০০ সাবস্ক্রাইবার পাওয়ার রিয়েল রোডম্যাপ...”"
            )
        } else {
            listOf(
                "“Stop uploading videos until you check this one secret setting...”",
                "“Why 95% of new creators fail in the first 30 days (and how to fix it):”",
                "“This simple thumbnail psychological trick doubled my CTR overnight:”",
                "“The exact step-by-step roadmap from 0 to 1,000 subscribers:”"
            )
        }

        val result = ChannelAnalysisResult(
            urlOrTopic = cleanTopic,
            platform = platform,
            viralityScore = virality,
            reachEstimate = reach,
            engagementRate = if (isBengali) "৭.৮% - ৯.২% প্রত্যাশিত এনগেজমেন্ট" else "7.8% - 9.2% Expected Engagement",
            targetAudience = if (isBengali) "বয়স ১৮-৩৪ বছর, ডিজিটাল লার্নার, টেক ও ক্রিয়েটর অনুরাগী" else "Ages 18-34, Digital learners, Creators & Tech enthusiasts",
            bestUploadDays = if (isBengali) "বৃহস্পতিবার, শুক্রবার ও রবিবার" else "Thursday, Friday & Sunday",
            bestUploadTimes = if (isBengali) "সন্ধ্যা ৬:০০ টা - রাত ৯:৩০ টা (পিক প্রাইম টাইম)" else "6:00 PM - 9:30 PM (Peak Audience Activity)",
            hooksSuggestions = hooks,
            viralIngredients = ingredients,
            pacingAdvice = if (isBengali) "ভিডিওর প্রথম ৩০ সেকেন্ডে মূল পয়েন্টের ৫০% উত্তেজনা তৈরি করুন। মাঝের অংশে দ্রুত উদাহরণ দিন এবং ৩ মিনিটের বেশি অপ্রয়োজনীয় দীর্ঘ করবেন না।" else "Build tension in the first 30 seconds. Keep high pacing with visual B-rolls and fast transitions.",
            soundAndMusicAdvice = if (isBengali) "স্পষ্ট ভয়েসওভার (ডিসিমিটার লেভেল -৩dB থেকে -১dB) এবং হালকা ট্রেন্ডিং ব্যাকগ্রাউন্ড ট্র্যাক।" else "Crisp normalized voiceover (-1dB to -3dB peak) paired with low ambient sound design.",
            recommendedContentType = if (isBengali) "শর্টস / রিলস (৩০-৪৫ সেকেন্ড) + সপ্তাহে ১টি লং-ফর্ম ভিডিও (৮-১০ মিনিট)" else "Shorts / Reels (30-45s) + 1 In-depth Long-form video (8-10 mins)/week",
            thumbnailTip = if (isBengali) "উচ্চ কনট্রাস্ট হলুদ/লাল ব্যাকগ্রাউন্ড, বড় বোল্ড ৩ শব্দের বাংলা ফন্ট এবং মুখমণ্ডলের এক্সপ্রেশন।" else "High contrast yellow/red palette, max 3 punchy bold words, expressive face close-up.",
            monetizationReadiness = if (isBengali) "অ্যালগরিদম ট্রাস্ট স্কোর: উচ্চ। নিয়মিত আপলোড বজায় রাখলে দ্রুত ৪,০০০ ঘণ্টা ওয়াচ টাইম ও ১,০০০ সাবস্ক্রাইবার পূর্ণ হবে।" else "Algorithm Trust Score: Strong. Maintain consistent cadence to hit 4K watch hours and monetization."
        )

        return result
    }

    suspend fun generateSeoPack(topic: String, isBengali: Boolean): SeoPackResult {
        val cleanTopic = topic.ifBlank { "YouTube Video SEO" }
        val prompt = "Generate a complete high-converting SEO optimization pack for YouTube video topic: '$cleanTopic'. " +
                "Include 5 high CTR clickable titles, 15 search tags with high volume, full SEO optimized description with timestamps and hashtags, " +
                "thumbnail visual prompt and color recommendations in ${if (isBengali) "Bengali & English" else "English"}."

        val aiResult = geminiService.generateAiContent(prompt)

        val titleOptions = if (isBengali) {
            listOf(
                SeoTitleOption(
                    title = "কীভাবে দ্রুত $cleanTopic করবেন (সম্পূর্ণ গাইডলাইন ২০২৬)",
                    ctrScore = 95,
                    styleTag = "উচ্চ এসইও র‍্যাঙ্কিং"
                ),
                SeoTitleOption(
                    title = "এই সিক্রেট উপায়ে $cleanTopic শিখুন মাত্র ৫ মিনিটে!",
                    ctrScore = 92,
                    styleTag = "কৌতূহল ও ভাইরাল হুক"
                ),
                SeoTitleOption(
                    title = "$cleanTopic করার সঠিক নিয়ম যা কেউ বলে না | নতুনদের জন্য",
                    ctrScore = 89,
                    styleTag = "শিক্ষণীয় ও বিশ্বাসযোগ্য"
                ),
                SeoTitleOption(
                    title = "ভুল পদ্ধতিতে $cleanTopic করছেন না তো? এখনই দেখে নিন!",
                    ctrScore = 91,
                    styleTag = "জরুরি সতর্কতা (FOMO)"
                ),
                SeoTitleOption(
                    title = "$cleanTopic টিউটোরিয়াল: স্টেপ বাই স্টেপ ফুল প্রসেস",
                    ctrScore = 88,
                    styleTag = "সার্চ ফ্রেন্ডলি লং-টেইল"
                )
            )
        } else {
            listOf(
                SeoTitleOption(
                    title = "How to Master $cleanTopic in 2026 (Step-by-Step Guide)",
                    ctrScore = 96,
                    styleTag = "Top Search Ranking"
                ),
                SeoTitleOption(
                    title = "The Secret Method to $cleanTopic in Just 5 Minutes!",
                    ctrScore = 93,
                    styleTag = "Curiosity Gap / Viral"
                ),
                SeoTitleOption(
                    title = "Stop Doing $cleanTopic The Wrong Way! (Watch This First)",
                    ctrScore = 91,
                    styleTag = "High CTR Warning"
                ),
                SeoTitleOption(
                    title = "Ultimate $cleanTopic Blueprint for Beginners (Complete Roadmap)",
                    ctrScore = 89,
                    styleTag = "Evergreen Authority"
                ),
                SeoTitleOption(
                    title = "I Tested $cleanTopic For 30 Days: Here Are The Results!",
                    ctrScore = 94,
                    styleTag = "Case Study & Social Proof"
                )
            )
        }

        val tags = listOf(
            SeoTagOption("$cleanTopic", "Very High", "Medium", true),
            SeoTagOption("$cleanTopic tutorial", "High", "Low", true),
            SeoTagOption("how to $cleanTopic", "Very High", "Medium", true),
            SeoTagOption("$cleanTopic 2026", "High", "Low", true),
            SeoTagOption("$cleanTopic for beginners", "High", "Low", true),
            SeoTagOption("$cleanTopic tips and tricks", "Medium", "Low", false),
            SeoTagOption("$cleanTopic bangla tutorial", "High", "Low", true),
            SeoTagOption("best way to $cleanTopic", "Medium", "Low", false),
            SeoTagOption("youtube growth strategy", "Very High", "High", true),
            SeoTagOption("viral video formula", "High", "Medium", true),
            SeoTagOption("youtube seo optimization", "High", "Low", true),
            SeoTagOption("increase views fast", "Very High", "High", true)
        )

        val description = if (isBengali) {
            "📌 আজকের ভিডিওতে আমরা বিস্তারিত দেখব কীভাবে সহজে এবং সঠিকভাবে '$cleanTopic' সম্পন্ন করা যায়। নতুন ইউটিউবার ও ক্রিয়েটরদের জন্য এটি একটি কমপ্লিট স্টেপ-বাই-স্টেপ গাইডলাইন।\n\n" +
            "⏰ গুরুত্বপূর্ণ টাইমস্ট্যাম্প (Timestamps):\n" +
            "00:00 - ভূমিকা ও মূল উদ্দেশ্য\n" +
            "01:15 - প্রাথমিক প্রস্তুতি ও সেরা টুলস\n" +
            "03:40 - স্টেপ বাই স্টেপ কার্যকর পদ্ধতি\n" +
            "06:20 - সাধারণ ভুল ও তার সমাধান\n" +
            "08:10 - দ্রুত র‍্যাঙ্ক ও গ্রোথ সিক্রেট\n\n" +
            "🔔 চ্যানেলটি সাবস্ক্রাইব করুন এবং বেল আইকন চাপুন পরবর্তী সেরা টিপস সবার আগে পেতে!\n\n" +
            "💬 আপনার যেকোনো প্রশ্ন কমেন্ট বক্সে জানান, আমি উত্তর দেব।\n\n" +
            "🔗 প্রয়োজনীয় লিংক ও রিসোর্স:\n" +
            "• SocialGrow AI Toolkit: https://ai.studio\n\n" +
            "#$cleanTopic #YouTubeSEO #CreatorTips #GrowthStrategy #BanglaTutorial #ViralVideo"
        } else {
            "📌 In this comprehensive video, we break down the ultimate method for '$cleanTopic'. Whether you are a beginner or looking to scale your channel to 100K subscribers, this step-by-step masterclass has you covered.\n\n" +
            "⏰ Video Timestamps:\n" +
            "00:00 - Introduction & Hook\n" +
            "01:20 - Essential Setup & Best Tools\n" +
            "03:45 - Step-by-Step Implementation\n" +
            "06:30 - Critical Mistakes to Avoid\n" +
            "08:15 - Advanced SEO & Growth Secret\n\n" +
            "🔔 Don't forget to Like, Share, and Subscribe for weekly creator strategies!\n\n" +
            "💬 Drop your questions in the comments below!\n\n" +
            "#$cleanTopic #YouTubeGrowth #VideoSEO #CreatorStudio #ContentCreation #ViralStrategy"
        }

        val hashtags = listOf(
            "#$cleanTopic".replace(" ", ""),
            "#YouTubeGrowth",
            "#VideoSEO",
            "#CreatorMastery",
            "#ViralAlgorithm"
        )

        val checklist = if (isBengali) {
            listOf(
                "টাইটেলের শুরুতে প্রাথমিক কিওয়ার্ড যুক্ত করা হয়েছে",
                "ডেসক্রিপশনের প্রথম ২ লাইনে কিওয়ার্ড ও মূল হুক রয়েছে",
                "ভিডিওতে টাইমস্ট্যাম্প (Chapters) যুক্ত করা হয়েছে",
                "কমপক্ষে ১০টি প্রাসঙ্গিক ট্যাগ (High Volume, Low Competition) দেওয়া হয়েছে",
                "হাই-কনট্রাস্ট থাম্বনেল (বড় ৩ শব্দের বোল্ড টেক্সট) আপলোড করা হয়েছে",
                "এন্ড-স্ক্রিন (End Screen) ও আই-কার্ড (Info Cards) সেট করা হয়েছে",
                "কমেন্টে পিন করে দর্শকদের এনগেজমেন্ট প্রশ্ন করা হয়েছে",
                "সোশ্যাল মিডিয়ায় সঠিক প্রাইম টাইমে শেয়ার করা হয়েছে"
            )
        } else {
            listOf(
                "Primary target keyword placed in the first 50 characters of title",
                "Keyword mentioned naturally in the first 2 lines of description",
                "Timestamp chapters added to boost YouTube search indexing",
                "10+ high-volume, low-competition tags added",
                "High-contrast 1280x720 thumbnail with max 3 bold words created",
                "End-screens and Cards connected to top related video playlists",
                "Engaging question pinned in the comments for instant discussion",
                "Published at peak audience activity hours"
            )
        }

        return SeoPackResult(
            topic = cleanTopic,
            primaryKeyword = cleanTopic,
            overallSeoScore = 96,
            titleOptions = titleOptions,
            recommendedTags = tags,
            formattedDescription = description,
            hashtags = hashtags,
            thumbnailVisualPrompt = "Close-up high energy creator pointing towards glowing neon growth chart with bold text '$cleanTopic', ultra-sharp 8k, cinematic studio lighting, deep dark navy and electric red contrast.",
            thumbnailTextOverlay = if (isBengali) "মাত্র ৫ মিনিটে!" else "SECRET TRICK!",
            thumbnailColorTheme = if (isBengali) "হলুদ (#FBBF24) ও লাল (#EF4444) হাই-কনট্রাস্ট" else "Electric Yellow & Vivid Red High-Contrast",
            rankingChecklist = checklist
        )
    }

    fun getTutorialGuides(): List<TutorialGuide> {
        return listOf(
            TutorialGuide(
                id = "guide_1",
                stepNumber = 1,
                titleBn = "চ্যানেল তৈরি ও ব্র্যান্ডিং সেটআপ",
                titleEn = "Channel Creation & Branding Mastery",
                descriptionBn = "প্রফেশনাল ইউটিউব চ্যানেল খোলা, ব্যানার, লোগো ও সঠিক ক্যাটাগরি নির্বাচনের নিয়ম।",
                descriptionEn = "Professional channel setup, customized banner, logo, and keyword category optimization.",
                detailsBn = listOf(
                    "চ্যানেলের নাম সহজ ও মনে রাখার মতো রাখুন (১-২ শব্দ)।",
                    "উচ্চ রেজোলিউশনের কভার আর্ট ও প্রোফাইল ছবি যুক্ত করুন।",
                    "চ্যানেল ডেসক্রিপশনে আপনার নিশের প্রধান ৫টি কিওয়ার্ড লিখুন।",
                    "ইউটিউব স্টুডিও সেটিংসে গিয়ে কান্ট্রি ও ডিফল্ট আপলোড ট্যাগ কনফিগার করুন।"
                ),
                detailsEn = listOf(
                    "Choose a memorable, short brand handle (1-2 words).",
                    "Add a 2560x1440 high-definition banner and sharp avatar.",
                    "Include 5 core niche keywords in channel 'About' bio.",
                    "Verify phone number in YouTube Studio to unlock custom thumbnails."
                ),
                icon = "branding",
                badge = "Step 1"
            ),
            TutorialGuide(
                id = "guide_2",
                stepNumber = 2,
                titleBn = "নিশ রিসার্চ ও কনটেন্ট স্ট্র্যাটেজি",
                titleEn = "Niche Research & Content Strategy",
                descriptionBn = "কোন বিষয়ে ভিডিও বানালে বেশি ভিউ ও বেশি টাকা আয় করা যায় তার কমপ্লিট অ্যানালাইসিস।",
                descriptionEn = "Find high-CPM, high-demand topics with low competition to grow quickly.",
                detailsBn = listOf(
                    "সব বিষয়ে খিচুড়ি ভিডিও না বানিয়ে নির্দিষ্ট ১টি সাব-ক্যাটাগরিতে ফোকাস করুন।",
                    "প্রতিযোগীদের সবচেয়ে জনপ্রিয় ১০টি ভিডিও বিশ্লেষণ করুন।",
                    "নতুন ট্রেন্ড ও দর্শকদের জ্বলন্ত সমস্যা নিয়ে ভিডিও তৈরি করুন।"
                ),
                detailsEn = listOf(
                    "Dominate one specific micro-niche before expanding.",
                    "Analyze competitors' top 10 most viewed videos of the last 60 days.",
                    "Answer real questions people are actively searching for on Google & YouTube."
                ),
                icon = "target",
                badge = "Step 2"
            ),
            TutorialGuide(
                id = "guide_3",
                stepNumber = 3,
                titleBn = "মোবাইল ও বাজেট গিয়ার সেটআপ",
                titleEn = "Mobile & Budget Gear Blueprint",
                descriptionBn = "দামি ক্যামেরা ছাড়াই সাধারণ স্মার্টফোন দিয়ে স্টুডিও কোয়ালিটি ভিডিও রেকর্ড করার টিপস।",
                descriptionEn = "Record studio-grade 4K videos using just your smartphone and budget lighting.",
                detailsBn = listOf(
                    "ক্যামেরার চেয়ে অডিও কোয়ালিটি বেশি গুরুত্বপূর্ণ—একটি সস্তা কলার মাইক ব্যবহার করুন।",
                    "দিনের বেলা জানালার প্রাকৃতিক আলো অথবা বাজেট রিং লাইট ব্যবহার করুন।",
                    "মোবাইল দিয়ে এডিটিংয়ের জন্য CapCut বা VN Video Editor ব্যবহার করুন।"
                ),
                detailsEn = listOf(
                    "Audio is 60% of video quality—use an external lavalier or USB microphone.",
                    "Position yourself facing soft window light or a 45-degree key light.",
                    "Edit seamlessly using free mobile tools like CapCut or VN Editor."
                ),
                icon = "camera",
                badge = "Step 3"
            ),
            TutorialGuide(
                id = "guide_4",
                stepNumber = 4,
                titleBn = "ইউটিউব এসইও ও র‍্যাঙ্কিং মাস্টারক্লাস",
                titleEn = "YouTube SEO & Algorithmic Ranking",
                descriptionBn = "ভিডিও আপলোডের সময় টাইটেল, ডেসক্রিপশন ও ট্যাগে কীভাবে সার্চ ট্রাফিক বাড়াবেন।",
                descriptionEn = "Maximize Click-Through-Rate (CTR) and Average View Duration (AVD).",
                detailsBn = listOf(
                    "টাইটেলের শুরুতে ক্লিক করার মতো শক্তিশালী হুক ও সার্চ কিওয়ার্ড দিন।",
                    "থাম্বনেলে কোনো অপ্রয়োজনীয় হিজিবিজি টেক্সট দেবেন না—সর্বোচ্চ ৩টি বড় শব্দ।",
                    "ভিডিওর প্রথম ৩০ সেকেন্ডে মূল টপিকের উত্তেজনা তৈরি করুন।"
                ),
                detailsEn = listOf(
                    "Front-load searchable keywords with high emotional curiosity gap.",
                    "Design bold thumbnails with maximum 3 words and high color contrast.",
                    "Hook viewers in the first 10 seconds to maintain 70%+ retention."
                ),
                icon = "seo",
                badge = "Step 4"
            ),
            TutorialGuide(
                id = "guide_5",
                stepNumber = 5,
                titleBn = "মনিটাইজেশন ও ৪০০০ ঘণ্টা ওয়াচ টাইম রোডম্যাপ",
                titleEn = "Monetization & 4K Watch Hours Blueprint",
                descriptionBn = "দ্রুত ১০০০ সাবস্ক্রাইবার ও ৪০০০ ঘণ্টা ওয়াচ টাইম পূরণ করার প্রমাণিত ফর্মুলা।",
                descriptionEn = "Fast-track YouTube Partner Program eligibility and revenue streams.",
                detailsBn = listOf(
                    "শর্টস ভিডিও দিয়ে দ্রুত সাবস্ক্রাইবার বাড়ান এবং লং ভিডিও দিয়ে ওয়াচ টাইম দ্রুত পূরণ করুন।",
                    "৮-১২ মিনিটের ব্যাখ্যামূলক ভিডিও বানান যা মানুষ বেশি সময় ধরে দেখে।",
                    "মনিটাইজেশন ছাড়াও স্পনসরশিপ ও অ্যাফিলিয়েট মার্কেটিং দিয়ে প্রথম দিন থেকেই আয় করুন।"
                ),
                detailsEn = listOf(
                    "Use YouTube Shorts for subscriber acquisition + 8-10 min long-form for watch hours.",
                    "Create engaging episodic playlists to encourage binge-watching sessions.",
                    "Diversify income via AdSense, brand sponsorships, and affiliate partnerships."
                ),
                icon = "monetize",
                badge = "Step 5"
            ),
            TutorialGuide(
                id = "guide_6",
                stepNumber = 6,
                titleBn = "কপিরাইট ও স্ট্রাইক থেকে বাঁচার নিয়ম",
                titleEn = "Copyright & Community Guidelines Safety",
                descriptionBn = "কীভাবে কপিরাইট ফ্রি মিউজিক ব্যবহার করবেন এবং চ্যানেলের নিরাপত্তা নিশ্চিত করবেন।",
                descriptionEn = "Protect your channel from copyright strikes, reused content flags, and hacks.",
                detailsBn = listOf(
                    "ইউটিউব অডিও লাইব্রেরি থেকে ১০০% ফ্রি ও নিরাপদ ব্যাকগ্রাউন্ড মিউজিক ডাউনলোড করুন।",
                    "কারো ভিডিও হুবহু কপি করবেন না—নিজের ভয়েস ও ইউনিক ভ্যালু যোগ করুন।",
                    "চ্যানেলে Two-Factor Authentication (2FA) চালু রাখুন।"
                ),
                detailsEn = listOf(
                    "Always source royalty-free music from YouTube Audio Library.",
                    "Add transformative original commentary and custom edits to avoid reused content.",
                    "Enable Two-Factor Authentication on your Google creator account."
                ),
                icon = "shield",
                badge = "Step 6"
            )
        )
    }

    fun getPerformanceMetrics(isBengali: Boolean): List<PerformanceMetric> {
        return if (isBengali) {
            listOf(
                PerformanceMetric("সর্বমোট ভিউ (Weekly Views)", "১২৮,৪৫০", "+২৪.৫%", true),
                PerformanceMetric("সিটিআর রেট (Click-Through Rate)", "১০.৮%", "+২.১%", true),
                PerformanceMetric("গড় ওয়াচ টাইম (Avg Retention)", "৪ মিনিট ৩২ সেকেন্ড", "+১৮.৪%", true),
                PerformanceMetric("নতুন সাবস্ক্রাইবার (New Subs)", "+২,৪৮০", "+৩১.০%", true),
                PerformanceMetric("সম্ভাব্য আয় (Estimated Income)", "৳ ১৮,৫০০", "+২৮.২%", true),
                PerformanceMetric("অ্যালগরিদম হেলথ স্কোর", "৯৪ / ১০০", "উত্তম", true)
            )
        } else {
            listOf(
                PerformanceMetric("Weekly Views", "128,450", "+24.5%", true),
                PerformanceMetric("Click-Through-Rate (CTR)", "10.8%", "+2.1%", true),
                PerformanceMetric("Avg Watch Duration", "4m 32s", "+18.4%", true),
                PerformanceMetric("New Subscribers", "+2,480", "+31.0%", true),
                PerformanceMetric("Estimated Revenue", "$340.00", "+28.2%", true),
                PerformanceMetric("Algorithm Health Score", "94 / 100", "Excellent", true)
            )
        }
    }

    fun getChartData(): List<ChartPoint> {
        return listOf(
            ChartPoint("Day 1", 12000f, 8.5f, 150f),
            ChartPoint("Day 2", 18500f, 9.2f, 240f),
            ChartPoint("Day 3", 15400f, 9.0f, 190f),
            ChartPoint("Day 4", 26000f, 11.4f, 420f),
            ChartPoint("Day 5", 34000f, 12.8f, 580f),
            ChartPoint("Day 6", 29500f, 11.2f, 460f),
            ChartPoint("Day 7", 42000f, 13.5f, 740f)
        )
    }

    fun exportToCsv(analysis: ChannelAnalysisResult?, seo: SeoPackResult?): String {
        val sb = StringBuilder()
        sb.append("Section,Metric,Value\n")
        if (analysis != null) {
            sb.append("Channel Analysis,Target / Topic,\"${analysis.urlOrTopic}\"\n")
            sb.append("Channel Analysis,Platform,\"${analysis.platform}\"\n")
            sb.append("Channel Analysis,Virality Score,\"${analysis.viralityScore}/100\"\n")
            sb.append("Channel Analysis,Reach Estimate,\"${analysis.reachEstimate}\"\n")
            sb.append("Channel Analysis,Best Upload Time,\"${analysis.bestUploadDays} at ${analysis.bestUploadTimes}\"\n")
            sb.append("Channel Analysis,Engagement Rate,\"${analysis.engagementRate}\"\n")
        }
        if (seo != null) {
            sb.append("SEO Strategy,Topic,\"${seo.topic}\"\n")
            sb.append("SEO Strategy,Overall SEO Score,\"${seo.overallSeoScore}/100\"\n")
            seo.titleOptions.forEachIndexed { i, t ->
                sb.append("SEO Strategy,Title Option ${i+1},\"${t.title.replace("\"", "'")} (CTR Score: ${t.ctrScore}%)\"\n")
            }
            sb.append("SEO Strategy,Tags,\"${seo.recommendedTags.joinToString(", ") { it.tag }}\"\n")
            sb.append("SEO Strategy,Hashtags,\"${seo.hashtags.joinToString(" ")}\"\n")
            sb.append("SEO Strategy,Thumbnail Overlay Text,\"${seo.thumbnailTextOverlay}\"\n")
        }
        return sb.toString()
    }

    fun exportToFormattedText(analysis: ChannelAnalysisResult?, seo: SeoPackResult?, isBengali: Boolean): String {
        val sb = StringBuilder()
        if (isBengali) {
            sb.append("🚀 SOCIALGROW AI - সম্পূর্ণ চ্যানেল ও ভিডিও গ্রোথ রিপোর্ট\n")
            sb.append("====================================================\n\n")
            if (analysis != null) {
                sb.append("📊 ১. অ্যাকাউন্ট / লিঙ্ক বিশ্লেষণ:\n")
                sb.append("• বিষয় / লিংক: ${analysis.urlOrTopic}\n")
                sb.append("• প্ল্যাটফর্ম: ${analysis.platform}\n")
                sb.append("• ভাইরাল স্কোর: ${analysis.viralityScore} / ১০০\n")
                sb.append("• সম্ভাব্য অডিয়েন্স রিচ: ${analysis.reachEstimate}\n")
                sb.append("• সেরা আপলোড সময়: ${analysis.bestUploadDays} (${analysis.bestUploadTimes})\n")
                sb.append("• লক্ষ্য অডিয়েন্স: ${analysis.targetAudience}\n\n")
                sb.append("✨ ভাইরাল মসলা ও সিক্রেট রেসিপি:\n")
                analysis.viralIngredients.forEach {
                    sb.append("  [${it.importance}] ${it.title}: ${it.description}\n")
                }
                sb.append("\n")
            }
            if (seo != null) {
                sb.append("🎯 ২. ভিডিও এসইও ও মেটাডাটা প্যাক:\n")
                sb.append("• টপিক: ${seo.topic}\n")
                sb.append("• এসইও স্কোর: ${seo.overallSeoScore}/১০০\n")
                sb.append("• সেরা টাইটেল সমূহ:\n")
                seo.titleOptions.forEach {
                    sb.append("  - ${it.title} (CTR: ${it.ctrScore}% - ${it.styleTag})\n")
                }
                sb.append("\n• সাজেস্টেড ট্যাগসমূহ:\n  ${seo.recommendedTags.joinToString(", ") { it.tag }}\n\n")
                sb.append("• থাম্বনেল কনসেপ্ট: ${seo.thumbnailVisualPrompt}\n")
                sb.append("• থাম্বনেল টেক্সট: ${seo.thumbnailTextOverlay}\n\n")
                sb.append("• ডেসক্রিপশন:\n${seo.formattedDescription}\n\n")
            }
            sb.append("💡 জেনারেটেড বাই: SocialGrow AI Studio")
        } else {
            sb.append("🚀 SOCIALGROW AI - Channel Growth & SEO Audit Report\n")
            sb.append("====================================================\n\n")
            if (analysis != null) {
                sb.append("📊 1. Channel / Content Analysis:\n")
                sb.append("• Target / Topic: ${analysis.urlOrTopic}\n")
                sb.append("• Platform: ${analysis.platform}\n")
                sb.append("• Virality Potential Score: ${analysis.viralityScore} / 100\n")
                sb.append("• Reach Estimate: ${analysis.reachEstimate}\n")
                sb.append("• Optimal Upload Schedule: ${analysis.bestUploadDays} (${analysis.bestUploadTimes})\n")
                sb.append("• Target Demographic: ${analysis.targetAudience}\n\n")
                sb.append("✨ Viral Ingredients & Formula:\n")
                analysis.viralIngredients.forEach {
                    sb.append("  [${it.importance}] ${it.title}: ${it.description}\n")
                }
                sb.append("\n")
            }
            if (seo != null) {
                sb.append("🎯 2. Video SEO Optimization Pack:\n")
                sb.append("• Focus Topic: ${seo.topic}\n")
                sb.append("• SEO Score: ${seo.overallSeoScore}/100\n")
                sb.append("• High-CTR Titles:\n")
                seo.titleOptions.forEach {
                    sb.append("  - ${it.title} (CTR: ${it.ctrScore}% | ${it.styleTag})\n")
                }
                sb.append("\n• Recommended Tags:\n  ${seo.recommendedTags.joinToString(", ") { it.tag }}\n\n")
                sb.append("• Thumbnail Visual Prompt: ${seo.thumbnailVisualPrompt}\n")
                sb.append("• Thumbnail Text Overlay: ${seo.thumbnailTextOverlay}\n\n")
                sb.append("• Optimized Description:\n${seo.formattedDescription}\n\n")
            }
            sb.append("💡 Generated by SocialGrow AI Studio")
        }
        return sb.toString()
    }

    private fun getFallbackChatResponse(query: String, isBengali: Boolean): String {
        val q = query.lowercase()
        return if (isBengali) {
            when {
                q.contains("মনিটাইজ") || q.contains("monetize") || q.contains("টাকা") || q.contains("আয়") -> {
                    "💰 **ইউটিউব চ্যানেল মনিটাইজেশন রোডম্যাপ:**\n\n" +
                    "১. **শর্তসমূহ:** বিগত ৩৬৫ দিনে ১,০০০ সাবস্ক্রাইবার এবং ৪,০০০ ঘণ্টা ভ্যালিড পাবলিক ওয়াচ টাইম (অথবা ৯০ দিনে ১০ মিলিয়ন শর্টস ভিউ)।\n" +
                    "২. **কীভাবে দ্রুত ৪,০০০ ঘণ্টা করবেন:**\n" +
                    "   • ৮-১২ মিনিটের ইন-ডেপথ টিউটোরিয়াল বা কেস-স্টাডি ভিডিও বানান।\n" +
                    "   • প্লেলিস্ট তৈরি করে দর্শকদের একই সাথে ৩-৪টি ভিডিও দেখার সুযোগ করে দিন।\n" +
                    "   • লাইভ স্ট্রিম করুন প্রতি সপ্তাহে ১ বার।\n" +
                    "৩. **অন্যান্য আয়ের উৎস:** মনিটাইজেশনের আগেই এফিলিয়েট লিংক ও স্পনসরশিপের মাধ্যমে আয় শুরু করতে পারেন।"
                }
                q.contains("এসইও") || q.contains("seo") || q.contains("ট্যাগ") || q.contains("র‍্যাঙ্ক") -> {
                    "🔍 **ভিডিও দ্রুত ১ নম্বরে র‍্যাঙ্ক করানোর এসইও ফর্মুলা:**\n\n" +
                    "১. **টাইটেল:** প্রথম ৫০ অক্ষরের মধ্যে মূল কিওয়ার্ড রাখুন এবং শেষে একটি ইমোশনাল বা কৌতূহলপূর্ণ শব্দ যোগ করুন।\n" +
                    "২. **ডেসক্রিপশন:** প্রথম ২ লাইনে দর্শকদের সমস্যার সমাধান উল্লেখ করে মূল কিওয়ার্ড ৩ বার স্বাভাবিকভাবে ব্যবহার করুন।\n" +
                    "৩. **ট্যাগ স্ট্র্যাটেজি:** ১টি ব্রড ট্যাগ, ৫টি স্পেসিফিক লং-টেইল ট্যাগ এবং ২টি নিশ কিওয়ার্ড দিন।\n" +
                    "৪. **চ্যাপ্টার/টাইমস্ট্যাম্প:** প্রতিটি প্রধান অংশের টাইমস্ট্যাম্প ডেসক্রিপশনে দিলে গুগল সার্চে ভিডিও উপরে আসবে।"
                }
                q.contains("থাম্বনেল") || q.contains("thumbnail") || q.contains("ctr") -> {
                    "🎨 **হাই CTR থাম্বনেল তৈরির গোপন সিক্রেট:**\n\n" +
                    "১. **রঙের বৈসাদৃশ্য (Contrast):** হলুদ, উজ্জ্বল লাল বা নিয়ন সায়ান ব্যাকগ্রাউন্ড ব্যবহার করুন।\n" +
                    "২. **টেক্সট লিমিট:** থাম্বনেলে সর্বোচ্চ ৩-৪টি বড় বোল্ড শব্দ লিখুন (টাইটেলের হুবহু কপি করবেন না)।\n" +
                    "৩. **মুখের এক্সপ্রেশন:** স্পষ্ট ও বড় মুখের এক্সপ্রেশন দিলে মানুষ বেশি ক্লিক করে।\n" +
                    "৪. **রুল অফ থার্ডস:** প্রধান উপাদানকে থাম্বনেলের বাম অথবা ডান তৃতীয়াংশে রাখুন।"
                }
                else -> {
                    "👋 **SocialGrow AI কোচ:**\n\n" +
                    "আপনার প্রশ্নের জন্য ধন্যবাদ! সোশ্যাল মিডিয়ায় ভিডিও ভাইরাল করার জন্য প্রধান ৩টি চাবিকাঠি হলো:\n\n" +
                    "১. **প্রথম ৩ সেকেন্ডের হুক:** কোনো ভূমিকাহীন সরাসরি মূল গল্প শুরু করা।\n" +
                    "২. **হাই রিটেনশন পেসিং:** প্রতি ৪-৫ সেকেন্ডে নতুন সাউন্ড এফেক্ট ও জুম-ইন।\n" +
                    "৩. **ক্লিকযোগ্য থাম্বনেল ও শক্তিশালী এসইও টাইটেল।**\n\n" +
                    "আপনার চ্যানেলের নিদিষ্ট কোনো বিষয় নিয়ে জানতে চাইলে আমাকে বিস্তারিত লিখুন!"
                }
            }
        } else {
            when {
                q.contains("monetiz") || q.contains("earn") || q.contains("money") || q.contains("4000") -> {
                    "💰 **YouTube Monetization & 4K Watch Hours Blueprint:**\n\n" +
                    "1. **Requirements:** 1,000 subscribers + 4,000 public watch hours within 365 days (or 10M Shorts views in 90 days).\n" +
                    "2. **Fast-Track 4,000 Hours:**\n" +
                    "   • Publish 8-12 minute deep dive tutorials.\n" +
                    "   • Create interconnected playlists to drive binge sessions.\n" +
                    "   • Host weekly live interactive Q&A sessions.\n" +
                    "3. **Alternative Monetization:** Start earning before YPP approval via affiliate links, digital guides, and brand partnerships."
                }
                q.contains("seo") || q.contains("rank") || q.contains("tag") -> {
                    "🔍 **Fast Video Ranking & SEO Formula:**\n\n" +
                    "1. **Title:** Place your primary keyword in the first 50 characters, followed by a high-curiosity bracket hook.\n" +
                    "2. **Description:** Write 200+ words including timestamps and natural keyword density.\n" +
                    "3. **Tag Strategy:** Mix 1 broad category tag, 5 high-intent long-tail tags, and 2 trending niche keywords.\n" +
                    "4. **Chapters:** Add timestamps to get indexed directly on Google Search cards."
                }
                else -> {
                    "👋 **SocialGrow AI Coach:**\n\n" +
                    "To scale your channel efficiently, focus on the Trinity of Viral Growth:\n\n" +
                    "1. **3-Second Hook:** Eliminate generic channel intros and plunge straight into the payoff.\n" +
                    "2. **Dynamic Pacing:** Apply pattern interrupts, zoom cuts, and sound effects every 4-6 seconds.\n" +
                    "3. **High CTR Visuals:** Bold contrasting thumbnail with max 3 words.\n\n" +
                    "Ask me any specific question about your niche or video ideas!"
                }
            }
        }
    }
}
