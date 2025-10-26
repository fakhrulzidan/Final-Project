package com.project.skripsi.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.ui.unit.Dp

// 1. Data class for Navigation Items (unchanged)
data class NavItem(val title: String, val icon: ImageVector)

// 2. Composable for a single Navigation Item (unchanged)
@Composable
fun RowScope.BottomNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Gray,
        animationSpec = tween(300), label = "Nav Icon Color"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = color.value,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            color = color.value,
            fontSize = 12.sp
        )
    }
}

// 3. Custom Shape for the Bottom Bar with rounded corners and the central "wave" cutout
class RoundedWavyCutoutShape(
    private val cornerRadius: Dp,
    private val cutoutRadius: Dp,
    private val waveHeight: Dp,
    private val centralOffsetFraction: Float // How much the wave dips relative to cutout center
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cornerRadiusPx = with(density) { cornerRadius.toPx() }
                val cutoutRadiusPx = with(density) { cutoutRadius.toPx() }
                val waveHeightPx = with(density) { waveHeight.toPx() }
                val centerX = size.width / 2f
                val cutoutStart = centerX - cutoutRadiusPx
                val cutoutEnd = centerX + cutoutRadiusPx

                // Start from bottom-left corner
                moveTo(0f, size.height)
                lineTo(0f, cornerRadiusPx)

                // Top-left rounded corner
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        left = 0f,
                        top = 0f,
                        right = cornerRadiusPx * 2,
                        bottom = cornerRadiusPx * 2
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Line to the start of the cutout (straight top edge)
                lineTo(cutoutStart - (cornerRadiusPx / 2), 0f) // Adjusted to start the wave earlier

                // --- Wavy Cutout Section ---
                // Start of the smooth wave dip
                cubicTo(
                    x1 = cutoutStart + (cutoutRadiusPx * 0.2f), y1 = 0f, // Control point 1
                    x2 = centerX - (cutoutRadiusPx * centralOffsetFraction), y2 = waveHeightPx, // Control point 2 (deepest part)
                    x3 = centerX, y3 = waveHeightPx
                )
                // End of the smooth wave dip
                cubicTo(
                    x1 = centerX + (cutoutRadiusPx * centralOffsetFraction), y1 = waveHeightPx, // Control point 3 (deepest part)
                    x2 = cutoutEnd - (cutoutRadiusPx * 0.2f), y2 = 0f, // Control point 4
                    x3 = cutoutEnd + (cornerRadiusPx / 2), y3 = 0f // Adjusted to end the wave later
                )
                // --- End Wavy Cutout Section ---

                // Line from end of cutout to top-right rounded corner
                lineTo(size.width - cornerRadiusPx, 0f)

                // Top-right rounded corner
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        left = size.width - cornerRadiusPx * 2,
                        top = 0f,
                        right = size.width,
                        bottom = cornerRadiusPx * 2
                    ),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Line to bottom-right corner
                lineTo(size.width, size.height)
                close() // Close the path to form the shape
            }
        )
    }
}

// 4. Composable for the Custom Bottom Navigation Bar
@Composable
fun CustomBottomNavBar() {
    val items = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Search", Icons.Default.Search),
        NavItem("Cart", Icons.Default.ShoppingCart),
        NavItem("Profile", Icons.Default.Person)
    )

    var selectedItem by remember { mutableStateOf("Home") }

    // Constants for the visual appearance
    val navBarHeight = 80.dp
    val centralIconSize = 60.dp
    val centralIconBorderWidth = 10.dp // The "grape-like" margin/outline width
    val centralIconColor = Color(0xFF673AB7) // Purple color from image
    val darkBarColor = Color(0xFF222222) // Dark background color
    val mainBackgroundColor = Color(0xFF121212) // A slightly darker background to show the border
    val cornerRadius = 24.dp // Top corner radius
    val cutoutRadius = (centralIconSize / 2f) + centralIconBorderWidth // Cutout includes the border
    val waveDipHeight = 43.dp // How deep the wave goes

    Box(
        modifier = Modifier
            .fillMaxWidth(),
//            .height(navBarHeight + centralIconSize / 2 - 10.dp) // Adjust height to accommodate the floating icon
//            .background(mainBackgroundColor), // Overall background to show the 'cutout' color
        contentAlignment = Alignment.BottomCenter
    ) {
        // --- Layer 1: The custom shaped navigation bar ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(navBarHeight)
                .align(Alignment.BottomCenter), // Align the Surface to the bottom
            color = darkBarColor,
            shape = RoundedWavyCutoutShape(
                cornerRadius = cornerRadius,
                cutoutRadius = cutoutRadius,
                waveHeight = waveDipHeight,
                centralOffsetFraction = 0.8f // Adjust this for wave shape
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp), // Add padding for items
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Determine the correct indices to skip the central "cutout" space
                val navItems = items.subList(0, 2) + items.subList(2, items.size)

                // Render the first two items
                for (i in 0 until 2) {
                    val item = navItems[i]
                    BottomNavItem(item, selectedItem == item.title) {
                        selectedItem = item.title
                    }
                }

                // Spacer for the central icon area (including its border)
                Spacer(modifier = Modifier.width(cutoutRadius * 2))

                // Render the last two items
                for (i in 2 until navItems.size) {
                    val item = navItems[i]
                    BottomNavItem(item, selectedItem == item.title) {
                        selectedItem = item.title
                    }
                }
            }
        }

        // --- Layer 2: The central "grape-like" icon ---
        Box(
            modifier = Modifier
                .size(centralIconSize)
                .align(Alignment.TopCenter) // Position relative to the parent Box
                .offset(y = (-navBarHeight / 2) + (waveDipHeight / 2) - 10.dp) // Lift it to sit in the dip
                .background(centralIconColor, shape = RoundedCornerShape(percent = 50)), // Central purple circle
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Store",
                tint = Color.White,
                modifier = Modifier.size(centralIconSize * 0.45f)
            )
        }
    }
}


// 5. Preview the result
@Preview(showBackground = true, widthDp = 360, heightDp = 200)
@Composable
fun PreviewCustomBottomNavBar() {
    Surface(color = Color.Black) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            CustomBottomNavBar()
        }
    }
}