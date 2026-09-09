package com.nomo.app.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nomo.app.data.local.MemoryEntity
import com.nomo.app.data.local.matchesSearch
import com.nomo.app.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    memories: List<MemoryEntity>,
    onMemoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    // Current month state
    var currentMonthCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    
    // Bottom Sheet state
    var selectedDateMemories by remember { mutableStateOf<List<MemoryEntity>?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val monthYearText = dateFormat.format(currentMonthCalendar.time)

    // Calculate days for the current month view
    val (daysInMonth, firstDayOfWeek) = remember(currentMonthCalendar) {
        val tempCal = currentMonthCalendar.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDay = tempCal.get(Calendar.DAY_OF_WEEK)
        val days = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        Pair(days, firstDay)
    }

    // Group memories by day of month (for the current displayed month/year)
    val memoriesByDay = remember(memories, currentMonthCalendar) {
        val targetMonth = currentMonthCalendar.get(Calendar.MONTH)
        val targetYear = currentMonthCalendar.get(Calendar.YEAR)
        
        memories.filter {
            val memCal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            memCal.get(Calendar.MONTH) == targetMonth && memCal.get(Calendar.YEAR) == targetYear
        }.groupBy {
            val memCal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            memCal.get(Calendar.DAY_OF_MONTH)
        }
    }

    val searchResults = remember(memories, searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else memories.filter { it.matchesSearch(searchQuery) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NomoCream)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search location, title, notes, date...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = NomoSage) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = NomoDeepCharcoal)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = NomoDeepCharcoal,
                    unfocusedTextColor = NomoDeepCharcoal,
                    focusedLabelColor = NomoDeepCharcoal,
                    unfocusedLabelColor = NomoDeepCharcoal.copy(alpha = 0.7f),
                    focusedPlaceholderColor = NomoDeepCharcoal.copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = NomoDeepCharcoal.copy(alpha = 0.5f),
                    focusedBorderColor = NomoSage,
                    unfocusedBorderColor = NomoCardBorder,
                    focusedContainerColor = NomoSurface,
                    unfocusedContainerColor = NomoSurface,
                    cursorColor = NomoDeepCharcoal
                )
            )

            if (searchQuery.isNotBlank()) {
                // Search Results View
                Text(
                    text = "Search Results (${searchResults.size})",
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NomoSage,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No memories found matching \"$searchQuery\"",
                            style = Typography.bodyMedium,
                            color = NomoDeepCharcoal.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(searchResults) { memory ->
                            SearchResultItem(
                                memory = memory,
                                onClick = { onMemoryClick(memory.id) }
                            )
                        }
                    }
                }
            } else {
                // Normal Calendar View
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val prev = currentMonthCalendar.clone() as Calendar
                        prev.add(Calendar.MONTH, -1)
                        currentMonthCalendar = prev
                    }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                    }
                    
                    Text(
                        text = monthYearText,
                        style = Typography.headlineMedium,
                        color = NomoDeepCharcoal,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = {
                        val next = currentMonthCalendar.clone() as Calendar
                        next.add(Calendar.MONTH, 1)
                        currentMonthCalendar = next
                    }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
                    }
                }

                // Days of week header
                Row(modifier = Modifier.fillMaxWidth()) {
                    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 8.dp),
                            style = Typography.bodyMedium,
                            color = NomoSage,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                // Calendar Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val emptySlots = if (firstDayOfWeek == Calendar.SUNDAY) 0 else firstDayOfWeek - 1
                    items(emptySlots) {
                        Spacer(modifier = Modifier.aspectRatio(1f))
                    }

                    items(daysInMonth) { index ->
                        val day = index + 1
                        val dayMemories = memoriesByDay[day] ?: emptyList()
                        
                        CalendarDayCell(
                            day = day,
                            memories = dayMemories,
                            onClick = {
                                if (dayMemories.isNotEmpty()) {
                                    selectedDateMemories = dayMemories
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Bottom Sheet for Dynamic Memory Grid
    if (selectedDateMemories != null) {
        val dateCal = currentMonthCalendar.clone() as Calendar
        dateCal.set(Calendar.DAY_OF_MONTH, selectedDateMemories!!.first().let { 
            Calendar.getInstance().apply { timeInMillis = it.timestamp }.get(Calendar.DAY_OF_MONTH)
        })
        val formattedDate = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(dateCal.time)

        ModalBottomSheet(
            onDismissRequest = { selectedDateMemories = null },
            sheetState = sheetState,
            containerColor = NomoCream
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Memories on $formattedDate",
                    style = Typography.titleLarge,
                    color = NomoDeepCharcoal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                DynamicMemoryGrid(
                    memories = selectedDateMemories!!,
                    onMemoryClick = onMemoryClick
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    memory: MemoryEntity,
    onClick: () -> Unit
) {
    val formattedDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(memory.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, NomoCardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NomoSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = File(memory.photoPath),
                contentDescription = memory.placeName,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = memory.dishName ?: memory.placeName,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NomoDeepCharcoal,
                    maxLines = 1
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = NomoSage,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = memory.placeName,
                        fontSize = 12.sp,
                        color = NomoSage,
                        maxLines = 1
                    )
                }

                if (!memory.note.isNullOrBlank()) {
                    Text(
                        text = memory.note,
                        fontSize = 11.sp,
                        color = NomoDeepCharcoal.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }

                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = NomoDeepCharcoal.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    memories: List<MemoryEntity>,
    onClick: () -> Unit
) {
    val hasMemories = memories.isNotEmpty()
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (hasMemories) NomoSurface else NomoCream.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = if (hasMemories) NomoSage.copy(alpha = 0.5f) else NomoCardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = hasMemories) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (hasMemories) {
            AsyncImage(
                model = File(memories.first().photoPath),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.7f
            )
            
            Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f)))
        }

        Text(
            text = day.toString(),
            style = Typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (hasMemories) androidx.compose.ui.graphics.Color.White else NomoDeepCharcoal
        )
        
        if (memories.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
                    .size(6.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(androidx.compose.ui.graphics.Color.White)
            )
        }
    }
}
