package com.grammaradventure.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.grammaradventure.app.domain.model.GrammarWorld
import com.grammaradventure.app.ui.theme.IndigoInk
import com.grammaradventure.app.ui.theme.OutlineMuted

fun parseHexColor(hex: String, fallback: Color = IndigoInk): Color {
    return try {
        val cleaned = hex.removePrefix("#")
        val colorInt = cleaned.toLong(16)
        when (cleaned.length) {
            6 -> Color(0xFF000000L or colorInt)
            8 -> Color(colorInt)
            else -> fallback
        }
    } catch (_: Exception) {
        fallback
    }
}

fun characterDisplayName(characterId: String): String = when (characterId) {
    "ACHARYA_VIDYADHAR" -> "Acharya Vidyadhar"
    "CHIKOO_BANDAR" -> "Chikoo Bandar"
    "DETECTIVE_IRA" -> "Detective Ira"
    "MAHARAJ_VAKYA" -> "Maharaj Vakya"
    "VAANI" -> "Vaani"
    else -> "Grammar Guide"
}

fun characterShortTag(characterId: String): String = when (characterId) {
    "ACHARYA_VIDYADHAR" -> "AV"
    "CHIKOO_BANDAR" -> "CB"
    "DETECTIVE_IRA" -> "DI"
    "MAHARAJ_VAKYA" -> "MV"
    "VAANI" -> "VN"
    else -> "GG"
}

fun worldIcon(worldId: String): ImageVector = when (worldId) {
    "WORLD_1" -> Icons.Rounded.MenuBook
    "WORLD_2" -> Icons.Rounded.Pets
    "WORLD_3" -> Icons.Rounded.Schedule
    "WORLD_4" -> Icons.Rounded.Search
    "WORLD_5" -> Icons.Rounded.Gavel
    "WORLD_6" -> Icons.Rounded.AutoAwesome
    else -> Icons.Rounded.Badge
}

@Composable
fun CharacterAvatarBadge(
    characterId: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    sizeDp: Int = 44
) {
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = characterShortTag(characterId),
            style = MaterialTheme.typography.titleMedium,
            color = accentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusTag(
    label: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun GrammarWorldCard(
    world: GrammarWorld,
    completedCount: Int,
    totalCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = parseHexColor(world.accentColorHex)
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, OutlineMuted),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = worldIcon(world.id),
                        contentDescription = null,
                        tint = accent
                    )
                }
                StatusTag(
                    label = "World ${world.order}",
                    containerColor = accent.copy(alpha = 0.12f),
                    contentColor = accent
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = world.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = world.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = accent,
                    trackColor = accent.copy(alpha = 0.15f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$completedCount/$totalCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
