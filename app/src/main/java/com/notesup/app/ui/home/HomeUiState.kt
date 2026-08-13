package com.notesup.app.ui.home

enum class HomeFilter(val label: String) {
    All("All"),
    Pinned("Pinned"),
    Recent("Recent"),
    Projects("Projects"),
}

enum class HomeView { Grid, List }
