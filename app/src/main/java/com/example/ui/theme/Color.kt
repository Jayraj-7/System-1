package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// THE ULTRA-MINIMAL STUDY OS COLOR PALETTE
// ==========================================

val DeepBlack = Color(0xFF0B0F14)     // Main App Screen Base Background
val Graphite = Color(0xFF111827)      // Main Card Surface, Sidebars, Modals
val ContentGrey = Color(0xFF1F2937)   // Lighter Graphite for card nesting and containers

// Text Colours
val MainText = Color(0xFFE5E7EB)      // Readable High-Contrast Grey
val SecondaryText = Color(0xFF9CA3AF) // Subtitle and Metadata Grey
val DarkText = Color(0xFF4B5563)      // System Terminal Muted Grey

// Functional Minimal Accent Lights
val ElectricBlue = Color(0xFF3B82F6)  // Primary interactions, buttons, timers, standard focus
val NeonPurple = Color(0xFF8B5CF6)    // Progression, Leaderboard, Leveling, XP metrics
val CrimsonRed = Color(0xFFEF4444)    // Overdue alerts, high intensity threat bosses
val EmeraldGreen = Color(0xFF10B981)  // Defeated states, completed items, mastered nodes

// ==========================================
// COMPATIBILITY ALIASES (PREVENT BUILD BREAKS)
// ==========================================
val HudBlack = DeepBlack
val HudDarkGrey = Graphite
val HudCardGrey = ContentGrey
val HudBorderCyan = ElectricBlue.copy(alpha = 0.15f)

val NeonCyan = ElectricBlue
val NeonRed = CrimsonRed
val TacticalAmber = NeonPurple
val CyberPurple = NeonPurple

val TextWhite = MainText
val TextMuted = SecondaryText
val TextDark = DarkText
