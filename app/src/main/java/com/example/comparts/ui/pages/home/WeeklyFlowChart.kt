package com.example.comparts.ui.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comparts.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeeklyFlowChart(transactions: List<Transaction>) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    // Last 7 days labels
    val last7Days = (0..6).map { i ->
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -i)
        sdf.format(cal.time)
    }.reversed()

    val dayLabels = last7Days.map { dateStr ->
        val date = sdf.parse(dateStr)
        SimpleDateFormat("EEE", Locale.getDefault()).format(date!!)
    }

    // Aggregate data
    val inData = last7Days.map { date ->
        transactions.filter { it.transactionType.uppercase() == "IN" && it.transactionDate?.startsWith(date) == true }
            .sumOf { it.transactionQuantity }
    }
    
    val outData = last7Days.map { date ->
        transactions.filter { it.transactionType.uppercase() == "OUT" && it.transactionDate?.startsWith(date) == true }
            .sumOf { it.transactionQuantity }
    }

    // Calculate dynamic max value for scaling
    val rawMax = (inData + outData).maxOrNull()?.coerceAtLeast(1) ?: 1
    // Round up to nearest 10 for clean scale
    val maxVal = ((rawMax + 9) / 10) * 10

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 4.dp)
    ) {
        // Y-Axis Scale
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 24.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            Text(text = maxVal.toString(), fontSize = 10.sp, color = Color.Gray)
            Text(text = (maxVal / 2).toString(), fontSize = 10.sp, color = Color.Gray)
            Text(text = "0", fontSize = 10.sp, color = Color.Gray)
        }

        // Chart Area
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            // Horizontal Guide Lines
            Column(modifier = Modifier.fillMaxSize().padding(bottom = 24.dp), verticalArrangement = Arrangement.SpaceBetween) {
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.5f))
            }

            // Bars
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                last7Days.forEachIndexed { index, _ ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // IN Bar (Green) - Solid Rectangle
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight(fraction = (inData[index].toFloat() / maxVal).coerceIn(0f, 1f))
                                        .background(Color(0xFF00C853), RectangleShape)
                                )
                                // OUT Bar (Red) - Solid Rectangle
                                Box(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight(fraction = (outData[index].toFloat() / maxVal).coerceIn(0f, 1f))
                                        .background(Color(0xFFFF4C4C), RectangleShape)
                                )
                            }
                        }
                        // X-Axis Label
                        Text(
                            text = dayLabels[index],
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp).height(16.dp)
                        )
                    }
                }
            }
        }
    }
}
